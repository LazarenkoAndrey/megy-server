package ru.megy.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Reserve;
import ru.megy.repository.entity.Store;

import java.util.List;

public interface StoreRepository extends CrudRepository<Store, Long> {
    List<Store> findAllByBackupAndSha512(Backup backup, String sha512);

    @Query( "select distinct s from Reserve r join r.store s where r.backup.id = :backupId")
    List<Store> findAllByBackup(@Param("backupId") Long backupId);
}
