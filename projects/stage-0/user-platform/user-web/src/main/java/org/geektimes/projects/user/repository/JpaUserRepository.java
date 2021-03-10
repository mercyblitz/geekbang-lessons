package org.geektimes.projects.user.repository;

import java.util.Collection;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.geektimes.projects.user.domain.User;

/**
 * @Desc: JPA实现
 * @author: liuawei
 * @date: 2021-03-10 17:07
 */
public class JpaUserRepository implements UserRepository{

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;


    @Override
    public boolean save(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        user.setId(null);
        user.setEmail("");
        user.setName("");
        entityManager.persist(user);
        transaction.commit();
        return true;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        String sql = String.format("SELECT id,name,password,email,phoneNumber FROM users WHERE phoneNumber= %s and password= %s", userName, password);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createNativeQuery(sql);
        query.getResultList();
        transaction.commit();
        return new User();
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }
}
