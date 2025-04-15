package com.example.task_mng_opakovanie.api;

import com.example.task_mng_opakovanie.api.request.UserAddRequest;
import com.example.task_mng_opakovanie.domain.User;
import java.util.List;

public interface UserService {
    long add(UserAddRequest request);
    void delete(long id);
    User get(long id);
    List<User> getAll();

}
