<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title><c:choose><c:when test="${formAction == 'insert'}">Thêm phòng mới</c:when><c:otherwise>Chỉnh sửa phòng</c:otherwise></c:choose></title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
  <h2><c:choose><c:when test="${formAction == 'insert'}">Thêm phòng mới</c:when><c:otherwise>Chỉnh sửa phòng: <c:out value="${room.roomNumber}"/></c:otherwise></c:choose></h2>

  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger" role="alert">
      <c:out value="${errorMessage}"/>
    </div>
  </c:if>

  <form action="${pageContext.request.contextPath}/admin/rooms" method="POST">
  <h2><c:choose><c:when test="${formAction == 'insert'}">Thêm phòng mới</c:when><c:otherwise>Chỉnh sửa phòng: <c:out value="${room.name}"/></c:otherwise></c:choose></h2>
  <form action="${pageContext.request.contextPath}/admin/rooms" method="POST">
    <input type="hidden" name="action" value="${formAction}">
    <c:if test="${formAction == 'update'}">
      <input type="hidden" name="id" value="${room.id}">
    </c:if>

    <div class="mb-3">
      <label for="roomNumber" class="form-label">Số phòng:</label>
      <input type="text" class="form-control" id="roomNumber" name="roomNumber" value="<c:out value='${room.roomNumber}'/>" required>
    </div>
    <div class="mb-3">
      <label for="pricePerNight" class="form-label">Giá mỗi đêm (VNĐ):</label>
      <input type="number" step="1000" min="0" class="form-control" id="pricePerNight" name="pricePerNight" value="${room.pricePerNight}" required>
    </div>
    <div class="mb-3">
      <label for="roomType" class="form-label">Kiểu phòng:</label>
      <select class="form-select" id="roomType" name="roomType" required>
        <option value="">-- Chọn kiểu phòng --</option>
        <option value="SINGLE" <c:if test="${'SINGLE' == room.roomType}">selected</c:if>>Phòng đơn</option>
        <option value="DOUBLE" <c:if test="${'DOUBLE' == room.roomType}">selected</c:if>>Phòng đôi</option>
        <option value="SUITE" <c:if test="${'SUITE' == room.roomType}">selected</c:if>>Suite</option>
        --%>
      </select>
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
<%-- Include admin_footer nếu có --%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>