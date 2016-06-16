package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.megy.exception.ViewException;
import ru.megy.mvc.objects.BackupVO;
import ru.megy.mvc.objects.RepoVO;
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
public class RepoController {
    private static final Logger logger = LoggerFactory.getLogger(RepoController.class);

    @Autowired
    private RepoService repoService;

    @RequestMapping("/pages/repoList")
    public String repoList(@RequestParam(value = "selected", required=false) Long selected, Model model) {
        List<Repo> repoList = repoService.getRepos();
        model.addAttribute("repoList", repoList);
        model.addAttribute("selectedRepoId", selected);

        return "/pages/repoList";
    }

    @RequestMapping("/pages/repoCreate")
    public String gotoRepoCreate(Model model) {
        if(!model.containsAttribute("repoVO")) {
            model.addAttribute("repoVO", new RepoVO());
        }

        return "/pages/repoCreate";
    }

    @RequestMapping("/action/repo/add")
    public String repoAction(@Valid RepoVO repoVO, BindingResult bindingResult, Model model) throws ViewException {
        if(bindingResult.hasErrors()) {
            return gotoRepoCreate(model);
        }

        Long repoId = null;
        try {
            Path pathValue = Paths.get(repoVO.getPath());
            repoId = repoService.createRepository(pathValue);
        } catch (Exception e) {
            logger.error("/action/repo/add", e);
            bindingResult.rejectValue("path", "repoCreate.exception", e.getMessage());
            return gotoRepoCreate(model);
        }

        return "redirect:/pages/repoList?selected="+repoId;
    }
}