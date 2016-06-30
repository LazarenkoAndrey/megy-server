package ru.megy.service.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.megy.exception.ServiceException;
import ru.megy.service.TaskService;
import ru.megy.service.type.TaskStatusEnum;

import java.util.Date;
import java.util.SortedMap;
import java.util.SortedSet;

public abstract class TaskThreadWithResult extends TaskThread{
    private static final Logger logger = LoggerFactory.getLogger(TaskThreadWithResult.class);
    private SortedMap<String, SortedSet<String>> result;

    public TaskThreadWithResult(String name, TaskService taskService) {
        super(name, taskService);
    }

    public SortedMap<String, SortedSet<String>> getResult() {
        return result;
    }

    public void setResult(SortedMap<String, SortedSet<String>> result) {
        this.result = result;
    }
}
