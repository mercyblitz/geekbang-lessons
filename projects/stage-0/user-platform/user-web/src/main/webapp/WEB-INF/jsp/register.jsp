<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <title>用户注册</title>
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
</head>
<body>

    <div class="container">
        <form action="/user/register" method="post">
            <h3 align="center">用户注册</h3>
            <p> 账号：
                <input  type="text"  name="name" />
            </p>
            <p>密码：
                <input  type="password" name="password" />
            </p>
            <p>邮箱：
                <input type="email"  name="email" />
            </p>
            <p>电话号码：
                <input  type="text"  name="phoneNumber" />
            </p>

            <button type="submit"> 朕要注册</button>

        </form>
    </div>
</body>