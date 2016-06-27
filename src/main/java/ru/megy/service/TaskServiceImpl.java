package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.megy.service.entity.TaskThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private AtomicLong idSequence;
    private List<TaskThread> activeTaskList;
    private List<TaskThread> completedTaskList;

    public TaskServiceImpl() {
        activeTaskList = Collections.synchronizedList(new ArrayList<>());
        completedTaskList = Collections.synchronizedList(new ArrayList<>());
        idSequence = new AtomicLong(0L);
    }

    @Override
    public List<TaskThread> getActiveTaskList() {
        return activeTaskList;
    }

    @Override
    public List<TaskThread> getCompletedTaskList() {
        return completedTaskList;
    }

    @Override
    public long nextTaskId() {
        return idSequence.addAndGet(1L);
    }

    @Override
    public void addActiveTask(TaskThread taskThread) {
        activeTaskList.add(taskThread);
    }

    @Override
    public void completedTask(TaskThread taskThread) {
        completedTaskList.add(taskThread);
        activeTaskList.remove(taskThread);

        if(completedTaskList.size()>20) {
            completedTaskList.subList(0, completedTaskList.size()-21).clear();
        }
    }
}
