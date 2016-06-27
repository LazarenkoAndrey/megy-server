package ru.megy.service;

import ru.megy.service.entity.TaskThread;

import java.util.List;

public interface TaskService {
    List<TaskThread> getActiveTaskList();
    List<TaskThread> getCompletedTaskList();
    long nextTaskId();
    void addActiveTask(TaskThread taskThread);
    void completedTask(TaskThread taskThread);
}
