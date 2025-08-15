class FormHandler {
    constructor(app) {
        this.app = app;
    }

    async handleSubmit(event) {
        const form = event.target;
        console.log('Form submit handler called for:', form);
        console.log('Form action:', form.action);
        console.log('Form method:', form.method);

        if (form.id === 'editBudgetForm' || form.id === 'editCategoryForm' || form.id === 'editItemForm' ||
            form.id === 'editShoppingListForm' ||
            form.action.includes('/items') ||
            (form.action.includes('/lists/') && form.action.includes('/items')) ||
            form.action.includes('/toggle') ||
            form.action.includes('/api/') ||
            form.action.includes('/budgets') ||
            form.action.includes('/categories') ||
            form.action.includes('/shopping-lists')) {
            console.log('Skipping form with custom handler or item form:', form.id || form.action);
            event.preventDefault();
            return false;
        }

        if (form.dataset.submitting === 'true') {
            event.preventDefault();
            console.log('Form submission prevented - already submitting');
            return false;
        }

        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn ? submitBtn.innerHTML : '';
        console.log('Submit button found:', submitBtn);
        console.log('Original button text:', originalText);

        form.dataset.submitting = 'true';
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = 'â³';
        }

        try {
            const formData = new FormData(form);
            const url = form.action;
            const method = form.method || 'POST';

            console.log('Form data entries:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }

            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            if (!isValid) {
                throw new Error('Please fill all required fields');
            }

            console.log('Sending form data to:', url);
            
            let requestOptions = { method: method };
            
            let requestUrl = url;
            
            if (method.toUpperCase() === 'GET') {
                const params = new URLSearchParams();
                for (let [key, value] of formData.entries()) {
                    params.append(key, value);
                }
                requestUrl = url + (url.includes('?') ? '&' : '?') + params.toString();
            } else {
                requestOptions.body = formData;
            }
            const response = await this.app.modules.apiClient.makeRequest(requestUrl, requestOptions);

            console.log('Response received:', response);

            if (response.success) {
                this.app.modules.uiManager.showAlert(response.message || 'Operation completed successfully!', 'success');

                if (response.redirect) {
                    window.location.href = response.redirect;
                } else if (response.reload) {
                    window.location.reload();
                } else {
                    if (url.includes('/items')) {
                        window.location.reload();
                    }
                }
            } else {
                this.app.modules.uiManager.showAlert(response.message || 'An error occurred', 'danger');
            }
        } catch (error) {
            console.error('Form submission error:', error);
            this.app.modules.uiManager.showAlert(error.message || 'Error submitting form', 'danger');
        } finally {
            form.dataset.submitting = 'false';
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
            }
        }
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = FormHandler;
}

if (typeof window !== 'undefined') {
    window.FormHandler = FormHandler;
}