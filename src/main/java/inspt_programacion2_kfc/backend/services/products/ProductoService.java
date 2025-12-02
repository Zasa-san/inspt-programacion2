package inspt_programacion2_kfc.backend.services.products;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.files.FileUploadService;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FileUploadService fileUploadService;

    public ProductoService(ProductoRepository productoRepository, FileUploadService fileUploadService) {
        this.productoRepository = productoRepository;
        this.fileUploadService = fileUploadService;
    }

    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }

    public List<ProductoEntity> findAllAvailable() {
        return productoRepository.findByAvailableTrue();
    }

    public ProductoEntity findById(Long id) {
        if (id == null) {
            throw new ProductException("ID producto invalido.");
        }
        return productoRepository.findById(id).orElse(null);
    }

    public void create(ProductoEntity producto, MultipartFile imageFile) {
        Objects.requireNonNull(producto, "Producto no puede ser nulo");

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadService.saveFile(imageFile, "products");
                producto.setImgUrl(imageUrl);
            } catch (IOException e) {
                throw new ProductImageException("Error al guardar la imagen del producto", e);
            }
        } else if (producto.getImgUrl() == null || producto.getImgUrl().isBlank()) {
            producto.setImgUrl(AppConstants.DEFAULT_IMG_URL);
        }

        productoRepository.save(producto);
    }

    public void create(ProductoEntity producto) {
        Objects.requireNonNull(producto, "Producto no puede ser nulo");
        productoRepository.save(producto);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new ProductNotFoundException("ID no puede ser NULL.");
        }

        ProductoEntity producto = findById(id);
        if (producto == null) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }

        if (producto.getImgUrl() != null && !producto.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
            fileUploadService.deleteFile(producto.getImgUrl());
        }

        productoRepository.deleteById(id);
    }

    public void update(Long id, ProductoEntity updatedData, MultipartFile imageFile, boolean removeImage) {
        if (id == null) {
            throw new ProductNotFoundException("ID no puede ser NULL.");
        }

        ProductoEntity existing = findById(id);
        if (existing == null) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (existing.getImgUrl() != null && !existing.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
                    fileUploadService.deleteFile(existing.getImgUrl());
                }
                String imageUrl = fileUploadService.saveFile(imageFile, "products");
                existing.setImgUrl(imageUrl);
            } catch (IOException e) {
                throw new ProductImageException("Error al guardar la imagen del producto", e);
            }
        } else if (removeImage) {
            if (existing.getImgUrl() != null && !existing.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
                fileUploadService.deleteFile(existing.getImgUrl());
            }
            existing.setImgUrl(AppConstants.DEFAULT_IMG_URL);
        }

        productoRepository.save(existing);
    }

    public void toggleAvailability(Long id) {
        ProductoEntity producto = findById(id);
        if (producto != null) {
            producto.setAvailable(!producto.isAvailable());
            productoRepository.save(producto);
        }
    }
}
