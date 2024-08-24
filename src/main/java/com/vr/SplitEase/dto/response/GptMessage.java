package com.vr.SplitEase.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptMessage {
    private String role;
    private String content;
}
