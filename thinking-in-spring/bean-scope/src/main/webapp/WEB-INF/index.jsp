<?xml version="1.0" encoding="UTF-8" ?>
<jsp:directive.page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" />
<html>
    <head>
        <link rel="stylesheet" href="<spring:theme code='styleSheet'/>" type="text/css"/>
    </head>
    <body>
        \${userObject.name} : ${userObject.name}
        \${applicationScope['scopedTarget.user'].name} : ${applicationScope['scopedTarget.user'].name}
    </body>
</html>