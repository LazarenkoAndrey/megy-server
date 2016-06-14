package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ActionController {
    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);

    @Autowired
    private RepoService repoService;
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

    @RequestMapping("/action/repo")
    public String repoAction(@Valid PathVO pathVO, BindingResult bindingResult, @RequestParam("method") String method, Model model) throws ViewException {
        if(method.equals("add")) {
            if(bindingResult.hasErrors()) {
                return "/pages/repoCreate";
            }

            Long repoId = null;
            try {
                Path pathValue = Paths.get(pathVO.getPath());
                repoId = repoService.createRepository(pathValue);
            } catch (Exception e) {
                logger.error("repoAction: " + method, e);
                bindingResult.rejectValue("path", "repoCreate.exception", e.getMessage());
                return "/pages/repoCreate";
            }

            return "redirect:/pages/repoList?selected="+repoId;
        } else {
            throw new ViewException("Method value is incorrect1");
        }
    }

}