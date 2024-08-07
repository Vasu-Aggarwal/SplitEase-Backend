package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;
import com.vr.SplitEase.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/addCategory")
    public ResponseEntity<AddCategoryResponse> addCategory(@RequestBody @Valid AddCategoryRequest addCategoryRequest){
        AddCategoryResponse addCategoryResponse = categoryService.addCategory(addCategoryRequest);
        return new ResponseEntity<>(addCategoryResponse, HttpStatus.OK);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<List<AddCategoryResponse>> getCategories(){
        List<AddCategoryResponse> categoryResponseList = categoryService.getAllCategories();
        return new ResponseEntity<>(categoryResponseList, HttpStatus.OK);
    }
}
