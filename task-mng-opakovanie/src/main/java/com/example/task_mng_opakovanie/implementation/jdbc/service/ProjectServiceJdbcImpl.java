package com.example.task_mng_opakovanie.implementation.jdbc.service;

import com.example.task_mng_opakovanie.api.ProjectService;
import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.ProjectAddRequest;
import com.example.task_mng_opakovanie.api.request.ProjectEditRequest;
import com.example.task_mng_opakovanie.domain.Project;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.ProjectJdbcRepository;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.TaskJdbcRepository;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.UserJdbcRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceJdbcImpl implements ProjectService {
    private final ProjectJdbcRepository repository;
    private final UserJdbcRepository userRepository;
    private final TaskJdbcRepository taskRepository;

    public ProjectServiceJdbcImpl(ProjectJdbcRepository repository, UserJdbcRepository userRepository, TaskJdbcRepository taskRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public long add(ProjectAddRequest request) {
        return repository.createProject(request);
    }

    @Override
    public void delete(long id) {
        if(this.get(id) != null){
            taskRepository.deleteByProject(id);
            repository.delete(id);
        }
    }

    @Override
    public Project get(long id) {
        return repository.getById(id);
    }

    @Override
    public List<Project> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Project> getAllByUser(long user_id) {
        if (userRepository.getById(user_id) != null) {
            return repository.getAllByUserId(user_id);
        }
        return null;
    }

    @Override
    public void edit(ProjectEditRequest request, long id) {
        if(this.get(id) != null){
            repository.update(id, request);
        }
    }
}
