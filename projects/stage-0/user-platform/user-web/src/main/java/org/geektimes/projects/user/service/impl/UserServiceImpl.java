package org.geektimes.projects.user.service.impl;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.InMemoryUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.service.UserService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

public class UserServiceImpl implements UserService {

    @Override
    public boolean register(User user) {
//        UserRepository userRepository = new InMemoryUserRepository();
//        userRepository.save(user);
//        return true;
        try {
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");

            Context context = new InitialContext();
//            Context envContext  = (Context)context.lookup("java:/comp/env");
            DataSource ds = (DataSource)context.lookup("java:/comp/env/jdbc/UserPlatformDB");
            Connection connection = ds.getConnection();

            System.out.println("dataSource " + connection.getMetaData());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        UserRepository userRepository = new InMemoryUserRepository();
        return userRepository.getById(id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
