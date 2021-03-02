package org.geektimes.projects.user.repository;

import org.geektimes.web.mvc.sql.Insert;
import org.geektimes.web.mvc.sql.Select;
import org.geektimes.projects.user.domain.User;

import java.util.Collection;

/**
 * 用户存储仓库
 *
 * @since 1.0
 */
public interface UserRepository  {

    @Insert("insert into mvc_user (%s) values (%s)")
    boolean save(User user);

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    @Select(value = "select * from mvc_user;", returnType = "org.geektimes.projects.user.domain.User")
    Collection<User> getAll();
}
