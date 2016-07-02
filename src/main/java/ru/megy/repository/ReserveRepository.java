package ru.megy.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Reserve;

import java.util.List;

public interface ReserveRepository extends CrudRepository<Reserve, Long> {
    List<Reserve> findAllByBackup(Backup backup);

    @Query( "select r from Reserve r where r.backup.id = :backupId and :versionId between r.versionId and r.lastVersionId")
    List<Reserve> findAllByBackupAndVersion(@Param("backupId") Long backupId, @Param("versionId") Long versionId);

    @Query( "select r from Reserve r where r.backup.id = :backupId")
    List<Reserve> findAllByBackup(@Param("backupId") Long backupId);
}
