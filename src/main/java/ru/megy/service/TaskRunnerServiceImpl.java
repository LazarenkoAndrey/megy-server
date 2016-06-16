package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.megy.exception.ServiceException;
import ru.megy.service.entity.ResultCheckFileSystem;
import ru.megy.service.entity.TaskThread;

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
    public Long doBackupSync(long backupId) {
        TaskThread taskThread = new TaskThread("backup.sync(" + backupId + ")", taskService) {
            @Override
            public void process() throws ServiceException {
                logger.info("Synchronization of repository. backupId: {}", backupId);
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
        TaskThread taskThread = new TaskThread("checkFreeSpace", taskService) {
            @Override
            public void process() throws ServiceException {
                ResultCheckFileSystem result = checkFileSystemService.checkFreeSpace();

                if(!result.isResult()) {
                    logger.info("Result of check files system: {}\n{}", result.isResult(), result.getMessage());
                    messageService.addMessage("Result of check files system:\r\n" + result.getMessage());
                }
            }
        };

        logger.info("Starting of task for check free space, taskId: {}", taskThread.getId());
        taskThread.start();

        return taskThread.getId();
    }
}
