package com.antigravity.commerce.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Uploads a file to the storage provider (e.g. Supabase, S3).
     * @param file The file to upload.
     * @param path The path/directory where the file should be stored.
     * @return The public URL of the uploaded file.
     */
    String uploadFile(MultipartFile file, String path);

    /**
     * Deletes a file from the storage provider.
     * @param fileUrl The public URL of the file to delete.
     */
    void deleteFile(String fileUrl);
}
