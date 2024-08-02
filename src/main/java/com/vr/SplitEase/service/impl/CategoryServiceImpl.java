package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;
import com.vr.SplitEase.entity.Category;
import com.vr.SplitEase.entity.SubCategory;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.CategoryRepository;
import com.vr.SplitEase.repository.SubCategoryRepository;
import com.vr.SplitEase.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCategoryResponse addCategory(AddCategoryRequest addCategoryRequest) {
        Category category = categoryRepository.findByName(addCategoryRequest.getCategoryName()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        SubCategory subCategory = modelMapper.map(addCategoryRequest, SubCategory.class);
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);
        return modelMapper.map(subCategory, AddCategoryResponse.class);
    }
}
