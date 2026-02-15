package inspt_programacion2_kfc.frontend.services;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    //private final ProductoService productoService;
    //private final CustomizacionesService customizacionesService;
/*
    public ProductService(ProductoService productoService, CustomizacionesService customizacionesService) {
        this.productoService = productoService;
      //  this.customizacionesService = customizacionesService;
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
                .map(c -> new Customizacion(String.valueOf(c.getId()), c.getNombre(), c.getPriceModifier(), null, c.getTipo().name(), c.getGrupo()))
                .collect(Collectors.toList());
    }

    private String getImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return AppConstants.DEFAULT_IMG_URL;
        }
        return url;
    }  */
}
