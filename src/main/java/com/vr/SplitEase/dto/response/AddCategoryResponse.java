package com.vr.SplitEase.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddCategoryResponse {
    private Integer subCategoryId;
    private String name;
    private String imageUrl;
    private Integer categoryId;
}
