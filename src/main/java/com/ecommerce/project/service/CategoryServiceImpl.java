package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<Category>();
    Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public String addCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
        return "category added successfully";
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"No such category Found !"));

        categories.remove(category);
        return "Category with categoryID = "+categoryId + " deleted";
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {

        Optional<Category> optionalCatgeory = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst();
        if(optionalCatgeory.isPresent()){

            Category categoryFound = optionalCatgeory.get();
            categoryFound.setCategoryName(category.getCategoryName());
            return categoryFound;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found !");
        }
    }
}
