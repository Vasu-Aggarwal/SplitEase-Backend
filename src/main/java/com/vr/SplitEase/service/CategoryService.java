package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;

public interface CategoryService {
    AddCategoryResponse addCategory(AddCategoryRequest addCategoryRequest);
}
