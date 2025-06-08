<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Trang chủ" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<section class="hero-section">
    <div class="container container-hero-content">
        <h1>Chào mừng đến với Hotel Booking!</h1>
        <p class="lead-text">Khám phá và đặt ngay những phòng khách sạn tuyệt vời nhất cho kỳ nghỉ đáng nhớ của bạn.</p>

        <div class="search-form-container">
            <form action="${pageContext.request.contextPath}/rooms" method="GET">
                <div class="row g-3 align-items-end">
                    <div class="col-lg-3 col-md-6">
                        <label for="heroDestination" class="form-label">Điểm đến</label>
                        <input type="text" class="form-control" id="heroDestination" name="destination" placeholder="VD: Hà Nội, Đà Nẵng">
                    </div>
                    <div class="col-lg-2 col-md-6">
                        <label for="heroCheckIn" class="form-label">Ngày nhận phòng</label>
                        <input type="date" class="form-control" id="heroCheckIn" name="checkIn" min="<%= java.time.LocalDate.now().toString() %>">
                    </div>
                    <div class="col-lg-2 col-md-6">
                        <label for="heroCheckOut" class="form-label">Ngày trả phòng</label>
                        <input type="date" class="form-control" id="heroCheckOut" name="checkOut" min="<%= java.time.LocalDate.now().plusDays(1).toString() %>">
                    </div>
                    <div class="col-lg-2 col-md-6">
                        <label for="heroGuests" class="form-label">Số khách</label>
                        <select class="form-select" id="heroGuests" name="guests">
                            <option value="1">1 người</option>
                            <option value="2" selected>2 người</option>
                            <option value="3">3 người</option>
                            <option value="4">4+ người</option>
                        </select>
                    </div>
                    <div class="col-lg-3 col-md-12">
                        <button type="submit" class="btn btn-warning w-100"><i class="fas fa-search"></i> TÌM KIẾM NGAY</button>
                    </div>
                </div>
            </form>
        </div>

        <p class="mt-4 mb-2" style="font-size: 1.1rem; text-shadow: 1px 1px 2px rgba(0,0,0,0.3);">Hoặc khám phá tất cả phòng của chúng tôi:</p>
        <a href="${pageContext.request.contextPath}/rooms" class="view-all-rooms-btn">
            <i class="fas fa-th-list"></i> Xem tất cả phòng
        </a>
    </div>
</section>

<main class="content-sections-wrapper">
    <div class="container py-5">

        <section id="featured-hotels" class="mb-5">
            <h2 class="section-title">Khách sạn nổi bật</h2>
            <div class="row">
                <div class="col-md-6 col-lg-4">
                    <div class="card hotel-card">
                        <img src="${pageContext.request.contextPath}/images/1.jpg" class="card-img-top" alt="Khách sạn Mường Thanh">
                        <div class="card-body">
                            <h5 class="card-title">Khách sạn Mường Thanh - Hà Nội</h5>
                            <p class="card-text">Nằm ở vị trí trung tâm, tiện nghi hiện đại và dịch vụ chuyên nghiệp, lý tưởng cho mọi du khách.</p>
                            <a href="#" class="btn btn-primary"><i class="fas fa-eye"></i> Xem chi tiết</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card hotel-card">
                        <img src="${pageContext.request.contextPath}/images/hotel2.jpg" class="card-img-top" alt="InterContinental Danang">
                        <div class="card-body">
                            <h5 class="card-title">InterContinental - Đà Nẵng</h5>
                            <p class="card-text">Khu nghỉ dưỡng sang trọng với tầm nhìn tuyệt đẹp ra biển Sơn Trà, kiến trúc độc đáo.</p>
                            <a href="#" class="btn btn-primary"><i class="fas fa-eye"></i> Xem chi tiết</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card hotel-card">
                        <img src="${pageContext.request.contextPath}/images/hotel3.jpg" class="card-img-top" alt="Vinpearl Resort Nha Trang">
                        <div class="card-body">
                            <h5 class="card-title">Vinpearl Resort - Nha Trang</h5>
                            <p class="card-text">Thiên đường nghỉ dưỡng với công viên giải trí VinWonders và bãi biển riêng tuyệt đẹp.</p>
                            <a href="#" class="btn btn-primary"><i class="fas fa-eye"></i> Xem chi tiết</a>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Điểm đến phổ biến -->
        <section id="popular-destinations" class="mb-5">
            <h2 class="section-title">Điểm đến yêu thích</h2>
            <div class="row">
                <div class="col-md-6 col-lg-3">
                    <div class="card hotel-card destination-card">
                        <img src="${pageContext.request.contextPath}/images/destination-hanoi.jpg" class="card-img-top" alt="Hà Nội">
                        <div class="card-body text-center">
                            <h5 class="card-title">Hà Nội</h5>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="card hotel-card destination-card">
                        <img src="${pageContext.request.contextPath}/images/destination-danang.jpg" class="card-img-top" alt="Đà Nẵng">
                        <div class="card-body text-center">
                            <h5 class="card-title">Đà Nẵng</h5>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="card hotel-card destination-card">
                        <img src="${pageContext.request.contextPath}/images/destination-phuquoc.jpg" class="card-img-top" alt="Phú Quốc">
                        <div class="card-body text-center">
                            <h5 class="card-title">Phú Quốc</h5>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="card hotel-card destination-card">
                        <img src="${pageContext.request.contextPath}/images/destination-sapa.jpg" class="card-img-top" alt="Sa Pa">
                        <div class="card-body text-center">
                            <h5 class="card-title">Sa Pa</h5>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section id="why-us" class="py-5 bg-light rounded">
            <h2 class="section-title">Tại sao chọn Hotel Booking?</h2>
            <div class="row text-center">
                <div class="col-lg-4 col-md-6 mb-4">
                    <i class="fas fa-hand-holding-usd fa-3x mb-3 text-success feature-icon"></i>
                    <h4>Giá cả phải chăng</h4>
                    <p class="text-muted">Luôn có những ưu đãi tốt nhất, phù hợp với mọi ngân sách cho chuyến đi của bạn.</p>
                </div>
                <div class="col-lg-4 col-md-6 mb-4">
                    <i class="fas fa-shield-virus fa-3x mb-3 text-primary feature-icon"></i>
                    <h4>An toàn & Bảo mật</h4>
                    <p class="text-muted">Thông tin cá nhân và thanh toán của bạn được bảo vệ tuyệt đối với công nghệ hiện đại.</p>
                </div>
                <div class="col-lg-4 col-md-12 mb-4">
                    <i class="fas fa-headset fa-3x mb-3 text-info feature-icon"></i>
                    <h4>Hỗ trợ chuyên nghiệp</h4>
                    <p class="text-muted">Đội ngũ tư vấn viên nhiệt tình, sẵn sàng giải đáp mọi thắc mắc 24/7.</p>
                </div>
            </div>
        </section>

    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>