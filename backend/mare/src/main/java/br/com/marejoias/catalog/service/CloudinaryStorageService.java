package br.com.marejoias.catalog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryStorageService implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorageService.class);

    private final Cloudinary cloudinary;

    public CloudinaryStorageService(@Value("${cloudinary.url}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
        log.info("CloudinaryStorageService inicializado com sucesso para o cloud configurado.");
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("Iniciando o upload do arquivo '{}' para o Cloudinary...", originalFilename);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String secureUrl = uploadResult.get("secure_url").toString();
            log.info("Upload realizado com sucesso! URL gerada: {}", secureUrl);

            return secureUrl;

        } catch (IOException e) {
            log.error("Erro crítico ao processar os bytes do arquivo '{}': {}", originalFilename, e.getMessage(), e);
            throw new RuntimeException("Falha ao processar upload da imagem: " + e.getMessage(), e);
        }
    }
}