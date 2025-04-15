package com.example.task_mng_opakovanie.controller;

import com.example.task_mng_opakovanie.api.ProjectService;
import com.example.task_mng_opakovanie.api.TaskService;
import com.example.task_mng_opakovanie.api.UserService;
import com.example.task_mng_opakovanie.api.request.UserAddRequest;
import com.example.task_mng_opakovanie.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok().body(userService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getById(@PathVariable("id") long id){
        return ResponseEntity.ok().body(userService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> addUser(@RequestBody UserAddRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.add(request));
    }

    @DeleteMapping("{id}") //TODO Remove everything with this user
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id){
        userService.delete(id);


        return ResponseEntity.ok().build();
    }



}
