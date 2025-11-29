package inspt_programacion2_kfc.backend.helpers;

import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.orders.Pedido;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import org.springframework.stereotype.Component;

@Component
public class PedidoHelper {

    private final MovimientoStockService stockService;

    public PedidoHelper(MovimientoStockService stockService) {
        this.stockService = stockService;
    }


    public void registrarMovimientoStock(Pedido guardado, TipoMovimiento movimiento, String motivo) {
        for (ItemPedido item : guardado.getItems()) {
            stockService.registrarMovimiento(
                    item.getProducto(),
                    movimiento,
                    item.getQuantity(),
                    motivo + guardado.getId(),
                    guardado.getId());
        }
    }

    public int obtenerStockPorIdProducto(Long idProducto) {
        return stockService.calcularStockProducto(idProducto);
    }
}
