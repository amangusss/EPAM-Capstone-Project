class UIManager {
    constructor(app) {
        this.app = app;
    }

    showAlert(message, type = 'info') {
        const existingAlerts = document.querySelectorAll('.alert');
        existingAlerts.forEach(alert => alert.remove());

        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const container = document.querySelector('.container');
        if (container) {
            container.insertBefore(alertDiv, container.firstChild);
        }

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }

    handleModalShow(event) {
        const modal = event.target;
        const form = modal.querySelector('form');

        if (form) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.dataset.originalText = submitBtn.innerHTML;
            }
        }
    }

    handleModalHidden(event) {
        const modal = event.target;
        const form = modal.querySelector('form');

        if (form) {
            form.reset();

            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = submitBtn.dataset.originalText || 'Submit';
            }
        }
    }

    showPrompt(message, defaultValue = '') {
        return prompt(message, defaultValue);
    }

}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = UIManager;
}

if (typeof window !== 'undefined') {
    window.UIManager = UIManager;
}