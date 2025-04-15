package com.example.task_mng_opakovanie.implementation.jdbc.service;

import com.example.task_mng_opakovanie.api.UserService;
import com.example.task_mng_opakovanie.api.request.UserAddRequest;
import com.example.task_mng_opakovanie.domain.User;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.ProjectJdbcRepository;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.TaskJdbcRepository;
import com.example.task_mng_opakovanie.implementation.jdbc.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceJdbcImpl implements UserService {
    private final UserJdbcRepository repository;
    private final ProjectJdbcRepository projectRepository;
    private final TaskJdbcRepository taskRepository;


    public UserServiceJdbcImpl(UserJdbcRepository repository, ProjectJdbcRepository projectRepository, TaskJdbcRepository taskRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public long add(UserAddRequest request) {
        return repository.addUser(request);
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null){
            taskRepository.deleteByUser(id);
            projectRepository.DeleteByUser(id);
            repository.deleteUser(id);
        }
    }

    @Override
    public User get(long id) {
        return repository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();
    }
}
