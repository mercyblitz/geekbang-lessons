<%--
  Created by IntelliJ IDEA.
  User: qpy
  Date: 2021/3/4
  Time: 22:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>注册失败</title>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <%
        request.setCharacterEncoding("utf-8") ;
        String result = (String) request.getAttribute("result");
    %>
    <h2><%=result%></h2>
</head>
<body>

</body>
</html>
