<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Đã xảy ra lỗi" scope="request"/>
<jsp:include page="../../WEB-INF/jsp/common/header.jsp"/>
<%--<html>--%>
<%--<head>--%>

<%--</head>--%>
<%--<body>--%>
<div class="container mt-5">
  <div class="card">
    <div class="card-header bg-success text-white">
      <h2><i class="bi bi-check-circle-fill"></i> Đặt phòng thành công!</h2>
    </div>
    <div class="card-body">
      <p class="lead">Cảm ơn bạn, <strong><c:out value="${booking.guestName}"/></strong>, đã đặt phòng tại Hotel Booking.</p>
      <p>Chúng tôi đã gửi một email xác nhận đến <strong><c:out value="${booking.guestEmail}"/></strong>.</p>

      <hr>

      <h4>Chi tiết đặt phòng:</h4>
      <dl class="row">
        <dt class="col-sm-4">Mã đặt phòng:</dt>
        <dd class="col-sm-8"><strong>BK-${booking.bookingId}</strong></dd>

        <dt class="col-sm-4">Phòng:</dt>
        <dd class="col-sm-8"><c:out value="${room.roomType}"/> - Số <c:out value="${room.roomNumber}"/></dd>

        <dt class="col-sm-4">Ngày nhận phòng:</dt>
        <dd class="col-sm-8"><fmt:formatDate value="${booking.checkInDate}" pattern="dd/MM/yyyy"/></dd>

        <dt class="col-sm-4">Ngày trả phòng:</dt>
        <dd class="col-sm-8"><fmt:formatDate value="${booking.checkOutDate}" pattern="dd/MM/yyyy"/></dd>

        <dt class="col-sm-4">Tổng số đêm:</dt>
        <dd class="col-sm-8">
           <c:if test="${not empty booking.checkInDate and not empty booking.checkOutDate}">
            <c:set var="nights" value="${(booking.checkOutDate.time - booking.checkInDate.time) / (1000 * 60 * 60 * 24)}"/>
             <c:if test="${nights < 1}"><c:set var="nights" value="1"/></c:if>
             <fmt:formatNumber value="${nights}" minFractionDigits="0" maxFractionDigits="0"/> đêm
             </c:if>
             <c:if test="${empty booking.checkInDate or empty booking.checkOutDate}">
              N/A </c:if>
        </dd>

        <dt class="col-sm-4">Tổng tiền:</dt>
        <dd class="col-sm-8">
          <strong><fmt:formatNumber value="${booking.totalPrice}" type="currency" currencyCode="VND"/></strong>
        </dd>
      </dl>

      <hr>
      <p>Chúng tôi rất mong được đón tiếp bạn!</p>
    </div>
    <div class="card-footer text-center">
      <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
        <i class="bi bi-house-door"></i> Về trang chủ
      </a>
      <a href="${pageContext.request.contextPath}/room_list" class="btn btn-secondary">
        <i class="bi bi-door-open"></i> Xem các phòng khác
      </a>
    </div>
  </div>
</div>

<jsp:include page="../../WEB-INF/jsp/common/footer.jsp"/>