<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<footer class="footer-main mt-auto">
  <div class="container">
    <p class="mb-1">© <span id="currentYearFooter"></span> Hotel Booking. Mọi quyền được bảo lưu.</p>
    <p class="mb-2">
      <a href="${pageContext.request.contextPath}/privacy-policy">Chính sách bảo mật</a> |
      <a href="${pageContext.request.contextPath}/terms-of-service">Điều khoản sử dụng</a> |
      <a href="${pageContext.request.contextPath}/admin/login">Kênh quản trị</a>
    </p>
    <div class="social-icons mt-3">
      <a href="#" class="text-white me-3" title="Facebook"><i class="fab fa-facebook-f"></i></a>
      <a href="#" class="text-white me-3" title="Twitter"><i class="fab fa-twitter"></i></a>
      <a href="#" class="text-white me-3" title="Instagram"><i class="fab fa-instagram"></i></a>
      <a href="#" class="text-white" title="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
    </div>
  </div>
</footer>

<%-- Bootstrap JS Bundle (bao gồm Popper) --%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<%-- File script.js chung của bạn (nếu có) --%>
<%-- <script src="${pageContext.request.contextPath}/js/script.js"></script> --%>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Cập nhật năm hiện tại cho footer
    const currentYearSpan = document.getElementById('currentYearFooter');
    if (currentYearSpan) {
      currentYearSpan.textContent = new Date().getFullYear();
    }

    // --- JS cho date picker TRONG THANH TÌM KIẾM CỦA HEADER ---
    var headerCheckInInput = document.getElementById('checkIn'); // ID từ header
    var headerCheckOutInput = document.getElementById('checkOut'); // ID từ header

    if (headerCheckInInput) {
      // Không set min ở đây nữa vì đã có trong HTML, chỉ xử lý logic 'change'
      headerCheckInInput.addEventListener('change', function() {
        if (headerCheckInInput.value) {
          var checkInDateObj = new Date(headerCheckInInput.value);
          checkInDateObj.setDate(checkInDateObj.getDate() + 1);

          var nextDay_dd = String(checkInDateObj.getDate()).padStart(2, '0');
          var nextDay_mm = String(checkInDateObj.getMonth() + 1).padStart(2, '0');
          var nextDay_yyyy = checkInDateObj.getFullYear();
          var minCheckoutDate = nextDay_yyyy + '-' + nextDay_mm + '-' + nextDay_dd;

          if (headerCheckOutInput) {
            headerCheckOutInput.setAttribute('min', minCheckoutDate);
            if (headerCheckOutInput.value && new Date(headerCheckOutInput.value) < new Date(minCheckoutDate)) {
              headerCheckOutInput.value = '';
            }
          }
        } else {
          if (headerCheckOutInput) {
            headerCheckOutInput.removeAttribute('min');
            headerCheckOutInput.value = '';
          }
        }
      });
      // Xử lý khi trang load lại với giá trị checkIn đã có trong header
      if (headerCheckInInput.value && headerCheckOutInput) {
        var checkInDateObj = new Date(headerCheckInInput.value);
        checkInDateObj.setDate(checkInDateObj.getDate() + 1);
        var nextDay_dd = String(checkInDateObj.getDate()).padStart(2, '0');
        var nextDay_mm = String(checkInDateObj.getMonth() + 1).padStart(2, '0');
        var nextDay_yyyy = checkInDateObj.getFullYear();
        var minCheckoutDate = nextDay_yyyy + '-' + nextDay_mm + '-' + nextDay_dd;
        headerCheckOutInput.setAttribute('min', minCheckoutDate);
      }
    }


    // --- JS cho date picker TRONG FORM TÌM KIẾM CỦA HERO SECTION ---
    var heroCheckInInput = document.getElementById('heroCheckIn');
    var heroCheckOutInput = document.getElementById('heroCheckOut');

    if (heroCheckInInput) {
      heroCheckInInput.addEventListener('change', function() {
        if (heroCheckInInput.value) {
          var heroCheckInDateObj = new Date(heroCheckInInput.value);
          heroCheckInDateObj.setDate(heroCheckInDateObj.getDate() + 1);

          var heroNextDay_dd = String(heroCheckInDateObj.getDate()).padStart(2, '0');
          var heroNextDay_mm = String(heroCheckInDateObj.getMonth() + 1).padStart(2, '0');
          var heroNextDay_yyyy = heroCheckInDateObj.getFullYear();
          var heroMinCheckoutDate = heroNextDay_yyyy + '-' + heroNextDay_mm + '-' + heroNextDay_dd;

          if (heroCheckOutInput) {
            heroCheckOutInput.setAttribute('min', heroMinCheckoutDate);
            if (heroCheckOutInput.value && new Date(heroCheckOutInput.value) < new Date(heroMinCheckoutDate)) {
              heroCheckOutInput.value = '';
            }
          }
        } else {
          if (heroCheckOutInput) {
            heroCheckOutInput.removeAttribute('min');
            heroCheckOutInput.value = '';
          }
        }
      });
      // Xử lý khi trang load lại với giá trị heroCheckIn đã có
      if (heroCheckInInput.value && heroCheckOutInput) {
        var heroCheckInDateObj = new Date(heroCheckInInput.value);
        heroCheckInDateObj.setDate(heroCheckInDateObj.getDate() + 1);
        var heroNextDay_dd = String(heroCheckInDateObj.getDate()).padStart(2, '0');
        var heroNextDay_mm = String(heroCheckInDateObj.getMonth() + 1).padStart(2, '0');
        var heroNextDay_yyyy = heroCheckInDateObj.getFullYear();
        var heroMinCheckoutDate = heroNextDay_yyyy + '-' + heroNextDay_mm + '-' + heroNextDay_dd;
        heroCheckOutInput.setAttribute('min', heroMinCheckoutDate);
      }
    }
  });
</script>
</body>
</html>