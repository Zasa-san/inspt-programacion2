package inspt_programacion2_kfc.backend.services.orders;

import inspt_programacion2_kfc.backend.exceptions.cart.CartEmptyException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderAlreadyDeliveredException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderCancelledException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderException;
import inspt_programacion2_kfc.backend.exceptions.order.OrderNotFoundException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.stock.StockException;
import inspt_programacion2_kfc.backend.helpers.PedidoHelper;
import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.backend.models.orders.EstadoPedido;
import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.orders.Pedido;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.orders.ItemsPedidoRepository;
import inspt_programacion2_kfc.backend.repositories.orders.PedidoRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.utils.PedidoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemsPedidoRepository itemsPedidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoHelper pedidoHelper;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         ItemsPedidoRepository itemsPedidoRepository, PedidoHelper pedidoHelper) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.itemsPedidoRepository = itemsPedidoRepository;
        this.pedidoHelper = pedidoHelper;
    }

    public List<ItemPedido> findAll() {
        return itemsPedidoRepository.findAll();
    }

    private Pedido findByIdPedido(Long idPedido) {
        if (idPedido == null) {
            throw new OrderNotFoundException("ID pedido invalido.");
        }
        return pedidoRepository.findById(idPedido).orElse(null);
    }

    private ProductoEntity findByIdProducto(Long idProducto) {
        if (idProducto == null) {
            throw new ProductException("ID producto invalido.");
        }
        return productoRepository.findById(idProducto).orElse(null);
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

        // Agrupar cantidades por producto para validar stock correctamente
        // (un producto puede aparecer múltiples veces con diferentes customizaciones)
        Map<Long, Integer> cantidadesPorProducto = new HashMap<>();
        Map<Long, String> nombresPorProducto = new HashMap<>();
        
        for (CartItemDto cartItem : items) {
            Long productoId = cartItem.getProductoId();
            // Fusiona los productos por clave en el map y suma las cantidades
            cantidadesPorProducto.merge(productoId, cartItem.getQuantity(), Integer::sum);
            if (cartItem.getProductoName() != null) {
                nombresPorProducto.putIfAbsent(productoId, cartItem.getProductoName());
            }
        }
        
        // Validar stock para cada producto (cantidad total)
        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Long productoId = entry.getKey();
            int cantidadTotal = entry.getValue();
            int stockActual = pedidoHelper.obtenerStockPorIdProducto(productoId);
            
            if (stockActual < cantidadTotal) {
                String nombre = nombresPorProducto.getOrDefault(productoId, "");
                throw new StockException(String.format("No hay stock suficiente para el producto: %s", nombre));
            }
        }

        Pedido pedido = new Pedido();
        pedido.setEstado(estadoInicial);

        int total = 0;
        for (CartItemDto cartItem : items) {
            Long productoId = cartItem.getProductoId();
            if (productoId != null) {
                ProductoEntity producto = findByIdProducto(productoId);
                if (producto != null) {
                    ItemPedido item = PedidoUtils.mapItemPedido(cartItem, producto);
                    total += item.getSubtotal();
                    pedido.addItem(item);
                }
            }
        }
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        pedidoHelper.registrarMovimientoStock(guardado, TipoMovimiento.SALIDA, AppConstants.MOVIMIENTO_VENTA);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        if (id == null || id < 0) {
            throw new OrderNotFoundException("ID pedido invalido.");
        }

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
        pedidoHelper.registrarMovimientoStock(guardado, TipoMovimiento.ENTRADA, AppConstants.MOVIMIENTO_CANCELACION);
    }

    @Transactional
    public void marcarEntregado(Long id) {
        if (id == null || id < 0) {
            throw new OrderNotFoundException("ID pedido invalido.");
        }

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
        if (id == null || id < 0) {
            throw new OrderNotFoundException("ID pedido invalido.");
        }

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
