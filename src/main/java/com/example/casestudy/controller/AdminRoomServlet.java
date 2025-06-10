package com.example.casestudy.controller;

import com.example.casestudy.Service.BookingServiceImpl;
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
        BookingServiceImpl tempBookingService = new BookingServiceImpl();
        RoomServiceImpl tempRoomService = new RoomServiceImpl();

        tempBookingService.setRoomService(tempRoomService);
        tempRoomService.setBookingService(tempBookingService);

        this.roomService = tempRoomService;

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

        try {
            if ("insert".equals(action)) {
                processInsertRoom(request, response);
            } else if ("update".equals(action)) {
                processUpdateRoom(request, response); // Gọi phương thức xử lý update
            } else {
                System.out.println("AdminRoomServlet - doPost - Invalid action: " + action); // Log action không hợp lệ
                response.sendRedirect(request.getContextPath() + "/admin/rooms?error=invalidAction");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = "Lỗi cơ sở dữ liệu: " + e.getMessage();
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate entry")) {
                if (e.getMessage().toLowerCase().contains("room_number")) { // Giả sử có constraint tên 'room_number_unique' hoặc cột 'room_number'
                    errorMessage = "Số phòng '" + request.getParameter("roomNumber") + "' đã tồn tại.";
                } else {
                    errorMessage = "Dữ liệu bị trùng lặp.";
                }
            }

            Room roomForForm = new Room();
            if ("update".equals(action) && request.getParameter("id") != null) {
                try {
                    roomForForm.setRoomId(Integer.parseInt(request.getParameter("id")));
                } catch (NumberFormatException nfe) {  }
            }
            roomForForm.setRoomNumber(request.getParameter("roomNumber"));
            roomForForm.setRoomType(request.getParameter("roomType"));
            roomForForm.setDescription(request.getParameter("description"));
            try {
                if (request.getParameter("capacity") != null && !request.getParameter("capacity").isEmpty()) {
                    roomForForm.setCapacity(Integer.parseInt(request.getParameter("capacity")));
                }
                if (request.getParameter("pricePerNight") != null && !request.getParameter("pricePerNight").isEmpty()) {
                    roomForForm.setPricePerNight(new BigDecimal(request.getParameter("pricePerNight").replace(",", "")));
                }
            } catch (NumberFormatException nfe) {  }
            roomForForm.setAvailable("on".equals(request.getParameter("isAvailable")));

            request.setAttribute("errorMessage", errorMessage);
            loadFormAttributesAndForward(request, response, roomForForm, action != null ? action : "list"); // action có thể null nếu vào đây từ exception khác

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra: " + e.getMessage());
            try {
                listRoomsAndShowError(request, response, "Đã có lỗi không mong muốn xảy ra.");
            } catch (SQLException exSQL) {
                throw new ServletException("Lỗi nghiêm trọng khi xử lý request và cả khi hiển thị danh sách phòng.", exSQL);
            }
        }
    }
    private void listRooms(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        System.out.println("AdminRoomServlet - listRooms - Calling roomService.getAllRooms()");
        List<Room> listRoom = roomService.getAllRooms();
        System.out.println("AdminRoomServlet - listRooms - Number of rooms retrieved: " + (listRoom != null ? listRoom.size() : "null"));
        if (listRoom != null && !listRoom.isEmpty()) {
            for (Room r : listRoom) {
                System.out.println("AdminRoomServlet - listRooms - Room ID: " + r.getRoomId() + ", isAvailable (for display): " + r.isAvailable());
            }
        }
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

    private void processInsertRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException, ServletException {

        System.out.println("AdminRoomServlet - processInsertRoom - Called.");

        String roomNumber = request.getParameter("roomNumber");
        String pricePerNightStr = request.getParameter("pricePerNight");
        String roomType = request.getParameter("roomType");
        String description = request.getParameter("description");
        String isAvailableStr = request.getParameter("isAvailable");
        boolean isAvailable = "on".equals(isAvailableStr);
        String capacityStr = request.getParameter("capacity");

        String imageUrl = request.getParameter("imageUrl");
        System.out.println("AdminRoomServlet - processInsertRoom - Image URL from form: [" + imageUrl + "]");

        Room newRoom = new Room();
        newRoom.setAvailable(isAvailable);
        String relativeImagePath = null;


        newRoom.setImageUrl(relativeImagePath);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            newRoom.setImageUrl(imageUrl.trim());
        } else {
            newRoom.setImageUrl(null);
        }


        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Số phòng không được để trống.");
            newRoom.setRoomType(roomType);
            newRoom.setDescription(description);
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }
        newRoom.setRoomNumber(roomNumber);

        if (roomType == null || roomType.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Kiểu phòng không được để trống.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }
        newRoom.setRoomType(roomType);

        if (roomService.checkRoomNumberExists(newRoom.getRoomNumber())) {
            System.out.println("AdminRoomServlet - processInsertRoom - Room number already exists: " + newRoom.getRoomNumber());
            request.setAttribute("errorMessage", "Số phòng '" + newRoom.getRoomNumber() + "' đã tồn tại. Vui lòng chọn số phòng khác.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }

        if (capacityStr == null || capacityStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Sức chứa không được để trống.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                request.setAttribute("errorMessage", "Sức chứa phải là số dương.");
                newRoom.setCapacity(0);
                loadFormAttributesAndForward(request, response, newRoom, "insert");
                return;
            }
            newRoom.setCapacity(capacity);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Sức chứa không hợp lệ.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }

        newRoom.setDescription(description);

        BigDecimal pricePerNight;
        if (pricePerNightStr == null || pricePerNightStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không được để trống.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }
        try {
            pricePerNight = new BigDecimal(pricePerNightStr.replace(",", ""));
            if (pricePerNight.compareTo(BigDecimal.ZERO) < 0) {
                request.setAttribute("errorMessage", "Giá mỗi đêm không được là số âm.");
                newRoom.setPricePerNight(pricePerNight);
                loadFormAttributesAndForward(request, response, newRoom, "insert");
                return;
            }
            newRoom.setPricePerNight(pricePerNight);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không hợp lệ. Vui lòng nhập số.");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
            return;
        }

        System.out.println("AdminRoomServlet - processInsertRoom - Room to add: " + newRoom.getRoomNumber() + ", Type=" + newRoom.getRoomType() +
                ", ImageURL=[" + newRoom.getImageUrl() + "]" );
        boolean success = roomService.addRoom(newRoom);

        if (success) {
            System.out.println("AdminRoomServlet - processInsertRoom - Add success. Redirecting...");
            response.sendRedirect(request.getContextPath() + "/admin/rooms?successMessage=added");
        } else {
            System.out.println("AdminRoomServlet - processInsertRoom - Add failed.");
            request.setAttribute("errorMessage", "Thêm phòng thất bại. Cơ sở dữ liệu không có thay đổi hoặc số phòng đã tồn tại (nếu service không ném lỗi).");
            loadFormAttributesAndForward(request, response, newRoom, "insert");
        }
    }

    private void processUpdateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException, ServletException {

        System.out.println("AdminRoomServlet - processUpdateRoom - Called.");
        int id ;
        Room roomToUpdate;


        try {
            id = Integer.parseInt(request.getParameter("id"));
            roomToUpdate = roomService.getRoomById(id);
            if (roomToUpdate == null) {
                System.out.println("AdminRoomServlet - processUpdateRoom - Room not found for update: " + id);
                response.sendRedirect(request.getContextPath() + "/admin/rooms?error=notFoundOnUpdate");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("AdminRoomServlet - processUpdateRoom - Invalid ID for update: " + request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/admin/rooms?error=invalidIdOnUpdate");
            return;
        }
        String roomNumber = request.getParameter("roomNumber");
        String pricePerNightStr = request.getParameter("pricePerNight");
        String roomType = request.getParameter("roomType");
        String description = request.getParameter("description");
        String isAvailableStr = request.getParameter("isAvailable");
        boolean isAvailable = "on".equals(isAvailableStr);
        String capacityStr = request.getParameter("capacity");
        String newImageUrl = request.getParameter("imageUrl");
        System.out.println("AdminRoomServlet - processUpdateRoom - New Image URL from form: [" + newImageUrl + "]");

        if (newImageUrl != null && !newImageUrl.trim().isEmpty()) {
            roomToUpdate.setImageUrl(newImageUrl.trim());
        } else {
            roomToUpdate.setImageUrl(null);
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Số phòng không được để trống.");

            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }
        roomToUpdate.setRoomNumber(roomNumber);

        if (roomType == null || roomType.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Kiểu phòng không được để trống.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }
        roomToUpdate.setRoomType(roomType);

        if (capacityStr == null || capacityStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Sức chứa không được để trống.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                request.setAttribute("errorMessage", "Sức chứa phải là số dương.");
                loadFormAttributesAndForward(request, response, roomToUpdate, "update");
                return;
            }
            roomToUpdate.setCapacity(capacity);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Sức chứa không hợp lệ.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }

        roomToUpdate.setDescription(description);
        roomToUpdate.setAvailable(isAvailable);

        BigDecimal pricePerNight;
        if (pricePerNightStr == null || pricePerNightStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không được để trống.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }
        try {
            pricePerNight = new BigDecimal(pricePerNightStr.replace(",", ""));
            if (pricePerNight.compareTo(BigDecimal.ZERO) < 0) {
                request.setAttribute("errorMessage", "Giá mỗi đêm không được là số âm.");
                roomToUpdate.setPricePerNight(pricePerNight);
                loadFormAttributesAndForward(request, response, roomToUpdate, "update");
                return;
            }
            roomToUpdate.setPricePerNight(pricePerNight);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Giá mỗi đêm không hợp lệ. Vui lòng nhập số.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
            return;
        }


        System.out.println("AdminRoomServlet - processUpdateRoom - Room to update: " +
                "Number=" + roomToUpdate.getRoomNumber() +
                ", New ImageURL=[" + roomToUpdate.getImageUrl() + "]");
        boolean success = roomService.updateRoom(roomToUpdate);

        if (success) {
            System.out.println("AdminRoomServlet - processUpdateRoom - Update success. Redirecting...");
            response.sendRedirect(request.getContextPath() + "/admin/rooms?successMessage=updated");
        } else {
            System.out.println("AdminRoomServlet - processUpdateRoom - Update failed.");
            request.setAttribute("errorMessage", "Cập nhật phòng thất bại. Cơ sở dữ liệu không có thay đổi hoặc số phòng mới đã tồn tại.");
            loadFormAttributesAndForward(request, response, roomToUpdate, "update");
        }
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