package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddCategoryRequest;
import com.vr.SplitEase.dto.request.GptRequest;
import com.vr.SplitEase.dto.response.AddCategoryResponse;
import com.vr.SplitEase.dto.response.DeleteResponse;
import com.vr.SplitEase.dto.response.GetCategoryResponse;
import com.vr.SplitEase.dto.response.GptResponse;
import com.vr.SplitEase.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/addCategory")
    public ResponseEntity<AddCategoryResponse> addCategory(@RequestBody @Valid AddCategoryRequest addCategoryRequest){
        AddCategoryResponse addCategoryResponse = categoryService.addCategory(addCategoryRequest);
        return new ResponseEntity<>(addCategoryResponse, HttpStatus.OK);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<List<GetCategoryResponse>> getCategories(){
        List<GetCategoryResponse> categoryResponseList = categoryService.getAllCategories();
        return new ResponseEntity<>(categoryResponseList, HttpStatus.OK);
    }

    @GetMapping("/findCategory")
    public ResponseEntity<DeleteResponse> decideCategory(@RequestParam("prompt") String prompt){
        GptRequest request = new GptRequest(model, prompt);
        GptResponse response = restTemplate.postForObject(apiUrl, request, GptResponse.class);
        assert response != null;
        return new ResponseEntity<>(DeleteResponse.builder().message(response.getChoices().get(0).getMessage().getContent()).build(), HttpStatus.OK);
    }
}
