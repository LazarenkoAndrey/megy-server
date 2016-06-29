package ru.megy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.type.ItemTypeEnum;

import javax.persistence.EnumType;

public interface BackupVersionRepository extends CrudRepository<BackupVersion, Long> {
    Page<BackupVersion> findAll(Pageable pageable);
    Page<BackupVersion> findAllByBackup(Backup backup, Pageable pageable);

    @Query("select sum(r.sizeByte) from Reserve r where r.backup.id = :backupId and :versionId between r.versionId and r.lastVersionId and r.type = 'FILE'")
    Long getTotalSize(@Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(r) from Reserve r where r.backup.id = :backupId and :versionId between r.versionId and r.lastVersionId and r.type = :itemType")
    Long getTotalItems(@Param("itemType") ItemTypeEnum itemType, @Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(r) from Reserve r where r.backup.id = :backupId and r.versionId = :versionId and r.type = :itemType" +
           "    and not exists(select o from Reserve o where nvl(o.path,'.') =  nvl(r.path,'.') and o.backup.id = :backupId and o.lastVersionId = :versionId - 1L and o.type = :itemType)")
    Long getNumberAddedItems(@Param("itemType") ItemTypeEnum itemType, @Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(r) from Reserve r where r.backup.id = :backupId and r.lastVersionId = :versionId - 1L and r.type = :itemType" +
            "    and not exists(select o from Reserve o where nvl(o.path,'.') = nvl(r.path,'.') and o.backup.id = :backupId and o.versionId = :versionId and o.type = :itemType)")
    Long getNumberRemovedItems(@Param("itemType") ItemTypeEnum itemType, @Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(r) from Reserve r where r.backup.id = :backupId and r.versionId = :versionId and r.type = :itemType" +
            "    and exists(select o from Reserve o where nvl(o.path,'.') = nvl(r.path,'.') and o.backup.id = :backupId and o.lastVersionId = :versionId - 1L and o.type = :itemType)")
    Long getNumberUpdatedItems(@Param("itemType") ItemTypeEnum itemType, @Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query("select count(s) from Store s where s.backup.id = :backupId " +
            "    and not exists(select o from Reserve o join o.store os where o.backup.id = :backupId and :versionId - 1L between o.versionId and o.lastVersionId and os.id=s.id)")
    Long getNumberNewStorages(@Param("backupId") Long backupId, @Param("versionId") Long versionId);

}
