package com.aemstore.core.models;

import com.aemstore.core.models.User;

public interface UserService {
    boolean userExists(String email);
    boolean createUser(User user);

    User doLogin(String email , String password);
}
