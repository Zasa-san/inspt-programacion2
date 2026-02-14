package inspt_programacion2_kfc.backend.helpers;

import org.springframework.stereotype.Component;

import inspt_programacion2_kfc.backend.models.pedidos.ItemPedido;
import inspt_programacion2_kfc.backend.models.pedidos.Pedido;
import inspt_programacion2_kfc.backend.models.pedidos.PedidoProducto;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Component
public class PedidoHelper {

    private final MovimientoStockService stockService;

    public PedidoHelper(MovimientoStockService stockService) {
        this.stockService = stockService;
    }

    public void registrarMovimientoStock(Pedido guardado, TipoMovimiento movimiento, String motivo) {
        for (ItemPedido item : guardado.getItems()) {
            for (PedidoProducto customizacion : item.getCustomizaciones()) {
                if (customizacion.getIngrediente() == null) {
                    continue;
                }
                int cantidad = customizacion.getCantidad() * item.getQuantity();
                stockService.registrarMovimiento(
                        customizacion.getIngrediente().getItem(),
                        movimiento,
                        cantidad,
                        motivo + guardado.getId(),
                        guardado.getId());
            }
        }
    }

    public int obtenerStockPorIdProducto(Long idProducto) {
        return stockService.calcularStockItem(idProducto);
    }
}
