package org.geektimes.projects.user.web.controller;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

/**
 * @Desc: 用户控制器
 * @author: liuawei
 * @date: 2021-03-01 16:39
 */
@Path("/register")
public class AuthController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Resource(name = "bean/Validator")
    private Validator validator;

    /**
     * 请求示例：
     *
     * 请求处理成功跳转成功页面 127.0.0.1:8080/register?name=evan&password=evanpassword
     * 请求处理失败跳转失败页面 127.0.0.1:8080/register?name=evan&password=
     *
     * @param request
     *            HTTP 请求
     * @param response
     *            HTTP 相应
     * @return
     * @throws Throwable
     */
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        if (request.getMethod().equalsIgnoreCase("GET")){
            return "login-form.jsp";
        }else {
            // 参数校验
            User user = new User();
            // 为了满足作业的ID判断
            user.setId(1L);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            Set<ConstraintViolation<User>> validators = validator.validate(user);
            for (ConstraintViolation<User> c : validators) {
                if (StringUtils.isNotBlank(c.getMessage())){
                    request.setAttribute("msg",c.getMessage());
                    return "registerFailed.jsp";
                }
            }
            if (userService.register(user)) {
                request.getServletContext().log("注册成功");
                return "registerSuccess.jsp";
            }
            return "registerFailed.jsp";
        }
    }
}