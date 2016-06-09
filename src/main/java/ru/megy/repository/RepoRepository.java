package ru.megy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.megy.repository.entity.Repo;

public interface RepoRepository extends CrudRepository<Repo, Long> {
}
