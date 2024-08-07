package com.vr.SplitEase.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddCategoryResponse {
    private String category;
    private Integer categoryId;
    private List<SubCategoryResponse> subcategories;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SubCategoryResponse{
        private Integer subCategoryId;
        private String name;
        private String imageUrl;
    }
}
