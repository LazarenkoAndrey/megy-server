package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.megy.exception.ServiceException;
import ru.megy.service.entity.ResultCheckFileSystem;
import ru.megy.service.entity.TaskThread;
import ru.megy.service.entity.TaskThreadWithResult;

import java.util.SortedMap;
import java.util.SortedSet;

@Service
public class TaskRunnerServiceImpl implements TaskRunnerService {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunnerServiceImpl.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private CheckFileSystemService checkFileSystemService;
    @Autowired
    private MessageService messageService;

    @Override
    public Long doBackupCheck(long backupId) {
        TaskThread taskThread = new TaskThreadWithResult("Check of backup (backupId" + backupId + ")", taskService) {
            @Override
            public void process() throws ServiceException {
                logger.info("Check of backup (backupId: {})", backupId);
                SortedMap<String, SortedSet<String>> result = backupService.check(Long.valueOf(backupId), this);
                this.setResult(result);
                logger.info("Check of backup was completed successfully");
            }
        };

        logger.info("Starting of task for check of backup, taskId: {}", taskThread.getId());
        taskThread.start();

        return taskThread.getId();
    }

    @Override
    public Long doBackupSync(long backupId) {
        TaskThread taskThread = new TaskThread("Synchronization of repository (backupId: " + backupId + ")", taskService) {
            @Override
            public void process() throws ServiceException {
                logger.info("Synchronization of repository (backupId: {})", backupId);
                Long versionId = backupService.sync(Long.valueOf(backupId), this);
                logger.info("Sync of backup was completed successfully. BackupId: {}, versionId: {}", backupId, versionId);
            }
        };

        logger.info("Starting of task for sync of backup, taskId: {}", taskThread.getId());
        taskThread.start();

        return taskThread.getId();
    }

    @Override
    public Long doCheckFreeSpace() {
        TaskThread taskThread = new TaskThread("Check free space ", taskService) {
            @Override
            public void process() throws ServiceException {
                ResultCheckFileSystem result = checkFileSystemService.checkFreeSpace();

                if(!result.isResult()) {
                    logger.info("Result of check free space: {}\n{}", result.isResult(), result.getMessage());
                    messageService.addMessage("Result of check free space:\r\n" + result.getMessage());
                }
            }
        };

        logger.info("Starting of task for check free space, taskId: {}", taskThread.getId());
        taskThread.start();

        return taskThread.getId();
    }
}
