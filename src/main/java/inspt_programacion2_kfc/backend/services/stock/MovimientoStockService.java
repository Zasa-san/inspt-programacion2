package inspt_programacion2_kfc.backend.services.stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.MovimientoStock;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.stock.MovimientoStockRepository;

@Service
@Slf4j
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;

    public MovimientoStockService(MovimientoStockRepository movimientoStockRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
    }

    @Transactional
    public void registrarMovimiento(ProductoEntity producto, TipoMovimiento tipo, int cantidad, String motivo, Long pedidoId) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        MovimientoStock mov = new MovimientoStock();
        mov.setProducto(producto);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setMotivo(motivo);
        mov.setPedidoId(pedidoId);
        movimientoStockRepository.save(mov);
        log.info("Stock del producto id {} actualizado correctamente - idpedido {}", producto.getId(), pedidoId);
    }

    public int calcularStockProducto(Long productoId) {
        List<MovimientoStock> movimientos = movimientoStockRepository.findByProductoId(productoId);
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
            result.put(p.getId(), calcularStockProducto(p.getId()));
        }
        return result;
    }
}


