package app.utils;

import app.back.exception.BackInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${storage.path}")
    private String storagePath;

    private final Logger log = LoggerFactory.getLogger(FileService.class);

    public InputStream storeFile(String folderName, MultipartFile multipartFile, String fileName) {
        if(storagePath.endsWith("/") && folderName.startsWith("/")) {
            folderName = folderName.substring(1);
        }
        var folderDirectory = storagePath + folderName;
        var folderPath = Paths.get(folderDirectory);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectories(folderPath);
            } catch(IOException e) {
                log.error("Une erreur est survenue à la création du dossier " + folderDirectory + ".", e);
                throw new BackInternalException("Une erreur est survenue à l'écriture du fichier.");
            }
        }

        if(folderDirectory.endsWith("/") && fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        var filePath = folderDirectory + "/" + fileName;
        Path path = Paths.get(filePath);
        try {
            Files.write(path, multipartFile.getBytes());
        } catch(IOException e) {
            log.error("Une erreur est survenue à l'écriture d'un fichier", e);
            throw new BackInternalException("Une erreur est survenue à l'écriture du fichier.");
        }

        return getFileInputStream(folderName, multipartFile.getOriginalFilename());
    }

    public InputStream getFileInputStream(String folderName, String fileName) {
        var filePath = storagePath + folderName + "/" + fileName;
        Path path = Paths.get(filePath);
        if(!Files.exists(path)) {
            return null;
        }

        try {
            return Files.newInputStream(path);
        } catch(IOException e) {
            log.error("Une erreur est survenue à l'écriture d'un fichier", e);
            throw new BackInternalException("Une erreur est survenue à la lecture du fichier.");
        }
    }

}
