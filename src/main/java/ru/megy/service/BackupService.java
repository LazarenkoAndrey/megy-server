package ru.megy.service;

import org.springframework.transaction.annotation.Transactional;
import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Repo;
import ru.megy.service.entity.TaskThread;

import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

public interface BackupService {
    List<Backup> getBackupList();
    Backup getBackup(Long id);
    List<Backup> getBackupList(Repo repo);
    Long createBackup(Long repoId, Path path) throws ServiceException;
    SortedMap<String, SortedSet<String>> check(Long backupId, TaskThread taskThread) throws ServiceException;
    Long sync(Long backupId, TaskThread taskThread) throws ServiceException;
}
