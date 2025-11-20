package inspt_programacion2_kfc.frontend.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private final ProductoService productoService;

    public ProductImageController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("empty file");
            }

            String original = file.getOriginalFilename();
            if (original == null || original.isBlank()) {
                original = "file";
            }
            int idx = original.lastIndexOf('.');
            String ext = idx >= 0 ? original.substring(idx) : "";
            String filename = UUID.randomUUID() + ext;

            Path dir = Path.of(uploadDir, "products");
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String publicPath = "/uploads/products/" + filename;

            ProductoEntity producto = productoService.findById(id);
            if (producto == null) {
                // remove uploaded file if product not found
                try {
                    Files.deleteIfExists(target);
                } catch (IOException ex) {
                    /* ignore */ }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("product not found");
            }

            producto.setImgUrl(publicPath);
            productoService.save(producto);

            return ResponseEntity.ok(publicPath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
