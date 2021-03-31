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
	
	
### 第四周作业
- 代码分支  week_04
- 需求一：完善 my dependency-injection 模块
	- ContainerInitializer
	- 运行程序访问 http://localhost:8080/register
- 需求二：完善 my-configuration 模块
	- 在FrontControllerServlet#service方法中分别为servletContext和Thread设置config
	- 访问 http://localhost:8080/hello/world 可以获取application.name属性值
	
	
### 第五周作业
- 代码 https://github.com/haokevin/geekbang-lessons/tree/week05
- 构造Post请求涉及类
	- org.geektimes.rest.client.HttpPostInvocation
	- org.geektimes.rest.demo.RestClientDemo#testPost