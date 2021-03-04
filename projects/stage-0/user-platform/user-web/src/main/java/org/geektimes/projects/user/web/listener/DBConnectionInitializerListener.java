package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.geektimes.projects.user.sql.DBConnectionManager.*;

@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {

    public static Map<String,Object> DB = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String databaseURL = "jdbc:derby:~/user-platform;create=true";
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection connection = DriverManager.getConnection(databaseURL);
            Statement statement = connection.createStatement();
            boolean isExist = statement.execute("SELECT COUNT(*) FROM users");
            if (isExist) {
                statement.execute(DROP_USERS_TABLE_DDL_SQL);
            }

            statement.execute(CREATE_USERS_TABLE_DDL_SQL);
            statement.executeUpdate(INSERT_USER_DML_SQL);
            DBConnectionManager dbConnectionManager = new DBConnectionManager();
            dbConnectionManager.setConnection(connection);
            DB.put( "db", dbConnectionManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnectionManager dbConnectionManager = (DBConnectionManager) DB.get("db");
        dbConnectionManager.releaseConnection();
    }
}
