<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Admin Login</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <style>
    body { display: flex; align-items: center; padding-top: 40px; padding-bottom: 40px; background-color: #f5f5f5; }
    .form-signin { width: 100%; max-width: 330px; padding: 15px; margin: auto; }
  </style>
</head>
<body class="text-center">
<main class="form-signin">
  <form method="POST" action="${pageContext.request.contextPath}/admin/login">
    <h1 class="h3 mb-3 fw-normal">Đăng nhập Admin</h1>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger" role="alert">
          ${errorMessage}
      </div>
    </c:if>

    <div class="form-floating mb-2">
      <input type="text" class="form-control" id="username" name="username" placeholder="Username" required autofocus>
      <label for="username">Tên đăng nhập</label>
    </div>
    <div class="form-floating mb-3">
      <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
      <label for="password">Mật khẩu</label>
    </div>

    <button class="w-100 btn btn-lg btn-primary" type="submit">Đăng nhập</button>
    <p class="mt-5 mb-3 text-muted">© Your Hotel 2023</p>
  </form>
</main>
</body>
</html>