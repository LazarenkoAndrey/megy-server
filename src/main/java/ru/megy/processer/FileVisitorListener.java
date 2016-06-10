package ru.megy.processer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.megy.repository.entity.Item;
import ru.megy.repository.entity.SnapshotVersion;
import ru.megy.repository.type.ItemTypeEnum;
import ru.megy.service.entity.TaskThread;
import ru.megy.util.FUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class FileVisitorListener implements FileVisitor<Path> {
    private static final Logger logger = LoggerFactory.getLogger(FileVisitorListener.class);

    private SnapshotVersion snapshotVersion;
    private TaskThread taskThread;
    private float maxPercent;
    private Path repoPath;
    private Item currentDir;
    private long cntItem;
    private long totalItem;

    private Item rootItem;
    private List<Item> itemList;

    public FileVisitorListener(SnapshotVersion SnapshotVersion, TaskThread taskThread, float maxPercent) {
        this.snapshotVersion = SnapshotVersion;
        this.taskThread = taskThread;
        this.maxPercent = maxPercent;
        repoPath = Paths.get(SnapshotVersion.getRepo().getPath());
        cntItem = 0L;
        totalItem = FUtils.getCountItems(repoPath);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(dir, BasicFileAttributeView.class);
        BasicFileAttributes basicFileAttributes = basicFileAttributeView.readAttributes();

        Item item = new Item();
        item.setChildes(new HashSet<>());
        item.setType(ItemTypeEnum.DIR);
        item.setPath(repoPath.relativize(dir).toString());
        item.setVersions(snapshotVersion);
        item.setName(dir.getFileName().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(basicFileAttributes.lastModifiedTime().toMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        item.setLastModified(calendar.getTime());
        item.setLength(0);
        item.setSizeByte(0L);
        item.setParent(currentDir);
        if(currentDir!=null) {
            currentDir.getChildes().add(item);
        }
        currentDir = item;
        addStore(item);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(file, BasicFileAttributeView.class);
        BasicFileAttributes basicFileAttributes = basicFileAttributeView.readAttributes();

        Item item = new Item();
        item.setType(ItemTypeEnum.FILE);
        item.setPath(repoPath.relativize(file).toString());
        item.setVersions(snapshotVersion);
        item.setName(file.getFileName().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(basicFileAttributes.lastModifiedTime().toMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        item.setLastModified(calendar.getTime());
        item.setLength(0);
        item.setSizeByte(basicFileAttributes.size());
        item.setParent(currentDir);
        if(snapshotVersion.getCalcSha512()) {
            item.setSha512(FUtils.sha512(file));
        }
        currentDir.getChildes().add(item);
        addStore(item);

        cntItem++;
        taskThread.setPercent(maxPercent * cntItem / totalItem);
        if(taskThread.isStopping()){
            logger.error("It was interrupt from taskThread");
            throw new IOException(new InterruptedException("It was interrupt from taskThread"));
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        logger.error("visitFileFailed {}", file.toString(), exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        int length = 0;
        long sizeByte = 0L;
        SortedSet<String> hashList = new TreeSet<>();

        for(Item item : currentDir.getChildes()) {
            if(snapshotVersion.getCalcSha512()) {
                hashList.add(item.getSha512());
            }
            sizeByte += item.getSizeByte();
            length += item.getLength();
            length++;
        }
        currentDir.setSizeByte(sizeByte);
        currentDir.setLength(length);
        if(snapshotVersion.getCalcSha512()) {
            currentDir.setSha512(FUtils.sha512(hashList));
        }
        currentDir = currentDir.getParent();

        cntItem++;
        taskThread.setPercent(maxPercent * cntItem / totalItem);
        if(taskThread.isStopping()){
            logger.error("It was interrupt from taskThread");
            throw new IOException(new InterruptedException("It was interrupt from taskThread"));
        }

        return FileVisitResult.CONTINUE;

    }

    private void addStore(Item item) {
        if(itemList ==null) {
            itemList = new ArrayList<>();
        }
        itemList.add(item);
        if(rootItem ==null) {
            rootItem = item;
        }
    }

    public Path getRepoPath() {
        return repoPath;
    }

    public Item getRootItem() {
        return rootItem;
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
