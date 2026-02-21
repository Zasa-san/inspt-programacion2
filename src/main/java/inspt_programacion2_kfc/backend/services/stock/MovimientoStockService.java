package inspt_programacion2_kfc.backend.services.stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.stock.StockException;
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
            result.put(p.getId(), calcularStockItem(p.getId()));
        }
        return result;
    }

    public List<MovimientoStock> findAllMovimientos() {
        return movimientoStockRepository.findAllByOrderByFechaDesc();
    }
}
