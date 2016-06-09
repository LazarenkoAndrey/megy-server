package ru.megy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Repo;
import ru.megy.repository.entity.SnapshotVersion;

public interface SnapshotVersionRepository extends CrudRepository<SnapshotVersion, Long> {
    Page<SnapshotVersion> findAll(Pageable pageable);
    Page<SnapshotVersion> findAllByRepo(Repo repo, Pageable pageable);
}
