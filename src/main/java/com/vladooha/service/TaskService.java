package com.vladooha.service;

import com.vladooha.data.entities.Task;
import com.vladooha.data.entities.TaskStatus;
import com.vladooha.data.repo.TaskRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private static final Logger logger = LogManager.getLogger(TaskService.class);

    private static final long JOB_TIME = 1000 * 60 * 2;

    @Autowired
    private TaskRepo taskRepo;

    @NotNull
    public Task createTask() {
        logger.info("Task creating...");

        Task task = new Task();
        task.setStatus(TaskStatus.created);
        task.setTimestamp(getCurrTime());

        taskRepo.save(task);

        logger.info("Task[" + task.getId() + "] created");

        return task;
    }

    @Nullable
    public Task getTask(UUID id) {
        logger.info("Attempt to find task[" + id + "]");

        Optional<Task> optionalTask = taskRepo.findById(id);

        if (optionalTask.isPresent()) {
            logger.info("Task[" + id + "] found");

            return optionalTask.get();
        } else {
            logger.info("Task[" + id + "] not found");

            return null;
        }
    }

    @Async
    public void doTask(Task task) {
        logger.info("Task[" + task.getId() + "] running...");

        task.setStatus(TaskStatus.running);
        task.setTimestamp(getCurrTime());

        taskRepo.save(task);

        try {
            Thread.sleep(JOB_TIME);
        } catch (InterruptedException e) {
            logger.error("Exception occurred during task[" + task.getId() + "] run", e);
        }

        task.setStatus(TaskStatus.finished);
        task.setTimestamp(getCurrTime());

        taskRepo.save(task);

        logger.info("Task[" + task.getId() + "] job finished");
    }

    @NotNull
    private String getCurrTime() {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        return fmt.print(dt);
    }
}
