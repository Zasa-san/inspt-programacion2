package inspt_programacion2_kfc.backend.services.stock;

import inspt_programacion2_kfc.backend.exceptions.stock.StockException;
import inspt_programacion2_kfc.backend.models.bundles.ProductoComponenteEntity;
import inspt_programacion2_kfc.backend.models.ingredients.IngredienteEntity;
import inspt_programacion2_kfc.backend.models.ingredients.ProductoIngredienteEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.MovimientoStock;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.bundles.ProductoComponenteRepository;
import inspt_programacion2_kfc.backend.repositories.ingredients.ProductoIngredienteRepository;
import inspt_programacion2_kfc.backend.repositories.stock.MovimientoStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final ProductoComponenteRepository productoComponenteRepository;

    public MovimientoStockService(
            MovimientoStockRepository movimientoStockRepository,
            ProductoIngredienteRepository productoIngredienteRepository,
            ProductoComponenteRepository productoComponenteRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.productoComponenteRepository = productoComponenteRepository;
    }

    @Transactional
    public void registrarMovimiento(IngredienteEntity ingrediente, TipoMovimiento tipo, int cantidad, String motivo, Long pedidoId) {
        if (cantidad <= 0) {
            throw new StockException("La cantidad debe ser mayor a cero.");
        }

        if (ingrediente != null) {
            movimientoStockRepository.save(new MovimientoStock(ingrediente, tipo, cantidad, motivo, pedidoId));
        }
    }

    public int calcularStockIngrediente(Long ingredienteId) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByIngredienteId(ingredienteId);
        int stock = 0;
        for (MovimientoStock m : movimientos) {
            if (m.getTipo() == TipoMovimiento.ENTRADA) {
                stock += m.getCantidad();
            } else if (m.getTipo() == TipoMovimiento.SALIDA) {
                stock -= m.getCantidad();
            }
        }
        return stock;
    }

    public int calcularStockProducto(Long productoId) {
        return calcularStockProducto(productoId, new HashSet<>());
    }

    private int calcularStockProducto(Long productoId, Set<Long> visiting) {
        if (productoId == null) {
            return 0;
        }
        if (!visiting.add(productoId)) {
            return 0;
        }

        // 1) Si tiene receta, el stock deriva de ingredientes
        List<ProductoIngredienteEntity> receta = productoIngredienteRepository.findByProductoId(productoId);
        if (receta != null && !receta.isEmpty()) {
            int maxUnidades = Integer.MAX_VALUE;
            for (ProductoIngredienteEntity linea : receta) {
                int cantidadPorProducto = linea.getCantidad();
                if (cantidadPorProducto <= 0) {
                    continue;
                }
                Long ingredienteId = linea.getIngrediente() != null ? linea.getIngrediente().getId() : null;
                if (ingredienteId == null) {
                    continue;
                }
                int stockIngrediente = calcularStockIngrediente(ingredienteId);
                int posibles = stockIngrediente / cantidadPorProducto;
                maxUnidades = Math.min(maxUnidades, posibles);
            }
            visiting.remove(productoId);
            return maxUnidades == Integer.MAX_VALUE ? 0 : Math.max(0, maxUnidades);
        }

        // 2) Si no tiene receta, puede ser bundle: stock deriva de sus componentes
        List<ProductoComponenteEntity> componentes = productoComponenteRepository.findByProductoId(productoId);
        if (componentes == null || componentes.isEmpty()) {
            visiting.remove(productoId);
            return 0;
        }

        int maxUnidades = Integer.MAX_VALUE;
        for (ProductoComponenteEntity linea : componentes) {
            int cantidadPorBundle = linea.getCantidad();
            if (cantidadPorBundle <= 0 || linea.getComponente() == null || linea.getComponente().getId() == null) {
                continue;
            }
            int stockComponente = calcularStockProducto(linea.getComponente().getId(), visiting);
            int posibles = stockComponente / cantidadPorBundle;
            maxUnidades = Math.min(maxUnidades, posibles);
        }

        visiting.remove(productoId);
        return maxUnidades == Integer.MAX_VALUE ? 0 : Math.max(0, maxUnidades);
    }

    public void validarDisponibilidadPorIngredientes(Map<Long, Integer> cantidadesPorProducto) {
        if (cantidadesPorProducto == null || cantidadesPorProducto.isEmpty()) {
            return;
        }

        Map<Long, Integer> productosExpandidos = expandirBundles(cantidadesPorProducto);

        Map<Long, Integer> requerimientosPorIngrediente = new HashMap<>();
        Map<Long, String> nombreIngredientePorId = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : productosExpandidos.entrySet()) {
            Long productoId = entry.getKey();
            int unidadesProducto = entry.getValue() != null ? entry.getValue() : 0;
            if (productoId == null || unidadesProducto <= 0) {
                continue;
            }

            List<ProductoIngredienteEntity> receta = productoIngredienteRepository.findByProductoId(productoId);
            if (receta == null || receta.isEmpty()) {
                throw new StockException("Hay productos sin receta/ingredientes configurados.");
            }

            for (ProductoIngredienteEntity linea : receta) {
                IngredienteEntity ingrediente = linea.getIngrediente();
                if (ingrediente == null || ingrediente.getId() == null) {
                    continue;
                }
                int cantidadPorProducto = linea.getCantidad();
                if (cantidadPorProducto <= 0) {
                    continue;
                }
                int requerido = unidadesProducto * cantidadPorProducto;
                requerimientosPorIngrediente.merge(ingrediente.getId(), requerido, Integer::sum);
                nombreIngredientePorId.putIfAbsent(ingrediente.getId(), ingrediente.getName());
            }
        }

        for (Map.Entry<Long, Integer> req : requerimientosPorIngrediente.entrySet()) {
            Long ingredienteId = req.getKey();
            int requerido = req.getValue() != null ? req.getValue() : 0;
            int stock = calcularStockIngrediente(ingredienteId);
            if (stock < requerido) {
                String nombre = nombreIngredientePorId.getOrDefault(ingredienteId, "Ingrediente");
                throw new StockException(String.format("Stock insuficiente de %s. Requerido: %d, disponible: %d.", nombre, requerido, stock));
            }
        }
    }

    private Map<Long, Integer> expandirBundles(Map<Long, Integer> cantidadesPorProducto) {
        Map<Long, Integer> result = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Long productoId = entry.getKey();
            int unidades = entry.getValue() != null ? entry.getValue() : 0;
            if (productoId == null || unidades <= 0) {
                continue;
            }
            expandirProducto(productoId, unidades, result, new HashSet<>());
        }
        return result;
    }

    private void expandirProducto(Long productoId, int unidades, Map<Long, Integer> acumulador, Set<Long> visiting) {
        if (productoId == null || unidades <= 0) {
            return;
        }
        if (!visiting.add(productoId)) {
            throw new StockException("Configuración inválida: bundle cíclico.");
        }

        // si tiene receta, es "producto base" (o al menos evaluable por ingredientes)
        List<ProductoIngredienteEntity> receta = productoIngredienteRepository.findByProductoId(productoId);
        if (receta != null && !receta.isEmpty()) {
            acumulador.merge(productoId, unidades, Integer::sum);
            visiting.remove(productoId);
            return;
        }

        // si no tiene receta, intentamos tratarlo como bundle
        List<ProductoComponenteEntity> componentes = productoComponenteRepository.findByProductoId(productoId);
        if (componentes == null || componentes.isEmpty()) {
            acumulador.merge(productoId, unidades, Integer::sum);
            visiting.remove(productoId);
            return;
        }

        for (ProductoComponenteEntity linea : componentes) {
            if (linea.getComponente() == null || linea.getComponente().getId() == null) {
                continue;
            }
            int cantidadPorBundle = linea.getCantidad();
            if (cantidadPorBundle <= 0) {
                continue;
            }
            expandirProducto(linea.getComponente().getId(), unidades * cantidadPorBundle, acumulador, visiting);
        }

        visiting.remove(productoId);
    }

    public Map<Long, Integer> calcularStockParaProductos(List<ProductoEntity> productos) {
        Map<Long, Integer> result = new HashMap<>();
        for (ProductoEntity p : productos) {
            result.put(p.getId(), calcularStockProducto(p.getId()));
        }
        return result;
    }

    public Map<Long, Integer> calcularStockParaIngredientes(List<IngredienteEntity> ingredientes) {
        Map<Long, Integer> result = new HashMap<>();
        for (IngredienteEntity i : ingredientes) {
            result.put(i.getId(), calcularStockIngrediente(i.getId()));
        }
        return result;
    }

    @Transactional
    public void registrarMovimientoPorProducto(ProductoEntity producto, TipoMovimiento tipo, int unidadesProducto, String motivo, Long pedidoId) {
        if (unidadesProducto <= 0) {
            throw new StockException("La cantidad debe ser mayor a cero.");
        }
        if (producto == null || producto.getId() == null) {
            throw new StockException("Producto inválido.");
        }

        List<ProductoIngredienteEntity> receta = productoIngredienteRepository.findByProductoId(producto.getId());
        if (receta != null && !receta.isEmpty()) {
            for (ProductoIngredienteEntity linea : receta) {
                IngredienteEntity ingrediente = linea.getIngrediente();
                int cantidadPorProducto = linea.getCantidad();
                if (ingrediente == null || cantidadPorProducto <= 0) {
                    continue;
                }
                int cantidadMovimiento = unidadesProducto * cantidadPorProducto;
                registrarMovimiento(ingrediente, tipo, cantidadMovimiento, motivo, pedidoId);
            }
            return;
        }

        // bundle: aplicar movimientos a sus componentes
        List<ProductoComponenteEntity> componentes = productoComponenteRepository.findByProductoId(producto.getId());
        if (componentes == null || componentes.isEmpty()) {
            throw new StockException("El producto no tiene receta ni componentes configurados.");
        }
        for (ProductoComponenteEntity linea : componentes) {
            if (linea.getComponente() == null || linea.getComponente().getId() == null) {
                continue;
            }
            int cantidadPorBundle = linea.getCantidad();
            if (cantidadPorBundle <= 0) {
                continue;
            }
            registrarMovimientoPorProducto(linea.getComponente(), tipo, unidadesProducto * cantidadPorBundle, motivo, pedidoId);
        }
    }
}


