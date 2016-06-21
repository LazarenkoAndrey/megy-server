package ru.megy.service;

import org.springframework.transaction.annotation.Transactional;
import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.entity.Repo;
import ru.megy.service.entity.TaskThread;

import java.nio.file.Path;
import java.util.List;

public interface BackupService {
    List<Backup> getBackupList();
    Backup getBackup(Long id);

    @Transactional
    List<Backup> getBackupList(Repo repo);

    Long createBackup(Long repoId, Path path) throws ServiceException;
    List<BackupVersion> getVersionList(Long backupId, int top);
    Long sync(Long backupId, TaskThread taskThread) throws ServiceException;
}
