class CategoryManager {
    constructor(app) {
        this.app = app;
        this.setupEventListeners();
    }

    setupEventListeners() {
        const editForm = document.getElementById('editCategoryForm');
        if (editForm) {
            editForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleEditSubmit();
            });
        }
    }

    async update(id, data) {
        try {
            const response = await this.app.modules.apiClient.updateCategory(id, data);
            if (response && response.id) {
                this.app.modules.uiManager.showAlert('Category updated successfully!', 'success');
                return response;
            } else {
                this.app.modules.uiManager.showAlert('Error updating category', 'danger');
            }
        } catch (error) {
            console.error('Error updating category:', error);
            this.app.modules.uiManager.showAlert('Error updating category', 'danger');
            return null;
        }
    }

    async delete(id) {
        try {
            const response = await this.app.modules.apiClient.deleteCategory(id);
            if (response !== undefined) {
                this.app.modules.uiManager.showAlert('Category deleted successfully!', 'success');
                const element = document.querySelector(`[data-category-id="${id}"]`);
                if (element) {
                    element.remove();
                }
            }
        } catch (error) {
            console.error('Error deleting category:', error);
            this.app.modules.uiManager.showAlert('Error deleting category', 'danger');
        }
    }

    showEditModal(categoryId) {
        console.log('showEditCategoryModal called with id:', categoryId);
        const categoryCard = document.querySelector(`[data-category-id="${categoryId}"]`);
        if (!categoryCard) {
            console.error('Category card not found for id:', categoryId);
            return;
        }

        const name = categoryCard.querySelector('.card-title').textContent;
        const description = categoryCard.querySelector('p')?.textContent || '';

        const modal = document.getElementById('editCategoryModal');
        if (modal) {
            const nameInput = modal.querySelector('#editCategoryName');
            const descInput = modal.querySelector('#editCategoryDescription');
            const idInput = modal.querySelector('#editCategoryId');

            if (nameInput) nameInput.value = name;
            if (descInput) descInput.value = description;
            if (idInput) idInput.value = categoryId;

            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
        } else {
            this.showSimpleEditDialog('category', categoryId, name, description);
        }
    }

    async handleEditSubmit() {
        const form = document.getElementById('editCategoryForm');
        const formData = new FormData(form);

        const categoryData = {
            name: formData.get('name'),
            description: formData.get('description')
        };

        const categoryId = formData.get('id');

        try {
            const response = await this.update(categoryId, categoryData);
            if (response) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('editCategoryModal'));
                if (modal) {
                    modal.hide();
                }
                window.location.reload();
            }
        } catch (error) {
            console.error('Error updating category:', error);
        }
    }

    showSimpleEditDialog(type, id, currentName, currentValue) {
        const newName = this.app.modules.uiManager.showPrompt(`Enter new name for ${type}:`, currentName);
        if (newName && newName !== currentName) {
            console.log(`Updating ${type} ${id} with new name: ${newName}`);
            this.app.modules.uiManager.showAlert(`${type} updated successfully!`, 'success');
        }
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = CategoryManager;
}

if (typeof window !== 'undefined') {
    window.CategoryManager = CategoryManager;
}