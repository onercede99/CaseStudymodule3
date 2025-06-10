<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title><c:choose><c:when test="${formAction == 'insert'}">Thêm phòng mới</c:when><c:otherwise>Chỉnh sửa phòng</c:otherwise></c:choose></title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">

  <h2>Thêm phòng mới (Admin)</h2>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-3">
    <div class="container-fluid">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/rooms">Admin Panel</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="adminNavbar">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li>
            <hr class="nav-item">
            <a href="${pageContext.request.contextPath}/" target="_blank">
              <i class="fas fa-home"></i> Về Trang Chủ
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/admin/rooms">Quản lý Phòng</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings">Quản lý Đặt phòng</a>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item" style="align-content: center">
                         <span class="navbar-text me-3">
                             Chào <c:out value="${sessionScope.adminUser.username}"/>!
                         </span>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/logout">Đăng xuất</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <h2><c:choose><c:when test="${formAction == 'insert'}">Thêm phòng mới</c:when><c:otherwise>Chỉnh sửa phòng: <c:out value="${room.roomNumber}"/></c:otherwise></c:choose></h2>

  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger" role="alert">
      <i class="fas fa-exclamation-triangle"></i> <c:out value="${errorMessage}"/>
    </div>
  </c:if>

  <form action="${pageContext.request.contextPath}/admin/rooms" method="POST">
    <input type="hidden" name="action" value="${formAction}">
    <c:if test="${formAction == 'update'}">
      <input type="hidden" name="id" value="${room.roomId}">
    </c:if>

    <div class="mb-3">
      <label for="roomNumber" class="form-label">Số phòng:</label>
      <input type="text" class="form-control" id="roomNumber" name="roomNumber"
             value="<c:out value='${not empty room.roomNumber ? room.roomNumber : param.roomNumber}'/>" required>
    </div>

  <div class="mb-3">
    <label for="imageUrl" class="form-label">Đường dẫn ảnh (URL):</label>
    <input type="url" class="form-control" id="imageUrl" name="imageUrl"
           value="<c:out value='${not empty room.imageUrl ? room.imageUrl : ""}'/>"
           placeholder="Ví dụ: https://example.com/path/to/image.jpg">
    <c:if test="${formAction == 'update' && not empty room.imageUrl}">
      <div class="mt-2">
        <p>Ảnh hiện tại (nếu URL hợp lệ):</p>
        <img src="${room.imageUrl}" alt="Ảnh phòng <c:out value='${room.roomNumber}'/>"
             style="max-width: 200px; max-height: 150px; border: 1px solid #ddd;"
             onerror="this.style.display='none'; this.parentElement.insertAdjacentHTML('beforeend', '<small class=\'text-danger\'>Không thể tải ảnh từ URL này.</small>')">
      </div>
    </c:if>
    <small class="form-text text-muted">Nhập URL đầy đủ của ảnh. Để trống nếu không có ảnh hoặc muốn xóa ảnh hiện tại (nếu có logic xóa).</small>
  </div>


      <div class="mb-3">
      <label for="pricePerNight" class="form-label">Giá mỗi đêm (VNĐ):</label>
      <input type="number" step="1000" min="0" class="form-control" id="pricePerNight" name="pricePerNight" value="${room.pricePerNight}" required>
    </div>
    <div class="mb-3">
      <label for="roomType" class="form-label">Kiểu phòng:</label>
      <select class="form-select" id="roomType" name="roomType" required>
        <option value="">-- Chọn kiểu phòng --</option>
        <option value="Standard Single" <c:if test="${'Standard Single' == room.roomType}">selected</c:if>>Standard Single</option>
        <option value="Standard Double" <c:if test="${'Standard Double' == room.roomType}">selected</c:if>>Standard Double</option>
        <option value="Deluxe Queen" <c:if test="${'Deluxe Queen' == room.roomType}">selected</c:if>>Deluxe Queen</option>
        <option value="Luxury Suite" <c:if test="${'Luxury Suite' == room.roomType}">selected</c:if>>Luxury Suite</option>
        <option value="SUITE" <c:if test="${'SUITE' == room.roomType}">selected</c:if>>Suite</option>
      </select>
    </div>
    <div class="mb-3">
      <label for="capacity" class="form-label">Sức chứa (số người):</label>
      <input type="number" class="form-control" id="capacity" name="capacity" value="${room.capacity > 0 ? room.capacity : 2}" min="1" required>
    </div>
    <div class="mb-3">
      <label for="description" class="form-label">Mô tả:</label>
      <textarea class="form-control" id="description" name="description" rows="3"><c:out value='${room.description}'/></textarea>
    </div>
    <div class="mb-3 form-check">
      <input type="checkbox" class="form-check-input" id="isAvailable" name="isAvailable" value="on" <c:if test="${room.available or formAction == 'insert'}">checked</c:if>>
      <label class="form-check-label" for="isAvailable">Còn trống / Cho phép đặt</label>
    </div>
    <button type="submit" class="btn btn-primary">Lưu phòng</button>
    <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-secondary">Hủy</a>
  </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>