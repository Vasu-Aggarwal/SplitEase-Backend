package com.vr.SplitEase.dto.response;

import com.vr.SplitEase.config.constants.ActivityType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetUserLogsResponse {
    private Integer id;
    private String userUuid;
    private ActivityType activityType;
    private String details;
    private String createdOn;
}
