package org.geektimes.projects.user.jolokia;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.Address;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.management.UserManagerInterface;
import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.jmx.JolokiaMBeanServerUtil;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class JolokiaDemo {
    public static void main(String[] args) throws Exception {
        User user = new User();
        Address address = new Address();
        // 将静态的 MBean 接口转化成 DynamicMBean
        ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
        StandardMBean standardMBean = new StandardMBean(new UserManager(user, address), UserManagerInterface.class);

        MBeanServer jolokiaServer = JolokiaMBeanServerUtil.getJolokiaMBeanServer();
        jolokiaServer.registerMBean(standardMBean, objectName);

        J4pClient j4pClient = new J4pClient("http://localhost:8080/jolokia");
        J4pReadRequest req = new J4pReadRequest("jolokia:type=Discovery");
        J4pReadResponse resp = j4pClient.execute(req);
        System.out.println(resp);
    }
}
