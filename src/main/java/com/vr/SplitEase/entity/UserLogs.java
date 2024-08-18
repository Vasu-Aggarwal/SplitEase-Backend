package com.vr.SplitEase.entity;

import com.vr.SplitEase.config.constants.ActivityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogs extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_uuid", nullable = false)
    private String userUuid;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ActivityType activityType;

    private String details;
}
