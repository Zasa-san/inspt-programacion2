package inspt_programacion2_kfc.backend.services.stock;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.stock.StockException;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.Item;
import inspt_programacion2_kfc.backend.models.stock.MovimientoStock;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.stock.ItemRepository;
import inspt_programacion2_kfc.backend.repositories.stock.MovimientoStockRepository;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;
    private final ItemRepository itemRepository;

    public MovimientoStockService(MovimientoStockRepository movimientoStockRepository, ItemRepository itemRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void registrarMovimiento(Item item, TipoMovimiento tipo, int cantidad, String motivo, Long pedidoId) {
        if (cantidad <= 0) {
            throw new StockException("La cantidad debe ser mayor a cero.");
        }

        if (item != null) {
            movimientoStockRepository.save(new MovimientoStock(item, tipo, cantidad, motivo, pedidoId));
        }
    }

    @Transactional
    public void registrarMovimiento(Long itemId, TipoMovimiento tipo, int cantidad, String motivo, Long pedidoId) {
        if (itemId == null) {
            return;
        }
        itemRepository.findById(itemId)
                .ifPresent(item -> registrarMovimiento(item, tipo, cantidad, motivo, pedidoId));
    }

    public int calcularStockItem(Long itemId) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByItemId(itemId);
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

    public Map<Long, Integer> calcularStockParaProductos(List<ProductoEntity> productos) {
        Map<Long, Integer> result = new HashMap<>();
        for (ProductoEntity p : productos) {
            result.put(p.getId(), calcularStockProducto(p));
        }
        return result;
    }

    public int calcularStockProducto(ProductoEntity producto) {
        if (producto == null || producto.getGruposIngredientes() == null || producto.getGruposIngredientes().isEmpty()) {
            return 0;
        }

        int stockMaximoPosible = Integer.MAX_VALUE;
        boolean tieneIngredientesRequeridos = false;

        for (GrupoIngrediente grupo : producto.getGruposIngredientes()) {
            if (grupo == null || grupo.getIngredientes() == null || grupo.getIngredientes().isEmpty()) {
                continue;
            }

            List<Ingrediente> defaultsGrupo = grupo.getIngredientes().stream()
                    .filter(Ingrediente::isSeleccionadoPorDefecto)
                    .toList();

            List<Ingrediente> requeridos = List.of();
            if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OBLIGATORIO) {
                requeridos = defaultsGrupo.isEmpty() ? grupo.getIngredientes() : defaultsGrupo;
            } else if (!defaultsGrupo.isEmpty()) {
                if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OPCIONAL_UNICO) {
                    requeridos = List.of(defaultsGrupo.getFirst());
                } else {
                    requeridos = defaultsGrupo;
                }
            }

            for (Ingrediente ingrediente : requeridos) {
                if (ingrediente == null || ingrediente.getItem() == null) {
                    return 0;
                }

                int cantidadIngrediente = ingrediente.getCantidad();
                if (cantidadIngrediente <= 0) {
                    return 0;
                }

                int stockItem = calcularStockItem(ingrediente.getItem().getId());
                int stockPosibleConIngrediente = Math.max(0, stockItem / cantidadIngrediente);
                stockMaximoPosible = Math.min(stockMaximoPosible, stockPosibleConIngrediente);
                tieneIngredientesRequeridos = true;
            }
        }

        return tieneIngredientesRequeridos ? stockMaximoPosible : 0;
    }

    public List<MovimientoStock> findAllMovimientos() {
        return movimientoStockRepository.findAllByOrderByFechaDesc();
    }

    public List<MovimientoStock> findMovimientosByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoStockRepository.findAllByFechaBetweenOrderByFechaDesc(desde, hasta);
    }
}
