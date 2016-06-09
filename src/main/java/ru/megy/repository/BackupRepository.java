package ru.megy.repository;

import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Backup;

public interface BackupRepository extends CrudRepository<Backup, Long> {
}
