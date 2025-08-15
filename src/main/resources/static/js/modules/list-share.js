class ListShareManager {
    constructor() {
        console.log('ListShareManager: Constructor called');
        this.listId = this.getListIdFromUrl();
        console.log('ListShareManager: List ID from URL:', this.listId);
        this.init();
    }

    init() {
        this.setupEventListeners();
    }

    getListIdFromUrl() {
        const urlParts = window.location.pathname.split('/');
        return urlParts[2];
    }

    setupEventListeners() {
        console.log('ListShareManager: Setting up event listeners');
        
        const shareForm = document.getElementById('shareForm');
        if (shareForm) {
            console.log('ListShareManager: Found share form, adding submit listener');
            shareForm.addEventListener('submit', (e) => this.handleShareSubmit(e));
        } else {
            console.log('ListShareManager: Share form not found');
        }

        document.addEventListener('click', (e) => {
            if (e.target.matches('[data-action="revoke"]')) {
                console.log('ListShareManager: Revoke button clicked');
                this.handleRevokeShare(e.target);
            }
        });
    }

    async handleShareSubmit(event) {
        event.preventDefault();
        console.log('ListShareManager: Form submission started');

        const form = event.target;
        
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton.disabled) {
            console.log('ListShareManager: Form already submitting, ignoring');
            return;
        }

        submitButton.disabled = true;
        const originalText = submitButton.textContent;
        submitButton.textContent = 'Sharing...';

        const formData = new FormData(form);

        try {
            const shareData = {
                shoppingListId: parseInt(this.listId),
                sharedToEmail: formData.get('userEmail'),
                permission: formData.get('permission'),
                expirationDate: formData.get('expirationDate') || null
            };

            console.log('ListShareManager: Share data:', shareData);
            console.log('ListShareManager: window.app:', window.app);
            console.log('ListShareManager: window.app.makeRequest:', window.app?.makeRequest);

            const response = await window.app.makeRequest('/api/list-shares', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(shareData)
            });

            console.log('ListShareManager: Response received:', response);
            console.log('ListShareManager: Response status:', response.status);
            console.log('ListShareManager: Response ok:', response.ok);

            if (response.ok) {
                window.app.showAlert('List shared successfully!', 'success');
                form.reset();
                setTimeout(() => window.location.reload(), 1000);
            } else {
                const error = await response.json();
                console.log('ListShareManager: Backend error response:', error);
                const errorMessage = error.message || error.error || 'Unknown error';
                window.app.showAlert(`Failed to share list: ${errorMessage}`, 'danger');
            }
        } catch (error) {
            console.error('Error sharing list:', error);
            if (error.response && typeof error.response.json === 'function') {
                try {
                    const errorData = await error.response.json();
                    const errorMessage = errorData.message || errorData.error || 'Unknown error';
                    window.app.showAlert(`Failed to share list: ${errorMessage}`, 'danger');
                } catch (jsonError) {
                    window.app.showAlert('Failed to share list. Please try again.', 'danger');
                }
            } else {
                window.app.showAlert('Failed to share list. Please try again.', 'danger');
            }
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        }
    }

    async handleRevokeShare(button) {
        const shareId = button.getAttribute('data-id');
        console.log('ListShareManager: Revoking share with ID:', shareId);

        if (!confirm('Are you sure you want to revoke this share?')) {
            return;
        }

        try {
            console.log('ListShareManager: Making revoke request to:', `/api/list-shares/${shareId}/revoke`);
            const response = await window.app.makeRequest(`/api/list-shares/${shareId}/revoke`, {
                method: 'POST'
            });

            console.log('ListShareManager: Revoke response:', response);
            console.log('ListShareManager: Response status:', response.status);
            console.log('ListShareManager: Response ok:', response.ok);

            if (response.ok) {
                window.app.showAlert('Share revoked successfully!', 'success');
                button.closest('.d-flex').remove();
            } else {
                const error = await response.json();
                console.log('ListShareManager: Revoke error response:', error);
                window.app.showAlert(`Failed to revoke share: ${error.message || 'Unknown error'}`, 'danger');
            }
        } catch (error) {
            console.error('Error revoking share:', error);
            window.app.showAlert('Failed to revoke share. Please try again.', 'danger');
        }
    }
}

function initializeListShareManager() {
    if (document.querySelector('.card-header h5')?.textContent === 'Share with User') {
        console.log('ListShareManager: Found share page, checking app availability...');
        console.log('window.app:', window.app);
        console.log('window.app.modules:', window.app?.modules);
        
        if (window.app && window.app.modules) {
            console.log('ListShareManager: App is ready, initializing...');
            window.listShareManager = new ListShareManager();
        } else {
            console.log('ListShareManager: App not ready, retrying in 100ms...');
            setTimeout(initializeListShareManager, 100);
        }
    }
}

document.addEventListener('DOMContentLoaded', initializeListShareManager);