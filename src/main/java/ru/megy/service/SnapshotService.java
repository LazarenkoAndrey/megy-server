package ru.megy.service;

import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.SnapshotVersion;
import ru.megy.service.entity.TaskThread;

import java.util.List;

public interface SnapshotService {
    List<SnapshotVersion> getVersions(Long repoId, int top);
    Long createSnapshot(Long repositoryId, boolean calcSha512, TaskThread taskThread) throws ServiceException;
}
