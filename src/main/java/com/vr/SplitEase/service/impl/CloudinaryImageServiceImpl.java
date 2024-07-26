package com.vr.SplitEase.service.impl;

import com.cloudinary.Cloudinary;
import com.vr.SplitEase.service.CloudinaryImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService {

    private final Cloudinary cloudinary;

    public CloudinaryImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map uploadImage(MultipartFile image) {

        try {
            return cloudinary.uploader().upload(image.getBytes(), Map.of());
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed !!");
        }
    }
}
