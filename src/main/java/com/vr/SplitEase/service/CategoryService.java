package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;

import java.util.List;

public interface CategoryService {
    AddCategoryResponse addCategory(AddCategoryRequest addCategoryRequest);
    List<AddCategoryResponse> getAllCategories();
}
