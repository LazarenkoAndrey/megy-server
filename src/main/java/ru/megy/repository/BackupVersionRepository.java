package ru.megy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;

public interface BackupVersionRepository extends CrudRepository<BackupVersion, Long> {
    Page<BackupVersion> findAll(Pageable pageable);
    Page<BackupVersion> findAllByBackup(Backup backup, Pageable pageable);

    @Query("select sum(r.sizeByte) from Reserve r where r.backup.id = :backupId and :versionId between r.versionId and r.lastVersionId and r.type='FILE'")
    Long getTotalSize(@Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(r) from Reserve r where r.backup.id = :backupId and :versionId between r.versionId and r.lastVersionId and r.type='FILE'")
    Long getTotalFiles(@Param("backupId") Long backupId, @Param("versionId") Long versionId);
}
