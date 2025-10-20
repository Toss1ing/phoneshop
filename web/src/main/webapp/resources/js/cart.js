$(document).ready(function () {
    const ajaxUrl = '/phoneshop-web/ajaxCart';
    const totalQuantityElement = '#cart-total-quantity'
    const totalPriceElement = '#cart-total-price'
    const addToCardButton = '.add-to-cart-btn'
    const phoneIdElement = 'phone-id'

    const eventType = 'click'

    const alertSuccessMessage = 'Product added to cart'
    const alertDangerMessage = 'Please enter a valid numeric quantity!'

    const alertSuccessType = 'success'
    const alertDangerType = 'danger'

    const getQuantityInputSelector = (phoneId) => `input[name="quantity_${phoneId}"]`;

    $.get(ajaxUrl)
        .done(function (cart) {
            $(totalQuantityElement).text(cart.totalQuantity);
            $(totalPriceElement).text(cart.totalPrice.toFixed(2));
        });

    $(document).on(eventType, addToCardButton, function () {
        let phoneId = $(this).data(phoneIdElement);
        let inputVal = $(getQuantityInputSelector(phoneId)).val();

        let quantity;

        if (/^\s*-?\d+\s*$/.test(inputVal)) {
            quantity = parseInt(inputVal, 10);
        } else {
            showAlert(alertDangerMessage, alertDangerType);
            return;
        }

        let requestData = {
            phoneId: phoneId,
            quantity: quantity
        };

        $.ajax({
            url: ajaxUrl,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestData),
            success: function (cart) {
                $(totalQuantityElement).text(cart.totalQuantity);
                $(totalPriceElement).text(cart.totalPrice.toFixed(2));
                $(getQuantityInputSelector(phoneId)).val(1);
                showAlert(alertSuccessMessage, alertSuccessType);
            },
            error: function (xhr) {
                showAlert(xhr.responseText, alertDangerType);
            }
        });
    });
});
