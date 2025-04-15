package com.example.task_mng_opakovanie.controller;


import com.example.task_mng_opakovanie.api.TaskService;
import com.example.task_mng_opakovanie.api.request.TaskAddRequest;
import com.example.task_mng_opakovanie.api.request.TaskAssignStatusRequest;
import com.example.task_mng_opakovanie.api.request.TaskEditRequest;
import com.example.task_mng_opakovanie.api.request.TaskStatusEditRequest;
import com.example.task_mng_opakovanie.domain.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long projectId){

        if(userId != null){
            return ResponseEntity.ok().body(taskService.getByUserId(userId));
        } else if (projectId != null) {
            return ResponseEntity.ok().body(taskService.getByProjectId(projectId));
        } else {
            return ResponseEntity.ok().body(taskService.getAll());
        }

    }

    @PostMapping
    public ResponseEntity<Long> addTask(@RequestBody TaskAddRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.add(request));
    }


    @PutMapping("{id}")
    public ResponseEntity<Void> edit(@RequestBody TaskEditRequest request, @PathVariable("id") long id){
        taskService.edit(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") long id, @RequestBody TaskStatusEditRequest request){
        taskService.changeStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/assign")
    public ResponseEntity<Void> assign(@PathVariable("id") long id, @RequestBody TaskAssignStatusRequest request){
        taskService.assign(id, request.getProject_id());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") long id){
        return ResponseEntity.ok().body(taskService.get(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id){
        taskService.delete(id);
        return ResponseEntity.ok().build();
    }




}
