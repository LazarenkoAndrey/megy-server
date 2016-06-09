package ru.megy.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Reserve;

import java.util.List;

public interface ReserveRepository extends CrudRepository<Reserve, Long> {
    List<Reserve> findAllByBackup(Backup backup);

    @Query( "select r from Reserve r where r.backup = :backup and :versionId between r.versionsId and r.lastVersionsId")
    List<Reserve> findAllByBackupAndVersion(@Param("backup") Backup backup, @Param("versionId") Long versionId);
}
