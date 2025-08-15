class ListsManager {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupSearchAndFilter();
        console.log('Lists Manager initialized');
    }

    setupEventListeners() {
        const createListBtn = document.querySelector('[data-action="create-list"]');
        if (createListBtn) {
            createListBtn.addEventListener('click', () => this.showCreateListModal());
        }

        const filterInputs = document.querySelectorAll('[data-filter]');
        filterInputs.forEach(input => {
            input.addEventListener('change', () => this.applyFilters());
        });

        const sortSelect = document.querySelector('[data-sort]');
        if (sortSelect) {
            sortSelect.addEventListener('change', () => this.applySorting());
        }

        const bulkActions = document.querySelectorAll('[data-bulk-action]');
        bulkActions.forEach(btn => {
            btn.addEventListener('click', () => this.handleBulkAction(btn.dataset.bulkAction));
        });
    }

    setupSearchAndFilter() {
        const searchInput = document.querySelector('#searchLists');
        if (searchInput) {
            let searchTimeout;
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    this.searchLists(e.target.value);
                }, 300);
            });
        }
    }

    showCreateListModal() {
        const modal = new bootstrap.Modal(document.getElementById('createListModal'));
        modal.show();
    }

    searchLists(query) {
        const listElements = document.querySelectorAll('[data-list-id]');
        const queryLower = query.toLowerCase();

        listElements.forEach(element => {
            const name = element.querySelector('.card-title').textContent.toLowerCase();
            const description = element.querySelector('.text-muted')?.textContent.toLowerCase() || '';

            const matches = name.includes(queryLower) || description.includes(queryLower);
            element.style.display = matches ? 'block' : 'none';
        });
    }

    applyFilters() {
        const statusFilter = document.querySelector('[data-filter="status"]')?.value;
        const priorityFilter = document.querySelector('[data-filter="priority"]')?.value;
        const categoryFilter = document.querySelector('[data-filter="category"]')?.value;

        const listElements = document.querySelectorAll('[data-list-id]');

        listElements.forEach(element => {
            let shouldShow = true;

            if (statusFilter && statusFilter !== 'all') {
                const status = element.dataset.status;
                if (status !== statusFilter) shouldShow = false;
            }

            if (priorityFilter && priorityFilter !== 'all') {
                const priority = element.dataset.priority;
                if (priority !== priorityFilter) shouldShow = false;
            }

            if (categoryFilter && categoryFilter !== 'all') {
                const category = element.dataset.category;
                if (category !== categoryFilter) shouldShow = false;
            }

            element.style.display = shouldShow ? 'block' : 'none';
        });
    }

    applySorting() {
        const sortBy = document.querySelector('[data-sort]')?.value;
        if (!sortBy) return;

        const listsContainer = document.querySelector('.lists-container');
        const listElements = Array.from(listsContainer.querySelectorAll('[data-list-id]'));

        listElements.sort((a, b) => {
            let aValue, bValue;

            switch (sortBy) {
                case 'name':
                    aValue = a.querySelector('.card-title').textContent.toLowerCase();
                    bValue = b.querySelector('.card-title').textContent.toLowerCase();
                    return aValue.localeCompare(bValue);

                case 'date':
                    aValue = new Date(a.dataset.createdAt || 0);
                    bValue = new Date(b.dataset.createdAt || 0);
                    return bValue - aValue;

                case 'priority':
                    const priorityOrder = {'URGENT': 4, 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1};
                    aValue = priorityOrder[a.dataset.priority] || 0;
                    bValue = priorityOrder[b.dataset.priority] || 0;
                    return bValue - aValue;

                case 'items':
                    aValue = parseInt(a.dataset.totalItems) || 0;
                    bValue = parseInt(b.dataset.totalItems) || 0;
                    return bValue - aValue;

                default:
                    return 0;
            }
        });

        listElements.forEach(element => listsContainer.appendChild(element));
    }

    handleBulkAction(action) {
        const selectedLists = this.getSelectedLists();
        if (selectedLists.length === 0) {
            return;
        }

        switch (action) {
            case 'delete':
                this.bulkDelete(selectedLists);
                break;
            case 'archive':
                this.bulkArchive(selectedLists);
                break;
            case 'share':
                this.bulkShare(selectedLists);
                break;
        }
    }

    getSelectedLists() {
        const checkboxes = document.querySelectorAll('[data-list-checkbox]:checked');
        return Array.from(checkboxes).map(cb => cb.value);
    }

    async bulkDelete(listIds) {
        try {
            const promises = listIds.map(id => window.app.deleteShoppingList(id));
            await Promise.all(promises);

        } catch (error) {
            console.error('Bulk delete error:', error);
        }
    }

    async bulkArchive(listIds) {
        try {
            const promises = listIds.map(id =>
                window.app.makeRequest(`/api/lists/${id}/archive`, {method: 'POST'})
            );
            await Promise.all(promises);

            listIds.forEach(id => {
                const element = document.querySelector(`[data-list-id="${id}"]`);
                if (element) {
                    element.classList.add('archived');
                }
            });
        } catch (error) {
            console.error('Bulk archive error:', error);
        }
    }

    async bulkShare(listIds) {
        const shareModal = new bootstrap.Modal(document.getElementById('bulkShareModal'));
        shareModal.show();

        document.getElementById('bulkShareModal').dataset.listIds = listIds.join(',');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('.lists-container')) {
        window.listsManager = new ListsManager();
    }
});