package ru.megy.service;

import ru.megy.exception.ServiceException;
import ru.megy.service.entity.ResultCheckFileSystem;

public interface CheckFileSystemService {
    ResultCheckFileSystem checkFreeSpace() throws ServiceException;
}
