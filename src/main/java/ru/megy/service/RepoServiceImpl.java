package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.megy.exception.ServiceException;
import ru.megy.repository.RepoRepository;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.Repo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepoServiceImpl implements RepoService {
    private static final Logger logger = LoggerFactory.getLogger(RepoServiceImpl.class);

    @Autowired
    private RepoRepository repoRepository;

    @Transactional
    @Override
    public Repo getRepo(Long id) {
        return repoRepository.findOne(id);
    }

    @Transactional
    @Override
    public List<Repo> getRepoList() {
        List<Repo> repoList = new ArrayList<>();
        repoRepository.findAll().forEach(repo -> repoList.add(repo));

        return repoList;
    }

    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public Long createRepository(Path path) throws ServiceException {
        if(path==null) {
            throw new ServiceException("path is null");
        }
        if(!Files.isReadable(path)) {
            throw new ServiceException("path is not readable");
        }
        if(!Files.isDirectory(path)) {
            throw new ServiceException("path is not directory");
        }

        Repo repo = new Repo();
        repo.setPath(path.toAbsolutePath().normalize().toString());
        repo = repoRepository.save(repo);

        return repo.getId();
    }
}
