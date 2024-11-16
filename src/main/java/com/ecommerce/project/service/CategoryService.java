package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories();
    public String addCategory(Category category);

    String deleteCategory(Long categoryId);

    Category updateCategory(Long categoryId, Category category);
}
