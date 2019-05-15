package com.vladooha.controller;

import com.vladooha.data.entities.Task;
import com.vladooha.service.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class TaskController {
    private static final Logger logger = LogManager.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    public String getMainPage() {
        logger.info("GET '/'");

        return "index";
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<String> getTaskStatus(@PathVariable("id") String id) {
        logger.info("GET '/task/'" + id);

        try {
            UUID uuid = UUID.fromString(id);

            Task task = taskService.getTask(uuid);
            if (task != null) {
                return new ResponseEntity<>(task.getStatus().toString(), HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            logger.error("Wrong GUID '" + id + "'", e);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String createTask() {
        logger.info("POST '/task'");

        // I didn't really get when i should change status 'created' to status 'running'
        // Just creating here new task with status 'created'
        Task task = taskService.createTask();
        // And running async method that right now gonna change status to 'running' and after to 'finished'
        taskService.doTask(task);

        String uuidStr = task.getId().toString();

        return uuidStr;
    }
}
