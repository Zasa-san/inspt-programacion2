package inspt_programacion2_kfc.backend.services.ingredients;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.models.ingredients.IngredienteEntity;
import inspt_programacion2_kfc.backend.models.ingredients.ProductoIngredienteEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.ingredients.ProductoIngredienteRepository;

@Service
public class ProductoIngredienteService {

    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final IngredienteService ingredienteService;

    public ProductoIngredienteService(
            ProductoIngredienteRepository productoIngredienteRepository,
            IngredienteService ingredienteService) {
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.ingredienteService = ingredienteService;
    }

    public Map<Long, Integer> obtenerRecetaMap(Long productoId) {
        List<ProductoIngredienteEntity> receta = productoIngredienteRepository.findByProductoId(productoId);
        Map<Long, Integer> result = new HashMap<>();
        for (ProductoIngredienteEntity linea : receta) {
            if (linea.getIngrediente() != null && linea.getIngrediente().getId() != null) {
                result.put(linea.getIngrediente().getId(), linea.getCantidad());
            }
        }
        return result;
    }

    @Transactional
    public void clearReceta(Long productoId) {
        if (productoId == null) {
            return;
        }
        productoIngredienteRepository.deleteByProductoId(productoId);
    }

    @Transactional
    public void setReceta(ProductoEntity producto, List<Long> ingredienteIds, List<Integer> cantidades) {
        if (producto == null || producto.getId() == null) {
            throw new ProductException("Producto inválido para asignar receta.");
        }
        if (ingredienteIds == null || cantidades == null || ingredienteIds.size() != cantidades.size()) {
            throw new ProductException("Datos de ingredientes incompletos.");
        }

        productoIngredienteRepository.deleteByProductoId(producto.getId());

        boolean any = false;
        for (int idx = 0; idx < ingredienteIds.size(); idx++) {
            Long ingredienteId = ingredienteIds.get(idx);
            int cantidad = cantidades.get(idx) != null ? cantidades.get(idx) : 0;
            if (ingredienteId == null || cantidad <= 0) {
                continue;
            }

            IngredienteEntity ingrediente = ingredienteService.findById(ingredienteId);
            if (ingrediente == null) {
                continue;
            }

            ProductoIngredienteEntity linea = new ProductoIngredienteEntity();
            linea.setProducto(producto);
            linea.setIngrediente(ingrediente);
            linea.setCantidad(cantidad);
            productoIngredienteRepository.save(linea);
            any = true;
        }

        if (!any) {
            throw new ProductException("La receta debe incluir al menos 1 ingrediente con cantidad > 0.");
        }
    }
}
