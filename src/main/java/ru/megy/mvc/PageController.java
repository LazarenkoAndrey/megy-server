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
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private BackupService backupService;
    @Autowired
    private RepoService repoService;
    @Autowired
    private TaskService taskService;

    @RequestMapping("/pages/backupList")
    public String backupList(Model model) {
        List<Backup> backupList = backupService.getBackups();
        model.addAttribute("backupList", backupList);

        return "/pages/backupList";
    }

    @RequestMapping("/pages/repoList")
    public String repoList(Model model) {
        List<Repo> repoList = repoService.getRepos();
        model.addAttribute("repoList", repoList);

        return "/pages/repoList";
    }

    @RequestMapping("/pages/taskList")
    public String taskList(@RequestParam("selected") Long selectedTaskId, Model model) {
        List<TaskThread> activeTaskList = taskService.getActiveTaskList();
        List<TaskThread> completedTaskList = taskService.getCompletedTaskList();
        model.addAttribute("activeTaskList", activeTaskList);
        model.addAttribute("completedTaskList", completedTaskList);
        model.addAttribute("selectedTaskId", selectedTaskId);

        return "/pages/taskList";
    }

    @RequestMapping("/pages/backupView/{backupId}")
    public String backupView(@PathVariable("backupId") long backupId, Model model) {
        Backup backup = backupService.getBackup(backupId);
        model.addAttribute("backup", backup);

        List<BackupVersion> backupVersionList = backupService.getVersions(backupId, 100);
        model.addAttribute("backupVersionList", backupVersionList);

        return "/pages/backupView";
    }

    @RequestMapping("/action/backup")
    public String backupAction(@RequestParam("backupId") long backupId, @RequestParam("method") String method, Model model) throws ViewException {
        if(method.equals("sync")) {
            TaskThread taskThread = new TaskThread("backup.sync(" + backupId + ")", taskService) {
                @Override
                public void process() throws ServiceException {
                    logger.info("Starting to sync of backup. backupId: {}", backupId);
                    Long versionId = backupService.sync(Long.valueOf(backupId), this);
                    logger.info("Sync of backup was completed successfully. backupId: {}, versionId: {}", backupId, versionId);
                }
            };
            taskThread.start();

            return "redirect:/pages/taskList?selected="+taskThread.getId();
        } else {
            throw new ViewException("Method value is incorrect");
        }
    }
}