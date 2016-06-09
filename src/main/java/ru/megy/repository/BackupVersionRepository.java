package ru.megy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;

public interface BackupVersionRepository extends CrudRepository<BackupVersion, Long> {
    Page<BackupVersion> findAll(Pageable pageable);
    Page<BackupVersion> findAllByBackup(Backup backup, Pageable pageable);
}
