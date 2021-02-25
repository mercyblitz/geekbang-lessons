//package org.geektimes.projects.user.repository;
//
//import org.geektimes.projects.user.domain.User;
//
//import javax.naming.Context;
//import javax.sql.DataSource;
//import java.util.Collection;
//
//public class DatabaseUserRepository implements UserRepository {
//
//    private final DataSource dataSource;
//
//    public DatabaseUserRepository(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    public DatabaseUserRepository() {
//        this.dataSource = initDataSource();
//    }
//
//    private DataSource initDataSource() {
//        Context context = new DefaultContext();
//        return (DataSource) context.lookup("jdbc/UserPlatformDB");
//    }
//
//    @Override
//    public boolean save(User user) {
//        return false;
//    }
//
//    @Override
//    public boolean deleteById(Long userId) {
//        return false;
//    }
//
//    @Override
//    public boolean update(User user) {
//        return false;
//    }
//
//    @Override
//    public User getById(Long userId) {
//        return null;
//    }
//
//    @Override
//    public User getByNameAndPassword(String userName, String password) {
//        return null;
//    }
//
//    @Override
//    public Collection<User> getAll() {
//        return null;
//    }
//}
