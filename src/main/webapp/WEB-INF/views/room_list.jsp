
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Danh sách phòng" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<main class="content-sections-wrapper">
    <div class="container py-4">
        <h1 class="mb-4 section-title">Danh sách phòng khách sạn</h1>


        <c:choose>
            <c:when test="${not empty listRoom}">
                <div class="row">
                    <c:forEach var="room" items="${listRoom}">
                        <div class="col-md-6 col-lg-4 mb-4">
                            <div class="card hotel-card h-100">
                                <c:if test="${not empty room.imageUrl}">
                                    <img src="${room.imageUrl}" class="card-img-top" alt="Ảnh phòng ${room.roomNumber}"
                                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/placeholder.png'; this.alt='Lỗi tải ảnh';">
                                </c:if>
                                <c:if test="${empty room.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/images/placeholder.png" class="card-img-top" alt="Không có ảnh">
                                </c:if>
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">Phòng ${room.roomNumber} - ${room.roomType}</h5>
                                    <p class="card-text text-muted small">Sức chứa: ${room.capacity} người</p>
                                    <p class="card-text price-text fw-bold fs-5 text-danger">
                                        <fmt:formatNumber value="${room.pricePerNight}" type="currency" currencyCode="VND" maxFractionDigits="0"/> / đêm
                                    </p>
                                    <p class="card-text description-text">
                                        <c:choose>
                                            <c:when test="${fn:length(room.description) > 100}">
                                                <c:out value="${fn:substring(room.description, 0, 100)}"/>...
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${room.description}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <div class="mt-auto">
                                        <a href="${pageContext.request.contextPath}/booking?roomId=${room.roomId}" class="btn btn-primary w-100">
                                            <i class="fas fa-calendar-plus"></i> Đặt ngay
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>


            </c:when>
            <c:otherwise>
                <p style="color:red;">DEBUG: listRoom is empty or null. Size: ${fn:length(listRoom)}</p>
                <c:if test="${empty warningMessage && empty errorMessage}">
                    <div class="alert alert-info" role="alert">
                        <i class="fas fa-info-circle"></i> Hiện tại không có phòng nào phù hợp hoặc chưa có phòng nào được thêm.
                    </div>
                </c:if>
                <c:if test="${not empty warningMessage}"><div class="alert alert-warning">${warningMessage}</div></c:if>
                <c:if test="${not empty errorMessage}"><div class="alert alert-danger">${errorMessage}</div></c:if>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
