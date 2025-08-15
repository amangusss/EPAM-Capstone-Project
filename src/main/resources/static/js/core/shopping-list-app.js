class ShoppingListApp {
    constructor() {
        this.csrfToken = this.getCsrfToken();
        this.modules = {};
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupGlobalErrorHandling();
        this.initializeModules();
        console.log('Shopping List App initialized');
    }

    getCsrfToken() {
        const metaTag = document.querySelector('meta[name="_csrf"]');
        return metaTag ? metaTag.getAttribute('content') : '';
    }

    setupEventListeners() {
        document.addEventListener('submit', (e) => {
            this.handleFormSubmit(e);
        });

        document.addEventListener('click', (e) => this.handleButtonClick(e));

        document.addEventListener('show.bs.modal', (e) => this.handleModalShow(e));
        document.addEventListener('hidden.bs.modal', (e) => this.handleModalHidden(e));
    }

    initializeModules() {
        this.modules.formHandler = new FormHandler(this);
        this.modules.apiClient = new ApiClient(this);
        this.modules.uiManager = new UIManager(this);
        this.modules.shoppingLists = new ShoppingListManager(this);
        this.modules.items = new ItemManager(this);
        this.modules.categories = new CategoryManager(this);
        this.modules.budgets = new BudgetManager(this);
    }

    setupGlobalErrorHandling() {
        window.addEventListener('unhandledrejection', (event) => {
            console.error('Unhandled promise rejection:', event.reason);

            if (event.reason && event.reason.message &&
                event.reason.message.includes('message port closed')) {
                console.log('Ignoring message port error (browser extension related)');
                return;
            }

            this.modules.uiManager.showAlert('An error occurred. Please try again.', 'danger');
        });

        window.addEventListener('error', (event) => {
            console.error('Global error:', event.error);

            if (event.error && event.error.message &&
                event.error.message.includes('message port closed')) {
                console.log('Ignoring message port error (browser extension related)');
            }
        });
    }

    async handleFormSubmit(event) {
        await this.modules.formHandler.handleSubmit(event);
    }

    handleButtonClick(event) {
        const button = event.target.closest('button');
        if (!button) return;

        const action = button.dataset.action;
        const id = button.dataset.id;
        const type = button.dataset.type;

        switch (action) {
            case 'edit':
                this.handleEdit(type, id);
                break;
            case 'delete':
                this.handleDelete(type, id);
                break;
            case 'toggle':
                this.handleToggle(type, id);
                break;
            case 'share':
                this.handleShare(id);
                break;
        }
    }

    handleEdit(type, id) {
        switch (type) {
            case 'category':
                this.modules.categories.showEditModal(id);
                break;
            case 'budget':
                this.modules.budgets.showEditModal(id);
                break;
            case 'list':
                this.modules.shoppingLists.showEditModal(id);
                break;
            case 'item':
                this.modules.items.showEditModal(id);
                break;
            default:
                console.log(`Edit not implemented for type: ${type}`);
        }
    }

    showEditBudgetModal(id) {
        this.modules.budgets.showEditModal(id);
    }

    showEditCategoryModal(id) {
        this.modules.categories.showEditModal(id);
    }

    async handleDelete(type, id) {
        console.log('App.handleDelete called with:', { type, id, idType: typeof id });
        const confirmMessage = `Are you sure you want to delete this ${this.getTypeName(type)}?`;
        if (!confirm(confirmMessage)) return;

        try {
            switch (type) {
                case 'category':
                    await this.modules.categories.delete(id);
                    break;
                case 'budget':
                    await this.modules.budgets.delete(id);
                    break;
                case 'list':
                    console.log('Deleting shopping list with ID:', id);
                    await this.modules.shoppingLists.delete(id);
                    break;
                case 'item':
                    await this.modules.items.delete(id);
                    break;
                default:
                    console.log(`Delete not implemented for type: ${type}`);
                    return;
            }

            window.location.reload();
        } catch (error) {
            console.error(`Error deleting ${type}:`, error);
            this.modules.uiManager.showAlert(`Error deleting ${this.getTypeName(type)}`, 'danger');
        }
    }

    async handleToggle(type, id) {
        try {
            switch (type) {
                case 'item':
                    await this.modules.items.toggleStatus(id);
                    break;
                default:
                    console.log(`Toggle not implemented for type: ${type}`);
            }
        } catch (error) {
            console.error(`Error toggling ${type}:`, error);
            this.modules.uiManager.showAlert(`Error toggling ${this.getTypeName(type)}`, 'danger');
        }
    }

    handleShare(id) {
        this.modules.shoppingLists.showShareModal(id);
    }

    getTypeName(type) {
        const typeNames = {
            'category': 'category',
            'budget': 'budget',
            'list': 'shopping list',
            'item': 'item'
        };
        return typeNames[type] || 'item';
    }

    handleModalShow(event) {
        this.modules.uiManager.handleModalShow(event);
    }

    handleModalHidden(event) {
        this.modules.uiManager.handleModalHidden(event);
    }

    showAlert(message, type = 'info') {
        if (this.modules && this.modules.uiManager) {
            this.modules.uiManager.showAlert(message, type);
        } else {
            alert(message);
        }
    }

    makeRequest(url, options = {}) {
        if (this.modules && this.modules.apiClient) {
            return this.modules.apiClient.makeRequest(url, options);
        } else {
            return fetch(url, options);
        }
    }

    deleteShoppingList(id) {
        if (this.modules && this.modules.shoppingLists) {
            return this.modules.shoppingLists.delete(id);
        } else {
            return this.makeRequest(`/api/shopping-lists/${id}`, { method: 'DELETE' });
        }
    }

    addItemToList(listId, data) {
        if (this.modules && this.modules.items) {
            return this.modules.items.addToList(listId, data);
        } else {
            return this.makeRequest(`/api/items?shoppingListId=${listId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }
    }

    createShoppingList(data) {
        if (this.modules && this.modules.shoppingLists) {
            return this.modules.shoppingLists.create(data);
        } else {
            return this.makeRequest('/api/shopping-lists', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }
    }

    toggleItemStatus(itemId) {
        if (this.modules && this.modules.items) {
            return this.modules.items.toggleStatus(itemId);
        } else {
            return this.makeRequest(`/api/items/${itemId}/toggle`, { method: 'POST' });
        }
    }

    deleteItem(itemId) {
        if (this.modules && this.modules.items) {
            return this.modules.items.delete(itemId);
        } else {
            return this.makeRequest(`/api/items/${itemId}`, { method: 'DELETE' });
        }
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = ShoppingListApp;
}

if (typeof window !== 'undefined') {
    window.ShoppingListApp = ShoppingListApp;
}