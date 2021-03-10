package org.geektimes.projects.user.web.system;

import javax.annotation.PreDestroy;

/**
 * @Desc: 容器关闭
 * @author: liuawei
 * @date: 2021-03-09 18:04
 */
public class ContentDestoryed {



    @PreDestroy
    public void destory(){

        // 处理PreDestroy组件方法
        System.out.println("处理PreDestroy组件方法");
    }

}
