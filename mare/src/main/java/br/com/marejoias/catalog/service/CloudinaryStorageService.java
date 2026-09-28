package br.com.marejoias.catalog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryStorageService implements ImageStorageService {

    @Override
    public String uploadImage(MultipartFile file) {
        return null; // O código real de integração com a API do Cloudinary vai entrar aqui
    }
}