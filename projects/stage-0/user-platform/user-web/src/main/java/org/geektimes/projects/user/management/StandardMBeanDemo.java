package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

public class StandardMBeanDemo {

    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        User user = new User();
        Address address = new Address();
        // 将静态的 MBean 接口转化成 DynamicMBean
        ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
        StandardMBean standardMBean = new StandardMBean(new UserManager(user, address), UserManagerInterface.class);

        MBeanInfo mBeanInfo = standardMBean.getMBeanInfo();

        mBeanServer.registerMBean(standardMBean, objectName);

        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }
}
