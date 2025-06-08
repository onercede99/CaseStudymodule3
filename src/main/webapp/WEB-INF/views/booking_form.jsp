<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="pageTitle" value="Đặt phòng: ${room.roomType}" scope="request"/>
<jsp:include page="../../WEB-INF/jsp/common/header.jsp"/>

<div class="row g-5">
    <div class="col-lg-7">
        <h2 class="mb-4 fw-light">Hoàn tất đặt phòng của bạn</h2>
        <form action="${pageContext.request.contextPath}/booking" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="roomId" value="${room.roomId}">
            <h4 class="card-title text-primary">${room.roomType} (Phòng ${room.roomNumber})</h4>
            <p class="card-text text-muted small">${room.description}</p>
            <fmt:formatNumber value="${room.pricePerNight}" type="currency" currencySymbol="₫" minFractionDigits="0"/>

            <div class="card shadow-sm mb-4 border-light">
                <div class="card-header bg-light py-3">
                    <h5 class="mb-0"><i class="bi bi-person-fill me-2"></i>Thông tin khách hàng</h5>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label for="guestName" class="form-label">Họ và Tên <span class="text-danger">*</span></label>
                        <input type="text" class="form-control form-control-lg" id="guestName" name="guestName" value="${guestName}" required>
                        <div class="invalid-feedback">Vui lòng nhập họ tên.</div>
                    </div>
                    <div class="mb-3">
                        <label for="guestEmail" class="form-label">Địa chỉ Email</label>
                        <input type="email" class="form-control form-control-lg" id="guestEmail" name="guestEmail" value="${guestEmail}" placeholder="vidu@email.com">
                        <div class="form-text text-muted">Để chúng tôi gửi xác nhận đặt phòng cho bạn (không bắt buộc).</div>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm mb-4 border-light">
                <div class="card-header bg-light py-3">
                    <h5 class="mb-0"><i class="bi bi-calendar-check-fill me-2"></i>Chi tiết lưu trú</h5>
                </div>
                <div class="card-body p-4">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="checkInDate" class="form-label">Ngày nhận phòng <span class="text-danger">*</span></label>
                            <input type="date" class="form-control form-control-lg" id="checkInDate" name="checkInDate" value="${checkInDate}" required
                                   min="<%= java.time.LocalDate.now().toString() %>">
                            <div class="invalid-feedback">Vui lòng chọn ngày nhận phòng.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="checkOutDate" class="form-label">Ngày trả phòng <span class="text-danger">*</span></label>
                            <input type="date" class="form-control form-control-lg" id="checkOutDate" name="checkOutDate" value="${checkOutDate}" required
                                   min="<%= java.time.LocalDate.now().plusDays(1).toString() %>">
                            <div class="invalid-feedback">Vui lòng chọn ngày trả phòng.</div>
                        </div>
                    </div>
                    <p class="text-muted mt-3 small">Tổng giá sẽ được tính toán và hiển thị ở bước tiếp theo.</p>
                </div>
            </div>

            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-success btn-lg fw-bold py-3 text-uppercase">
                    <i class="bi bi-check-circle-fill"></i> Xác nhận và đặt phòng
                </button>
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary btn-lg py-3">
                    <i class="bi bi-arrow-left-circle"></i> Hủy và quay lại
                </a>
            </div>
        </form>
    </div>

    <div class="col-lg-5">
        <div class="card shadow-sm sticky-top border-light" style="top: 100px;">
            <div class="card-header bg-primary text-white py-3">
                <h5 class="mb-0"><i class="bi bi-bookmark-star-fill me-2"></i>Tóm tắt phòng đã chọn</h5>
            </div>
            <img src="${not empty room.imageUrl ? room.imageUrl : 'https://via.placeholder.com/400x250.png?text=Ảnh+Phòng'}"
                 class="card-img-top" alt="${room.roomType}" style="max-height: 250px; object-fit: cover;">
            <div class="card-body p-4">
                <h4 class="card-title text-primary">${room.roomType} (Phòng ${room.roomNumber})</h4>
                <p class="card-text text-muted small">${room.description}</p>
                <hr>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold fs-5">Giá mỗi đêm:</span>
                    <span class="fs-4 text-danger fw-bold">
                        <fmt:setLocale value="vi_VN"/>
                        <fmt:formatNumber value="${room.pricePerNight}" type="currency" currencySymbol="₫" minFractionDigits="0"/>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
<br/>

<script>
    (function () {
        'use strict'
        var forms = document.querySelectorAll('.needs-validation')
        Array.prototype.slice.call(forms)
            .forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }

                    const checkInDateElem = form.querySelector('#checkInDate');
                    const checkOutDateElem = form.querySelector('#checkOutDate');
                    if (checkInDateElem && checkOutDateElem) {
                        const checkInDate = new Date(checkInDateElem.value);
                        const checkOutDate = new Date(checkOutDateElem.value);
                        if (checkOutDate <= checkInDate) {
                            checkOutDateElem.setCustomValidity('Ngày trả phòng phải sau ngày nhận phòng.');
                            event.preventDefault();
                            event.stopPropagation();
                        } else {
                            checkOutDateElem.setCustomValidity('');
                        }
                    }
                    form.classList.add('was-validated')
                }, false)
            })
    })()
</script>

<jsp:include page="../../WEB-INF/jsp/common/footer.jsp"/>