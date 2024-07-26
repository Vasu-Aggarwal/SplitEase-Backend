package com.vr.SplitEase.controller;

import com.vr.SplitEase.service.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/cloudinary")
public class CloudinaryImageController {

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @PostMapping("/upload/image")
    public ResponseEntity<Map> uploadImage(@RequestParam("image")MultipartFile image){
        Map data = cloudinaryImageService.uploadImage(image);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
