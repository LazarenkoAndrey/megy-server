package ru.megy.service;

import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Repo;

import java.nio.file.Path;
import java.util.List;

public interface RepoService {
    Repo getRepo(Long id);
    List<Repo> getRepoList();
    Long createRepository(Path path) throws ServiceException;
}
