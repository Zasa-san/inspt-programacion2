package inspt_programacion2_kfc.frontend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import org.springframework.stereotype.Service;

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
                .map(p -> {
                    String img = p.getImgUrl();
                    if (img == null || img.isBlank()) {
                        img = "/img/producto-default.png";
                    }
                    return new Producto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), img);
                })
                .collect(Collectors.toList());
    }

    public Producto findById(Long id) {
        ProductoEntity productoBack = productoService.findById(id);

        return new Producto(productoBack.getId(), productoBack.getName(), productoBack.getDescription(), productoBack.getPrice(), getImageUrl(productoBack.getImgUrl()));
    }

    private String getImageUrl(String url) {
        if (url.isBlank()) {
            url = AppConstants.DEFAULT_IMG_URL;
        }
        return url;
    }
}
