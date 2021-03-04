package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @Author xialh
 * @Date 2021/3/3 11:27 下午
 */
@Path("/register")
public class RegisterControler  implements PageController {
    @GET
    @Path("/doRegister")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name =request.getParameter("uName");
        String uPwd =request.getParameter("uPwd");
        String uPhone =request.getParameter("uPhone");
        String uEmail =request.getParameter("uEmail");
        User user = new User();
        user.setName(name);
        user.setPassword(uPwd);
        user.setEmail(uEmail);
        user.setPhoneNumber(uPhone);
        DatabaseUserRepository repository = new DatabaseUserRepository(new DBConnectionManager());
        repository.save(user);

        return "success.jsp";
    }


}
