package com.example.casestudy.controller;

import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Room;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminRoomServlet", urlPatterns = "/admin/rooms")
public class AdminRoomServlet extends HttpServlet {
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        roomService = new RoomServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteRoom(request, response);
                    break;
                default:
                    listRooms(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi với cơ sở dữ liệu: " + e.getMessage());
            try {
                listRoomsAndShowError(request, response, "Đã xảy ra lỗi với cơ sở dữ liệu khi thực hiện hành động.");
            } catch (Exception ex) {
                throw new ServletException("Lỗi nghiêm trọng khi xử lý request và cả khi hiển thị danh sách phòng.", ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi không xác định: " + e.getMessage(), e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (!"insert".equals(action) && !"update".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/rooms?error=invalidAction");
            return;
        }

        // Lấy các tham số từ request
        String roomNumber = request.getParameter("roomNumber");
        String pricePerNightStr = request.getParameter("pricePerNight");
        String roomType = request.getParameter("roomType"); // Thêm trường này nếu có
        String description = request.getParameter("description");
        String isAvailableStr = request.getParameter("isAvailable");
        boolean isAvailable = "on".equals(isAvailableStr);

        Room room = new Room();

        if ("update".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                room = roomService.getRoomById(id);
                if (room == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/rooms?error=notFoundOnUpdate");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?error=invalidIdOnUpdate");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    listRoomsAndShowError(request, response, "Lỗi cơ sở dữ liệu khi lấy phòng để cập nhật.");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
        }


        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setDescription(description);

        room.setAvailable(isAvailable);


        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Số phòng không được để trống.");
            loadFormAttributesAndForward(request, response, room, action);
            return;
        }

        if (roomType == null || roomType.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Kiểu phòng không được để trống.");
            loadFormAttributesAndForward(request, response, room, action);
            return;
        }

        BigDecimal pricePerNight;
        if (pricePerNightStr == null || pricePerNightStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không được để trống.");
            loadFormAttributesAndForward(request, response, room, action);
            return;
        }
        try {
            pricePerNight = new BigDecimal(pricePerNightStr.replace(",", "")); // Loại bỏ dấu phẩy nếu có
            if (pricePerNight.compareTo(BigDecimal.ZERO) < 0) {
                request.setAttribute("errorMessage", "Giá mỗi đêm không được là số âm.");
                loadFormAttributesAndForward(request, response, room, action);
                return;
            }
            room.setPricePerNight(pricePerNight);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không hợp lệ. Vui lòng nhập số.");
            if (!"update".equals(action)) room.setPricePerNight(null);
            loadFormAttributesAndForward(request, response, room, action);
            return;
        }


        try {
            boolean success;
            String successActionMessage = "";
            String successRedirectParam = "";

            if ("insert".equals(action)) {
                success = roomService.addRoom(room);
                successActionMessage = "Thêm phòng";
                successRedirectParam = "added";
            } else {
                success = roomService.updateRoom(room);
                successActionMessage = "Cập nhật phòng";
                successRedirectParam = "updated";
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?successMessage=" + successRedirectParam);
            } else {
                request.setAttribute("errorMessage", successActionMessage + " thất bại. Cơ sở dữ liệu không có thay đổi.");
                loadFormAttributesAndForward(request, response, room, action);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String specificErrorMessage = "Lỗi cơ sở dữ liệu: ";
            if (e.getMessage().toLowerCase().contains("duplicate entry")) {
                if (e.getMessage().toLowerCase().contains("room_number_unique") || e.getMessage().toLowerCase().contains("'rooms.room_number'")) { // Tên constraint/cột tùy DB
                    specificErrorMessage += "Số phòng '" + room.getRoomNumber() + "' đã tồn tại.";
                } else {
                    specificErrorMessage += "Dữ liệu bị trùng lặp.";
                }
            } else {
                specificErrorMessage += e.getMessage();
            }
            request.setAttribute("errorMessage", specificErrorMessage);
            loadFormAttributesAndForward(request, response, room, action);
        }
    }





    private void listRooms(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        List<Room> listRoom = roomService.getAllRooms();
        request.setAttribute("listRoom", listRoom);

        String successMessageKey = request.getParameter("successMessage");
        if (successMessageKey != null) {
            if ("added".equals(successMessageKey)) {
                request.setAttribute("successAlert", "Phòng đã được thêm thành công!");
            } else if ("updated".equals(successMessageKey)) {
                request.setAttribute("successAlert", "Phòng đã được cập nhật thành công!");
            } else if ("deleted".equals(successMessageKey)) {
                request.setAttribute("successAlert", "Phòng đã được xóa thành công!");
            }
        }
        String errorMessageKey = request.getParameter("error");
        if (errorMessageKey != null) {
            if ("notFoundOnUpdate".equals(errorMessageKey)) {
                request.setAttribute("errorAlert", "Lỗi: Không tìm thấy phòng để cập nhật.");
            } else if ("invalidIdOnUpdate".equals(errorMessageKey)) {
                request.setAttribute("errorAlert", "Lỗi: ID phòng không hợp lệ khi cập nhật.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin/room_list_admin.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Room newRoom = new Room();
        newRoom.setAvailable(true);
        request.setAttribute("room", newRoom);
        request.setAttribute("formAction", "insert");
        request.getRequestDispatcher("/WEB-INF/jsp/admin/room_form_admin.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Room existingRoom = roomService.getRoomById(id);
            if (existingRoom == null) {
                listRoomsAndShowError(request, response, "Không tìm thấy phòng với ID=" + id + " để chỉnh sửa.");
                return;
            }
            request.setAttribute("room", existingRoom);
            request.setAttribute("formAction", "update");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/room_form_admin.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            listRoomsAndShowError(request, response, "ID phòng không hợp lệ để chỉnh sửa.");
        }
    }

    private void insertRoom(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String roomNumber = request.getParameter("roomNumber");
        BigDecimal price = new BigDecimal(request.getParameter("pricePerNight"));
        String description = request.getParameter("description");
        boolean isAvailable = "on".equalsIgnoreCase(request.getParameter("isAvailable"));


        Room newRoom = new Room();
        newRoom.setRoomNumber(roomNumber);
        newRoom.setPricePerNight(price);
        newRoom.setDescription(description);
        newRoom.setAvailable(isAvailable);

        roomService.addRoom(newRoom);
        response.sendRedirect(request.getContextPath() + "/admin/rooms?success=add");
    }

    private void updateRoom(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        String roomNuber = request.getParameter("roomNumber");
        BigDecimal price = new BigDecimal(request.getParameter("pricePerNight"));
        String description = request.getParameter("description");
        boolean isAvailable = "on".equalsIgnoreCase(request.getParameter("isAvailable"));

        Room room = roomService.getRoomById(id);
        if (room != null) {
            room.setRoomNumber(roomNuber);
            room.setPricePerNight(price);
            room.setDescription(description);
            room.setAvailable(isAvailable);
            roomService.updateRoom(room);
        }
        response.sendRedirect(request.getContextPath() + "/admin/rooms?success=update");
    }

    private void deleteRoom(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean deleted = roomService.deleteRoom(id);
            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?successMessage=deleted");
            } else {
                listRoomsAndShowError(request, response, "Không thể xóa phòng. Có thể phòng không tồn tại hoặc có ràng buộc dữ liệu.");
            }
        } catch (NumberFormatException e) {
            listRoomsAndShowError(request, response, "ID phòng không hợp lệ để xóa.");
        } catch (SQLException e) {
            e.printStackTrace();
            String deleteErrorMessage = "Lỗi cơ sở dữ liệu khi xóa phòng: ";
            if (e.getMessage().toLowerCase().contains("foreign key constraint fails")) {
                deleteErrorMessage += "Không thể xóa phòng này vì nó đang được tham chiếu bởi dữ liệu khác (ví dụ: đơn đặt phòng).";
            } else {
                deleteErrorMessage += e.getMessage();
            }
            listRoomsAndShowError(request, response, deleteErrorMessage);
        }
    }

    private void loadFormAttributesAndForward(HttpServletRequest request, HttpServletResponse response, Room room, String formAction) throws ServletException, IOException {
        request.setAttribute("room", room);
        request.setAttribute("formAction", formAction);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/room_form_admin.jsp").forward(request, response);
    }

    private void listRoomsAndShowError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException, SQLException {
        List<Room> listRoom = roomService.getAllRooms();
        request.setAttribute("listRoom", listRoom);
        request.setAttribute("errorAlert", errorMessage);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/room_list_admin.jsp").forward(request, response);
    }
}