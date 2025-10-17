function showAlert(message, type = 'danger', duration = 2000) {
    let container = $('#alert-container');

    if (container.length === 0) {
        $('body').append(`
            <div id="alert-container" class="d-flex position-fixed top-0 start-50 translate-middle-x mt-3 justify-content-center" style="z-index: 1050;"></div>
        `);
        container = $('#alert-container');
    }

    container.empty();

    const alertMessage = $(`
        <div class="alert alert-${type} alert-dismissible fade show d-flex justify-content-center align-items-center" role="alert">
            <span>${message}</span>
        </div>
    `);

    container.append(alertMessage);

    setTimeout(() => {
        alertMessage.alert('close');
    }, duration);
}
