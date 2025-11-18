<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Mass Add</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/pages/fragments/header.jsp"/>

<div class="container mt-5 pt-3">

    <c:if test="${not empty errorMassAdd}">
        <div class="text-center text-danger">${errorMassAdd}</div>
    </c:if>

    <div class="d-flex align-items-left mt-4 mb-2">
        <h3 class="mb-4">Mass add to cart</h3>
    </div>

    <div class="d-flex justify-content-between mb-4">
        <a href="/phoneshop-web/productList" class="btn btn-outline-secondary">Back to products</a>
    </div>


    <%--@elvariable id="massAddToCart" type="com.es.core.dto.cart.MassAddToCart"--%>
    <form:form method="post" modelAttribute="massAddToCart">
        <jsp:include page="/WEB-INF/pages/fragments/csrf.jsp"/>
        <table class="table table-bordered table-striped align-middle table-sm">
            <thead class="table-light">
            <tr class="text-center">
                <th>#</th>
                <th>Product Code</th>
                <th>Quantity</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach var="i" begin="0" end="7">
                <tr class="text-center">
                    <td><strong>${i + 1}</strong></td>

                    <td>
                        <form:input path="productModels[${i}]" cssClass="form-control form-control-sm text-center"/>
                        <form:errors path="productModels[${i}]" cssClass="text-danger small"/>
                    </td>
                    <td>
                        <form:input path="quantities[${i}]" cssClass="form-control form-control-sm text-center"/>
                        <form:errors path="quantities[${i}]" cssClass="text-danger small"/>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>

        <button type="submit" class="btn btn-primary">Add to cart</button>
    </form:form>


</div>

</body>
</html>
