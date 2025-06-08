package com.example.casestudy.controller; // Thay đổi package cho phù hợp

import com.example.casestudy.Service.BookingService;
import com.example.casestudy.Service.BookingServiceImpl;
import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Booking;
import com.example.casestudy.model.Room;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "AdminBookingServlet", urlPatterns = "/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    private BookingService bookingService;
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        bookingService = new BookingServiceImpl();
        roomService = new RoomServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Hành động mặc định là hiển thị danh sách
        }

        try {
            switch (action) {
                case "new":
                    showNewBookingForm(request, response);
                    break;
                case "edit":
                    showEditBookingForm(request, response);
                    break;
                case "delete":
                    deleteBooking(request, response);
                    break;
                default:
                    listBookings(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/error_admin.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings"); // Nếu không có action, quay lại danh sách
            return;
        }
        request.setCharacterEncoding("UTF-8");

        try {
            switch (action) {
                case "insert":
                    insertBooking(request, response);
                    break;
                case "update":
                    updateBooking(request, response);
                    break;
                case "delete": // Có thể xử lý delete ở đây nếu dùng POST
                    deleteBooking(request, response);
                    break;
                default:
                    listBookings(request, response);
                    break;
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý đặt phòng: " + e.getMessage());
            try {
                if ("insert".equals(action) || "update".equals(action)) {

                    loadFormData(request);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
                } else {
                    listBookings(request, response);
                }
            } catch (SQLException | ServletException | IOException ex) {
                ex.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi nghiêm trọng khi xử lý yêu cầu.");
            }
        }
    }

    private void listBookings(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Booking> listBooking = bookingService.getAllBookings();
        request.setAttribute("listBooking", listBooking);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_list_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void loadFormData(HttpServletRequest request) throws SQLException {
        List<Room> listRoom = roomService.getAllRooms();
        request.setAttribute("listRoom", listRoom);

    }


    private void showNewBookingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        loadFormData(request);
        request.setAttribute("booking", new Booking());
        request.setAttribute("formAction", "insert");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditBookingForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Booking existingBooking = bookingService.getBookingById(id);
        if (existingBooking == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=notFound");
            return;
        }
        loadFormData(request);
        request.setAttribute("booking", existingBooking);
        request.setAttribute("formAction", "update");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void insertBooking(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ParseException, ServletException { // Thêm ServletException
        // Lấy các tham số
        String roomIdStr = request.getParameter("roomId");
        String checkInDateStr = request.getParameter("checkInDate");
        String checkOutDateStr = request.getParameter("checkOutDate");
        String totalPriceStr = request.getParameter("totalPrice"); // Lấy dưới dạng String trước
        String status = request.getParameter("status");
        String guestName = request.getParameter("guestName");
        String guestEmail = request.getParameter("guestEmail");

        BigDecimal totalPrice = null;
        if (totalPriceStr != null && !totalPriceStr.trim().isEmpty()) {
            try {
                totalPrice = new BigDecimal(totalPriceStr.trim());
                if (totalPrice.compareTo(BigDecimal.ZERO) < 0) { // Kiểm tra nếu giá trị âm (tùy chọn)
                    request.setAttribute("errorMessage", "Tổng tiền không thể là số âm.");
                    loadFormData(request); // Load lại dữ liệu cho form
                    // Đặt lại các giá trị người dùng đã nhập để họ không phải nhập lại
                    request.setAttribute("submittedRoomId", roomIdStr);
                    request.setAttribute("submittedCheckInDate", checkInDateStr);
                    request.setAttribute("submittedCheckOutDate", checkOutDateStr);
                    // ... các trường khác ...
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng Tổng Tiền không hợp lệ. Vui lòng nhập số.");
                loadFormData(request);
                request.setAttribute("submittedRoomId", roomIdStr);
                request.setAttribute("submittedCheckInDate", checkInDateStr);
                request.setAttribute("submittedCheckOutDate", checkOutDateStr);
                // ...
                request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
                return; // Dừng thực thi nếu lỗi
            }
        } else {
            // Xử lý trường hợp totalPrice rỗng hoặc null
            // Nếu totalPrice là bắt buộc:
            request.setAttribute("errorMessage", "Vui lòng nhập Tổng Tiền.");
            loadFormData(request);
            request.setAttribute("submittedRoomId", roomIdStr);
            request.setAttribute("submittedCheckInDate", checkInDateStr);
            request.setAttribute("submittedCheckOutDate", checkOutDateStr);
            // ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return; // Dừng thực thi nếu lỗi
            // Hoặc nếu bạn muốn đặt giá trị mặc định là 0 nếu rỗng:
            // totalPrice = BigDecimal.ZERO;
        }
        // === KẾT THÚC XỬ LÝ totalPrice ===

        // Validate các trường khác (roomId, dates, guestName...) tương tự nếu cần
        // Ví dụ: roomId
        int roomId;
        try {
            roomId = Integer.parseInt(roomIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Mã phòng không hợp lệ.");
            loadFormData(request);
            // ... đặt lại các giá trị đã nhập ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return;
        }

        // Validate dates
        if (checkInDateStr == null || checkInDateStr.trim().isEmpty() ||
                checkOutDateStr == null || checkOutDateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Ngày nhận và trả phòng là bắt buộc.");
            loadFormData(request);
            // ... đặt lại các giá trị đã nhập ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate;
        Date checkOutDate;
        try {
            checkInDate = (Date) dateFormat.parse(checkInDateStr);
            checkOutDate = (Date) dateFormat.parse(checkOutDateStr);
        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ (yyyy-MM-dd).");
            loadFormData(request);
            // ... đặt lại các giá trị đã nhập ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return;
        }

        // Kiểm tra checkOutDate phải sau checkInDate
        if (!checkOutDate.after(checkInDate)) {
            request.setAttribute("errorMessage", "Ngày trả phòng phải sau ngày nhận phòng.");
            loadFormData(request);
            // ... đặt lại các giá trị đã nhập ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return;
        }


        Booking newBooking = new Booking();

        Room room = roomService.getRoomById(roomId); // Lấy thông tin phòng đầy đủ
        if (room == null) {
            request.setAttribute("errorMessage", "Phòng không tồn tại.");
            loadFormData(request);
            // ... đặt lại các giá trị đã nhập ...
            request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
            return;
        }
        newBooking.setRoom(room);

        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);
        newBooking.setTotalPrice(totalPrice); // totalPrice đã được validate
        newBooking.setStatus(status != null ? status : "PENDING"); // Giá trị mặc định cho status
        newBooking.setGuestName(guestName);
        newBooking.setGuestEmail(guestEmail);


        // TODO: Kiểm tra phòng có sẵn trong khoảng ngày đã chọn không
        // TODO: Tính toán totalPrice chính xác dựa trên số đêm và giá phòng nếu totalPrice không được nhập thủ công

        bookingService.createBooking(newBooking); // Giả sử tên phương thức là createBooking
        response.sendRedirect(request.getContextPath() + "/admin/bookings?success=add");
    }

    private void updateBooking(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ParseException {
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        int roomId = Integer.parseInt(request.getParameter("roomId"));
        String checkInDateStr = request.getParameter("checkInDate");
        String checkOutDateStr = request.getParameter("checkOutDate");
         BigDecimal totalPrice = new BigDecimal(request.getParameter("totalPrice"));
         String status = request.getParameter("status");
         String guestName = request.getParameter("guestName");
         String guestEmail = request.getParameter("guestEmail");


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate = (Date) dateFormat.parse(checkInDateStr);
        Date checkOutDate = (Date) dateFormat.parse(checkOutDateStr);

        Booking bookingToUpdate = bookingService.getBookingById(bookingId);
        if (bookingToUpdate == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=updateNotFound");
            return;
        }

        Room room = new Room();
        room.setRoomId(roomId);
        bookingToUpdate.setRoom(room);


        bookingToUpdate.setCheckInDate(checkInDate);
        bookingToUpdate.setCheckOutDate(checkOutDate);



        // TODO: Validate dữ liệu
        // TODO: Tính toán lại totalPrice nếu cần

        bookingService.updateBooking(bookingToUpdate);
        response.sendRedirect(request.getContextPath() + "/admin/bookings?success=update");
    }

    private void deleteBooking(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            bookingService.deleteBookingById(id);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=delete");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể xóa đặt phòng này. Lỗi: " + e.getMessage());
            listBookings(request, response);
        }
    }
}