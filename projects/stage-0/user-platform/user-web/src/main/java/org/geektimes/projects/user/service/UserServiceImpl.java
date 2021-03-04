package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

public class UserServiceImpl implements UserService {

    @Override
    public boolean register(User user) {
        return false;
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
