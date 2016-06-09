package ru.megy.service;

import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.service.entity.TaskThread;

import java.nio.file.Path;
import java.util.List;

public interface BackupService {
    List<Backup> getBackups();
    Backup getBackup(Long id);
    Long createBackup(Long repoId, Path path) throws ServiceException;
    List<BackupVersion> getVersions(Long backupId, int top);
    Long sync(Long backupId, TaskThread taskThread) throws ServiceException;
}
