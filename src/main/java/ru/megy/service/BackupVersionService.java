package ru.megy.service;

import org.springframework.transaction.annotation.Transactional;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.service.entity.BackupVersionStatistic;

import java.util.List;

public interface BackupVersionService {
    BackupVersion getVersion(Long id);
    List<BackupVersion> getVersionList(Long backupId, int top);
    List<BackupVersionStatistic> getStatistics(BackupVersion backupVersion);
}
