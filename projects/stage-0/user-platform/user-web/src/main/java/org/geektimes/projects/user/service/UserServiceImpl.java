package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author zhimingxiao
 * @since
 */
public class UserServiceImpl implements UserService{


    private UserRepository userRepository = new DatabaseUserRepository(new DBConnectionManager());

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
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
