<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Kết quả tìm kiếm phòng" scope="request"/>
<jsp:include page="../../WEB-INF/jsp/common/header.jsp"/>

<h2 class="mb-4 fw-light">
    <c:choose>
        <c:when test="${not empty paramDestination}">
            Phòng trống tại <strong class="text-primary">"${paramDestination}"</strong>
            <c:if test="${not empty paramCheckIn}">
                từ <fmt:parseDate value="${paramCheckIn}" pattern="yyyy-MM-dd" var="parsedCheckInDate" />
                <strong class="text-primary"><fmt:formatDate value="${parsedCheckInDate}" pattern="dd/MM/yyyy"/></strong>
            </c:if>
            <c:if test="${not empty paramCheckOut}">
                đến <fmt:parseDate value="${paramCheckOut}" pattern="yyyy-MM-dd" var="parsedCheckOutDate" />
                <strong class="text-primary"><fmt:formatDate value="${parsedCheckOutDate}" pattern="dd/MM/yyyy"/></strong>
            </c:if>
        </c:when>
        <c:otherwise>
            Phòng có sẵn
            <c:if test="${not empty paramCheckIn}">
                cho ngày <fmt:parseDate value="${paramCheckIn}" pattern="yyyy-MM-dd" var="parsedCheckInDate" />
                <strong class="text-primary"><fmt:formatDate value="${parsedCheckInDate}" pattern="dd/MM/yyyy"/></strong>
            </c:if>
        </c:otherwise>
    </c:choose>
</h2>
<hr class="mb-4">

<div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
    <c:choose>
        <c:when test="${not empty roomList}">
            <c:forEach var="room" items="${roomList}">
                <div class="col">
                    <div class="card h-100 shadow-sm border-light room-card">
                        <img src="${not empty room.imageUrl ? room.imageUrl : 'https://via.placeholder.com/350x220.png?text=Ảnh+Phòng'}"
                             class="card-img-top" alt="${room.roomType}" style="height: 220px; object-fit: cover;">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title text-primary">${room.roomType} (Phòng ${room.roomNumber})</h5>
                            <p class="card-text text-muted small flex-grow-1 mb-2">
                                <c:choose>
                                    <c:when test="${fn:length(room.description) > 120}">
                                        ${fn:substring(room.description, 0, 120)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${room.description}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="mt-auto">
                                <p class="card-text mb-2">
                                    <strong class="fs-4 text-danger">
                                        <fmt:setLocale value="vi_VN"/>
                                        <fmt:formatNumber value="${room.pricePerNight}" type="currency" currencySymbol="₫" minFractionDigits="0"/>
                                    </strong>
                                    <span class="text-muted">/ đêm</span>
                                </p>
                                <p class="card-text">Sức chứa: <c:out value="${room.capacity}"/> người</p>
                                <a href="${pageContext.request.contextPath}/booking?roomId=${room.roomId}&checkInDate=${paramCheckIn}&checkOutDate=${paramCheckOut}" class="btn btn-warning w-100 fw-bold text-uppercase">
                                    Chọn phòng <i class="bi bi-chevron-right"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:if test="${empty message && empty errorMessage && empty warningMessage}">
                <div class="col-12">
                    <div class="alert alert-info text-center py-4" role="alert">
                        <h4 class="alert-heading"><i class="bi bi-info-circle"></i> Không tìm thấy phòng</h4>
                        <p>Hiện không có phòng nào phù hợp với tiêu chí tìm kiếm của bạn. Vui lòng thử lại với các lựa chọn khác.</p>
                    </div>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="../../WEB-INF/jsp/common/footer.jsp"/>