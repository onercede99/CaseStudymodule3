<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>

<html>
<head>
  <title>Quản lý Phòng - Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <c:if test="${not empty successAlert}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:out value="${successAlert}"/>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <c:if test="${not empty errorAlert}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:out value="${errorAlert}"/>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <h2>Danh sách phòng (Admin)</h2>
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

  <h2>Danh sách phòng (Admin)</h2>
  <a href="${pageContext.request.contextPath}/admin/rooms?action=new" class="btn btn-success mb-3">Thêm phòng mới</a>

  <c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.success == 'add'}">Thêm phòng thành công!</c:when>
        <c:when test="${param.success == 'update'}">Cập nhật phòng thành công!</c:when>
        <c:when test="${param.success == 'delete'}">Xóa phòng thành công!</c:when>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${errorMessage}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </c:if>

  <table class="table table-striped table-hover">
    <thead>
    <tr>
      <th>ID</th>
      <th>Số phòng</th>
      <th>Kiểu phòng</th>
      <th>Giá/đêm</th>
      <th>Sức chứa</th>
      <th>Mô tả</th>
      <th>Ảnh</th>
      <th>Trạng thái</th>
      <th>Hành động</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="room" items="${listRoom}">
      <tr>
        <td><c:out value="${room.roomId}"/></td>
        <td><c:out value="${room.roomNumber}"/></td>
        <td><c:out value="${room.roomType}"/></td>
        <td><c:out value="${String.format('%,.0f đ', room.pricePerNight)}"/></td>
        <td><c:out value="${room.capacity}"/> người</td>
        <td><c:out value="${room.description}"/></td>
        <td>
          <c:if test="${not empty room.imageUrl}">
            <img src="${room.imageUrl}" class="card-img-top" alt="Ảnh phòng ${room.roomNumber}" style="width: 80px; height: 60px; object-fit: cover; border-radius: 4px;"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/placeholder.png'; this.alt='Lỗi tải ảnh';">
          </c:if>
          <c:if test="${empty room.imageUrl}">
            <img src="${pageContext.request.contextPath}/images/placeholder.png" class="card-img-top" alt="Không có ảnh">
          </c:if>
        </td>
        <td>
          <c:if test="${room.available}">
            <span class="badge bg-success">Còn trống</span>
          </c:if>
          <c:if test="${not room.available}">
            <span class="badge bg-danger">Hết phòng</span>
          </c:if>
        </td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/rooms?action=edit&id=${room.roomId}" class="btn btn-sm btn-primary"><i class="fas fa-edit"></i>Sửa</a>
          <a href="${pageContext.request.contextPath}/admin/rooms?action=delete&id=${room.roomId}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa phòng này không?');"><i class="fas fa-trash-alt"></i>Xóa</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty listRoom}">
      <tr><td colspan="7" class="text-center">Không có phòng nào.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>