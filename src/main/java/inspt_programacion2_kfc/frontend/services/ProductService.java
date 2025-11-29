package inspt_programacion2_kfc.frontend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.CustomizacionesService;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.frontend.models.Customizacion;
import inspt_programacion2_kfc.frontend.models.Producto;

@Service
public class ProductService {

    private final ProductoService productoService;
    private final CustomizacionesService customizacionesService;

    public ProductService(ProductoService productoService, CustomizacionesService customizacionesService) {
        this.productoService = productoService;
        this.customizacionesService = customizacionesService;
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
        String img = getImageUrl(p.getImgUrl());
        List<Customizacion> customizaciones = mapCustomizaciones(p);
        return new Producto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), img, customizaciones);
    }

    private List<Customizacion> mapCustomizaciones(ProductoEntity producto) {
        List<CustomizacionEntity> entities = customizacionesService.findByProducto(producto);
        return entities.stream()
                .map(c -> new Customizacion(c.getId(), c.getNombre(), c.getPriceModifier(), c.getTipo().name(), c.getGrupo()))
                .collect(Collectors.toList());
    }

    private String getImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return AppConstants.DEFAULT_IMG_URL;
        }
        return url;
    }
}
