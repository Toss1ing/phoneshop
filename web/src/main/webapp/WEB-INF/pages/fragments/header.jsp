<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<nav class="navbar navbar-expand-lg bg-light fixed-top border border-secondary">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/productList">
            <h2>
                Phonify
            </h2>
        </a>
        <div class="collapse navbar-collapse justify-content-end" id="navbarContent">
            <ul class="navbar-nav">

                <li class="nav-item">
                    <a class="nav-link position-relative" href="${pageContext.request.contextPath}/cart">
                        Cart:
                        <span id="cart-total-quantity" class="fw-bold">0</span> items —
                        $<span id="cart-total-price" class="fw-bold">0.00</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/util.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/cart.js?v=1.0"></script>

