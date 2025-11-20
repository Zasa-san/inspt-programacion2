package inspt_programacion2_kfc.backend.services.orders;

import inspt_programacion2_kfc.backend.exceptions.cart.CartEmptyException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderAlreadyDeliveredException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderCancelledException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderNotFoundException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.stock.StockException;
import inspt_programacion2_kfc.backend.models.orders.CartItemDto;
import inspt_programacion2_kfc.backend.models.orders.EstadoPedido;
import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.orders.Pedido;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.orders.PedidoRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    private Pedido findByIdPedido(Long idPedido) {
        Optional<Pedido> pedido = pedidoRepository.findById(idPedido);
        return pedido.orElse(null);
    }

    private ProductoEntity findByIdProducto(Long idProducto) {
        Optional<ProductoEntity> producto = productoRepository.findById(idProducto);
        return producto.orElse(null);
    }

    @Transactional
    public void crearPedidoDesdeCarrito(List<CartItemDto> items) {
        crearPedidoDesdeCarrito(items, EstadoPedido.CREADO);
    }

    /**
     * Crea un pedido a partir del carrito, validando stock y permitiendo
     * especificar el estado inicial (por ejemplo CREADO o PAGADO).
     */
    @Transactional
    public void crearPedidoDesdeCarrito(List<CartItemDto> items, EstadoPedido estadoInicial) {
        if (items == null || items.isEmpty()) {
            throw new CartEmptyException("El carrito está vacío.");
        }

        // Se valida el stock
        for (CartItemDto cartItem : items) {
            Long productoId = cartItem.getProductoId();
            int stockActual = movimientoStockService.calcularStockProducto(productoId);
            if (stockActual < cartItem.getQuantity()) {
                String nombre = cartItem.getProductoName() != null ? cartItem.getProductoName() : "";
                throw new StockException("No hay stock suficiente para el producto: " + nombre);
            }
        }

        Pedido pedido = new Pedido();
        pedido.setEstado(estadoInicial);

        int total = 0;
        for (CartItemDto cartItem : items) {
            Long productoId = cartItem.getProductoId();
            if (productoId == null) throw new ProductException("Producto con id nulo, revise la base de datos.");

            ProductoEntity producto = findByIdProducto(productoId);

            if (producto != null) {
                ItemPedido item = new ItemPedido();
                item.setProducto(producto);
                item.setQuantity(cartItem.getQuantity());
                item.setUnitPrice(producto.getPrice());
                item.setSubtotal(producto.getPrice() * cartItem.getQuantity());
                total += item.getSubtotal();
                pedido.addItem(item);
            }

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
        if (id == null || id < 0) throw new OrderNotFoundException("ID pedido invalido.");

        Pedido pedido = findByIdPedido(id);

        if (pedido == null) {
            throw new OrderNotFoundException(String.format("Pedido con id %s no encontrado.", id));
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OrderCancelledException("El pedido ya está cancelado.");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new OrderAlreadyDeliveredException("No se puede cancelar un pedido ya entregado.");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        Pedido guardado = pedidoRepository.save(pedido);

        for (ItemPedido item : guardado.getItems()) {
            movimientoStockService.registrarMovimiento(
                    item.getProducto(),
                    TipoMovimiento.ENTRADA,
                    item.getQuantity(),
                    "Cancelación pedido #" + guardado.getId(),
                    guardado.getId());
        }
    }

    @Transactional
    public void marcarEntregado(Long id) {
        if (id == null || id < 0) throw new OrderNotFoundException("ID pedido invalido.");

        Pedido pedido = findByIdPedido(id);

        if (pedido == null) {
            throw new OrderNotFoundException(String.format("Pedido con id %s no encontrado.", id));
        }

        if (pedido.getEstado() != EstadoPedido.PAGADO) {
            throw new OrderException("Sólo se pueden marcar como ENTREGADO los pedidos en estado PAGADO.");
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void marcarComoPagado(Long id) {
        if (id == null || id < 0) throw new OrderNotFoundException("ID pedido invalido.");

        Pedido pedido = findByIdPedido(id);

        if (pedido == null) {
            throw new OrderNotFoundException(String.format("Pedido con id %s no encontrado.", id));
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OrderCancelledException("No se puede marcar como pagado un pedido cancelado.");
        }
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new OrderException("El pedido ya está marcado como pagado.");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new OrderAlreadyDeliveredException("El pedido ya está entregado.");
        }

        pedido.setEstado(EstadoPedido.PAGADO);
        pedidoRepository.save(pedido);
    }
}
