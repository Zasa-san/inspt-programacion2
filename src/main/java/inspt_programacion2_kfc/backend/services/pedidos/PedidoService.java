package inspt_programacion2_kfc.backend.services.pedidos;

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
import inspt_programacion2_kfc.backend.models.pedidos.EstadoPedido;
import inspt_programacion2_kfc.backend.models.pedidos.ItemPedido;
import inspt_programacion2_kfc.backend.models.pedidos.Pedido;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.orders.ItemsPedidoRepository;
import inspt_programacion2_kfc.backend.repositories.orders.PedidoRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.backend.utils.PedidoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemsPedidoRepository itemsPedidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoHelper pedidoHelper;
    private final MovimientoStockService stockService;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         ItemsPedidoRepository itemsPedidoRepository, PedidoHelper pedidoHelper, MovimientoStockService stockService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.itemsPedidoRepository = itemsPedidoRepository;
        this.pedidoHelper = pedidoHelper;
        this.stockService = stockService;
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

    @Transactional
    public void crearPedidoDesdeCarrito(List<CartItemDto> items, EstadoPedido estadoInicial) {
        if (items == null || items.isEmpty()) {
            throw new CartEmptyException("El carrito está vacío.");
        }

        int precioFinal = 0;
        List<Ingrediente> movimientos = new ArrayList<>();

        for (CartItemDto cartItem : items) {
            ProductoEntity producto = findByIdProducto(cartItem.getProductoId());
            for (GrupoIngrediente grupoIngrediente : producto.getGruposIngredientes()) {
                for (Ingrediente ingrediente : grupoIngrediente.getIngredientes()) {
                    if (stockService.calcularStockItem(ingrediente.getItem().getId()) < ingrediente.getCantidad()) {
                        System.out.printf("Ingrediente %s sin stock suficiente", ingrediente.getItem().getName());
                        throw new StockException("Producto o ingredientes sin stock.");
                    }
                    precioFinal += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
                    movimientos.add(ingrediente);
                }
            }
        }

        Pedido pedido = new Pedido();
        pedido.setEstado(estadoInicial);
        pedido.setTotal(precioFinal);

        Pedido guardado = pedidoRepository.save(pedido);
        for (Ingrediente ingrediente : movimientos) {
            stockService.registrarMovimiento(ingrediente.getItem(), TipoMovimiento.SALIDA, ingrediente.getCantidad(), AppConstants.MOVIMIENTO_VENTA, guardado.getId());
        }
    }

    /**
     * Crea un pedido a partir del carrito, validando stock y permitiendo
     * especificar el estado inicial (por ejemplo CREADO o PAGADO).
     */
//    @Transactional
//    public void crearPedidoDesdeCarrito(List<CartItemDto> items, EstadoPedido estadoInicial) {
//        if (items == null || items.isEmpty()) {
//            throw new CartEmptyException("El carrito está vacío.");
//        }
//
//        // Agrupar cantidades por producto para validar stock correctamente
//        // (un producto puede aparecer múltiples veces con diferentes customizaciones)
//        Map<Long, Integer> cantidadesPorProducto = new HashMap<>();
//        Map<Long, String> nombresPorProducto = new HashMap<>();
//
//        for (CartItemDto cartItem : items) {
//            Long productoId = cartItem.getProductoId();
//            // Fusiona los productos por clave en el map y suma las cantidades
//            cantidadesPorProducto.merge(productoId, cartItem.getQuantity(), Integer::sum);
//            if (cartItem.getProductoName() != null) {
//                nombresPorProducto.putIfAbsent(productoId, cartItem.getProductoName());
//            }
//        }
//
//        // Validar stock para cada producto (cantidad total)
//        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
//            Long productoId = entry.getKey();
//            int cantidadTotal = entry.getValue();
//            int stockActual = pedidoHelper.obtenerStockPorIdProducto(productoId);
//
//            if (stockActual < cantidadTotal) {
//                String nombre = nombresPorProducto.getOrDefault(productoId, "");
//                throw new StockException(String.format("No hay stock suficiente para el producto: %s", nombre));
//            }
//        }
//
//        Pedido pedido = new Pedido();
//        pedido.setEstado(estadoInicial);
//
//        int total = 0;
//        for (CartItemDto cartItem : items) {
//            Long productoId = cartItem.getProductoId();
//            if (productoId != null) {
//                ProductoEntity producto = findByIdProducto(productoId);
//                if (producto != null) {
//                    ItemPedido item = PedidoUtils.mapItemPedido(cartItem, producto);
//                    total += item.getSubtotal();
//                    pedido.addItem(item);
//                }
//            }
//        }
//        pedido.setTotal(total);
//
//        Pedido guardado = pedidoRepository.save(pedido);
//        pedidoHelper.registrarMovimientoStock(guardado, TipoMovimiento.SALIDA, AppConstants.MOVIMIENTO_VENTA);
//    }

    /*
    TODO tabla intermedia ProductoPedido, para saber sus customizaciones y poder cancelar
     */
    @Transactional
    public void cancelarPedido(Long id) {
        if (id == null || id < 0) {
            throw new OrderNotFoundException("ID pedido invalido.");
        }

        Pedido pedido = findByIdPedido(id);

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
