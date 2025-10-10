<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html>
<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <title>Phonify</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/pages/fragments/header.jsp"/>

<div class="container mt-5 pt-3">
    <div class="d-flex justify-content-between align-items-center mt-4 mb-3">

        <h3 class="mb-0">Phones</h3>
        <form class="d-flex" action="" method="get">
            <label>
                <input class="form-control" type="search" placeholder="Search..." name="search" value="${param.search}">
            </label>
            <input type="hidden" name="sortField" value="${param.sortField}"/>
            <input type="hidden" name="sortOrder" value="${param.sortOrder}"/>
            <button class="btn btn-outline-primary ms-2" type="submit">Search</button>
        </form>
    </div>
    <c:if test="${empty phones}">
        <div class="d-flex justify-content-center align-items-center" style="height: 50px;">
            <h4 class="text-muted">Телефоны не найдены</h4>
        </div>
    </c:if>

    <c:if test="${not empty phones}">
        <table class="table table-bordered table-striped align-middle table-sm">
            <thead>
            <tr class="text-center">
                <th>Image</th>
                <th>
                    Brand

                    <tags:sortLink sortField="brand" sortOrder="asc" label="↑"/>
                    <tags:sortLink sortField="brand" sortOrder="desc" label="↓"/>
                </th>
                <th>
                    Model
                    <tags:sortLink sortField="model" sortOrder="asc" label="↑"/>
                    <tags:sortLink sortField="model" sortOrder="desc" label="↓"/>
                </th>
                <th>
                    Price
                    <tags:sortLink sortField="price" sortOrder="asc" label="↑"/>
                    <tags:sortLink sortField="price" sortOrder="desc" label="↓"/>
                </th>
                <th>Colors</th>
                <th>
                    Display Size
                    <tags:sortLink sortField="displaySizeInches" sortOrder="asc" label="↑"/>
                    <tags:sortLink sortField="displaySizeInches" sortOrder="desc" label="↓"/>
                </th>
                <th>Quantity</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="phone" items="${phones}">
                <tr class="text-center">
                    <td style="width: 120px;">
                        <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}"
                             alt="${phone.model}" class="img-fluid rounded" style="max-height: 100px;">
                    </td>
                    <td>${phone.brand}</td>
                    <td>${phone.model}</td>
                    <td>$ <fmt:formatNumber value="${phone.price}" type="number" minFractionDigits="2"/></td>
                    <td>
                        <c:forEach var="color" items="${phone.colors}" varStatus="loop">
                            ${color.code}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>${phone.displaySizeInches}"</td>
                    <td style="width: 100px;">
                        <input class="form-control form-control-sm text-center quantity-input"
                               name="quantity_${phone.id}" value="1">
                    </td>
                    <td style="width: 130px;">
                        <button class="btn btn-outline-primary btn-sm add-to-cart-btn" data-phone-id="${phone.id}">
                            Add to Cart
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="d-flex justify-content-center mt-4">
            <nav aria-label="Phone pagination">
                <ul class="pagination">

                    <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                        <a class="page-link"
                           href="?page=${currentPage - 1}&size=${pageSize}&sortField=${param.sortField}&sortOrder=${param.sortOrder}&search=${param.search}"
                           aria-label="Previous">
                            <;
                        </a>
                    </li>

                    <c:set var="maxPagesToShow" value="10"/>
                    <c:set var="startPage" value="${currentPage - maxPagesToShow / 2}"/>
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
                            <a class="page-link"
                               href="?page=0&size=${pageSize}&sortField=${param.sortField}&sortOrder=${param.sortOrder}&search=${param.search}">1</a>
                        </li>
                        <li class="page-item disabled"><span class="page-link">...</span></li>
                    </c:if>

                    <c:forEach begin="${startPage}" end="${endPage}" var="pageIndex">
                        <li class="page-item ${pageIndex == currentPage ? 'active' : ''}">
                            <a class="page-link"
                               href="?page=${pageIndex}&size=${pageSize}&sortField=${param.sortField}&sortOrder=${param.sortOrder}&search=${param.search}">
                                    ${pageIndex + 1}
                            </a>
                        </li>
                    </c:forEach>

                    <c:if test="${endPage < totalPages - 1}">
                        <li class="page-item disabled"><span class="page-link">...</span></li>
                        <li class="page-item">
                            <a class="page-link"
                               href="?page=${totalPages - 1}&size=${pageSize}&sortField=${param.sortField}&sortOrder=${param.sortOrder}&search=${param.search}">
                                    ${totalPages}
                            </a>
                        </li>
                    </c:if>

                    <li class="page-item ${currentPage + 1 >= totalPages ? 'disabled' : ''}">
                        <a class="page-link"
                           href="?page=${currentPage + 1}&size=${pageSize}&sortField=${param.sortField}&sortOrder=${param.sortOrder}&search=${param.search}"
                           aria-label="Next">
                            >;
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
