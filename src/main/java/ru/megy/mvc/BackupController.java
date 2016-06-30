package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.megy.exception.ViewException;
import ru.megy.mvc.objects.BackupVO;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.entity.Repo;
import ru.megy.service.BackupService;
import ru.megy.service.BackupVersionService;
import ru.megy.service.RepoService;
import ru.megy.service.TaskRunnerService;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class BackupController {
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private BackupService backupService;
    @Autowired
    private BackupVersionService backupVersionService;
    @Autowired
    private RepoService repoService;
    @Autowired
    private TaskRunnerService taskRunnerService;

    @RequestMapping("/pages/backupView/{backupId}")
    public String backupView(@PathVariable("backupId") long backupId, Model model) {
        Backup backup = backupService.getBackup(backupId);
        model.addAttribute("backup", backup);

        List<BackupVersion> backupVersionList = backupVersionService.getVersionList(backupId, 100);
        model.addAttribute("backupVersionList", backupVersionList);

        return "/pages/backupView";
    }

    @RequestMapping("/pages/backupCreate")
    public String gotoBackupCreate(@RequestParam("repoId") long repoId, Model model) {
        if(!model.containsAttribute("backupVO")) {
            BackupVO backupVO = new BackupVO();
            backupVO.setRepoId(repoId);
            model.addAttribute("backupVO", backupVO);
        }
        Repo repo = repoService.getRepo(repoId);
        model.addAttribute("repo", repo);

        return "/pages/backupCreate";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/action/backup/add")
    public String repoAction(@Valid BackupVO backupVO, BindingResult bindingResult, Model model) throws ViewException {
        if(bindingResult.hasErrors()) {
            return gotoBackupCreate(backupVO.getRepoId(), model);
        }

        Long backupId = null;
        try {
            Path pathValue = Paths.get(backupVO.getPath());
            backupId = backupService.createBackup(backupVO.getRepoId(), pathValue);
        } catch (Exception e) {
            logger.error("/action/backup/add", e);
            bindingResult.rejectValue("path", "backupCreate.exception", e.getMessage());
            return gotoBackupCreate(backupVO.getRepoId(), model);
        }

        return "redirect:/pages/repoView/" + backupVO.getRepoId() + "?selected=" + backupId;
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/action/backup/sync")
    public String backupSyncAction(@RequestParam("backupId") long backupId) throws ViewException {
        Long taskId = taskRunnerService.doBackupSync(backupId);

        return "redirect:/pages/taskList?selected="+taskId;
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/action/backup/check")
    public String backupCheckAction(@RequestParam("backupId") long backupId) throws ViewException {
        Long taskId = taskRunnerService.doBackupCheck(backupId);

        return "redirect:/pages/taskList?selected="+taskId;
    }
}