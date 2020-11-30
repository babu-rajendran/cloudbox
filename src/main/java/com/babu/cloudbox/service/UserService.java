package com.babu.cloudbox.service;

import com.babu.cloudbox.model.User;

public interface UserService {

    public User findUserByEmail(String email);

    public void saveUser(User user);
}
