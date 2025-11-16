package inspt_programacion2_kfc.backend.services.orders;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.models.orders.EstadoPedido;
import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.orders.Pedido;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.orders.PedidoRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.models.CartItem;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public PedidoService(PedidoRepository pedidoRepository,
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public void crearPedidoDesdeCarrito(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        // Se valida el stock
        for (CartItem cartItem : items) {
            Long productoId = cartItem.getProducto().getId();
            int stockActual = movimientoStockService.calcularStockProducto(productoId);
            if (stockActual < cartItem.getQuantity()) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto: "
                        + cartItem.getProducto().getName());
            }
        }

        Pedido pedido = new Pedido();

        int total = 0;
        for (CartItem cartItem : items) {
            Long productoId = cartItem.getProducto().getId();
            ProductoEntity producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));

            ItemPedido item = new ItemPedido();
            item.setProducto(producto);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(producto.getPrice());
            item.setSubtotal(producto.getPrice() * cartItem.getQuantity());

            total += item.getSubtotal();
            pedido.addItem(item);
        }

        pedido.setTotal(total);
        Pedido guardado = pedidoRepository.save(pedido);

        for (ItemPedido item : guardado.getItems()) {
            movimientoStockService.registrarMovimiento(
                    item.getProducto(),
                    TipoMovimiento.SALIDA,
                    item.getQuantity(),
                    "Venta pedido #" + guardado.getId(),
                    guardado.getId());
        }

    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya está cancelado.");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido ya entregado.");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        Pedido guardado = pedidoRepository.save(pedido);

        for (ItemPedido item : guardado.getItems()) {
            movimientoStockService.registrarMovimiento(
                    item.getProducto(),
                    TipoMovimiento.ENTRADA,
                    item.getQuantity(),
                    "Cancelación del pedido #" + guardado.getId(),
                    guardado.getId());
        }
    }
}


