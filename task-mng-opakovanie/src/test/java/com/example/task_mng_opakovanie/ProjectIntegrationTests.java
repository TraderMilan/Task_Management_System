package com.example.task_mng_opakovanie;

import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.ProjectAddRequest;
import com.example.task_mng_opakovanie.api.request.ProjectEditRequest;
import com.example.task_mng_opakovanie.domain.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;

public class ProjectIntegrationTests extends IntegrationTest {


    @Test
    public void update(){
        final ProjectAddRequest addRequest = generateRequest();
        final long id = createProject(addRequest);


        final ProjectEditRequest editRequest = generateEditRequest();
        final ResponseEntity<Void> editResponse = restTemplate.exchange(
                "/project/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, editResponse.getStatusCode());

        //Overenie
        final ResponseEntity<Project> getResponse = restTemplate.getForEntity(
                "/project/" + id,
                Project.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(getResponse.getBody().getDescription(), editRequest.getDescription());
        Assertions.assertEquals(getResponse.getBody().getName(), editRequest.getName());

    }


    @Test
    public void delete(){
        final ProjectAddRequest request = generateRequest();
        final long id = createProject(request);

        final ResponseEntity<Void> delete = restTemplate.exchange(
                "/project/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, delete.getStatusCode());

        // Test ci nam hodi error po getovani vymazaneho
        final ResponseEntity<ResourceNotFound> response = restTemplate.getForEntity(
                "/project/" + id,
                ResourceNotFound.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());



    }

    @Test
    public void getAll() {
        final ResponseEntity<List<Project>> response = restTemplate.exchange(
                "/project",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().size() >= 2);

    }

    @Test
    public void getByUser() {
        final ResponseEntity<List<Project>> response = restTemplate.exchange(
                "/project?userId=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void getById() {
        final ProjectAddRequest request = generateRequest();
        final long id = createProject(request);

        final ResponseEntity<Project> response = restTemplate.getForEntity(
                "/project/" + id,
                Project.class
        );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(id, response.getBody().getId());
        Assertions.assertEquals(request.getUser_id(), response.getBody().getUser_id());
        Assertions.assertEquals(request.getName(), response.getBody().getName());
        Assertions.assertEquals(request.getDescription(), response.getBody().getDescription());

    }

    @Test
    public void add() {
        createProject(generateRequest());
    }

    @Test
    public void addWithoutDescription() {
        final ProjectAddRequest request = new ProjectAddRequest(1L, "name", null);
        ResponseEntity<Long> project = restTemplate.postForEntity(
                "/project",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, project.getStatusCode());
        final Long id = project.getBody();
        Assertions.assertNotNull(id);

        final ResponseEntity<Project> response = restTemplate.getForEntity(
                "/project/" + id,
                Project.class
        );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(id, response.getBody().getId());
        Assertions.assertEquals(request.getUser_id(), response.getBody().getUser_id());
        Assertions.assertEquals(request.getName(), response.getBody().getName());
        Assertions.assertNull(response.getBody().getDescription());


    }

    public ProjectEditRequest generateEditRequest(){
        return new ProjectEditRequest("editedName", "editedDescription");
    }

    public ProjectAddRequest generateRequest() {
        return new ProjectAddRequest(
                1L,
                "name" + Math.random(),
                "description" + Math.random()
        );
    }

    public long createProject(ProjectAddRequest request) {
        ResponseEntity<Long> project = restTemplate.postForEntity(
                "/project",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, project.getStatusCode());
        Assertions.assertNotNull(project.getBody());

        return project.getBody();

    }

}
