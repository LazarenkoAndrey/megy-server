package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

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