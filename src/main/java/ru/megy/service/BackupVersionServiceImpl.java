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
import ru.megy.repository.type.ItemTypeEnum;
import ru.megy.service.entity.BackupVersionStatistic;

import javax.persistence.EnumType;
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

    @Transactional
    @Override
    public List<BackupVersionStatistic> getStatistics(BackupVersion backupVersion) {
        Long backupId = backupVersion.getBackup().getId();
        Long versionId = backupVersion.getId();

        BackupVersionStatistic statistic;
        List<BackupVersionStatistic> list = new ArrayList<>();

        statistic = new BackupVersionStatistic();
        statistic.setName("Total files size (byte)");
        statistic.setValue(backupVersionRepository.getTotalSize(backupId, versionId));
        list.add(statistic);

        statistic = new BackupVersionStatistic();
        statistic.setName("Total number of files");
        statistic.setValue(backupVersionRepository.getTotalItems(ItemTypeEnum.FILE, backupId, versionId));
        list.add(statistic);

        statistic = new BackupVersionStatistic();
        statistic.setName("The number of added files");
        statistic.setValue(backupVersionRepository.getNumberAddedItems(ItemTypeEnum.FILE, backupId, versionId));
        list.add(statistic);

        statistic = new BackupVersionStatistic();
        statistic.setName("The number of updated files");
        statistic.setValue(backupVersionRepository.getNumberUpdatedItems(ItemTypeEnum.FILE, backupId, versionId));
        list.add(statistic);

        statistic = new BackupVersionStatistic();
        statistic.setName("The number of removed files");
        statistic.setValue(backupVersionRepository.getNumberRemovedItems(ItemTypeEnum.FILE, backupId, versionId));
        list.add(statistic);

        statistic = new BackupVersionStatistic();
        statistic.setName("The number of new storages");
        statistic.setValue(backupVersionRepository.getNumberNewStorages(backupId, versionId));
        list.add(statistic);

        return list;
    }
}
