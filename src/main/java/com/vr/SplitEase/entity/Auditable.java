package com.vr.SplitEase.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
    @CreationTimestamp
    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;

    @LastModifiedDate
    @JoinColumn(name = "modified_on")
    private LocalDateTime modifiedOn;
}