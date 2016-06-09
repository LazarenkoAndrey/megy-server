package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.megy.exception.ServiceException;
import ru.megy.processer.FileVisitorListener;
import ru.megy.repository.ItemRepository;
import ru.megy.repository.RepoRepository;
import ru.megy.repository.SnapshotVersionRepository;
import ru.megy.repository.entity.Repo;
import ru.megy.repository.entity.SnapshotVersion;
import ru.megy.service.entity.TaskThread;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SnapshotServiceImpl implements SnapshotService {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    @Autowired
    private SnapshotVersionRepository snapshotVersionRepository;
    @Autowired
    private RepoRepository repoRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    @Override
    public List<SnapshotVersion> getVersions(Long repoId, int top) {
        Pageable pageable = new PageRequest(0, top, new Sort(Sort.Direction.DESC, "createdDate"));
        Page<SnapshotVersion> pages;

        if(repoId!=null) {
            Repo repo = repoRepository.findOne(repoId);
            pages = snapshotVersionRepository.findAllByRepo(repo, pageable);
        } else {
            pages = snapshotVersionRepository.findAll(pageable);
        }

        List<SnapshotVersion> SnapshotVersionList = new ArrayList<>();
        pages.forEach(version -> SnapshotVersionList.add(version));

        return SnapshotVersionList;
    }

    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public Long createSnapshot(Long repoId, boolean calcSha512, TaskThread taskThread) throws ServiceException {
        Repo repo = repoRepository.findOne(repoId);
        if(repo==null) {
            throw new ServiceException(String.format("Repository with id %d don't found", repoId));
        }

        SnapshotVersion snapshotVersion = new SnapshotVersion();
        snapshotVersion.setCreatedDate(new Date());
        snapshotVersion.setRepo(repo);
        snapshotVersion.setCalcSha512(calcSha512);

        FileVisitorListener fileVisitorListener = new FileVisitorListener(snapshotVersion, taskThread, 90.0f);
        try {
            Files.walkFileTree(fileVisitorListener.getRepoPath(), fileVisitorListener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        snapshotVersion = snapshotVersionRepository.save(snapshotVersion);
        itemRepository.save(fileVisitorListener.getItemList());
        taskThread.setPercent(100.0f);

        return snapshotVersion.getId();
    }
}
