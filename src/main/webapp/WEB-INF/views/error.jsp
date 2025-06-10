<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Đã xảy ra lỗi" scope="request"/>
<jsp:include page="../../WEB-INF/jsp/common/header.jsp"/>

<div class="text-center py-5">
  <i class="bi bi-emoji-frown-fill text-danger display-1 mb-3"></i>
  <h1 class="display-5">Rất tiếc! Đã có lỗi xảy ra.</h1>

  <p class="lead">
    <c:choose>
      <c:when test="${not empty errorMessage}">
        ${errorMessage}
      </c:when>
      <c:when test="${not empty requestScope['javax.servlet.error.message']}">
        Lỗi: ${requestScope['javax.servlet.error.message']}
      </c:when>
      <c:otherwise>
        Một lỗi không mong muốn đã xảy ra. Vui lòng thử lại sau.
      </c:otherwise>
    </c:choose>
  </p>
  <hr class="my-4">
  <p>Nếu vấn đề vẫn tiếp diễn, vui lòng liên hệ với bộ phận hỗ trợ của chúng tôi.</p>
  <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">
    <i class="bi bi-house-door"></i> Về trang chủ
  </a>
</div>

    <c:if test="${pageContext.request.serverName == 'localhost'}">
        <hr/>
        <div class="mt-4 alert alert-secondary">
            <h5>Thông tin gỡ lỗi (Developer):</h5>
            <c:if test="${not empty requestScope['javax.servlet.error.status_code']}">
                <p><strong>Status Code:</strong> ${requestScope['javax.servlet.error.status_code']}</p>
            </c:if>
            <c:if test="${not empty requestScope['javax.servlet.error.exception_type']}">
                <p><strong>Exception Type:</strong> ${requestScope['javax.servlet.error.exception_type']}</p>
            </c:if>
            <c:if test="${not empty requestScope['javax.servlet.error.exception']}">
                <p><strong>Exception:</strong> ${requestScope['javax.servlet.error.exception']}</p>
                <pre style="white-space: pre-wrap; font-size: 0.8em;">${requestScope['javax.servlet.error.exception'].printStackTrace(pageContext.out)}</pre>
            </c:if>
        </div>
    </c:if>

<jsp:include page="../../WEB-INF/jsp/common/footer.jsp"/>