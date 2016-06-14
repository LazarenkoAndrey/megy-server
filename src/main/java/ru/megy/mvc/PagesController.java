package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.megy.exception.ServiceException;
import ru.megy.exception.ViewException;
import ru.megy.mvc.objects.PathVO;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.entity.Repo;
import ru.megy.service.BackupService;
import ru.megy.service.RepoService;
import ru.megy.service.TaskService;
import ru.megy.service.entity.TaskThread;

import java.util.List;

@Controller
public class PagesController {
    private static final Logger logger = LoggerFactory.getLogger(PagesController.class);

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
    public String repoList(@RequestParam(value = "selected", required=false) Long selectedTaskId, Model model) {
        List<Repo> repoList = repoService.getRepos();
        model.addAttribute("repoList", repoList);
        model.addAttribute("selectedRepoId", selectedTaskId);

        return "/pages/repoList";
    }

    @RequestMapping("/pages/repoCreate")
    public ModelAndView repoCreate() {
        return new ModelAndView("/pages/repoCreate", "pathVO", new PathVO());
    }

    @RequestMapping("/pages/taskList")
    public String taskList(@RequestParam(value = "selected", required=false) Long selectedTaskId, Model model) {
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
}