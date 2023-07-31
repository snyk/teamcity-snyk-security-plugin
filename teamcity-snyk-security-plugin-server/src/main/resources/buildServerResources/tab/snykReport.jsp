<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty error}">
  <c:out value="${error}"/>: <b><c:out value="${startPage}"/></b>. <!-- Probably need to replace startPage with something else -->
</c:if>

<c:if test="${empty error}">
  <div>
      ${snykHtmlReportContent}
  </div>
</c:if>
