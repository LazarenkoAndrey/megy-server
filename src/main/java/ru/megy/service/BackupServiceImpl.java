package ru.megy.service;

import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.megy.exception.ServiceException;
import ru.megy.repository.*;
import ru.megy.repository.entity.*;
import ru.megy.repository.type.ItemTypeEnum;
import ru.megy.service.entity.TaskThread;
import ru.megy.util.FUtils;
import ru.megy.util.FileVisitorListener;
import ru.megy.util.objects.Item;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BackupServiceImpl implements BackupService {
    private static final Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);
    private static final String LOCK_FILE_NAME = ".lock";

    @Autowired
    private BackupVersionRepository backupVersionRepository;
    @Autowired
    private BackupRepository backupRepository;
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private ReserveRepository reserveRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Transactional
    @Override
    public Backup getBackup(Long id) {
        return backupRepository.findOne(id);
    }

    @Transactional
    @Override
    public List<Backup> getBackupList() {
        List<Backup> backupList = new ArrayList<>();
        backupRepository.findAll().forEach(backup -> backupList.add(backup));

        return backupList;
    }

    @Transactional
    @Override
    public List<Backup> getBackupList(Repo repo) {
        List<Backup> backupList = new ArrayList<>();
        backupRepository.findAllByRepo(repo).forEach(backup -> backupList.add(backup));

        return backupList;
    }

    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public Long createBackup(Long repoId, Path path) throws ServiceException {
        if(path==null) {
            throw new ServiceException("path is null");
        }
        if(!Files.isReadable(path)) {
            throw new ServiceException("path is not readable");
        }
        if(!Files.isWritable(path)) {
            throw new ServiceException("path is not writable");
        }
        if(!Files.isDirectory(path)) {
            throw new ServiceException("path is not directory");
        }

        Repo repo = repoRepository.findOne(repoId);
        if(repo==null) {
            throw new ServiceException(String.format("Repository with id %d don't found", repoId));
        }

        Backup backup = new Backup();
        backup.setPath(path.toAbsolutePath().normalize().toString());
        backup.setRepo(repo);
        backup = backupRepository.save(backup);

        return backup.getId();
    }


    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public SortedMap<String, SortedSet<String>> check(Long backupId, TaskThread taskThread) throws ServiceException {
        Backup backup = backupRepository.findOne(backupId);
        if(backup==null) {
            throw new ServiceException(String.format("Backup with id %d don't found", backupId));
        }

        if(!Paths.get(backup.getPath()).toFile().exists()) {
            throw new ServiceException("Files directory not found. " + backup.getPath());
        }

        Repo repo = backup.getRepo();
        if(!Paths.get(repo.getPath()).toFile().exists()) {
            throw new ServiceException("Files directory not found. " + repo.getPath());
        }

        try(FileLock fileLock = lockBackup(backup)) {
            SortedMap<String, SortedSet<String>> checkMessages = Collections.synchronizedSortedMap(new TreeMap<>());
            Set<String> storePaths = new HashSet<>();
            List<Store> storeList = storeRepository.findAllByBackup(backup.getId());
            int storeListSize = storeList.size();
            for(int i=0; i<storeListSize; i++) {
                taskThread.setPercent(50.0f * i / storeListSize);
                if(taskThread.isStopping()){
                    throw new InterruptedException("It was interrupt from taskThread");
                }

                Store store = storeList.get(i);
                storePaths.add(store.getPath());
                Path storeRealPath = Paths.get(backup.getPath(), store.getPath());

                if(!Files.exists(storeRealPath)) {
                    addCheckMessage(checkMessages, store.getPath(), "File not found");
                    continue;
                }

                Long sizeByte = FUtils.getSizeItems(storeRealPath);
                if(!store.getSizeByte().equals(sizeByte)) {
                    addCheckMessage(checkMessages, store.getPath(), "File size is not correct");
                    continue;
                }

                String sha512 = FUtils.sha512(storeRealPath);
                if(!store.getSha512().equals(sha512)) {
                    addCheckMessage(checkMessages, store.getPath(), "Hash-sum is not correct");
                    continue;
                }
            }

            Path root = Paths.get(backup.getPath(), backup.getId().toString());
            AtomicLong size = new AtomicLong(0);
            Files.walk(root)
                    .forEach(path -> size.addAndGet(1));

            AtomicLong index = new AtomicLong(0);
            Files.walk(root)
                    .map(path -> {
                        index.addAndGet(1);
                        taskThread.setPercent(50.0f + 50.0f * index.get() / size.get());
                        if(taskThread.isStopping()){
                            throw new RuntimeException("It was interrupt from taskThread");
                        }
                        return path.toFile();
                    })
                    .filter(file -> file.isFile())
                    .map(file -> Paths.get(file.getAbsolutePath()).relativize(root))
                    .filter(path -> !storePaths.contains(path.toString()))
                    .forEach(path -> addCheckMessage(checkMessages, path.toString(), "The file is not a storage and can be removed"));

            taskThread.setPercent(100.0f);
            unlockBackup(fileLock);
            return checkMessages;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private void addCheckMessage(Map<String, SortedSet<String>> checkMessages, String value, String message) {
        if(!checkMessages.containsKey(message)) {
            checkMessages.put(message, Collections.synchronizedSortedSet(new TreeSet<>()));
        }
        SortedSet<String> set =  checkMessages.get(message);
        set.add(value);
    }

    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public Long sync(Long backupId, TaskThread taskThread) throws ServiceException {
        Backup backup = backupRepository.findOne(backupId);
        if(backup==null) {
            throw new ServiceException(String.format("Backup with id %d don't found", backupId));
        }

        if(!Paths.get(backup.getPath()).toFile().exists()) {
            throw new ServiceException("Files directory not found. " + backup.getPath());
        }

        Repo repo = backup.getRepo();
        if(!Paths.get(repo.getPath()).toFile().exists()) {
            throw new ServiceException("Files directory not found. " + repo.getPath());
        }


        try(FileLock fileLock = lockBackup(backup)) {
            FileVisitorListener fileVisitorListener = new FileVisitorListener(repo, false, taskThread, 10.0f);
            Files.walkFileTree(fileVisitorListener.getRepoPath(), fileVisitorListener);

            Map<String, Item> mapItem = new HashMap<>();
            for(Item item : fileVisitorListener.getItemList()) {
                mapItem.put(item.getPath(), item);
            }

            BackupVersion backupVersion = new BackupVersion();
            backupVersion.setCreatedDate(new Date());
            backupVersion.setBackup(backup);
            backupVersion = backupVersionRepository.save(backupVersion);
            backup.setLastVersion(backupVersion);
            backup = backupRepository.save(backup);

            List<Reserve> reserveList = reserveRepository.findAllByBackupAndVersion(backup.getId(), backup.getLastVersion()!=null ? backup.getLastVersion().getId() : null);

            Map<String, Reserve> mapReserve = new HashMap<>();
            for(Reserve reserve : reserveList) {
                mapReserve.put(reserve.getPath(), reserve);
            }

            taskThread.setPercent(20.0f);
            int totalItem = reserveList.size();
            int cntItem = 0;
            List<Reserve> forSave = new ArrayList<>();
            for (Reserve reserve : reserveList) {
                String path = reserve.getPath();
                Item item = mapItem.get(path);
                if (item == null) {
                    reserve.setLastVersionId(backupVersion.getId() - 1);
                    forSave.add(reserve);
                    logger.debug("{} DELETE {}", reserve.getBackup().getId(), reserve.getPath());
                } else if (!item.getLastModified().equals(reserve.getLastModified())
                        || item.getType() != reserve.getType()
                        || !item.getLength().equals(reserve.getLength())
                        || !item.getSizeByte().equals(reserve.getSizeByte())) {
                    Reserve newReserve = reserveNew(item, backupVersion);
                    forSave.add(newReserve);
                    reserve.setLastVersionId(backupVersion.getId() - 1);
                    forSave.add(reserve);
                    logger.debug("{} UPDATE {}", reserve.getBackup().getId(), reserve.getPath());
                }

                cntItem++;
                taskThread.setPercent(20.0f + 50.0f * cntItem / totalItem);
                if(taskThread.isStopping()){
                    throw new InterruptedException("It was interrupt from taskThread");
                }
            }

            totalItem = fileVisitorListener.getItemList().size();
            cntItem = 0;
            for (Item item : fileVisitorListener.getItemList()) {
                String path = item.getPath();
                Reserve reserve = mapReserve.get(path);
                if (reserve == null) {
                    Reserve newReserve = reserveNew(item, backupVersion);
                    forSave.add(newReserve);
                    logger.debug("{} CREATE {}", newReserve.getBackup().getId(), newReserve.getPath());
                }

                cntItem++;
                taskThread.setPercent(70.0f + 20.0f * cntItem / totalItem);
                if(taskThread.isStopping()){
                    throw new InterruptedException("It was interrupt from taskThread");
                }
            }

            reserveRepository.save(forSave);
            taskThread.setPercent(100.0f);
            taskThread.setResultUrl("/pages/versionView/"+backupVersion.getId());
            unlockBackup(fileLock);

            return backupVersion.getId();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private Reserve reserveNew(Item item, BackupVersion backupVersion) throws IOException {
        Reserve newReserve = new Reserve();
        newReserve.setBackup(backupVersion.getBackup());
        newReserve.setPath(item.getPath());
        newReserve.setName(item.getName());
        newReserve.setSizeByte(item.getSizeByte());
        newReserve.setLength(item.getLength());
        newReserve.setType(item.getType());
        newReserve.setVersionId(backupVersion.getId());
        newReserve.setLastVersionId(BackupVersion.MAX_VERSION);
        newReserve.setLastModified(item.getLastModified());

        if(item.getType() == ItemTypeEnum.FILE) {
            Store store = createStore(newReserve);
            store = storeRepository.save(store);
            newReserve.setStore(store);
        }

        return newReserve;
    }

    private Store createStore(Reserve newReserve) throws IOException {
        String repoPath = newReserve.getBackup().getRepo().getPath();
        String itemPath = newReserve.getPath();
        Path realFilePath = Paths.get(repoPath, itemPath);
        String sha512 = FUtils.sha512(realFilePath);
        Long size = FUtils.getSizeItems(realFilePath);

        Store store = null;
        List<Store> listStore = storeRepository.findAllByBackupAndSha512(newReserve.getBackup(), sha512);
        for(Store itemStore : listStore) {
            if(!itemStore.getSizeByte().equals(size)) {
                continue;
            }
            Path storeFilePath = Paths.get(itemStore.getBackup().getPath(), itemStore.getPath());
            if(FUtils.fileComparison(realFilePath, storeFilePath)) {
                store = itemStore;
            }
        }

        if(store == null) {
            Path newStorePath = pathForStore(sha512, newReserve.getBackup());
            Path backupPath = Paths.get(newReserve.getBackup().getPath());
            Path fullPath = newStorePath.resolve(backupPath);
            Files.createDirectories(fullPath.getParent());
            Files.copy(realFilePath, fullPath);

            store = new Store();
            store.setBackup(newReserve.getBackup());
            store.setPath(newStorePath.toString());
            store.setSha512(sha512);
            store.setSizeByte(size);
        }

        return store;
    }

    private Path pathForStore(String sha512, Backup backup) {
        Path backupPath = Paths.get(backup.getPath());

        Long backupId = backup.getId();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        String subDir = sha512.substring(0,2);
        String fileName = sha512.substring(2, 10);
        Path subPath = Paths.get(backupId.toString(), sdfYear.format(date), sdfDate.format(date), subDir);

        Path subFilePath = subPath.resolve(Paths.get(fileName));
        long num = 0;
        while(Files.exists(backupPath.resolve(subFilePath))) {
            subFilePath = subPath.resolve(Paths.get(fileName, Long.toString(num)));
            num++;
        }

        return subFilePath;
    }

    private FileLock lockBackup(Backup backup) throws IOException {
        String backupPath = backup.getPath();
        String lockFileName = LOCK_FILE_NAME + backup.getId().toString();
        Path pathLockFile = Paths.get(backupPath, lockFileName);
        RandomAccessFile raFile = new RandomAccessFile(pathLockFile.toFile(), "rw");
        FileLock fileLock = raFile.getChannel().tryLock();
        String text = "BackupId: " + backup.getId().toString() + ", time: " + (new Date()).toString();
        raFile.getChannel().write(ByteBuffer.wrap(text.getBytes(Charset.forName("UTF-8"))));

        return fileLock;
    }

    private void unlockBackup(FileLock fileLock) throws IOException {
        fileLock.release();
    }
}
