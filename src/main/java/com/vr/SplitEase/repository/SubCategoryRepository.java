package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Category;
import com.vr.SplitEase.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    Optional<SubCategory> findByName(String categoryName);
}
