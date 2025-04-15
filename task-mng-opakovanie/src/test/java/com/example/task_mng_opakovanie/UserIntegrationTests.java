package com.example.task_mng_opakovanie;

import com.example.task_mng_opakovanie.api.UserService;
import com.example.task_mng_opakovanie.api.exception.BadRequestException;
import com.example.task_mng_opakovanie.api.exception.ResourceNotFound;
import com.example.task_mng_opakovanie.api.request.UserAddRequest;
import com.example.task_mng_opakovanie.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UserIntegrationTests extends IntegrationTest{

    @Test
    public void getAll(){
        final ResponseEntity<List<User>> userResponse =  restTemplate.exchange(
                "/user",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        Assertions.assertNotNull(userResponse.getBody());
        Assertions.assertTrue(userResponse.getBody().size() >= 2);
    }


    @Test
    public void addUser(){
        createUser(generateRequest());
    }


    @Test
    public void getUser(){
        final UserAddRequest request = generateRequest();
        final long id = createUser(request);

        final ResponseEntity<User> response = restTemplate.getForEntity(
                "/user/" + id,
                User.class
        );
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        final User user = response.getBody();
        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(request.getName(), user.getName());
        Assertions.assertEquals(request.getEmail(), user.getEmail());
    }

    @Test
    public void existingEmail(){
        final UserAddRequest request = generateRequest();
        final long id = createUser(request);

        final ResponseEntity<BadRequestException> userResponse = restTemplate.postForEntity(
                "/user",
                request,
                BadRequestException.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, userResponse.getStatusCode());

    }


    @Test
    public void deleteUser(){
        final UserAddRequest request = generateRequest();
        final long id = createUser(request);

        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/user/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        //Otestovanie vymazania
        final ResponseEntity<ResourceNotFound> finalResponse = restTemplate.getForEntity(
                "/user/" + id,
                ResourceNotFound.class
        );
        Assertions.assertEquals(finalResponse.getStatusCode(), HttpStatus.NOT_FOUND);
    }


    //Vytvorenie request
    public UserAddRequest generateRequest(){
        return new UserAddRequest(
                "name"+ Math.random(),
                "email"+ Math.random()
        );
    }

    //Pomocná metóda na vytvorenie usera
    public long createUser(UserAddRequest request){
        final ResponseEntity<Long> response = restTemplate.postForEntity(
                "/user",
                    request,
                    Long.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        return response.getBody();
    }

}
