package com.example.task_mng_opakovanie.api;

import com.example.task_mng_opakovanie.api.request.ProjectAddRequest;
import com.example.task_mng_opakovanie.api.request.ProjectEditRequest;
import com.example.task_mng_opakovanie.domain.Project;
import java.util.List;


public interface ProjectService {
    long add(ProjectAddRequest request);
    void delete(long id);
    Project get(long id);
    List<Project> getAll();
    List<Project> getAllByUser(long user_id);
    void edit(ProjectEditRequest request, long id);
}
