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
            (form.action.includes('/items') && !form.action.includes('/budgets/create') && !form.action.includes('/categories/create') && !form.action.includes('/shopping-lists/create')) ||
            (form.action.includes('/lists/') && form.action.includes('/items')) ||
            form.action.includes('/toggle') ||
            form.action.includes('/api/') ||
            (form.action.includes('/budgets/edit') || form.action.includes('/budgets/update')) ||
            (form.action.includes('/categories/edit') || form.action.includes('/categories/update')) ||
            (form.action.includes('/shopping-lists/edit') || form.action.includes('/shopping-lists/update'))) {
            console.log('Skipping form with custom handler, edit form, or item form:', form.id || form.action);
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
            form.querySelectorAll('.is-invalid').forEach(field => {
                field.classList.remove('is-invalid');
            });
            form.querySelectorAll('.invalid-feedback').forEach(errorDiv => {
                errorDiv.remove();
            });
            
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

            let responseData;
            if (response.ok) {
                try {
                    responseData = await response.json();
                } catch (e) {
                    responseData = { success: true, message: 'Operation completed successfully' };
                }
            } else {
                responseData = { success: false, message: 'Request failed' };
            }

            console.log('Response data:', responseData);

            if (responseData.success) {
                this.app.modules.uiManager.showAlert(responseData.message || 'Operation completed successfully!', 'success');

                if (responseData.redirect) {
                    window.location.href = responseData.redirect;
                } else if (responseData.reload) {
                    window.location.reload();
                } else {
                    if (url.includes('/items') || url.includes('/budgets/create') || url.includes('/categories/create') || url.includes('/shopping-lists/create')) {
                        window.location.reload();
                    }
                }
            } else {
                if (responseData.fieldErrors) {
                    form.querySelectorAll('.is-invalid').forEach(field => {
                        field.classList.remove('is-invalid');
                    });
                    
                    Object.entries(responseData.fieldErrors).forEach(([fieldName, errorMessage]) => {
                        const field = form.querySelector(`[name="${fieldName}"]`);
                        if (field) {
                            field.classList.add('is-invalid');
                            let errorDiv = field.parentNode.querySelector('.invalid-feedback');
                            if (!errorDiv) {
                                errorDiv = document.createElement('div');
                                errorDiv.className = 'invalid-feedback';
                                field.parentNode.appendChild(errorDiv);
                            }
                            errorDiv.textContent = errorMessage;
                        }
                    });
                    
                    this.app.modules.uiManager.showAlert('Please correct the errors below', 'danger');
                } else {
                    this.app.modules.uiManager.showAlert(responseData.message || 'An error occurred', 'danger');
                }
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