package com.ecommerce.project.controller;


import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder
    ){
        CategoryResponse res = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category){
        CategoryDTO categoryDTO = categoryService.addCategory(category);
        return new ResponseEntity<>(categoryDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

            CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);

    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId){

             CategoryDTO newCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);
             return new ResponseEntity<>(newCategoryDTO, HttpStatus.OK);


    }
}
