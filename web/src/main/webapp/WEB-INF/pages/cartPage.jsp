<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <title>Cart</title>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/pages/fragments/header.jsp"/>

<div class="container mt-5 pt-3">
    <div class="d-flex align-items-left mt-4 mb-2">
        <h3 class="mb-4">Cart</h3>
    </div>

    <a href="/phoneshop-web/productList" class="btn btn-outline-secondary mb-4">Back to product list</a>

    <c:if test="${empty cartView.cart.items}">
        <div class="text-center text-muted">Your cart is empty</div>
    </c:if>

    <c:if test="${not empty deleteError}">
        <div class="text-center text-danger">${deleteError}</div>
    </c:if>

    <c:if test="${not empty cartView.cart.items}">
        <%--@elvariable id="cartView" type="com.es.core.dto.cart.CartView"--%>
        <form:form modelAttribute="cartView" method="post" action="/phoneshop-web/cart">
            <input type="hidden" name="_method" value="put"/>

            <table class="table table-bordered table-striped align-middle table-sm">
                <thead>
                <tr class="text-center">
                    <th>Image</th>
                    <th>Brand</th>
                    <th>Model</th>
                    <th>Price</th>
                    <th>Colors</th>
                    <th>Quantity</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cartView.cart.items}">
                    <tr class="text-center">
                        <td style="width: 120px;">
                            <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${item.phone.imageUrl}"
                                 alt="${item.phone.model}" class="img-fluid rounded" style="max-height: 100px;">
                        </td>
                        <td>${item.phone.brand}</td>
                        <td>${item.phone.model}</td>
                        <td><fmt:formatNumber value="${item.phone.price}" type="number" minFractionDigits="2"/></td>
                        <td>
                            <c:forEach var="color" items="${item.phone.colors}" varStatus="loop">
                                ${color.code}<c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </td>
                        <td style="width: 130px;">
                            <c:set var="phoneId" value="${item.phone.id}"/>
                            <form:input path="items[${phoneId}]" cssClass="form-control form-control-sm text-center"/>
                            <form:errors path="items[${phoneId}]" cssClass="text-danger small"/>
                        </td>
                        <td style="width: 100px;">
                            <button type="submit"
                                    class="btn btn-sm btn-outline-danger"
                                    form="deleteCartItemForm"
                                    formaction="/phoneshop-web/cart/${item.phone.id}"
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="d-flex justify-content-end gap-2 mt-3 mb-4">
                <button type="submit" class="btn btn-outline-primary">Update</button>
                <a href="/phoneshop-web/order" class="btn btn-outline-success">Order</a>
            </div>
        </form:form>

        <form id="deleteCartItemForm" method="post">
            <input type="hidden" name="_method" value="delete"/>
        </form>
    </c:if>
</div>
</body>
</html>
