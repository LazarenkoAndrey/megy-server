package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class GeneralController {
    private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);

    @RequestMapping("/profile")
    public String backupList(Model model, @AuthenticationPrincipal User user) {
       StringBuffer sbRoles = new StringBuffer();
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        for(GrantedAuthority grantedAuthority : authorities) {
            if(sbRoles.length()>0) {
                sbRoles.append(", ");
            }
            sbRoles.append(grantedAuthority.getAuthority());
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("roles", sbRoles.toString());

        return "/profile";
    }
}