package com.example.task_mng_opakovanie.controller;

import com.example.task_mng_opakovanie.api.ProjectService;
import com.example.task_mng_opakovanie.api.request.ProjectAddRequest;
import com.example.task_mng_opakovanie.api.request.ProjectEditRequest;
import com.example.task_mng_opakovanie.domain.Project;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAll(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok().body(projectService.getAllByUser(userId));
        }
        return ResponseEntity.ok().body(projectService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Project> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(projectService.get(id));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") long id) {
        projectService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Long> addProject(@RequestBody ProjectAddRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> edit(@PathVariable("id") long id, @RequestBody ProjectEditRequest request){
        projectService.edit(request, id);
        return ResponseEntity.ok().build();
    }


}
