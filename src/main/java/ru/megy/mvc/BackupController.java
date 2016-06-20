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
import ru.megy.service.BackupService;
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
    private RepoService repoService;
    @Autowired
    private TaskRunnerService taskRunnerService;

    @RequestMapping("/pages/backupList")
    public String backupList(@RequestParam(value = "selected", required=false) Long selected, Model model) {
        List<Backup> backupList = backupService.getBackupList();
        model.addAttribute("backupList", backupList);
        model.addAttribute("selectedBackupId", selected);

        return "/pages/backupList";
    }

    @RequestMapping("/pages/backupView/{backupId}")
    public String backupView(@PathVariable("backupId") long backupId, Model model) {
        Backup backup = backupService.getBackup(backupId);
        model.addAttribute("backup", backup);

        List<BackupVersion> backupVersionList = backupService.getVersionList(backupId, 100);
        model.addAttribute("backupVersionList", backupVersionList);

        return "/pages/backupView";
    }

    private void addRepoList(Model model) {
        model.addAttribute("repoList", repoService.getRepoList());
    }

    @RequestMapping("/pages/backupCreate")
    public String gotoBackupCreate(Model model) {
        if(!model.containsAttribute("backupVO")) {
            model.addAttribute("backupVO", new BackupVO());
        }

        addRepoList(model);

        return "/pages/backupCreate";
    }

    @Secured("ADMIN")
    @RequestMapping("/action/backup/add")
    public String repoAction(@Valid BackupVO backupVO, BindingResult bindingResult, Model model) throws ViewException {
        if(bindingResult.hasErrors()) {
            return gotoBackupCreate(model);
        }

        Long backupId = null;
        try {
            Path pathValue = Paths.get(backupVO.getPath());
            backupId = backupService.createBackup(backupVO.getRepoId(), pathValue);
        } catch (Exception e) {
            logger.error("/action/backup/add", e);
            bindingResult.rejectValue("path", "backupCreate.exception", e.getMessage());
            return gotoBackupCreate(model);
        }

        return "redirect:/pages/backupList?selected="+backupId;
    }

    @Secured("ADMIN")
    @RequestMapping("/action/backup/sync")
    public String backupAction(@RequestParam("backupId") long backupId) throws ViewException {
        Long taskId = taskRunnerService.doBackupSync(backupId);

        return "redirect:/pages/taskList?selected="+taskId;
    }
}