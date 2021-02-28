<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<form class="form-signin" action="registerUser" method="post">
			<div>
				姓名:<input type="text" name="name" value="" />
			</div>
			<div>
				密码:<input type="text" name="password" value="" />
			</div>
			<div>
				邮箱:<input type="text" name="email" value="" />
			</div>
			<div>
				手机号:<input type="text" name="phoneNumber" value="" />
			</div>
			<div>
				<input type="submit">
			</div>
		</form>
	</div>
</body>