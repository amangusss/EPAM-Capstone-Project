class ApiClient {
    constructor(app) {
        this.app = app;
    }

    async makeRequest(url, options = {}) {
        const defaultOptions = {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            credentials: 'same-origin'
        };

        if (this.app.csrfToken) {
            defaultOptions.headers['X-CSRF-TOKEN'] = this.app.csrfToken;
        }

        const finalOptions = { ...defaultOptions, ...options };

        try {
            console.log('Making request to:', url, 'with options:', finalOptions);

            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 30000); //30 sec

            const response = await fetch(url, {
                ...finalOptions,
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            console.log('Response received:', response.status, response.statusText);

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const jsonResponse = await response.json();
                console.log('JSON response:', jsonResponse);
                console.log('Response ok:', response.ok, 'Status:', response.status);
                return {
                    ok: response.ok,
                    json: async () => jsonResponse,
                    text: async () => JSON.stringify(jsonResponse),
                    status: response.status,
                    statusText: response.statusText
                };
            } else {
                const textResponse = await response.text();
                console.log('Text response:', textResponse);
                console.log('Response ok:', response.ok, 'Status:', response.status);
                return {
                    ok: response.ok,
                    json: async () => ({ success: true, message: 'Operation completed successfully' }),
                    text: async () => textResponse,
                    status: response.status,
                    statusText: response.statusText
                };
            }
        } catch (error) {
            if (error.name === 'AbortError') {
                console.error('Request timeout after 30 seconds');
                throw new Error('Request timeout - please try again');
            }
            console.error('Request error:', error);
            return {
                ok: false,
                json: async () => ({ message: error.message || 'Request failed' }),
                text: async () => error.message || 'Request failed',
                status: 500,
                statusText: 'Internal Error'
            };
        }
    }

    async get(url) {
        return this.makeRequest(url, { method: 'GET' });
    }

    async post(url, data) {
        const options = { method: 'POST' };

        if (data instanceof FormData) {
            options.body = data;
        } else {
            options.headers = { 'Content-Type': 'application/json' };
            options.body = JSON.stringify(data);
        }

        return this.makeRequest(url, options);
    }

    async get(url) {
        return this.makeRequest(url, { method: 'GET' });
    }

    async put(url, data) {
        const options = { method: 'PUT' };

        if (data instanceof FormData) {
            options.body = data;
        } else {
            options.headers = { 'Content-Type': 'application/json' };
            options.body = JSON.stringify(data);
        }

        return this.makeRequest(url, options);
    }

    async delete(url) {
        return this.makeRequest(url, { method: 'DELETE' });
    }

    async createShoppingList(data) {
        return this.post('/api/shopping-lists', data);
    }

    async updateShoppingList(id, data) {
        return this.put(`/api/shopping-lists/${id}`, data);
    }

    async deleteShoppingList(id) {
        console.log('ApiClient.deleteShoppingList called with ID:', id, 'Type:', typeof id);
        return this.delete(`/api/shopping-lists/${id}`);
    }

    async addItemToList(listId, data) {
        return this.post(`/api/items?shoppingListId=${listId}`, data);
    }

    async toggleItemStatus(itemId) {
        try {
            const item = await this.get(`/api/items/${itemId}`);
            if (item && item.id) {
                if (item.isPurchased) {
                    return this.post(`/api/items/${itemId}/unpurchase`);
                } else {
                    const actualPrice = item.estimatedPrice || 0.0;
                    return this.post(`/api/items/${itemId}/purchase?actualPrice=${actualPrice}`);
                }
            }
        } catch (error) {
            console.error('Error toggling item status:', error);
            throw error;
        }
    }

    async deleteItem(itemId) {
        return this.delete(`/api/items/${itemId}`);
    }

    async createCategory(data) {
        return this.post('/api/categories', data);
    }

    async updateCategory(id, data) {
        return this.put(`/api/categories/${id}`, data);
    }

    async deleteCategory(id) {
        return this.delete(`/api/categories/${id}`);
    }

    async createBudget(data) {
        return this.post('/api/budgets', data);
    }

    async updateBudget(id, data) {
        console.log('ApiClient.updateBudget called with:', { id, data });
        return this.put(`/api/budgets/${id}`, data);
    }

    async deleteBudget(id) {
        return this.delete(`/api/budgets/${id}`);
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = ApiClient;
}

if (typeof window !== 'undefined') {
    window.ApiClient = ApiClient;
}