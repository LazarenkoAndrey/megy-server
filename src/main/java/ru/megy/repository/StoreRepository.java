package ru.megy.repository;

import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Repo;
import ru.megy.repository.entity.Reserve;
import ru.megy.repository.entity.Store;

import java.util.List;

public interface StoreRepository extends CrudRepository<Store, Long> {
    List<Store> findAllByBackupAndSha512(Backup backup, String sha512);
}
