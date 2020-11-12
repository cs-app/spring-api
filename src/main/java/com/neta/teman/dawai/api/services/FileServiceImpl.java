package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.config.FileStorageProperties;
import com.neta.teman.dawai.api.applications.exceptions.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("cannot create dir", ex);
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private String dateToStringMilis(Date date) {
        if (Objects.isNull(date)) return new SimpleDateFormat("yyyymmddHHMMss").format(date);
        return "";
    }

    @Override
    public String storeFileCSV(MultipartFile file) {
        if (null == file || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) return null;
        log.info("upload : " + file.getName() + "|" + file.getOriginalFilename());
        String[] exts = StringUtils.split(file.getOriginalFilename(), ".");
        if (Objects.nonNull(exts) && exts.length > 0) {
            if ("csv".equalsIgnoreCase(exts[exts.length - 1])) {
                return storeFile(file);
            }
        }
        return null;
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (null == file || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) return null;
        // Normalize file name
        String fileName = (dateToStringMilis(new Date()) + "_" + StringUtils.cleanPath(file.getOriginalFilename()).replaceAll(" ", "_")).toUpperCase();

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            log.error("cannot store file {}", file, ex);
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public String storeFile(String folder, String base64, String extension) {

        if (Objects.nonNull(base64)) {
            base64 = base64.replace("data:application/pdf;base64,", "");
            base64 = base64.replace("data:image/jpeg;base64,", "");
            base64 = base64.replace("data:image/png;base64,", "");
            try {
                if (Objects.nonNull(folder) && !folder.trim().isEmpty()) {
                    File fileFolder = new File(getFileStorageLocation().toString() + File.separator + folder);
                    if (!fileFolder.exists()) {
                        if (fileFolder.mkdirs()) {
                            log.info("folder created ");
                        }
                    }
                }
                String name = RandomStringUtils.randomAlphanumeric(20) + "." + extension;
                File file = new File(getFileStorageLocation().toString() + File.separator + name);
                if (Objects.nonNull(folder) && !folder.trim().isEmpty()) {
                    file = new File(getFileStorageLocation().toString() + File.separator + folder + File.separator + name);
                }
                if (!file.exists() && file.createNewFile()) {
                    byte[] byteImg = Base64.decodeBase64(base64);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(byteImg);
                    fos.close();
                    return name;
                }
            } catch (IOException e) {
                log.error("cannot store file", e);
            }
        }
        return null;

    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                log.error("cannot load file file {}", fileName);
                return null;
            }
        } catch (MalformedURLException ex) {
            log.error("cannot load file as resource", ex);
            return null;
        }
    }

    @Override
    public Resource loadFileAsResource(String folder, String fileName) {
        try {
            File file = new File(getFileStorageLocation().toString() + File.separator + folder + File.separator + fileName);
//            Path filePath = this.fileStorageLocation.resolve(folder + File.separator + fileName).normalize();
            Resource resource = new UrlResource(file.toURI());
            if (resource.exists()) {
                return resource;
            } else {
                log.error("cannot load file file {}", fileName);
                return null;
            }
        } catch (MalformedURLException ex) {
            log.error("cannot load file as resource", ex);
            return null;
        }
    }

    @Override
    public String loadFileAsBase64(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                File file = resource.getFile();
                return Base64.encodeBase64String(fileToByteArray(file));
            } else {
                log.error("file not found");
                return null;
            }
        } catch (IOException ex) {
            log.error("file not found", ex);
            return null;
        }
    }

    private byte[] fileToByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            log.error("file not found", e);
        } finally {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("file not found", e);
                }
            }

        }

        return bytesArray;

    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }

    @Override
    public void deleteFile(String imgNameExist) {
        if (Objects.isNull(imgNameExist)) {
            return;
        }
        if (imgNameExist.trim().isEmpty()) {
            return;
        }
        Path filePath = this.fileStorageLocation.resolve(imgNameExist).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) return;
            File file = resource.getFile();
            if (!file.exists()) return;
            if (file.delete()) {
                log.debug("file has been deleted");
            }
        } catch (IOException e) {
            log.error("file not found", e);
        }

    }

    @Override
    public void deleteFile(String folder, String fileName) {
        File file = new File(getFileStorageLocation().toString() + File.separator + folder + File.separator + fileName);
        if (file.exists() && file.delete()) {
            log.info("filde deleted {}", fileName);
        }

    }


}
