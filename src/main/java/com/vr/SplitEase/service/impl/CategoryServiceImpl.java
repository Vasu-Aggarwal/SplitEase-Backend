package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;
import com.vr.SplitEase.entity.Category;
import com.vr.SplitEase.repository.CategoryRepository;
import com.vr.SplitEase.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCategoryResponse addCategory(AddCategoryRequest addCategoryRequest) {
        Category category = modelMapper.map(addCategoryRequest, Category.class);
        categoryRepository.save(category);
        return modelMapper.map(category, AddCategoryResponse.class);
    }
}
