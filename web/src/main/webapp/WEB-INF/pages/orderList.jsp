<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>

<body class="bg-light">
<jsp:include page="/WEB-INF/pages/fragments/header.jsp"/>

<div class="container mt-5 pt-3">
    <div class="d-flex align-items-left mt-4 mb-2">
        <h3 class="mb-4">Orders</h3>
    </div>

    <c:if test="${empty orders}">
        <div class="d-flex justify-content-center align-items-center" style="height: 50px;">
            <h4 class="text-muted">No orders found</h4>
        </div>
    </c:if>

    <c:if test="${not empty orders}">
        <table class="table table-bordered table-striped align-middle table-sm">
            <thead>
            <tr class="text-center">
                <th>Order number</th>
                <th>Customer</th>
                <th>Phone</th>
                <th>Address</th>
                <th>Total Price</th>
                <th>Status</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="order" items="${orders}">
                <tr class="text-center">
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/orders/${order.id}">
                                ${order.id}
                        </a>
                    </td>
                    <td>${order.firstName} ${order.lastName}</td>
                    <td>${order.contactPhoneNo}</td>
                    <td>${order.deliveryAddress}</td>
                    <td><fmt:formatNumber value="${order.totalPrice}" type="number" minFractionDigits="2"/></td>
                    <td>
                        <span class="badge bg-secondary">${order.status}</span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="d-flex justify-content-center mt-4">
            <ul class="pagination">

                <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                    <a class="page-link"
                       href="?page=${currentPage - 1}&size=${pageSize}">
                        <
                    </a>
                </li>

                <c:set var="maxPagesToShow" value="10"/>
                <c:set var="startPage" value="${currentPage - (maxPagesToShow / 2)}"/>
                <c:if test="${startPage < 0}">
                    <c:set var="startPage" value="0"/>
                </c:if>
                <c:set var="endPage" value="${startPage + maxPagesToShow - 1}"/>
                <c:if test="${endPage >= totalPages}">
                    <c:set var="endPage" value="${totalPages - 1}"/>
                    <c:set var="startPage" value="${endPage - maxPagesToShow + 1}"/>
                    <c:if test="${startPage < 0}">
                        <c:set var="startPage" value="0"/>
                    </c:if>
                </c:if>

                <c:if test="${startPage > 0}">
                    <li class="page-item">
                        <a class="page-link" href="?page=0&size=${pageSize}">1</a>
                    </li>
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                </c:if>

                <c:forEach begin="${startPage}" end="${endPage}" var="pageIndex">
                    <li class="page-item ${pageIndex == currentPage ? 'active' : ''}">
                        <a class="page-link" href="?page=${pageIndex}&size=${pageSize}">
                                ${pageIndex + 1}
                        </a>
                    </li>
                </c:forEach>

                <c:if test="${endPage < totalPages - 1}">
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                    <li class="page-item">
                        <a class="page-link" href="?page=${totalPages - 1}&size=${pageSize}">
                                ${totalPages}
                        </a>
                    </li>
                </c:if>

                <li class="page-item ${currentPage + 1 >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage + 1}&size=${pageSize}">
                        >
                    </a>
                </li>

            </ul>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
