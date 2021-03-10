<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<!-- Content here -->
		<h3>用户注册</h3>
		<form action="/user_web/register/doRegister" method="get"><table>
			<!-- form创建表单，method：提交方式 -->
			<!-- class="right" （选择表格的元素）-->
			<tr>
				<td class="right">用户名：</td>
				<td><input type="text" name="uName"/></td>
			</tr>
			<tr>
				<td class="right">密码：</td>
				<td><input type="password" name="uPwd"/>
				</td>
			</tr>
<%--			<tr>--%>
<%--				<td class="right">确认密码：</td>--%>
<%--				<td> <input type="password" name="uRepwd"/></td>--%>
<%--			</tr>--%>
<%--			<tr>--%>
<%--				<td class="right">性别：</td>--%>
<%--				<td>--%>
<%--					<input type="radio" name="uSex" value="男" checked="checked"/>男 		<!-- checked="checked"默认选择项 -->--%>
<%--					<input type="radio" name="uSex" value="女" /> 女--%>
<%--				</td>--%>
<%--			</tr>--%>
			<tr><td class="right">手机号码：</td><td><input type="text" name="uPhone"/></td></tr>
			<tr><td class="right">电子邮箱：</td><td><input type="text" name="uEmail"/></td></tr>
			<tr><td></td><td><input type="submit" value="注册"/><input type="reset" value="重置"></td>  </tr>

		</table>
		</form>

	</div>
</body>