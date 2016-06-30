package ru.megy.service;

public interface TaskRunnerService {
    Long doBackupCheck(long backupId);
    Long doBackupSync(long backupId);
    Long doCheckFreeSpace();
}
