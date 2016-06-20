package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
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
import ru.megy.repository.entity.Message;
import ru.megy.service.BackupService;
import ru.megy.service.MessageService;
import ru.megy.service.RepoService;
import ru.megy.service.TaskRunnerService;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @RequestMapping("/pages/messageList")
    public String backupList(@RequestParam(value = "selected", required=false) Long selected, Model model) {
        List<Message> messageList = messageService.getMessageList(100);
        model.addAttribute("messageList", messageList);
        model.addAttribute("selectedMessageId", selected);

        return "/pages/messageList";
    }

    @Secured("ADMIN")
    @RequestMapping("/action/message/sendTest")
    public String repoAction(Model model) throws ViewException {
        Long messageId = null;

        try {
            messageId = messageService.addMessage("Testing message.\nTime:" + System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("/action/message/sendTest", e);
            throw new ViewException(e.getMessage());
        }

        return "redirect:/pages/messageList?selected="+messageId;
    }
}