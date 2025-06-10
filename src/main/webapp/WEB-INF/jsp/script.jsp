<%--<script>--%>
<%--document.addEventListener('DOMContentLoaded', function() {--%>
<%--    console.log("Script.js loaded successfully!");--%>

<%--    const deleteForms = document.querySelectorAll('form[onsubmit*="confirm"]');--%>
<%--    deleteForms.forEach(form => {--%>
<%--        form.addEventListener('submit', function (event) {--%>
<%--            const message = this.getAttribute('data-confirm-message') || 'Bạn có chắc chắn muốn thực hiện hành động này?';--%>
<%--            if (!confirm(message)) {--%>
<%--                event.preventDefault();--%>
<%--            }--%>
<%--        });--%>
<%--    });--%>

<%--    const autoDismissAlerts = document.querySelectorAll('.alert-dismissible[data-auto-dismiss]');--%>
<%--    autoDismissAlerts.forEach(alertElement => {--%>
<%--        const delay = parseInt(alertElement.getAttribute('data-auto-dismiss'), 10) || 5000;--%>
<%--        setTimeout(() => {--%>
<%--            if (typeof bootstrap !== 'undefined' && bootstrap.Alert) {--%>
<%--                const alertInstance = bootstrap.Alert.getInstance(alertElement);--%>
<%--                if (alertInstance) {--%>
<%--                    alertInstance.close();--%>
<%--                } else {--%>
<%--                    const newAlertInstance = new bootstrap.Alert(alertElement);--%>
<%--                    newAlertInstance.close();--%>
<%--                }--%>
<%--            } else {--%>
<%--                alertElement.style.display = 'none';--%>
<%--            }--%>
<%--        }, delay);--%>
<%--    });--%>

<%--    const checkInDateField = document.getElementById('checkInDate'); // Giả sử ID của input ngày nhận phòng là 'checkInDate'--%>
<%--    const checkOutDateField = document.getElementById('checkOutDate'); // Giả sử ID của input ngày trả phòng là 'checkOutDate'--%>

<%--    if (checkInDateField && checkOutDateField) {--%>
<%--        checkInDateField.addEventListener('change', function () {--%>
<%--            if (checkOutDateField.value && checkOutDateField.value < this.value) {--%>
<%--                checkOutDateField.value = this.value; // Hoặc hiển thị thông báo lỗi--%>
<%--            }--%>
<%--            checkOutDateField.min = this.value; // Đặt ngày tối thiểu cho check-out--%>
<%--        });--%>

<%--        checkOutDateField.addEventListener('change', function () {--%>
<%--            if (checkInDateField.value && this.value < checkInDateField.value) {--%>
<%--// Có thể hiển thị thông báo lỗi rõ ràng hơn--%>
<%--                alert("Ngày trả phòng không được trước ngày nhận phòng.");--%>
<%--                this.value = checkInDateField.value;--%>
<%--            }--%>
<%--        });--%>

<%--        const today = new Date().toISOString().split('T')[0];--%>
<%--        if (!checkInDateField.value || checkInDateField.value < today) {--%>
<%--            if (checkInDateField.type === 'date') {--%>
<%--                checkInDateField.min = today;--%>
<%--                if (!checkInDateField.value) checkInDateField.value = today;--%>
<%--            }--%>
<%--        }--%>
<%--        if (checkOutDateField.type === 'date' && checkInDateField.value) {--%>
<%--            checkOutDateField.min = checkInDateField.value;--%>
<%--            if (!checkOutDateField.value || checkOutDateField.value < checkInDateField.value) {--%>
<%--                let nextDay = new Date(checkInDateField.value);--%>
<%--                nextDay.setDate(nextDay.getDate() + 1);--%>
<%--                checkOutDateField.value = nextDay.toISOString().split('T')[0];--%>
<%--            }--%>
<%--        }--%>
<%--    }--%>
<%--}--%>
<%--</script>--%>