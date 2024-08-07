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

import java.util.Comparator;
import java.util.List;

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

    @Override
    public List<AddCategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<SubCategory> subCategories = subCategoryRepository.findAll();

        List<AddCategoryResponse> categoryResponseList = categories.stream().map(category -> {
            AddCategoryResponse addCategoryResponse = new AddCategoryResponse();
            addCategoryResponse.setCategory(category.getName());
            addCategoryResponse.setCategoryId(category.getCategoryId());
            //Get the list of sub categories
            List<SubCategory> subCategories1 = subCategoryRepository.findByCategory(category).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            List<AddCategoryResponse.SubCategoryResponse> subCategoryResponses = subCategories1.stream().map(subCategory -> modelMapper.map(subCategory, AddCategoryResponse.SubCategoryResponse.class)).toList();
            addCategoryResponse.setSubcategories(subCategoryResponses);
            return addCategoryResponse;
        }).toList();

//        List<AddCategoryResponse> categoryResponseList = subCategories.stream().map(subCategory -> {
//                    AddCategoryResponse categoryResponse = modelMapper.map(subCategory, AddCategoryResponse.class);
//                    categoryResponse.setCategory(subCategory.getCategory().getName());
//                    categoryResponse.setCategoryId(subCategory.getCategory().getCategoryId());
//                    return categoryResponse;
//                }
//        ).sorted(Comparator.comparing(AddCategoryResponse::getCategory)).toList();
        return categoryResponseList;
    }
}
