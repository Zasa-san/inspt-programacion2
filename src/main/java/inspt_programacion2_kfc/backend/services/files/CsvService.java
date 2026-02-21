package inspt_programacion2_kfc.backend.services.files;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.models.pedidos.ItemPedido;
import inspt_programacion2_kfc.backend.models.pedidos.Pedido;
import inspt_programacion2_kfc.backend.models.stock.MovimientoStock;
import inspt_programacion2_kfc.backend.services.pedidos.PedidoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Service
public class CsvService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PedidoService pedidoService;
    private final MovimientoStockService movimientoStockService;

    public CsvService(PedidoService pedidoService, MovimientoStockService movimientoStockService) {
        this.pedidoService = pedidoService;
        this.movimientoStockService = movimientoStockService;
    }

    public byte[] exportarPedidosCsv() {
        List<Pedido> pedidos = pedidoService.findAllPedidosSorted();

        StringBuilder csv = new StringBuilder();
        csv.append("pedido_id,fecha,estado,total_centavos,total,cantidad_items,items\n");

        for (Pedido pedido : pedidos) {
            int cantidadItems = pedido.getItems() == null ? 0 : pedido.getItems().size();
            String items = pedido.getItems() == null ? "" : pedido.getItems().stream()
                    .map(this::descripcionItem)
                    .collect(Collectors.joining(" | "));

            csv.append(pedido.getId()).append(',')
                    .append(escapeCsv(pedido.getCreatedAt().format(DATE_FORMATTER))).append(',')
                    .append(escapeCsv(pedido.getEstado().name())).append(',')
                    .append(pedido.getTotal()).append(',')
                    .append(escapeCsv(formatearMoneda(pedido.getTotal()))).append(',')
                    .append(cantidadItems).append(',')
                    .append(escapeCsv(items))
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportarMovimientosStockCsv() {
        List<MovimientoStock> movimientos = movimientoStockService.findAllMovimientos();

        StringBuilder csv = new StringBuilder();
        csv.append("movimiento_id,fecha,item_id,item,tipo,cantidad,motivo,pedido_id\n");

        for (MovimientoStock movimiento : movimientos) {
            Long itemId = movimiento.getItem() != null ? movimiento.getItem().getId() : null;
            String itemNombre = movimiento.getItem() != null ? movimiento.getItem().getName() : "(item eliminado)";

            csv.append(movimiento.getId()).append(',')
                    .append(escapeCsv(movimiento.getFecha().format(DATE_FORMATTER))).append(',')
                    .append(itemId == null ? "" : itemId).append(',')
                    .append(escapeCsv(itemNombre)).append(',')
                    .append(escapeCsv(movimiento.getTipo().name())).append(',')
                    .append(movimiento.getCantidad()).append(',')
                    .append(escapeCsv(movimiento.getMotivo())).append(',')
                    .append(movimiento.getPedidoId() == null ? "" : movimiento.getPedidoId())
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String descripcionItem(ItemPedido item) {
        String nombre = item.getProductoNombre() != null ? item.getProductoNombre() : "Producto";
        return nombre + " x" + item.getQuantity();
    }

    private String formatearMoneda(int centavos) {
        return String.format(Locale.US, "%.2f", centavos / 100.0);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
