# geekbang-lessons
极客时间课程工程


### 第三周作业
- 需求一
	- mvn clean package
	- java -jar  user-web/target/user-web-v1-SNAPSHOT-war-exec.jar
	- 访问 http://localhost:8080/jolokia/read/org.geektimes.projects.user.management:type=User
	- MBean注册代码org.geektimes.projects.user.web.listener.TestingListener#registerMBean
- 需求二
	- 请看测试类`ConfigProviderResolverDemo`