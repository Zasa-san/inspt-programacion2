package inspt_programacion2_kfc.backend.helpers;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
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
                if (customizacion.getItemStockIdSnapshot() == null) {
                    continue;
                }
                int cantidad = customizacion.getCantidad() * item.getQuantity();
                stockService.registrarMovimiento(
                        customizacion.getItemStockIdSnapshot(),
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

    public PedidoProducto getPedidoProducto(CartItemDto cartItem, Ingrediente ingrediente) {
        PedidoProducto customizacion = new PedidoProducto();
        customizacion.setIngrediente(ingrediente);
        customizacion.setIngredienteIdSnapshot(ingrediente.getId());
        customizacion.setIngredienteNombre(ingrediente.getItem().getName());
        customizacion.setItemStockIdSnapshot(ingrediente.getItem().getId());
        customizacion.setItemStockNombre(ingrediente.getItem().getName());
        customizacion.setCantidad(ingrediente.getCantidad());
        int precioUnitarioExtra = ingrediente.getCantidad() * ingrediente.getItem().getPrice();
        customizacion.setPrecioUnitarioExtra(precioUnitarioExtra);
        customizacion.setSubtotalExtra(precioUnitarioExtra * cartItem.getQuantity());
        return customizacion;
    }

}
