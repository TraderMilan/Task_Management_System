package com.example.task_mng_opakovanie.implementation.jdbc.service;

import com.example.task_mng_opakovanie.api.ProjectService;
import com.example.task_mng_opakovanie.api.TaskService;
import com.example.task_mng_opakovanie.api.UserService;
import com.example.task_mng_opakovanie.api.exception.BadRequestException;
import com.example.task_mng_opakovanie.api.request.TaskAddRequest;
import com.example.task_mng_opakovanie.api.request.TaskEditRequest;
import com.example.task_mng_opakovanie.domain.Task;
import com.example.task_mng_opakovanie.domain.TaskStatus;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.TaskJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceJdbcImpl implements TaskService {
    private final TaskJdbcRepository repository;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskServiceJdbcImpl(TaskJdbcRepository repository, UserService userService, ProjectService projectService) {
        this.repository = repository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @Override
    public long add(TaskAddRequest request) {
        return repository.add(request);
    }

    @Override
    public Task get(long id) {
        return repository.getById(id);
    }

    @Override
    public List<Task> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Task> getByUserId(long userId) {
        if (userService.get(userId) != null) {
            return repository.getAllByUser(userId);
        }
        return null;

    }

    @Override
    public List<Task> getByProjectId(long projectId) {
        if (projectService.get(projectId) != null) {
            return repository.getAllByProject(projectId);
        }

        return null;
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null){
            repository.delete(id);
        }

    }

    @Override
    public void edit(long id, TaskEditRequest request) {
        if(this.get(id) != null){
            repository.edit(id, request);
        }
    }

    @Override
    public void changeStatus(long id, TaskStatus status) {
        if(this.get(id) != null){
            repository.updateStatus(id, status);
        }
    }

    @Override
    public void assign(long id, long projectId) {
        if (this.get(id) != null && projectService.get(projectId) != null) {
            if(this.get(id).getUser_id() != projectService.get(projectId).getUser_id()){
                throw new BadRequestException("Task and project must belong to the same user");
            }
            repository.assignProject(id, projectId);
        }
    }
}
