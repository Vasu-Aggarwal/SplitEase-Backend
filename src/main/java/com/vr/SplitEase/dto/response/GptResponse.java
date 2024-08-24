package com.vr.SplitEase.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptResponse {

    private List<Choice> choices;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Choice{
        private Integer index;
        private GptMessage message;
    }

}
