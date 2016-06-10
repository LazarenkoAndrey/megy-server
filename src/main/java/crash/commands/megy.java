package crash.commands;

import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import ru.megy.exception.ServiceException;
import ru.megy.repository.entity.Backup;
import ru.megy.repository.entity.BackupVersion;
import ru.megy.repository.entity.Repo;
import ru.megy.repository.entity.SnapshotVersion;
import ru.megy.service.*;
import ru.megy.service.entity.TaskThread;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Usage("Command for megy fs-server")
public class megy extends BaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(megy.class);

    @Command
    @Usage("List of properties")
    public void propertyList() {
        Environment environment = getEnvironment();
        Map<String, Map<String, Object>> mapSource = new TreeMap<>();
        for(Iterator it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                String nameSource = propertySource.getName();
                if(!mapSource.containsKey(nameSource)) {
                    mapSource.put(nameSource, new TreeMap<>());
                }
                Map<String, Object> map = mapSource.get(nameSource);

                map.putAll(((MapPropertySource) propertySource).getSource());
            }
        }

        for(String nameSource : mapSource.keySet()) {
            out.println("*** " + nameSource + " ***");
            Map<String, Object> map = mapSource.get(nameSource);
            for (String key : map.keySet()) {
                out.println("\t" + key + ": " + map.get(key));
            }
        }
    }

    @Command
    @Usage("Stop of server")
    public void stopServer() {
        System.exit(0);
    }

    @Command
    @Usage("List of tasks")
    public void taskList() {
        TaskService taskService = getTaskService();
        List<TaskThread> activeTaskList = taskService.getActiveTaskList();
        List<TaskThread> completedTaskList = taskService.getCompletedTaskList();

        out.println("*** Active tasks ***");
        for(TaskThread task : activeTaskList) {
            out.println(task.toString());
        }
        out.println();
        out.println("*** Completed tasks ***");
        for(TaskThread task : completedTaskList) {
            out.println(task.toString());
        }
    }

    @Command
    @Usage("Interrupt task")
    public void taskInterrupt(  @Usage("ID of task")
                                @Argument
                                String taskId) {
        long id = Long.valueOf(taskId);
        TaskService taskService = getTaskService();
        List<TaskThread> activeTaskList = taskService.getActiveTaskList();

        TaskThread taskThread = null;
        for(TaskThread task : activeTaskList) {
            if(task.getId()==id) {
                taskThread = task;
                break;
            }
        }

        if(taskThread==null) {
            out.println("Task don't found among the active tasks");
        } else {
            taskThread.stop();
            out.println("The task will be interrupted");
        }
    }

    @Command
    @Usage("List of snapshot versions")
    public void snapshotVersionList(@Usage("ID of repository")
                                    @Argument
                                    String repoId,
                                    @Usage("Number of versions")
                                    @Option(names="n")
                                    String count) {
        List<SnapshotVersion> snapshotVersionList;
        if(repoId==null) {
            snapshotVersionList = getSnapshotService().getVersions(null, Integer.valueOf(count != null ? count : "10"));
        } else {
            snapshotVersionList = getSnapshotService().getVersions(Long.valueOf(repoId), Integer.valueOf(count != null ? count : "10"));
        }

        for(SnapshotVersion snapshotVersion : snapshotVersionList) {
            out.println(snapshotVersion.toString());
        }
    }

    @Command
    @Usage("List of backup versions")
    public void backupVersionList(  @Usage("ID of backup")
                                    @Argument
                                    String backupId,
                                    @Usage("Number of versions")
                                    @Option(names="n")
                                    String count) {
        List<BackupVersion> backupVersionList;
        if(backupId==null) {
            backupVersionList = getBackupService().getVersions(null, Integer.valueOf(count != null ? count : "10"));
        } else {
            backupVersionList = getBackupService().getVersions(Long.valueOf(backupId), Integer.valueOf(count != null ? count : "10"));
        }

        for(BackupVersion backupVersion : backupVersionList) {
            out.println(backupVersion.toString());
        }
    }

    @Command
    @Usage("List of repositories")
    public void repoList() {
        List<Repo> repoList = getRepoService().getRepos();

        for(Repo repo : repoList) {
            out.println(repo.toString());
        }
    }

    @Command
    @Usage("Create repositories")
    public void repoCreate(@Usage("path")
                           @Argument
                           @Required
                           String dirPath) throws ServiceException {
        Path path = Paths.get(dirPath);
        Long repoId = getRepoService().createRepository(path);
        out.printf("Created repositor, repoId: %d%n", repoId);
    }

    @Command
    @Usage("Create snapshot")
    public void snapshotCreate( @Usage("ID of repository")
                                @Argument
                                @Required
                                String repoId,
                                @Usage("Calculate hash (y/n), defualt \"y\"")
                                @Option(names="c")
                                String calcSha512) throws ServiceException {
        TaskThread taskThread = new TaskThread("snapshot.create(" + repoId + ")", getTaskService()) {
            @Override
            public void process() throws ServiceException {
                logger.info("Starting to create snapshot. repoId: {}", repoId);
                Long versionId = getSnapshotService().createSnapshot(Long.valueOf(repoId), !("n".equals(calcSha512)), this);
                logger.info("Snapshot was completed successfully. repoId: {}, versionId: {}", repoId, versionId);
            }
        };
        out.printf("Creating task for snapshot, taskId: %d%n", taskThread.getId());
        taskThread.start();
    }

    @Command
    @Usage("List of backups")
    public void backupList() {
        List<Backup> backupList = getBackupService().getBackups();

        for(Backup backup : backupList) {
            out.println(backup.toString());
        }
    }

    @Command
    @Usage("Create backup")
    public void backupCreate(@Usage("ID of repository")
                           @Argument
                           @Required
                           String repoId,
                           @Usage("path")
                           @Argument
                           @Required
                           String dirPath) throws ServiceException {
        Path path = Paths.get(dirPath);
        Long repoIdValue = Long.valueOf(repoId);
        Long backupId = getBackupService().createBackup(repoIdValue, path);
        out.printf("Created backup, backupId: %d%n", backupId);
    }

    @Command
    @Usage("Sync backup")
    public void backupSync( @Usage("ID of backup")
                            @Argument
                            @Required
                            String backupId) throws ServiceException {
        TaskThread taskThread = new TaskThread("backup.sync(" + backupId + ")", getTaskService()) {
            @Override
            public void process() throws ServiceException {
                logger.info("Starting to sync of backup. backupId: {}", backupId);
                Long versionId = getBackupService().sync(Long.valueOf(backupId), this);
                logger.info("Sync of backup was completed successfully. backupId: {}, versionId: {}", backupId, versionId);
            }
        };

        out.printf("Creating task for sync of backup, taskId: %d%n", taskThread.getId());
        taskThread.start();
    }

    private SnapshotService getSnapshotService() {
        BeanFactory factory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
        return factory.getBean(SnapshotService.class);
    }

    private RepoService getRepoService() {
        BeanFactory factory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
        return factory.getBean(RepoService.class);
    }

    private BackupService getBackupService() {
        BeanFactory factory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
        return factory.getBean(BackupService.class);
    }

    private Environment getEnvironment() {
        BeanFactory factory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
        return factory.getBean(Environment.class);
    }

    private TaskService getTaskService() {
        BeanFactory factory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
        return factory.getBean(TaskService.class);
    }



}
