package ru.megy.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.megy.exception.ViewException;
import ru.megy.service.TaskService;
import ru.megy.service.entity.TaskThread;
import ru.megy.service.type.TaskStatusEnum;

import java.util.List;

@Controller
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @RequestMapping("/pages/taskList")
    public String taskList(@RequestParam(value = "selected", required=false) Long selectedTaskId, Model model) {
        List<TaskThread> activeTaskList = taskService.getActiveTaskList();
        List<TaskThread> completedTaskList = taskService.getCompletedTaskList();
        model.addAttribute("activeTaskList", activeTaskList);
        model.addAttribute("completedTaskList", completedTaskList);
        model.addAttribute("selectedTaskId", selectedTaskId);

        return "/pages/taskList";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/action/task/interrupt")
    public String interruptAction(@RequestParam("taskId") long taskId) throws ViewException {
        TaskThread taskThread = taskService.findActiveTask(taskId);
        if(taskThread!=null && taskThread.getStatus().equals(TaskStatusEnum.PROCESSING)) {
            taskThread.stop();
        }

        return "redirect:/pages/taskList?selected="+taskId;
    }

}