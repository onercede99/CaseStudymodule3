<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title>Quản Lý Đặt Phòng - Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_style.css">
  <style>
    body { padding: 20px; }
    .table th, .table td { vertical-align: middle; }
  </style>
</head>
<body>
<h2>Danh sách đặt phòng (Admin)</h2>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-3">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/rooms">Admin Panel</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="adminNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/">Về Trang Chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/admin/rooms">Quản lý Phòng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings">Quản lý Đặt phòng</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/logout">Đăng xuất</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container">
  <h1 class="mb-4">Danh Sách Đặt Phòng</h1>

  <c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.success == 'add'}">Đã thêm đặt phòng thành công!</c:when>
        <c:when test="${param.success == 'update'}">Đã cập nhật đặt phòng thành công!</c:when>
        <c:when test="${param.success == 'delete'}">Đã xóa đặt phòng thành công!</c:when>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  <c:if test="${not empty param.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.error == 'notFound'}">Không tìm thấy đặt phòng.</c:when>
        <c:when test="${param.error == 'updateNotFound'}">Không tìm thấy đặt phòng để cập nhật.</c:when>
        <c:otherwise>Đã xảy ra lỗi: ${param.error}</c:otherwise>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger" role="alert">
        ${errorMessage}
    </div>
  </c:if>

  <p>
    <a href="${pageContext.request.contextPath}/admin/bookings?action=new" class="btn btn-primary mb-3">Thêm Đặt Phòng Mới</a>
  </p>

  <c:choose>
    <c:when test="${not empty listBooking}">
      <table class="table table-striped table-bordered">
        <thead class="table-dark">
        <tr>
          <th>ID Đặt Phòng</th>
          <th>Phòng</th>
          <th>Người Đặt</th>
          <th>Ngày Nhận Phòng</th>
          <th>Ngày Trả Phòng</th>
          <th>Tổng Tiền</th>
          <th>Trạng Thái</th>
          <th>Hành Động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="booking" items="${listBooking}">
          <tr>
            <td>${booking.bookingId}</td>
            <td>
              <c:choose>
                <c:when test="${not empty booking.room.roomNumber}">${booking.room.roomNumber}</c:when>
<%--                <c:when test="${not empty booking.room.name}">${booking.room.name}</c:when> --%>
                <c:otherwise>N/A</c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:choose>
<%--                <c:when test="${not empty booking.user.username}">${booking.user.username}</c:when>--%>
<%--                <c:when test="${not empty booking.user.fullName}">${booking.user.fullName}</c:when>--%>
                <c:when test="${not empty booking.guestName}">${booking.guestName}</c:when>
                <c:otherwise>N/A</c:otherwise>
              </c:choose>
            </td>
            <td><fmt:formatDate value="${booking.checkInDate}" pattern="dd/MM/yyyy"/></td>
            <td><fmt:formatDate value="${booking.checkOutDate}" pattern="dd/MM/yyyy"/></td>
            <td>
              <c:if test="${not empty booking.totalPrice}">
                <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencyCode="VND"/>
              </c:if>
              <c:if test="${empty booking.totalPrice}">Chưa tính</c:if>
            </td>
            <td>${booking.status}</td>
            <td>
              <a href="${pageContext.request.contextPath}/admin/bookings?action=edit&id=${booking.bookingId}" class="btn btn-sm btn-warning me-1">Sửa</a>
              <a href="${pageContext.request.contextPath}/admin/bookings?action=delete&id=${booking.bookingId}"
                 class="btn btn-sm btn-danger"
                 onclick="return confirm('Bạn có chắc chắn muốn xóa đặt phòng này không?');">Xóa</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="alert alert-info" role="alert">
        Không có đặt phòng nào để hiển thị.
      </div>
    </c:otherwise>
  </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>