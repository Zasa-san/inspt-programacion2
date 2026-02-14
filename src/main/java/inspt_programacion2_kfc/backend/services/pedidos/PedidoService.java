package inspt_programacion2_kfc.backend.services.pedidos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import inspt_programacion2_kfc.backend.models.pedidos.PedidoProducto;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.TipoMovimiento;
import inspt_programacion2_kfc.backend.repositories.orders.ItemsPedidoRepository;
import inspt_programacion2_kfc.backend.repositories.orders.PedidoRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

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

        Pedido pedido = new Pedido();
        pedido.setEstado(estadoInicial);

        int total = 0;

        for (CartItemDto cartItem : items) {
            ProductoEntity productoBase = findByIdProducto(cartItem.getProductoId());

            if (productoBase == null) {
                throw new ProductException("Producto no encontrado.");
            }

            List<Ingrediente> ingredientesSeleccionados = resolveIngredientesSeleccionados(productoBase, cartItem.getIngredientesIds());
            validarStockIngredientes(ingredientesSeleccionados, cartItem.getQuantity());

            int unitPrice = calcularPrecioUnitario(ingredientesSeleccionados);

            ItemPedido item = new ItemPedido();
            item.setProducto(productoBase);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(unitPrice * cartItem.getQuantity());

            for (Ingrediente ingrediente : ingredientesSeleccionados) {
                PedidoProducto customizacion = new PedidoProducto();
                customizacion.setIngrediente(ingrediente);
                customizacion.setCantidad(ingrediente.getCantidad());
                item.addCustomizacion(customizacion);
            }

            total += item.getSubtotal();
            pedido.addItem(item);
        }

        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);

        for (ItemPedido item : guardado.getItems()) {
            for (PedidoProducto customizacion : item.getCustomizaciones()) {
                if (customizacion.getIngrediente() == null) {
                    continue;
                }
                int cantidad = customizacion.getCantidad() * item.getQuantity();
                stockService.registrarMovimiento(customizacion.getIngrediente().getItem(),
                        TipoMovimiento.SALIDA,
                        cantidad,
                        AppConstants.MOVIMIENTO_VENTA,
                        guardado.getId());
            }
        }
    }

    private List<Ingrediente> resolveIngredientesSeleccionados(ProductoEntity producto, List<Long> ingredientesIds) {
        Map<Long, Ingrediente> ingredientesPorId = new HashMap<>();
        for (GrupoIngrediente grupo : producto.getGruposIngredientes()) {
            for (Ingrediente ingrediente : grupo.getIngredientes()) {
                ingredientesPorId.put(ingrediente.getId(), ingrediente);
            }
        }

        List<Ingrediente> seleccionados = new ArrayList<>();
        if (ingredientesIds == null || ingredientesIds.isEmpty()) {
            for (Ingrediente ingrediente : ingredientesPorId.values()) {
                if (ingrediente.isSeleccionadoPorDefecto()) {
                    seleccionados.add(ingrediente);
                }
            }
            return seleccionados;
        }

        Set<Long> idsUnicos = new HashSet<>(ingredientesIds);
        for (Long ingredienteId : idsUnicos) {
            Ingrediente ingrediente = ingredientesPorId.get(ingredienteId);
            if (ingrediente == null) {
                throw new ProductException("Ingrediente invalido para el producto.");
            }
            seleccionados.add(ingrediente);
        }

        return seleccionados;
    }

    private int calcularPrecioUnitario(List<Ingrediente> ingredientesSeleccionados) {
        int precio = 0;
        for (Ingrediente ingrediente : ingredientesSeleccionados) {
            precio += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
        }
        return precio;
    }

    private void validarStockIngredientes(List<Ingrediente> ingredientesSeleccionados, int cantidadProducto) {
        for (Ingrediente ingrediente : ingredientesSeleccionados) {
            int stockActual = stockService.calcularStockItem(ingrediente.getItem().getId());
            int cantidadNecesaria = ingrediente.getCantidad() * cantidadProducto;
            if (stockActual < cantidadNecesaria) {
                System.out.printf("Ingrediente %s sin stock suficiente", ingrediente.getItem().getName());
                throw new StockException("Producto o ingredientes sin stock.");
            }
        }
    }

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
