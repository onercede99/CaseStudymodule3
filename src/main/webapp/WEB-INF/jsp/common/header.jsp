<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${not empty pageTitle ? pageTitle : 'Hotel Booking'}"/> - Đặt phòng khách sạn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary fixed-top shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            <i class="fas fa-hotel"></i> Hotel Booking
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath == '/' || pageContext.request.servletPath == '/index.jsp' ? 'active' : ''}" aria-current="page" href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath == '/rooms' ? 'active' : ''}" href="${pageContext.request.contextPath}"><i class="fas fa-door-open"></i> Phòng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-percent"></i> Khuyến mãi</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-info-circle"></i> Về chúng tôi</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-envelope"></i> Liên hệ</a>
                </li>
                <c:choose>
                    <c:when test="${not empty sessionScope.adminUser}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownUser" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-shield"></i> Admin: <c:out value="${sessionScope.adminUser.username}"/>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownUser">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/rooms"><i class="fas fa-bed"></i> QL Phòng</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/bookings"><i class="fas fa-calendar-alt"></i> QL Đặt phòng</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:when test="${not empty sessionScope.loggedInUser}"> <%-- Giữ lại cho user thường nếu có --%>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownUser" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-circle"></i> Chào, <c:out value="${sessionScope.loggedInUser.username}"/>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdownUser">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user-edit"></i> Hồ sơ</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/my-bookings"><i class="fas fa-calendar-check"></i> Đặt phòng của tôi</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/login"><i class="fas fa-user-cog"></i> Admin Login</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
<div class="container mt-3">
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> <c:out value="${sessionScope.successMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> <c:out value="${sessionScope.errorMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.warningMessage}">
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> <c:out value="${sessionScope.warningMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="warningMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="fas fa-info-circle"></i> <c:out value="${sessionScope.message}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
</div>
