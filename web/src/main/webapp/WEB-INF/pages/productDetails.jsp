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

    <div class="d-flex align-items-left mt-4">
        <a href="/phoneshop-web/productList" class="btn btn-outline-secondary mb-3">Back to product list</a>
    </div>
    <div class="row">
        <div class="col-md-5">
            <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}"
                 alt="${phone.model}" class="img-fluid rounded mb-3"/>
            <p>${phone.description}</p>

            <h4>Price: <fmt:formatNumber value="${phone.price}" type="number" minFractionDigits="2"/> $</h4>

                <input type="hidden" name="phone-id" value="${phone.id}"/>
                <div class="d-flex mb-3">
                        <input class="form-control form-control-sm text-center quantity-input"
                               name="quantity_${phone.id}" style="width: 100px" value="1">
                    <button class="btn btn-outline-primary btn-sm add-to-cart-btn ms-2" data-phone-id="${phone.id}">
                        Add to Cart
                    </button>
                </div>
        </div>

        <div class="col-md-7">
            <h5>Display</h5>
            <table class="table table-sm text-start">
                <tr>
                    <th width="400px">Size</th>
                    <td>${phone.displaySizeInches}"</td>
                </tr>
                <tr>
                    <th>Resolution</th>
                    <td>${phone.displayResolution}</td>
                </tr>
                <tr>
                    <th>Technology</th>
                    <td>${phone.displayTechnology}</td>
                </tr>
                <tr>
                    <th>Pixel density</th>
                    <td>${phone.pixelDensity}</td>
                </tr>
            </table>

            <h5>Dimensions & weight</h5>
            <table class="table table-sm">
                <tr>
                    <th width="400px">Length</th>
                    <td>${phone.lengthMm} mm</td>
                </tr>
                <tr>
                    <th>Width</th>
                    <td>${phone.widthMm} mm</td>
                </tr>
                <tr>
                    <th>Weight</th>
                    <td>${phone.weightGr} g</td>
                </tr>
            </table>

            <h5>Camera</h5>
            <table class="table table-sm">
                <tr>
                    <th width="400px">Front</th>
                    <td>${phone.frontCameraMegapixels} MP</td>
                </tr>
                <tr>
                    <th>Back</th>
                    <td>${phone.backCameraMegapixels} MP</td>
                </tr>
            </table>

            <h5>Battery</h5>
            <table class="table table-sm">
                <tr>
                    <th width="400px">Talk time</th>
                    <td>${phone.talkTimeHours}</td>
                </tr>
                <tr>
                    <th>Stand by time</th>
                    <td>${phone.standByTimeHours}</td>
                </tr>
                <tr>
                    <th>Battery capacity</th>
                    <td>${phone.batteryCapacityMah} mAh</td>
                </tr>
            </table>

            <h5>Other</h5>
            <table class="table table-sm">
                <tr>
                    <th width="400px">Colors</th>
                    <td>
                        <c:forEach var="color" items="${phone.colors}" varStatus="status">
                            ${color.code}<c:if test="${!status.last}">, </c:if>
                        </c:forEach>
                    </td>
                </tr>

                <tr>
                    <th>Device type</th>
                    <td>${phone.deviceType}</td>
                </tr>
                <tr>
                    <th>Bluetooth</th>
                    <td>${phone.bluetooth}</td>
                </tr>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
