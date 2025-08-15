class ItemManager {
    constructor(app) {
        this.app = app;
    }

    async addToList(listId, data) {
        try {
            console.log('ItemManager.addToList called with:', { listId, data });
            const response = await this.app.modules.apiClient.addItemToList(listId, data);
            console.log('ItemManager.addToList response:', response);
            if (response && response.id) {
                this.app.modules.uiManager.showAlert('Item added successfully!', 'success');
                return response;
            }
        } catch (error) {
            console.error('Error adding item:', error);
            this.app.modules.uiManager.showAlert('Error adding item', 'danger');
        }
    }

    async toggleStatus(itemId) {
        try {
            console.log('toggleItemStatus called with ID:', itemId);
            const response = await this.app.modules.apiClient.toggleItemStatus(itemId);
            if (response && response.id) {
                this.updateItemUI(itemId, response);
                this.app.modules.uiManager.showAlert('Item status updated!', 'success');
                return response;
            }
        } catch (error) {
            console.error('Error toggling item status:', error);
            this.app.modules.uiManager.showAlert('Error updating item status', 'danger');
        }
    }

    updateItemUI(itemId, itemData) {
        let itemElement = document.querySelector(`[data-item-id="${itemId}"]`);

        if (!itemElement) {
            const toggleButton = document.querySelector(`button[onclick*="toggleItemStatus(${itemId})"]`);
            if (toggleButton) {
                itemElement = toggleButton.closest('.d-flex, .card, .item-row, .list-group-item');
            }
        }

        if (!itemElement) {
            const allButtons = document.querySelectorAll('button[onclick*="toggleItemStatus"]');
            for (const button of allButtons) {
                if (button.onclick && button.onclick.toString().includes(itemId.toString())) {
                    itemElement = button.closest('.d-flex, .card, .item-row, .list-group-item');
                    break;
                }
            }
        }

        if (itemElement) {
            console.log('Found item element:', itemElement);
            console.log('Item data:', itemData);

            const completeButton = itemElement.querySelector('button[onclick*="toggleItemStatus"]');
            if (completeButton) {
                if (itemData.isPurchased) {
                    completeButton.innerHTML = '<i class="bi bi-arrow-counterclockwise"></i> Undo';
                    completeButton.className = 'btn btn-warning btn-sm';
                } else {
                    completeButton.innerHTML = '<i class="bi bi-check"></i> Complete';
                    completeButton.className = 'btn btn-success btn-sm';
                }
                console.log('Updated button state to:', itemData.isPurchased ? 'purchased' : 'unpurchased');
            }

            const checkbox = itemElement.querySelector('input[type="checkbox"]');
            if (checkbox) {
                checkbox.checked = itemData.isPurchased;
            }

            if (itemData.isPurchased) {
                itemElement.classList.add('purchased', 'bg-light');
                const itemName = itemElement.querySelector('.fw-bold, h6, .item-name');
                if (itemName) {
                    itemName.style.textDecoration = 'line-through';
                    itemName.style.opacity = '0.7';
                }
            } else {
                itemElement.classList.remove('purchased', 'bg-light');
                const itemName = itemElement.querySelector('.fw-bold, h6, .item-name');
                if (itemName) {
                    itemName.style.textDecoration = 'none';
                    itemName.style.opacity = '1';
                }
            }

            const statusBadge = itemElement.querySelector('.badge.bg-success');
            if (statusBadge && statusBadge.textContent === 'Purchased') {
                statusBadge.style.display = itemData.isPurchased ? 'inline' : 'none';
            }
        } else {
            console.warn('Could not find item element for ID:', itemId);
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }
    }

    async delete(itemId) {
        try {
            const response = await this.app.modules.apiClient.deleteItem(itemId);
            if (response !== undefined) {
                this.app.modules.uiManager.showAlert('Item deleted successfully!', 'success');
                const element = document.querySelector(`[data-item-id="${itemId}"]`) ||
                    document.querySelector(`button[onclick*="deleteItem(${itemId})"]`)?.closest('.d-flex, .card, .item-row, .list-group-item');
                if (element) {
                    element.remove();
                }
            }
        } catch (error) {
            console.error('Error deleting item:', error);
            this.app.modules.uiManager.showAlert('Error deleting item', 'danger');
        }
    }

    showEditModal(id) {
        console.log('Showing edit modal for item:', id);
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = ItemManager;
}

if (typeof window !== 'undefined') {
    window.ItemManager = ItemManager;
}