package org.geektimes.projects.user.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * @program: geekbang-lessons
 * @description:
 * @create: 2021-03-04 21:58
 */
@Path("/registerAction" )
public class RegisterController implements PageController {


    @Resource(name = "bean/UserService")
    private UserService userService;

    @Resource(name = "bean/Validator")
    private Validator validator;


    @POST
    @Path("/register")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        User user = new User();
        user.setName(request.getParameter("name"));
        user.setPassword(request.getParameter("password"));
        user.setPhoneNumber(request.getParameter("mobile"));
        user.setEmail(request.getParameter("email"));
        Set<ConstraintViolation<User>> validates = validator.validate( user );
        if(CollectionUtils.isNotEmpty( validates )){
            request.setAttribute("result","注册失败");
            return "fail.jsp";
        }
        if (userService.register(user)) {
            request.setAttribute("result", "注册成功");
            return "success.jsp";
        } else {
            request.setAttribute("result","注册失败");
            return "fail.jsp";
        }
    }


}
