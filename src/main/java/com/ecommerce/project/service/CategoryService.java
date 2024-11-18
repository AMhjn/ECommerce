package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

public interface CategoryService {
    public CategoryResponse getAllCategories(Integer pagerNumber, Integer pageSize, String sortBy, String sortOrder);
    public CategoryDTO addCategory(CategoryDTO category);

   CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO category);
}
