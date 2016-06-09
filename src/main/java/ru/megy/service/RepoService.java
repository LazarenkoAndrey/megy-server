package ru.megy.service;

import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Repo;

import java.nio.file.Path;
import java.util.List;

public interface RepoService {
    List<Repo> getRepos();
    Long createRepository(Path path) throws ServiceException;
}
