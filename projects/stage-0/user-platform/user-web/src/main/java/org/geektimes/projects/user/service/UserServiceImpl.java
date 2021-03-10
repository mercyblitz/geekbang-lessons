package org.geektimes.projects.user.service;

import javax.annotation.Resource;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.JpaUserRepository;
import org.geektimes.projects.user.sql.LocalTransactional;

public class UserServiceImpl implements UserService {


    @Resource(name = "bean/JpaUserRepository")
    private JpaUserRepository userRepository;



    @Override
    public boolean register(User user) {
        // 先查找在入库
        User userDb = null ;
//        userRepository.getByNameAndPassword(user.getPhoneNumber(), user.getPassword());
        if (userDb == null) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    @LocalTransactional
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
