import httpClient from '../httpClient';

export const categoryService = {
  // GET /allCategories
  getAllCategories: () => {
    return httpClient.get('/allCategories');
  },

  // POST /categories
  createCategory: (data) => {
    return httpClient.post('/categories', data);
  },

  // GET /categories/{id}
  getCategoryById: (id) => {
    return httpClient.get(`/categories/${id}`);
  },

  // PUT /categories/{id}
  updateCategory: (id, data) => {
    return httpClient.put(`/categories/${id}`, data);
  },

  // DELETE /categories/{id}
  deleteCategory: (id) => {
    return httpClient.delete(`/categories/${id}`);
  },
};

