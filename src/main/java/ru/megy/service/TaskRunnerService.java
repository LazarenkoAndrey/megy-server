package ru.megy.service;

public interface TaskRunnerService {
    Long doBackupSync(long backupId);
    Long doCheckFreeSpace();
}
