package com.vr.SplitEase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddCategoryRequest {
    @NotNull(message = "Category name cannot be null")
    @NotBlank(message = "Category name cannot be empty")
    private String categoryName;
    @NotNull(message = "Category name cannot be null")
    @NotBlank(message = "Category name cannot be empty")
    private String subCategoryName;
    private String categoryImg;
}
