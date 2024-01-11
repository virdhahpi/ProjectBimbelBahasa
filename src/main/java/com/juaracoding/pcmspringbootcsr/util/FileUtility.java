package com.juaracoding.pcmspringbootcsr.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtility {
    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);//set directory buat mendaratkan file
        if (!Files.exists(uploadPath)) {//cek apakah directory ada
            Files.createDirectories(uploadPath);//jika tidak ada maka buat directory
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {// convert dari multipart ke inputstream
            Path filePath = uploadPath.resolve(fileName);// masukin ke object path
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);// proses copy dengan attribute replace yang ada
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }
}