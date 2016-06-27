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
import ru.megy.repository.BackupRepository;
import ru.megy.repository.BackupVersionRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;

import java.util.ArrayList;
import java.util.List;

@Service
public class BackupVersionServiceImpl implements BackupVersionService {
    private static final Logger logger = LoggerFactory.getLogger(BackupVersionServiceImpl.class);

    @Autowired
    private BackupVersionRepository backupVersionRepository;
    @Autowired
    private BackupRepository backupRepository;

    @Transactional
    @Override
    public BackupVersion getVersion(Long id) {
        return backupVersionRepository.findOne(id);
    }

    @Transactional
    @Override
    public List<BackupVersion> getVersionList(Long backupId, int top) {
        Pageable pageable = new PageRequest(0, top, new Sort(Sort.Direction.DESC, "createdDate"));
        Page<BackupVersion> pages;

        if(backupId!=null) {
            Backup backup = backupRepository.findOne(backupId);
            pages = backupVersionRepository.findAllByBackup(backup, pageable);
        } else {
            pages = backupVersionRepository.findAll(pageable);
        }

        List<BackupVersion> backupVersionList = new ArrayList<>();
        pages.forEach(version -> backupVersionList.add(version));

        return backupVersionList;
    }
}