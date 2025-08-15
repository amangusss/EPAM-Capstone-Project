class ItemsManager {
    constructor() {
        this.currentListId = this.getListIdFromUrl();
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupDragAndDrop();
        this.setupRealTimeUpdates();
        console.log('Items Manager initialized for list:', this.currentListId);
    }

    getListIdFromUrl() {
        const match = window.location.pathname.match(/\/lists\/(\d+)/);
        return match ? match[1] : null;
    }

    setupEventListeners() {
        const addItemForm = document.querySelector('#addItemForm');
        if (addItemForm) {
            addItemForm.addEventListener('submit', (e) => this.handleAddItem(e));
        }

        document.addEventListener('change', (e) => {
            if (e.target.matches('[data-item-checkbox]')) {
                this.handleItemToggle(e.target);
            }
        });

        document.addEventListener('click', (e) => {
            if (e.target.matches('[data-action="edit-item"]')) {
                this.handleEditItem(e.target);
            }
            if (e.target.matches('[data-action="delete-item"]')) {
                this.handleDeleteItem(e.target);
            }
            if (e.target.matches('[data-action="move-item"]')) {
                this.handleMoveItem(e.target);
            }
        });

        const quickAddInput = document.querySelector('#quickAddItem');
        if (quickAddInput) {
            quickAddInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    this.quickAddItem(e.target.value);
                }
            });
        }
    }

    setupDragAndDrop() {
        const itemsContainer = document.querySelector('.items-container');
        if (!itemsContainer) return;

        new Sortable(itemsContainer, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            chosenClass: 'sortable-chosen',
            dragClass: 'sortable-drag',
            onEnd: (evt) => this.handleReorder(evt)
        });
    }

    setupRealTimeUpdates() {
        setInterval(() => {
            this.refreshItems();
        }, 30000);
    }

    async handleAddItem(event) {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);

        try {
            const data = {
                name: formData.get('name'),
                quantity: parseInt(formData.get('quantity')) || 1,
                price: parseFloat(formData.get('price')) || null,
                categoryId: formData.get('categoryId') || null,
                notes: formData.get('notes') || '',
                priority: formData.get('priority') || 'MEDIUM'
            };

            const response = await window.app.addItemToList(this.currentListId, data);
            if (response) {
                this.addItemToDOM(response);
                form.reset();
                this.updateListStatistics();
            }
        } catch (error) {
            console.error('Error adding item:', error);
        }
    }

    async quickAddItem(itemName) {
        if (!itemName.trim()) return;

        try {
            const data = {
                name: itemName.trim(),
                quantity: 1,
                priority: 'MEDIUM'
            };

            const response = await window.app.addItemToList(this.currentListId, data);
            if (response) {
                this.addItemToDOM(response);
                this.updateListStatistics();

                const quickAddInput = document.querySelector('#quickAddItem');
                if (quickAddInput) {
                    quickAddInput.value = '';
                }
            }
        } catch (error) {
            console.error('Quick add error:', error);
        }
    }

    addItemToDOM(itemData) {
        const itemsContainer = document.querySelector('.items-container');
        if (!itemsContainer) return;

        const itemElement = this.createItemElement(itemData);
        itemsContainer.appendChild(itemElement);

        itemElement.style.opacity = '0';
        itemElement.style.transform = 'translateY(-20px)';

        setTimeout(() => {
            itemElement.style.transition = 'all 0.3s ease';
            itemElement.style.opacity = '1';
            itemElement.style.transform = 'translateY(0)';
        }, 10);
    }

    createItemElement(itemData) {
        const div = document.createElement('div');
        div.className = 'item-row';
        div.dataset.itemId = itemData.id;
        div.dataset.priority = itemData.priority;
        div.dataset.purchased = itemData.purchased || false;

        div.innerHTML = `
            <div class="item-content">
                <div class="item-checkbox">
                    <input type="checkbox" 
                           data-item-checkbox 
                           ${itemData.purchased ? 'checked' : ''}
                           aria-label="Mark as purchased">
                </div>
                
                <div class="item-details">
                    <div class="item-name ${itemData.purchased ? 'purchased' : ''}">
                        ${itemData.name}
                    </div>
                    
                    <div class="item-meta">
                        ${itemData.quantity > 1 ? `<span class="quantity">${itemData.quantity}</span>` : ''}
                        ${itemData.price ? `<span class="price">${this.formatCurrency(itemData.price)}</span>` : ''}
                        ${itemData.notes ? `<span class="notes">${itemData.notes}</span>` : ''}
                    </div>
                </div>
                
                <div class="item-actions">
                    <button type="button" class="btn btn-sm btn-outline-primary" 
                            data-action="edit-item" data-id="${itemData.id}">
                        Edit
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger" 
                            data-action="delete-item" data-id="${itemData.id}">
                        Delete
                    </button>
                </div>
            </div>
        `;

        return div;
    }

    async handleItemToggle(checkbox) {
        const itemId = checkbox.closest('.item-row').dataset.itemId;
        const isPurchased = checkbox.checked;

        try {
            const response = await window.app.toggleItemStatus(itemId);
            if (response) {
                this.updateItemUI(itemId, isPurchased);
                this.updateListStatistics();
            }
        } catch (error) {
            checkbox.checked = !isPurchased;
            console.error('Toggle error:', error);
        }
    }

    updateItemUI(itemId, isPurchased) {
        const itemElement = document.querySelector(`[data-item-id="${itemId}"]`);
        if (!itemElement) return;

        const itemName = itemElement.querySelector('.item-name');
        if (itemName) {
            itemName.classList.toggle('purchased', isPurchased);
        }

        itemElement.dataset.purchased = isPurchased;
        itemElement.classList.toggle('item-purchased', isPurchased);
    }

    handleEditItem(button) {
        const itemId = button.dataset.id;
        const itemElement = button.closest('.item-row');

        this.showEditForm(itemElement, itemId);
    }

    showEditForm(itemElement, itemId) {
        const itemName = itemElement.querySelector('.item-name').textContent;
        const itemQuantity = itemElement.querySelector('.quantity')?.textContent || '1';
        const itemPrice = itemElement.querySelector('.price')?.textContent || '';
        const itemNotes = itemElement.querySelector('.notes')?.textContent || '';

        const editForm = document.createElement('div');
        editForm.className = 'edit-form';
        editForm.innerHTML = `
            <form class="edit-item-form">
                <div class="row g-2">
                    <div class="col-md-4">
                        <input type="text" class="form-control form-control-sm" 
                               name="name" value="${itemName}" required>
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control form-control-sm" 
                               name="quantity" value="${itemQuantity}" min="1">
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control form-control-sm" 
                               name="price" value="${itemPrice.replace(/[^\d.,]/g, '')}" 
                               step="0.01" min="0">
                    </div>
                    <div class="col-md-2">
                        <input type="text" class="form-control form-control-sm" 
                               name="notes" value="${itemNotes}">
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-sm btn-primary">Save</button>
                        <button type="button" class="btn btn-sm btn-secondary" 
                                onclick="this.closest('.edit-form').remove()">Cancel</button>
                    </div>
                </div>
            </form>
        `;

        const itemContent = itemElement.querySelector('.item-content');
        itemContent.style.display = 'none';
        itemElement.appendChild(editForm);

        const form = editForm.querySelector('form');
        form.addEventListener('submit', (e) => this.handleEditSubmit(e, itemId, itemElement));
    }

    async handleEditSubmit(event, itemId, itemElement) {
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);

        try {
            const data = {
                name: formData.get('name'),
                quantity: parseInt(formData.get('quantity')) || 1,
                price: parseFloat(formData.get('price')) || null,
                notes: formData.get('notes') || ''
            };

            const response = await window.app.makeRequest(`/api/items/${itemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.success) {
                this.updateItemInDOM(itemElement, response.data);
            }
        } catch (error) {
            console.error('Edit error:', error);
        }
    }

    updateItemInDOM(itemElement, itemData) {
        const editForm = itemElement.querySelector('.edit-form');
        if (editForm) {
            editForm.remove();
        }

        const itemContent = itemElement.querySelector('.item-content');
        itemContent.style.display = 'block';

        const itemName = itemElement.querySelector('.item-name');
        if (itemName) {
            itemName.textContent = itemData.name;
        }

        const quantity = itemElement.querySelector('.quantity');
        if (quantity) {
            quantity.textContent = itemData.quantity > 1 ? itemData.quantity : '';
        }

        const price = itemElement.querySelector('.price');
        if (price) {
            price.textContent = itemData.price ? this.formatCurrency(itemData.price) : '';
        }

        const notes = itemElement.querySelector('.notes');
        if (notes) {
            notes.textContent = itemData.notes || '';
        }

        this.updateListStatistics();
    }

    async handleDeleteItem(button) {
        const itemId = button.dataset.id;

        try {
            const response = await window.app.deleteItem(itemId);
            if (response) {
                this.updateListStatistics();
            }
        } catch (error) {
            console.error('Delete error:', error);
        }
    }

    handleMoveItem(button) {
        const itemId = button.dataset.id;
        this.showMoveDialog(itemId);
    }

    showMoveDialog(itemId) {
        const moveModal = new bootstrap.Modal(document.getElementById('moveItemModal'));
        moveModal.show();

        document.getElementById('moveItemModal').dataset.itemId = itemId;
    }

    async handleReorder(event) {
        const items = Array.from(event.to.children);
        const newOrder = items.map((item, index) => ({
            id: item.dataset.itemId,
            order: index
        }));

        try {
            await window.app.makeRequest(`/api/lists/${this.currentListId}/items/reorder`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ items: newOrder })
            });
        } catch (error) {
            console.error('Reorder error:', error);
        }
    }

    async refreshItems() {
        try {
            const response = await window.app.makeRequest(`/api/lists/${this.currentListId}/items`);
            if (response.success) {
                this.updateItemsList(response.data);
            }
        } catch (error) {
            console.error('Refresh error:', error);
        }
    }

    updateItemsList(items) {
        const container = document.querySelector('.items-container');
        if (!container) return;

        items.forEach(item => {
            const existingElement = container.querySelector(`[data-item-id="${item.id}"]`);
            if (existingElement) {
                this.updateItemElement(existingElement, item);
            } else {
                this.addItemToDOM(item);
            }
        });

        const existingIds = Array.from(container.querySelectorAll('[data-item-id]')).map(el => el.dataset.itemId);
        const newIds = items.map(item => item.id.toString());

        existingIds.forEach(id => {
            if (!newIds.includes(id)) {
                const element = container.querySelector(`[data-item-id="${id}"]`);
                if (element) {
                    element.remove();
                }
            }
        });

        this.updateListStatistics();
    }

    updateItemElement(element, itemData) {
        const itemName = element.querySelector('.item-name');
        if (itemName && itemName.textContent !== itemData.name) {
            itemName.textContent = itemData.name;
        }

        const checkbox = element.querySelector('[data-item-checkbox]');
        if (checkbox && checkbox.checked !== itemData.purchased) {
            checkbox.checked = itemData.purchased;
            this.updateItemUI(itemData.id, itemData.purchased);
        }

    }

    updateListStatistics() {
        const totalItems = document.querySelectorAll('[data-item-id]').length;
        const purchasedItems = document.querySelectorAll('[data-item-id][data-purchased="true"]').length;
        const totalPrice = this.calculateTotalPrice();

        const totalElement = document.querySelector('[data-stat="total-items"]');
        const purchasedElement = document.querySelector('[data-stat="purchased-items"]');
        const priceElement = document.querySelector('[data-stat="total-price"]');

        if (totalElement) totalElement.textContent = totalItems;
        if (purchasedElement) purchasedElement.textContent = purchasedItems;
        if (priceElement) priceElement.textContent = this.formatCurrency(totalPrice);

        const progressBar = document.querySelector('.progress-bar');
        if (progressBar && totalItems > 0) {
            const progress = (purchasedItems / totalItems) * 100;
            progressBar.style.width = `${progress}%`;
            progressBar.textContent = `${Math.round(progress)}%`;
        }
    }

    calculateTotalPrice() {
        const priceElements = document.querySelectorAll('.price');
        let total = 0;

        priceElements.forEach(element => {
            const price = parseFloat(element.textContent.replace(/[^\d.,]/g, '')) || 0;
            const quantity = parseInt(element.closest('.item-row').querySelector('.quantity')?.textContent) || 1;
            total += price * quantity;
        });

        return total;
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('us-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('.items-container')) {
        window.itemsManager = new ItemsManager();
    }
});