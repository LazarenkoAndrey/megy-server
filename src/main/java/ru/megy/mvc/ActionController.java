package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.megy.exception.ServiceException;
import ru.megy.exception.ViewException;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.entity.Repo;
import ru.megy.service.BackupService;
import ru.megy.service.RepoService;
import ru.megy.service.TaskService;
import ru.megy.service.entity.TaskThread;

import java.util.List;

@Controller
public class ActionController {
    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    private BackupService backupService;
    @Autowired
    private TaskService taskService;

    @RequestMapping("/action/backup")
    public String backupAction(@RequestParam("backupId") long backupId, @RequestParam("method") String method, Model model) throws ViewException {
        if(method.equals("sync")) {
            TaskThread taskThread = new TaskThread("backup.sync(" + backupId + ")", taskService) {
                @Override
                public void process() throws ServiceException {
                    logger.info("Starting to sync of backup. backupId: {}", backupId);
                    Long versionId = backupService.sync(backupId, this);
                    logger.info("Sync of backup was completed successfully. backupId: {}, versionId: {}", backupId, versionId);
                }
            };
            taskThread.start();

            return "redirect:/pages/taskList?selected="+taskThread.getId();
        } else {
            throw new ViewException("Method value is incorrect1");
        }
    }
}