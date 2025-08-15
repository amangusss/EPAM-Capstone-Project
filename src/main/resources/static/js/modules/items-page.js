class ItemsPageManager {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupFilters();
        console.log('Items Page Manager initialized');
    }

    setupEventListeners() {
        const addItemForm = document.getElementById('addItemForm');
        if (addItemForm) {
            addItemForm.addEventListener('submit', (e) => this.handleAddItem(e));
        }

        const editItemForm = document.getElementById('editItemForm');
        if (editItemForm) {
            editItemForm.addEventListener('submit', (e) => this.handleEditItem(e));
        }

        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.addEventListener('change', (e) => this.handleFilter());
        }

        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => this.handleFilter());
        }
    }

    setupFilters() {
        this.currentFilters = {
            search: '',
            category: '',
            status: ''
        };
    }

    ensureAppReady() {
        return new Promise((resolve, reject) => {
            if (window.app && window.app.modules) {
                resolve(window.app);
                return;
            }

            if (typeof window.ShoppingListApp !== 'undefined') {
                window.app = new window.ShoppingListApp();
                console.log('App initialized:', window.app);

                setTimeout(() => {
                    if (window.app && window.app.modules) {
                        resolve(window.app);
                    } else {
                        reject(new Error('Failed to initialize app modules'));
                    }
                }, 100);
            } else {
                reject(new Error('ShoppingListApp not found'));
            }
        });
    }

    showAlert(message, type = 'info') {
        if (window.app && window.app.modules && window.app.modules.uiManager) {
            window.app.modules.uiManager.showAlert(message, type);
        } else {
            alert(message);
        }
    }

    async handleAddItem(event) {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);

        try {
            const app = await this.ensureAppReady();

            const data = {
                name: formData.get('name'),
                description: formData.get('description'),
                quantity: parseInt(formData.get('quantity')) || 1,
                estimatedPrice: parseFloat(formData.get('estimatedPrice')) || null,
                categoryId: formData.get('categoryId') || null,
                priority: formData.get('priority') || 'MEDIUM',
                shoppingListId: parseInt(formData.get('shoppingListId'))
            };

            const response = await app.modules.items.addToList(data.shoppingListId, data);
            if (response) {
                this.showAlert('Item added successfully!', 'success');
                form.reset();

                const modal = bootstrap.Modal.getInstance(document.getElementById('addItemModal'));
                if (modal) {
                    modal.hide();
                }

                window.location.reload();
            }
        } catch (error) {
            console.error('Error adding item:', error);
            this.showAlert('Error adding item: ' + error.message, 'danger');
        }
    }

    async handleEditItem(event) {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);
        const itemId = formData.get('id');

        try {
            const app = await this.ensureAppReady();

            const data = {
                name: formData.get('name'),
                description: formData.get('description'),
                quantity: parseInt(formData.get('quantity')) || 1,
                estimatedPrice: parseFloat(formData.get('estimatedPrice')) || null,
                actualPrice: parseFloat(formData.get('actualPrice')) || null,
                categoryId: formData.get('categoryId') || null,
                priority: formData.get('priority') || 'MEDIUM'
            };

            const response = await app.modules.apiClient.makeRequest(`/api/items/${itemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response && response.success !== false) {
                this.showAlert('Item updated successfully!', 'success');

                const modal = bootstrap.Modal.getInstance(document.getElementById('editItemModal'));
                if (modal) {
                    modal.hide();
                }

                window.location.reload();
            }
        } catch (error) {
            console.error('Error updating item:', error);
            this.showAlert('Error updating item: ' + error.message, 'danger');
        }
    }

    handleSearch(searchTerm) {
        this.currentFilters.search = searchTerm.toLowerCase();
        this.applyFilters();
    }

    handleFilter() {
        const categoryFilter = document.getElementById('categoryFilter');
        const statusFilter = document.getElementById('statusFilter');

        this.currentFilters.category = categoryFilter.value;
        this.currentFilters.status = statusFilter.value;

        this.applyFilters();
    }

    applyFilters() {
        const itemCards = document.querySelectorAll('[data-item-id]');

        itemCards.forEach(card => {
            let shouldShow = true;

            if (this.currentFilters.search) {
                const itemName = card.querySelector('.card-title').textContent.toLowerCase();
                const itemDescription = card.querySelector('p')?.textContent.toLowerCase() || '';
                const itemCategory = card.querySelector('.badge.bg-secondary')?.textContent.toLowerCase() || '';

                if (!itemName.includes(this.currentFilters.search) &&
                    !itemDescription.includes(this.currentFilters.search) &&
                    !itemCategory.includes(this.currentFilters.search)) {
                    shouldShow = false;
                }
            }

            if (this.currentFilters.category) {
                const itemCategory = card.querySelector('.badge.bg-secondary')?.textContent || '';
                if (itemCategory !== this.currentFilters.category) {
                    shouldShow = false;
                }
            }

            if (this.currentFilters.status) {
                const isPurchased = card.classList.contains('border-success');
                if (this.currentFilters.status === 'purchased' && !isPurchased) {
                    shouldShow = false;
                } else if (this.currentFilters.status === 'unpurchased' && isPurchased) {
                    shouldShow = false;
                }
            }

            card.style.display = shouldShow ? 'block' : 'none';
        });

        this.updateFilteredCount();
    }

    updateFilteredCount() {
        const visibleItems = document.querySelectorAll('[data-item-id]:not([style*="display: none"])').length;
        const totalItems = document.querySelectorAll('[data-item-id]').length;

        const countElement = document.querySelector('.action-bar p');
        if (countElement) {
            countElement.innerHTML = `Total: <span>${visibleItems}</span> of ${totalItems} items`;
        }
    }

    clearFilters() {
        const searchInput = document.getElementById('searchInput');
        const categoryFilter = document.getElementById('categoryFilter');
        const statusFilter = document.getElementById('statusFilter');

        if (searchInput) searchInput.value = '';
        if (categoryFilter) categoryFilter.value = '';
        if (statusFilter) statusFilter.value = '';

        this.currentFilters = {
            search: '',
            category: '',
            status: ''
        };

        const itemCards = document.querySelectorAll('[data-item-id]');
        itemCards.forEach(card => {
            card.style.display = 'block';
        });

        this.updateFilteredCount();
    }

    editItem(itemId) {
        const itemCard = document.querySelector(`[data-item-id="${itemId}"]`);
        if (!itemCard) return;

        const name = itemCard.querySelector('.card-title').textContent;
        const description = itemCard.querySelector('p')?.textContent || '';
        const quantity = itemCard.querySelector('.badge.bg-info')?.textContent.replace('Qty: ', '') || '1';
        const estimatedPrice = itemCard.querySelector('.text-center .col-6:first-child .fw-bold')?.textContent.replace('$', '') || '';
        const actualPrice = itemCard.querySelector('.text-center .col-6:last-child .fw-bold')?.textContent.replace('$', '') || '';
        const category = itemCard.querySelector('.badge.bg-secondary')?.textContent || '';
        const priority = itemCard.querySelector('.badge.bg-warning')?.textContent || 'MEDIUM';

        const editForm = document.getElementById('editItemForm');
        if (editForm) {
            editForm.querySelector('#editItemId').value = itemId;
            editForm.querySelector('#editItemName').value = name;
            editForm.querySelector('#editItemDescription').value = description;
            editForm.querySelector('#editItemQuantity').value = quantity;
            editForm.querySelector('#editItemEstimatedPrice').value = estimatedPrice;
            editForm.querySelector('#editItemActualPrice').value = actualPrice;
            editForm.querySelector('#editItemPriority').value = priority;

            const categorySelect = editForm.querySelector('#editItemCategory');
            if (categorySelect) {
                Array.from(categorySelect.options).forEach(option => {
                    option.selected = option.textContent === category;
                });
            }
        }

        const editModal = new bootstrap.Modal(document.getElementById('editItemModal'));
        editModal.show();
    }

    async toggleItemStatus(itemId) {
        try {
            const app = await this.ensureAppReady();

            const response = await app.modules.items.toggleStatus(itemId);
            if (response) {
                this.showAlert('Item status updated successfully!', 'success');
                window.location.reload();
            }
        } catch (error) {
            console.error('Error toggling item status:', error);
            this.showAlert('Error updating item status: ' + error.message, 'danger');
        }
    }

    async deleteItem(itemId) {
        if (!confirm('Are you sure you want to delete this item?')) {
            return;
        }

        try {
            const app = await this.ensureAppReady();

            const response = await app.modules.items.delete(itemId);
            if (response !== undefined) {
                this.showAlert('Item deleted successfully!', 'success');
                window.location.reload();
            }
        } catch (error) {
            console.error('Error deleting item:', error);
            this.showAlert('Error deleting item: ' + error.message, 'danger');
        }
    }
}

window.editItem = function(itemId) {
    if (window.itemsPageManager) {
        window.itemsPageManager.editItem(itemId);
    }
};

window.toggleItemStatus = function(itemId) {
    if (window.itemsPageManager) {
        window.itemsPageManager.toggleItemStatus(itemId);
    }
};

window.deleteItem = function(itemId) {
    if (window.itemsPageManager) {
        window.itemsPageManager.deleteItem(itemId);
    }
};

window.clearFilters = function() {
    if (window.itemsPageManager) {
        window.itemsPageManager.clearFilters();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    window.itemsPageManager = new ItemsPageManager();
});