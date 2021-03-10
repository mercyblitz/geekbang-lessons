<%--
  Created by IntelliJ IDEA.
  User: qpy
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <title>注册的页面</title>
    <style>
        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }
    </style>
    <script type="text/javascript" src="static/js/jquery-3.5.1.slim.min.js"></script>
    <script>
      $(function () {
          $("#register").click(function () {
              $.ajax({
                  type:"POST",
                  url:"/registerAction/register",
                  data:$('#form').serialize(),
                  success:function (result) {
                      if("Yes"==result){
                          alert("登录成功！");
                      }else{
                          alert("用户名或密码错误");
                          $("#password").val("");
                          $("#password").focus();
                      }
                  },
                  error:function (err) {
                      alert("系统错误");
                  }
              });
          })
      })

    </script>
</head>
<body>
<form id = "form" action="registerAction" method="post" >
    <h1 class="h3 mb-3 font-weight-normal">用户注册</h1>
    <table align="center">
        <tr>
            <td>
                用户名：
            </td>
            <td>
                <input id="name" type="text" name="name">
            </td>
        </tr>
        <tr>
            <td>
                密码：
            </td>
            <td>
                <input id="password" type="password" name="password">
            </td>
        </tr>
        <tr>
            <td>
                电话号码：
            </td>
            <td>
                <input id="mobile" type="text" name="mobile">
            </td>
        </tr>
        <tr>
            <td>
                邮箱：
            </td>
            <td>
                <input id="email" type="text" name="email">
            </td>
        </tr>
        <tr>
            <td>
                <button type="button" id = "register"> 注册</button>
            </td>>
        </tr>
    </table>
</form>
</body>
</html>
