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

</head>
<body>
<form class="form-signup" action="sign-up" method="post">
    <h1 class="h3 mb-3 font-weight-normal">注册</h1>
    <label for="inputName" class="sr-only">请输入用户名</label>
    <input type="text" id="inputName" name="name" class="form-control" placeholder="请输入用户名" required autofocus>
    <label for="inputPhone" class="sr-only">请输入手机号码</label>
    <input type="text" id="inputPhone" name="phoneNumber" class="form-control" placeholder="请输入手机号码" required autofocus>
    <label for="inputEmail" class="sr-only">请输出电子邮件</label>
    <input type="email" id="inputEmail" name="email" class="form-control" placeholder="请输入电子邮件" required autofocus>
    <label for="inputPassword" class="sr-only">Password</label>
    <input type="password" id="inputPassword" name="password" class="form-control" placeholder="请输入密码" required>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign up</button>
    <p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
</form>
</body>
</html>
