package com.example.task_mng_opakovanie;

import com.example.task_mng_opakovanie.api.exception.BadRequestException;
import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.TaskAddRequest;
import com.example.task_mng_opakovanie.api.request.TaskAssignStatusRequest;
import com.example.task_mng_opakovanie.api.request.TaskEditRequest;
import com.example.task_mng_opakovanie.api.request.TaskStatusEditRequest;
import com.example.task_mng_opakovanie.domain.Task;
import com.example.task_mng_opakovanie.domain.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

public class TaskIntegrationTests extends IntegrationTest{

    @Test
    public void assignWrongUser(){
        final TaskAddRequest request = new TaskAddRequest(1L, null, "name", "des");
        final long id = createTask(request);

        final TaskAssignStatusRequest assignStatusRequest = new TaskAssignStatusRequest(2L);

        final ResponseEntity<BadRequestException> wrongAssignResponse = restTemplate.exchange(
                "/task/" + id + "/assign",
                HttpMethod.PUT,
                new HttpEntity<>(assignStatusRequest),
                BadRequestException.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, wrongAssignResponse.getStatusCode());



    }


    @Test
    public void assignProject(){
        final TaskAddRequest request = new TaskAddRequest(1L, null, "test", "desTest");
        final long id = createTask(request);

        final TaskAssignStatusRequest assignStatusRequest = new TaskAssignStatusRequest(1L);

        final ResponseEntity<Void> assignResponse = restTemplate.exchange(
                "/task/" + id + "/assign",
                HttpMethod.PUT,
                new HttpEntity<>(assignStatusRequest),
                Void.class
        );

        Assertions.assertEquals(HttpStatus.OK, assignResponse.getStatusCode());

        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );

        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(getResponse.getBody());

        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(assignStatusRequest.getProject_id(), getResponse.getBody().getProject_id());

    }



    @Test
    public void changeStatus(){
        final TaskAddRequest request = generateRequest();
        final long id = createTask(request);

        final TaskStatusEditRequest statusRequest = new TaskStatusEditRequest(TaskStatus.DONE);

        final ResponseEntity<Void> changeStatus = restTemplate.exchange(
                "/task/" + id + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest),
                Void.class
        );

        Assertions.assertEquals(changeStatus.getStatusCode(), HttpStatus.OK);

        //get and compare
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());

        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(statusRequest.getStatus(), getResponse.getBody().getStatus());



    }


    @Test
    public void update(){
        final TaskAddRequest request = generateRequest();
        final long id = createTask(request);

        final TaskEditRequest editRequest = new TaskEditRequest("editedName", "editDes", TaskStatus.DONE);

        final ResponseEntity<Void> editResponse = restTemplate.exchange(
                "/task/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, editResponse.getStatusCode());

        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());

        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(editRequest.getName(), getResponse.getBody().getName());
        Assertions.assertEquals(editRequest.getDescription(), getResponse.getBody().getDescription());
        Assertions.assertEquals(editRequest.getStatus(), getResponse.getBody().getStatus());

    }


    @Test
    public void delete(){
        final TaskAddRequest request = generateRequest();
        final long id = createTask(request);


        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/task/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        final ResponseEntity<ResourceNotFound> testResponse = restTemplate.getForEntity(
                "/task/" + id,
                ResourceNotFound.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, testResponse.getStatusCode());


    }



    @Test
    public void get(){
        final TaskAddRequest request = generateRequest();
        final long id = createTask(request);

        final ResponseEntity<Task> getRequest = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );

        Assertions.assertEquals(HttpStatus.OK, getRequest.getStatusCode());
        Assertions.assertNotNull(getRequest.getBody());
        Assertions.assertEquals(id, getRequest.getBody().getId());
        Assertions.assertEquals(request.getUser_id(), getRequest.getBody().getUser_id());
        Assertions.assertEquals(request.getProject_id(), getRequest.getBody().getProject_id());
        Assertions.assertEquals(request.getName(), getRequest.getBody().getName());
        Assertions.assertEquals(request.getDescription(), getRequest.getBody().getDescription());
        Assertions.assertEquals(TaskStatus.NEW, getRequest.getBody().getStatus());

    }


    @Test
    public void getAll(){
        final ResponseEntity<List<Task>> response = restTemplate.exchange(
                "/task",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>(){}
        );

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().size() >= 2);
    }

    @Test
    public void getAllByUser(){
        final ResponseEntity<List<Task>> response = restTemplate.exchange(
                "/task?user_id=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        Assertions.assertEquals(HttpStatus.OK,  response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void getAllByProject(){
        final ResponseEntity<List<Task>> response = restTemplate.exchange(
                "/task?project_id=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void insert(){
        createTask(generateRequest());
    }

    @Test
    public void insertWithoutDescription(){
        final TaskAddRequest request = new TaskAddRequest(1L, 1L, "test", null);

        final ResponseEntity<Long> response = restTemplate.postForEntity(
                "/task",
                request,
                Long.class
        );

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        final Long id = response.getBody();
        Assertions.assertNotNull(id);

        //get and compare

        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );

        Assertions.assertEquals(getResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(getResponse.getBody());

        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName());
        Assertions.assertEquals(request.getUser_id(), getResponse.getBody().getUser_id());
        Assertions.assertEquals(request.getProject_id(), getResponse.getBody().getProject_id());
        Assertions.assertNull(getResponse.getBody().getDescription());

    }

    @Test
    public void insertWithoutProjectId(){
        final TaskAddRequest request = new TaskAddRequest(1L, null, "test", "testDes");
        final ResponseEntity<Long> createdResponse = restTemplate.postForEntity(
                "/task",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createdResponse.getStatusCode());
        final Long id = createdResponse.getBody();
        Assertions.assertNotNull(id);

        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());

        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(request.getUser_id(), getResponse.getBody().getUser_id());
        Assertions.assertNull(getResponse.getBody().getProject_id());
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName());
        Assertions.assertEquals(request.getDescription(), getResponse.getBody().getDescription());

    }



    public TaskAddRequest generateRequest(){
        return new TaskAddRequest(
                1L,
                1L,
                "nameTest",
                "descriptionTest"
        );
    }

    public long createTask(TaskAddRequest request){
        final ResponseEntity<Long> response = restTemplate.postForEntity(
                "/task",
                request,
                Long.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        return response.getBody();

    }



}


