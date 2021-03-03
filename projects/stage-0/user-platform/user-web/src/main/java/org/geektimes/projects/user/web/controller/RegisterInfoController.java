package org.geektimes.projects.user.web.controller;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.service.impl.UserServiceImpl;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("/register-info")
public class RegisterInfoController implements PageController {
    private UserService userService = new UserServiceImpl();

    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)){
            return "error.jsp";
        }else {
            User user = new User();
            user.setName(email);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhoneNumber("13102221212");
            this.userService.register(user);
            return "success.jsp";
        }
    }

}
