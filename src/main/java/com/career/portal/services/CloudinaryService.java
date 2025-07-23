package com.career.portal.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String, String> uploadFile(MultipartFile file, Long userId) throws IOException {
        String publicId = "resumes/" + userId + "_" + file.getOriginalFilename();

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "raw",
                "access_mode", "public"
        ));

        String downloadUrl = cloudinary.url()
                .resourceType("raw")
                .publicId(publicId)
                .transformation(new Transformation().flags("attachment"))
                .secure(false)
                .generate();


        return Map.of(
                "url", downloadUrl,
                "public_id", (String) uploadResult.get("public_id")
        );
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
    }

    public String getSignedUrl(String publicId) {
        return cloudinary.url()
                .resourceType("raw")
                .publicId(publicId)
                .signed(true)
                .generate();
    }
}