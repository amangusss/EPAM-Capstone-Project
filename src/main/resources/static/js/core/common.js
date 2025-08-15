document.addEventListener('DOMContentLoaded', function() {

    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
});

function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

function makeRequest(url, method, data) {
    const headers = {
        'Content-Type': 'application/json'
    };

    const csrfToken = getCsrfToken();
    if (csrfToken) {
        headers[getCsrfHeader()] = csrfToken;
    }

    return fetch(url, {
        method: method,
        headers: headers,
        body: data ? JSON.stringify(data) : null
    });
}

function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}