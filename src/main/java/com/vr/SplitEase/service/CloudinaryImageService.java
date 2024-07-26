package com.vr.SplitEase.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryImageService {
    Map uploadImage(MultipartFile image);
}
