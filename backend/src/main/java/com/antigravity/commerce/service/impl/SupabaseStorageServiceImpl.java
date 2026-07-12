package com.antigravity.commerce.service.impl;

import com.antigravity.commerce.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
public class SupabaseStorageServiceImpl implements StorageService {

    @Value("${supabase.url:https://mock.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.bucket:public-bucket}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String path) {
        log.info("Uploading file {} to Supabase bucket {} at path {}", file.getOriginalFilename(), bucketName, path);
        // TODO: Implement actual Supabase REST API call using WebClient or RestTemplate
        // For now, return a mock public URL
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + path + "/" + uniqueFileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("Deleting file from Supabase: {}", fileUrl);
        // TODO: Extract path from URL and make DELETE request to Supabase
    }
}
