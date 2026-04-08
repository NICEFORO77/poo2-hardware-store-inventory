package com.niceforo.inventario.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class StorageService {
    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "producto" : file.getOriginalFilename());
        String extension = extractExtension(originalName);

        validateExtension(extension);

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(BAD_REQUEST, "La imagen excede el tamaño permitido de 15 MB");
        }

        return storeBytes(fileBytes(file), extension);
    }

    public String storeBase64(String fotoBase64, String fotoNombre) {
        if (!StringUtils.hasText(fotoBase64)) {
            return null;
        }

        try {
            String originalName = StringUtils.cleanPath(StringUtils.hasText(fotoNombre) ? fotoNombre : "producto.png");
            String extension = extractExtension(originalName);
            validateExtension(extension);

            String base64Content = fotoBase64;
            int commaIndex = fotoBase64.indexOf(',');
            if (commaIndex >= 0) {
                base64Content = fotoBase64.substring(commaIndex + 1);
            }

            byte[] decoded = Base64.getDecoder().decode(base64Content);
            if (decoded.length > MAX_FILE_SIZE_BYTES) {
                throw new ResponseStatusException(BAD_REQUEST, "La imagen excede el tamaño permitido de 15 MB");
            }

            return storeBytes(decoded, extension);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "La imagen enviada no es válida");
        }
    }

    private byte[] fileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "No se pudo leer la imagen");
        }
    }

    private String storeBytes(byte[] bytes, String extension) {
        String filename = UUID.randomUUID() + extension;
        Path destination = Paths.get(uploadDir).resolve(filename).normalize();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "No se pudo guardar la imagen");
        }
    }

    private String extractExtension(String originalName) {
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot >= 0) {
            return originalName.substring(lastDot).toLowerCase();
        }
        return "";
    }

    private void validateExtension(String extension) {
        if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
            throw new ResponseStatusException(BAD_REQUEST, "La foto debe ser jpg, jpeg, png o webp");
        }
    }
}
