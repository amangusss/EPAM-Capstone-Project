import './core/shopping-list-app.js';
import './core/form-handler.js';
import './core/api-client.js';
import './modules/ui-manager.js';
import './core/common.js';

import './modules/shopping-list-manager.js';
import './modules/item-manager.js';
import './modules/category-manager.js';
import './modules/budget-manager.js';
import './modules/list-share.js';
import './modules/lists.js';
import './modules/items.js';

document.addEventListener('DOMContentLoaded', () => {
    initializeTooltips();
    initializeApp();
});

function initializeTooltips() {
    const tooltipElements = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipElements.forEach(element => new bootstrap.Tooltip(element));

    setTimeout(() => {
        document.querySelectorAll('.alert:not(.alert-permanent)').forEach(alert => {
            new bootstrap.Alert(alert).close();
        });
    }, 5000);
}

function initializeApp() {
    if (typeof window.ShoppingListApp !== 'undefined') {
        window.app = new window.ShoppingListApp();
        console.log('Shopping List App initialized');
    } else {
        console.error('ShoppingListApp not found');
        setTimeout(initializeApp, 500);
    }
}

window.showEditBudgetModal = function(id) {
    if (window.app) {
        window.app.showEditBudgetModal(id);
    } else {
        setTimeout(() => window.showEditBudgetModal(id), 100);
    }
};

window.showEditCategoryModal = function(id) {
    if (window.app) {
        window.app.showEditCategoryModal(id);
    } else {
        setTimeout(() => window.showEditCategoryModal(id), 100);
    }
};

window.handleDelete = function(type, id) {
    if (window.app) {
        window.app.handleDelete(type, id);
    } else {
        setTimeout(() => window.handleDelete(type, id), 100);
    }
};