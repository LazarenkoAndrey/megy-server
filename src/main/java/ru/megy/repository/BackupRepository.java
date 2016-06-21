package ru.megy.repository;

import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Repo;

import java.util.List;

public interface BackupRepository extends CrudRepository<Backup, Long> {
    List<Backup> findAllByRepo(Repo repo);
}
