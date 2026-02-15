package inspt_programacion2_kfc.frontend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.models.Producto;

@Service
public class ProductService {

    private final ProductoService productoService;

    public ProductService(ProductoService productoService) {
        this.productoService = productoService;
    }

    public List<Producto> findAll() {
        return productoService.findAllAvailable()
                .stream()
                .map(this::mapToProducto)
                .collect(Collectors.toList());
    }

    public Producto findById(Long id) {
        ProductoEntity productoBack = productoService.findById(id);
        return mapToProducto(productoBack);
    }

    private Producto mapToProducto(ProductoEntity p) {
        if (p == null) {
            return null;
        }
        String img = getImageUrl(p.getImgUrl());
        return new Producto(p.getId(), p.getName(), p.getDescription(), p.getPrecioBase(), img);
    }

    private String getImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return AppConstants.DEFAULT_IMG_URL;
        }
        return url;
    }
}
