package com.vr.SplitEase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddCategoryResponse {
    private Integer subCategoryId;
    private String name;
    private String imageUrl;
    private String category;
    private Integer categoryId;
}
