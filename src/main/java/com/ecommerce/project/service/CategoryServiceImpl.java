package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
     private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        // PAgination
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepo.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories == null){
            throw new APIException("No Categories created till now !!!");
        }

        List<CategoryDTO> categoryDTOs = categories.stream().map(c -> modelMapper.map(c, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse =  new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastpage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFound = categoryRepo.findByCategoryName(category.getCategoryName());
        if(categoryFound != null){
            throw new APIException("Catgeory with "+category.getCategoryName() +" already exists !!!");
        }

        Category addedCategory = categoryRepo.save(category);
        CategoryDTO addedCategoryDTO = modelMapper.map(addedCategory,CategoryDTO.class);
        return addedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No such category Found !"));

         categoryRepo.deleteById(categoryId);
         CategoryDTO catgeoryDeletedDTO = modelMapper.map(category, CategoryDTO.class);
        return catgeoryDeletedDTO;
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {

        Category savedCategory;
        savedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Resource Not found !!!"));

        categoryDTO.setCategoryId(savedCategory.getCategoryId());
        Category category = modelMapper.map(categoryDTO,Category.class);
        category = categoryRepo.save(category);
        categoryDTO = modelMapper.map(category, CategoryDTO.class);
        return categoryDTO;
    }
}
