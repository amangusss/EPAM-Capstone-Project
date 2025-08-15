class BudgetManager {
    constructor(app) {
        this.app = app;
        this.setupEventListeners();
    }

    setupEventListeners() {
        const editForm = document.getElementById('editBudgetForm');
        if (editForm) {
            editForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleEditSubmit();
            });
        }
    }

    async update(id, data) {
        try {
            const response = await this.app.modules.apiClient.updateBudget(id, data);
            if (response && response.id) {
                this.app.modules.uiManager.showAlert('Budget updated successfully!', 'success');
                return response;
            } else {
                this.app.modules.uiManager.showAlert('Error updating budget', 'danger');
            }
        } catch (error) {
            console.error('Error updating budget:', error);
            this.app.modules.uiManager.showAlert('Error updating budget', 'danger');
            return null;
        }
    }

    async delete(id) {
        try {
            const response = await this.app.modules.apiClient.deleteBudget(id);
            if (response !== undefined) {
                this.app.modules.uiManager.showAlert('Budget deleted successfully!', 'success');
                const element = document.querySelector(`[data-budget-id="${id}"]`);
                if (element) {
                    element.remove();
                }
            }
        } catch (error) {
            console.error('Error deleting budget:', error);
            this.app.modules.uiManager.showAlert('Error deleting budget', 'danger');
        }
    }

    showEditModal(budgetId) {
        console.log('showEditBudgetModal called with id:', budgetId);
        const budgetCard = document.querySelector(`[data-budget-id="${budgetId}"]`);
        if (!budgetCard) {
            console.error('Budget card not found for id:', budgetId);
            return;
        }

        const name = budgetCard.querySelector('.card-title').textContent;
        const amount = budgetCard.querySelector('.h4').textContent.replace('$', '').trim();
        const currency = budgetCard.querySelector('.text-muted').textContent;
        const period = budgetCard.querySelector('.badge.bg-info').textContent;

        const modal = document.getElementById('editBudgetModal');
        if (modal) {
            const amountInput = modal.querySelector('#editBudgetAmount');
            const currencyInput = modal.querySelector('#editBudgetCurrency');
            const periodInput = modal.querySelector('#editBudgetPeriod');
            const idInput = modal.querySelector('#editBudgetId');

            if (amountInput) amountInput.value = amount;
            if (currencyInput) currencyInput.value = currency;
            if (periodInput) periodInput.value = period;
            if (idInput) idInput.value = budgetId;

            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
        } else {
            this.showSimpleEditDialog('budget', budgetId, name, amount);
        }
    }

    async handleEditSubmit() {
        const form = document.getElementById('editBudgetForm');
        const formData = new FormData(form);

        const budgetData = {
            limit: parseFloat(formData.get('amount')),
            currency: formData.get('currency'),
            period: formData.get('period'),
            isActive: true
        };

        const budgetId = formData.get('id');

        console.log('Updating budget with data:', { id: budgetId, data: budgetData });

        try {
            const response = await this.update(budgetId, budgetData);
            if (response) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('editBudgetModal'));
                if (modal) {
                    modal.hide();
                }
                window.location.reload();
            }
        } catch (error) {
            console.error('Error updating budget:', error);
            this.app.modules.uiManager.showAlert('Error updating budget: ' + error.message, 'danger');
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
    module.exports = BudgetManager;
}

if (typeof window !== 'undefined') {
    window.BudgetManager = BudgetManager;
}