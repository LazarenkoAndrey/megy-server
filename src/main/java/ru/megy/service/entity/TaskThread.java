package ru.megy.service.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.megy.exception.ServiceException;
import ru.megy.service.TaskService;
import ru.megy.service.type.TaskStatusEnum;
import java.util.*;

public abstract class TaskThread {
    private static final Logger logger = LoggerFactory.getLogger(TaskThread.class);
    private long id;
    private String name = "none";
    private TaskStatusEnum status;
    private long startTime;
    private long finishTime;
    private float percent;
    private String message;
    private TaskService taskService;
    private Thread thread;

    public TaskThread(String name, TaskService taskService) {
        this.name = name;
        this.taskService = taskService;
        this.id = this.taskService.nextTaskId();
        this.status = TaskStatusEnum.CREATED;
        this.percent = 0.0f;
    }

    public abstract void process() throws ServiceException;

    public void start() {
        thread = new Thread() {
            public void run() {
                startTime = System.currentTimeMillis();
                status = TaskStatusEnum.PROCESSING;
                try {
                    process();
                    status = TaskStatusEnum.COMPLETE;
                } catch (Throwable e) {
                    status = TaskStatusEnum.FAILED;
                    message = e.getMessage();
                    logger.error("process", e);
                } finally {
                    finishTime = System.currentTimeMillis();
                    taskService.completedTask(TaskThread.this);
                }
            }
        };
        taskService.addActiveTask(this);
        thread.start();
    }

    public void stop() {
        if(status!=null && status.equals(TaskStatusEnum.PROCESSING)) {
            status = TaskStatusEnum.STOPPING;
            if(thread!=null) {
                thread.interrupt();
            }
        }
    }

    public boolean isStopping() {
        return TaskStatusEnum.STOPPING.equals(status);
    }

    public long getId() {
        return id;
    }

    public TaskStatusEnum getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public Date getStartTime() {
        return new Date(startTime);
    }

    public Date getFinishTime() {
        if(finishTime>0) {
            return new Date(finishTime);
        } else {
            return null;
        }
    }

    public long getSpentTime() {
        long currentTime = System.currentTimeMillis();
        return (currentTime-startTime);
    }

    public long getLeftTime() {
        float percentProcessing = percent;
        if(status.equals(TaskStatusEnum.COMPLETE) || status.equals(TaskStatusEnum.FAILED)) {
            return 0L;
        } else if(percentProcessing > 0.0f) {
            long spendTime = getSpentTime();
            return Math.round(spendTime * (100.0f - percentProcessing) / percentProcessing);
        } else {
            return -1L;
        }
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TaskThread{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", percent=" + percent +
                ", message='" + message + '\'' +
                '}';
    }
}
