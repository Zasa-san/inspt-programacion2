package inspt_programacion2_kfc.backend.services.bundles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.models.bundles.ProductoComponenteEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.bundles.ProductoComponenteRepository;
import inspt_programacion2_kfc.backend.services.products.ProductoService;

@Service
public class ProductoComponenteService {

    private final ProductoComponenteRepository productoComponenteRepository;
    private final ProductoService productoService;

    public ProductoComponenteService(
            ProductoComponenteRepository productoComponenteRepository,
            ProductoService productoService) {
        this.productoComponenteRepository = productoComponenteRepository;
        this.productoService = productoService;
    }

    public Map<Long, Integer> obtenerComponentesMap(Long productoId) {
        List<ProductoComponenteEntity> componentes = productoComponenteRepository.findByProductoId(productoId);
        Map<Long, Integer> result = new HashMap<>();
        for (ProductoComponenteEntity linea : componentes) {
            if (linea.getComponente() != null && linea.getComponente().getId() != null) {
                result.put(linea.getComponente().getId(), linea.getCantidad());
            }
        }
        return result;
    }

    @Transactional
    public void clearComponentes(Long productoId) {
        if (productoId == null) {
            return;
        }
        productoComponenteRepository.deleteByProductoId(productoId);
    }

    @Transactional
    public void setComponentes(ProductoEntity producto, List<Long> componenteIds, List<Integer> cantidades) {
        if (producto == null || producto.getId() == null) {
            throw new ProductException("Producto inválido para asignar componentes.");
        }
        if (componenteIds == null || cantidades == null || componenteIds.size() != cantidades.size()) {
            throw new ProductException("Datos de componentes incompletos.");
        }

        productoComponenteRepository.deleteByProductoId(producto.getId());

        boolean any = false;
        for (int idx = 0; idx < componenteIds.size(); idx++) {
            Long componenteId = componenteIds.get(idx);
            int cantidad = cantidades.get(idx) != null ? cantidades.get(idx) : 0;
            if (componenteId == null || cantidad <= 0) {
                continue;
            }
            if (componenteId.equals(producto.getId())) {
                throw new ProductException("Un producto no puede contenerse a sí mismo como componente.");
            }

            ProductoEntity componente = productoService.findById(componenteId);
            if (componente == null) {
                continue;
            }

            ProductoComponenteEntity linea = new ProductoComponenteEntity();
            linea.setProducto(producto);
            linea.setComponente(componente);
            linea.setCantidad(cantidad);
            productoComponenteRepository.save(linea);
            any = true;
        }

        if (!any) {
            throw new ProductException("El bundle debe incluir al menos 1 componente con cantidad > 0.");
        }
    }
}

