package com.example.task_mng_opakovanie.api;

import com.example.task_mng_opakovanie.api.request.TaskAddRequest;
import com.example.task_mng_opakovanie.api.request.TaskEditRequest;
import com.example.task_mng_opakovanie.domain.Task;
import com.example.task_mng_opakovanie.domain.TaskStatus;

import java.util.List;


public interface TaskService {
    long add(TaskAddRequest request);
    Task get(long id);
    List<Task> getAll();
    List<Task> getByUserId(long userId);
    List<Task> getByProjectId(long projectId);
    void delete(long id);
    void edit(long id, TaskEditRequest request);
    void changeStatus(long id, TaskStatus status);
    void assign(long id, long projectId);
}
