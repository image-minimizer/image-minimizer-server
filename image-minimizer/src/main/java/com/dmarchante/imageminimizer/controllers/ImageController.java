package com.dmarchante.imageminimizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class ImageController {
    private S3Client s3Client;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    TaskController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("/")
    public String getHome() {
        return "home";
    }

    @GetMapping("/tasks")
    public Iterable<Tasks> getTasks(Model m) {
        Iterable<Tasks> tasks = taskRepository.findAll();
        return tasks;
    }

    @GetMapping("/tasks/{id}")
    public Tasks getTaskById(@PathVariable UUID id) {
        Tasks task = taskRepository.findById(id).get();
        return task;
    }

    @PostMapping("/tasks")
    public Tasks postTasks(String assignee, String description, String status, String title) {
        Tasks task = new Tasks();
        task.setAssignee(assignee);
        task.setDescription(description);
        task.setStatus(status);
        task.setTitle(title);

        taskRepository.save(task);
        return task;
    }

    @PutMapping("/tasks/{id}/state")
    public Tasks updateStatus(@PathVariable UUID id) {
        Tasks task = taskRepository.findById(id).get();

        if (task.getStatus().equals("Available")) {
            task.setStatus("Assigned");
        } else if (task.getStatus().equals("Assigned")) {
            task.setStatus("Accepted");
        } else {
            task.setStatus("Finished");
        }

        taskRepository.save(task);
        return task;
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<UUID> deleteTask(@PathVariable UUID id) {
        taskRepository.deleteById(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/users/{name}/tasks")
    public List<Tasks> getAssigneeTasks(@PathVariable String name) {
        return taskRepository.findByAssignee(name);
    }

    @PutMapping("/tasks/{id}/assign/{assignee}")
    public Tasks setAssignee(@PathVariable UUID id, @PathVariable String assignee) {
        Tasks task = taskRepository.findById(id).get();

        task.setAssignee(assignee);

        taskRepository.save(task);
        return task;
    }

    @PostMapping("/tasks/{id}/images")
    public Tasks uploadFile(
            @PathVariable UUID id,
            @RequestPart(value = "file") MultipartFile file
    ){
        String pic = this.s3Client.uploadFile(file);
        Tasks task = taskRepository.findById(id).get();
        task.setPic(pic);
        task.setResizedPic("http://cdmarch-taskmaster-fe-uploads-resize.s3-us-west-2.amazonaws.com/resized-" + pic.substring(64));
        taskRepository.save(task);
        return task;
    }
}
