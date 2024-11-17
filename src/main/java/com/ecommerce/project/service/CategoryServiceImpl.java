package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    Long nextId = 1L;
    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public void addCategory(Category category) {
        Category categoryFound = categoryRepo.findByCategoryName(category.getCategoryName());
        if(categoryFound != null){
            throw new APIException("Catgeory with "+category.getCategoryName() +" already exists !!!");
        }
        category.setCategoryId(nextId++);
        categoryRepo.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No such category Found !"));

        categoryRepo.deleteById(categoryId);
        return "Category with categoryID = "+categoryId + " deleted";
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {

        Category savedCategory;
        savedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Resource Not found !!!"));

        category.setCategoryId(savedCategory.getCategoryId());
        savedCategory = categoryRepo.save(category);
        return savedCategory;
    }
}
