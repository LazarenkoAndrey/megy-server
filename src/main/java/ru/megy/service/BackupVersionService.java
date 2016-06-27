package ru.megy.service;

import ru.megy.repository.entity.BackupVersion;

import java.util.List;

public interface BackupVersionService {
    BackupVersion getVersion(Long id);
    List<BackupVersion> getVersionList(Long backupId, int top);
}
