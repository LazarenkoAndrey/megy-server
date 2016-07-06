package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.service.BackupVersionService;
import ru.megy.service.entity.BackupVersionStatistic;

import java.util.List;

@Controller
public class VersionController {
    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private BackupVersionService backupVersionService;

    @RequestMapping("/pages/versionView/{versionId}")
    public String backupView(@PathVariable("versionId") long versionId, Model model) {
        BackupVersion backupVersion = backupVersionService.getVersion(versionId);
        List<BackupVersionStatistic> statisticList = backupVersionService.getStatistics(backupVersion);

        model.addAttribute("version", backupVersion);
        model.addAttribute("statisticList", statisticList);

        return "pages/versionView";
    }
}