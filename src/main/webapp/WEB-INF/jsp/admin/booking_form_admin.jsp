<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title>${formAction == 'insert' ? 'Thêm Đặt Phòng Mới' : 'Chỉnh Sửa Đặt Phòng'} - Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_style.css">
  <style>
    body { padding: 20px; }
    .form-label { font-weight: bold; }
  </style>
</head>
<body>
<h2>Thêm đặt phòng mới (Admin)</h2>
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
  <h1 class="mb-4">${formAction == 'insert' ? 'Thêm Đặt Phòng Mới' : 'Chỉnh Sửa Đặt Phòng'}</h1>

  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger" role="alert">
        ${errorMessage}
    </div>
  </c:if>

  <form method="POST" action="${pageContext.request.contextPath}/admin/bookings">
    <input type="hidden" name="action" value="${formAction}"/>

    <c:if test="${formAction == 'update'}">
      <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
    </c:if>

    <div class="mb-3">
      <label for="roomId" class="form-label">Chọn Phòng:</label>
      <select class="form-select" id="roomId" name="roomId" required>
        <option value="">-- Chọn một phòng --</option>
        <c:forEach var="roomItem" items="${listRoom}">
          <option value="${roomItem.roomId}"   ${(not empty booking.room and booking.room.roomId == roomItem.roomId) or (not empty submittedRoomId and submittedRoomId == roomItem.roomId) ? 'selected' : ''}>
              ${roomItem.roomNumber} - ${roomItem.roomType}
        </c:forEach>
      </select>
    </div>


    <div class="mb-3">
      <label for="customerName" class="form-label">Tên Khách Hàng:</label>
      <input type="text" class="form-control" id="guestName" name="guestName" value="${booking.guestName}" required>
    </div>
    <div class="mb-3">
      <label for="customerEmail" class="form-label">Email Khách Hàng:</label>
      <input type="email" class="form-control" id="guestEmail" name="guestEmail" value="${booking.guestEmail}">
    </div>


    <div class="mb-3">
      <label for="checkInDate" class="form-label">Ngày Nhận Phòng:</label>
      <input type="date" class="form-control" id="checkInDate" name="checkInDate"
             value="<fmt:formatDate value="${booking.checkInDate}" pattern="yyyy-MM-dd"/>" required>
    </div>

    <div class="mb-3">
      <label for="checkOutDate" class="form-label">Ngày Trả Phòng:</label>
      <input type="date" class="form-control" id="checkOutDate" name="checkOutDate"
             value="<fmt:formatDate value="${booking.checkOutDate}" pattern="yyyy-MM-dd"/>" required>
    </div>

    <div class="mb-3">
      <label for="status" class="form-label">Trạng Thái:</label>
      <select class="form-select" id="status" name="status" required>
        <option value="PENDING" ${booking.status == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
        <option value="CONFIRMED" ${booking.status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
        <option value="CHECKED_IN" ${booking.status == 'CHECKED_IN' ? 'selected' : ''}>Đã nhận phòng</option>
        <option value="CHECKED_OUT" ${booking.status == 'CHECKED_OUT' ? 'selected' : ''}>Đã trả phòng</option>
        <option value="CANCELLED" ${booking.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
      </select>
    </div>

    <div class="mb-3">
      <label for="totalPrice" class="form-label">Tổng Tiền:</label>
      <input type="number" step="0.01" class="form-control" id="totalPrice" name="totalPrice"
             value="${not empty submittedTotalPrice ? submittedTotalPrice : booking.totalPrice}">
      <small class="form-text text-muted">Tổng tiền sẽ được tính toán tự động (nếu có logic) hoặc có thể nhập thủ công (nếu cần).</small>
    </div>

    <button type="submit" class="btn btn-primary">
      ${formAction == 'insert' ? 'Thêm Đặt Phòng' : 'Cập Nhật Đặt Phòng'}
    </button>
    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-secondary">Hủy</a>
  </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>