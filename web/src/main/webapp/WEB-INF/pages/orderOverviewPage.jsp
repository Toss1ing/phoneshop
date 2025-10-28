<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Order Overview</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/pages/fragments/header.jsp"/>

<div class="container mt-5 pt-3">
    <div class="d-flex align-items-left mt-4 mb-2">
        <h3 class="mb-4">Thanks for your order</h3>
    </div>

    <div class="d-flex justify-content-between mb-4">
        <a href="/phoneshop-web/productList" class="btn btn-outline-secondary">Back to products</a>
        <span class="text-muted">Order secure-ID: <strong>${order.secureId}</strong></span>
    </div>

    <div class="table-responsive">
        <table class="table table-bordered table-striped text-center mb-0">
            <thead>
            <tr>
                <th>Brand</th>
                <th>Model</th>
                <th>Color</th>
                <th>Display size</th>
                <th>Quantity</th>
                <th>Price</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${order.orderItems}">
                <tr>
                    <td>${item.phone.brand}</td>
                    <td>${item.phone.model}</td>
                    <td>
                        <c:forEach var="color" items="${item.phone.colors}" varStatus="loop">
                            ${color.code}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>${item.phone.displaySizeInches}"</td>
                    <td>${item.quantity}</td>
                    <td><fmt:formatNumber value="${item.phone.price}" type="number" minFractionDigits="2"/>$</td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot class="fw-bold">
            <tr class="table-light">
                <td colspan="4"></td>
                <td class="text-end fw-bold">Subtotal</td>
                <td><fmt:formatNumber value="${order.subtotal}" type="number" minFractionDigits="2"/>$</td>
            </tr>
            <tr>
                <td colspan="4"></td>
                <td class="text-end fw-bold">Delivery</td>
                <td><fmt:formatNumber value="${order.deliveryPrice}" type="number" minFractionDigits="2"/>$</td>
            </tr>
            <tr class="table-light">
                <td colspan="4"></td>
                <td class="text-end fw-bold">TOTAL</td>
                <td><fmt:formatNumber value="${order.totalPrice}" type="number" minFractionDigits="2"/>$</td>
            </tr>
            </tfoot>
        </table>
    </div>

    <div class="card mt-4 shadow-sm">
        <div class="card-body">
            <h5 class="card-title mb-3">Delivery details</h5>
            <p><strong>First name:</strong> ${order.firstName}</p>
            <p><strong>Last name:</strong> ${order.lastName}</p>
            <p><strong>Address:</strong> ${order.deliveryAddress}</p>
            <p><strong>Phone:</strong> ${order.contactPhoneNo}</p>
            <p><strong>Additional information</strong> ${order.additionalInformation}</p>
        </div>
    </div>
</div>

</body>
</html>
