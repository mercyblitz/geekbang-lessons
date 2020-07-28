package org.geekbang.thinking.in.spring.configuration.metadata.model;

/**
 * @author xiaoheitalk
 * @type Student
 * @date 2020/7/24 10:14
 */
public class Student {
    private Long id;
    private String name;
    private String user;

    public Student() {
    }

    public Student(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Student(Long id, String name, String user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
