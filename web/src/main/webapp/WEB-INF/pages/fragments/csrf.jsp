<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${_csrf != null}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</c:if>
