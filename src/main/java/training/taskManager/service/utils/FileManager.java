package training.taskManager.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
@Slf4j
public class FileManager {
    private final String DIRECTORY_PATH = "C:\\test\\";

    public void upload(InputStream data, String key) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, key);
        Path file = Files.createFile(path);

        try (FileOutputStream stream = new FileOutputStream(file.toString())) {
            byte[] bytes = data.readAllBytes();
            stream.write(bytes);
        } catch (Exception e) {
            log.info("UPLOAD EXCEPTION - {}", e.getMessage());
        }

        data.close();
    }

    public String generateKey(String filename) {
        String key = filename + LocalDateTime.now();
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public Resource download(String fileKey) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, fileKey);

        return new UrlResource(path.toUri());
    }

    public void delete(String fileKey) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, fileKey);
        Files.delete(path);
    }
}
