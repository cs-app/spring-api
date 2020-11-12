package com.neta.teman.dawai.api.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String storeFileCSV(MultipartFile file);

    String storeFile(MultipartFile file);

    String storeFile(String folder, String base64, String ext);

    Resource loadFileAsResource(String fileName);

    Resource loadFileAsResource(String folder, String fileName);

    String loadFileAsBase64(String fileName);

    void deleteFile(String imgNameExist);

    void deleteFile(String folder, String fileName);
}
