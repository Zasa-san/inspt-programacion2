package inspt_programacion2_kfc.backend.services.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String saveFile(MultipartFile file, String subdirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("No se puede guardar un archivo vacío");
        }

        // Create subdirectory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subdirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative URL
        return "/uploads/" + subdirectory + "/" + filename;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Extract path from URL (remove /uploads/ prefix)
            if (fileUrl.startsWith("/uploads/")) {
                String relativePath = fileUrl.substring("/uploads/".length());
                Path filePath = Paths.get(uploadDir, relativePath);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw - file deletion is not critical
            System.err.println("Error deleting file: " + fileUrl + " - " + e.getMessage());
        }
    }
}
