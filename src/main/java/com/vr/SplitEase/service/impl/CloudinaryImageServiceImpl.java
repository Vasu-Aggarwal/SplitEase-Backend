package com.vr.SplitEase.service.impl;

import com.cloudinary.Cloudinary;
import com.vr.SplitEase.service.CloudinaryImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
            Map data = cloudinary.uploader().upload(image.getBytes(), Map.of());
            Map resultData = new HashMap();
            resultData.put("url", data.get("url").toString());
            resultData.put("format", data.get("format").toString());
            resultData.put("resource_type", data.get("resource_type").toString());
            resultData.put("created_at", data.get("created_at").toString());
            resultData.put("display_name", data.get("display_name").toString());
            return resultData;
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed !!");
        }
    }
}
