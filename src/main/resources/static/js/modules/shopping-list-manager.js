class ShoppingListManager {
    constructor(app) {
        this.app = app;
    }

    async create(data) {
        try {
            const response = await this.app.modules.apiClient.createShoppingList(data);
            if (response && response.id) {
                this.app.modules.uiManager.showAlert('Shopping list created successfully!', 'success');
                return response;
            }
        } catch (error) {
            console.error('Error creating shopping list:', error);
            this.app.modules.uiManager.showAlert('Error creating shopping list', 'danger');
        }
    }

    async delete(id) {
        try {
            console.log('ShoppingListManager.delete called with ID:', id, 'Type:', typeof id);
            const response = await this.app.modules.apiClient.deleteShoppingList(id);
            if (response !== undefined) {
                this.app.modules.uiManager.showAlert('Shopping list deleted successfully!', 'success');
                const element = document.querySelector(`[data-list-id="${id}"]`);
                if (element) {
                    element.remove();
                }
            }
        } catch (error) {
            console.error('Error deleting shopping list:', error);
            this.app.modules.uiManager.showAlert('Error deleting shopping list', 'danger');
        }
    }

    async share(id) {
        console.log('Sharing shopping list:', id);
    }

    showEditModal(id) {
        console.log('Showing edit modal for shopping list:', id);
    }

    showShareModal(id) {
        console.log('Showing share modal for shopping list:', id);
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = ShoppingListManager;
}

if (typeof window !== 'undefined') {
    window.ShoppingListManager = ShoppingListManager;
}