package ru.megy.service;

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
import java.io.FileNotFoundException;
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
    public Long sync(Long backupId, TaskThread taskThread) throws ServiceException {
        try {
            Backup backup = backupRepository.findOne(backupId);
            if(backup==null) {
                throw new ServiceException(String.format("Backup with id %d don't found", backupId));
            }

            if(!Paths.get(backup.getPath()).toFile().exists()) {
                throw new FileNotFoundException("Files directory not found. " + backup.getPath());
            }

            Repo repo = backup.getRepo();
            if(!Paths.get(repo.getPath()).toFile().exists()) {
                throw new FileNotFoundException("Files directory not found. " + repo.getPath());
            }

            FileLock fileLock = lockBackup(backup);
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

            List<Reserve> reserveList = reserveRepository.findAllByBackupAndVersion(backup, backup.getLastVersion()!=null ? backup.getLastVersion().getId() : null);

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
                    logger.error("It was interrupt from taskThread");
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
                    logger.error("It was interrupt from taskThread");
                    throw new InterruptedException("It was interrupt from taskThread");
                }
            }

            reserveRepository.save(forSave);
            taskThread.setPercent(100.0f);
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
        Path pathFile = Paths.get(repoPath, itemPath);
        String sha512 = FUtils.sha512(pathFile);
        Long size = FUtils.getSizeItems(pathFile);

        Store store = null;
        List<Store> listStore = storeRepository.findAllByBackupAndSha512(newReserve.getBackup(), sha512);
        for(Store itemStore : listStore) {
            if(!itemStore.getSizeByte().equals(size)) {
                continue;
            }
            Path compPath = Paths.get(itemStore.getBackup().getPath(), itemStore.getPath());
            if(FUtils.fileComparison(pathFile, compPath)) {
                store = itemStore;
            }
        }

        if(store == null) {
            String newPathString = pathForStore(sha512, newReserve.getBackup());
            Path newFullPath = Paths.get(newReserve.getBackup().getPath(), newPathString);
            Files.createDirectories(newFullPath.getParent());
            Files.copy(pathFile, newFullPath);

            store = new Store();
            store.setBackup(newReserve.getBackup());
            store.setPath(newPathString);
            store.setSha512(sha512);
            store.setSizeByte(size);
        }

        return store;
    }

    private String pathForStore(String sha512, Backup backup) {
        String backupPath = backup.getPath();

        Long backupId = backup.getId();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        String sub1 = sha512.substring(0,2);
        String sub2 = sha512.substring(2,10);
        String pathDir = backupId.toString() + File.separatorChar +
                sdfYear.format(date) + File.separatorChar +
                sdfDate.format(date) + File.separatorChar +
                sub1;

        Path dir = Paths.get(backupPath);
        Path pathFile = Paths.get(pathDir, sub2);
        long num = 0;
        while(Files.exists(dir.resolve(pathFile))) {
            pathFile = Paths.get(pathDir, sub2+ num);
            num++;
        }

        return pathFile.toString();
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
