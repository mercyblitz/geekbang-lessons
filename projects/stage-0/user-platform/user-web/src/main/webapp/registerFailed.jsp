<head>
	<jsp:directive.include
			file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
	<title>My Home Page</title>
</head>
<body>
<div class="container-lg">
	<!-- Content here -->
	registerFailed <%= request.getAttribute("msg") %>
</div>
</body>