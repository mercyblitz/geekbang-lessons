package org.geektimes.projects.user.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.InMemoryUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.service.UserService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public boolean register(User user) {
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);

        Iterator<ConstraintViolation<User>> it = violationSet.iterator();
        if(it.hasNext()) {
            throw new RuntimeException(it.next().getMessage());
        }

        entityManager.persist(user);
        return true;
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
