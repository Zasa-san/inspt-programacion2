package inspt_programacion2_kfc.backend.services.pedidos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<Pedido> findAllPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> findAllPedidosSorted() {
        return pedidoRepository.findAllByOrderByCreatedAtDesc();
    }

    public Page<Pedido> findPedidosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return pedidoRepository.findAll(pageable);
    }

    public Page<Pedido> findPedidosPaginados(int page, int size, EstadoPedido estado) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (estado == null) {
            return pedidoRepository.findAll(pageable);
        }
        return pedidoRepository.findByEstado(estado, pageable);
    }

    public List<Pedido> findPedidosByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return pedidoRepository.findAllByCreatedAtBetweenOrderByCreatedAtDesc(desde, hasta);
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
            validarPrecioUnitarioContraBackend(cartItem, unitPrice, productoBase.getName());

            ItemPedido item = new ItemPedido();
            item.setProducto(productoBase);
            item.setProductoIdSnapshot(productoBase.getId());
            item.setProductoNombre(productoBase.getName());
            item.setPrecioBaseUnitario(productoBase.getPrecioBase());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(unitPrice * cartItem.getQuantity());

            for (Ingrediente ingrediente : ingredientesSeleccionados) {
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
                item.addCustomizacion(customizacion);
            }

            total += item.getSubtotal();
            pedido.addItem(item);
        }

        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);

        for (ItemPedido item : guardado.getItems()) {
            for (PedidoProducto customizacion : item.getCustomizaciones()) {
                if (customizacion.getItemStockIdSnapshot() == null) {
                    continue;
                }
                int cantidad = customizacion.getCantidad() * item.getQuantity();
                stockService.registrarMovimiento(customizacion.getItemStockIdSnapshot(),
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

        Set<Long> idsUnicos = new HashSet<>();
        if (ingredientesIds != null) {
            idsUnicos.addAll(ingredientesIds);
        }

        if (idsUnicos.isEmpty()) {
            List<Ingrediente> defaults = new ArrayList<>();
            for (GrupoIngrediente grupo : producto.getGruposIngredientes()) {
                if (grupo == null || grupo.getIngredientes() == null || grupo.getIngredientes().isEmpty()) {
                    continue;
                }

                List<Ingrediente> defaultsGrupo = grupo.getIngredientes().stream()
                        .filter(Ingrediente::isSeleccionadoPorDefecto)
                        .toList();

                if (!defaultsGrupo.isEmpty()) {
                    if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OPCIONAL_UNICO) {
                        defaults.add(defaultsGrupo.get(0));
                    } else {
                        defaults.addAll(defaultsGrupo);
                    }
                }
            }
            return defaults;
        }

        List<Ingrediente> seleccionados = new ArrayList<>();
        for (Long ingredienteId : idsUnicos) {
            Ingrediente ingrediente = ingredientesPorId.get(ingredienteId);
            if (ingrediente == null) {
                throw new ProductException("Ingrediente invalido para el producto.");
            }
            seleccionados.add(ingrediente);
        }

        validarConfiguracionIngredientes(producto, seleccionados);
        return seleccionados;
    }

    private void validarConfiguracionIngredientes(ProductoEntity producto, List<Ingrediente> seleccionados) {
        Map<Long, List<Ingrediente>> seleccionadosPorGrupo = new HashMap<>();
        for (Ingrediente ingrediente : seleccionados) {
            if (ingrediente == null || ingrediente.getGrupo() == null || ingrediente.getGrupo().getId() == null) {
                continue;
            }
            seleccionadosPorGrupo
                    .computeIfAbsent(ingrediente.getGrupo().getId(), key -> new ArrayList<>())
                    .add(ingrediente);
        }

        for (GrupoIngrediente grupo : producto.getGruposIngredientes()) {
            if (grupo == null || grupo.getId() == null || grupo.getIngredientes() == null || grupo.getIngredientes().isEmpty()) {
                continue;
            }

            List<Ingrediente> seleccionGrupo = seleccionadosPorGrupo.getOrDefault(grupo.getId(), List.of());
            List<Ingrediente> defaultsGrupo = grupo.getIngredientes().stream()
                    .filter(Ingrediente::isSeleccionadoPorDefecto)
                    .toList();

            if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OBLIGATORIO) {
                List<Ingrediente> obligatorios = defaultsGrupo.isEmpty() ? grupo.getIngredientes() : defaultsGrupo;
                Set<Long> obligatoriosIds = obligatorios.stream().map(Ingrediente::getId).collect(java.util.stream.Collectors.toSet());
                Set<Long> seleccionIds = seleccionGrupo.stream().map(Ingrediente::getId).collect(java.util.stream.Collectors.toSet());

                if (!seleccionIds.equals(obligatoriosIds)) {
                    throw new ProductException("Configuración inválida: ingredientes obligatorios incompletos o inválidos.");
                }
                continue;
            }

            if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OPCIONAL_UNICO && seleccionGrupo.size() > 1) {
                throw new ProductException("Configuración inválida: solo se permite una selección en grupo opcional único.");
            }
        }
    }

    private int calcularPrecioUnitario(List<Ingrediente> ingredientesSeleccionados) {
        int precio = 0;
        for (Ingrediente ingrediente : ingredientesSeleccionados) {
            precio += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
        }
        return precio;
    }

    private void validarPrecioUnitarioContraBackend(CartItemDto cartItem, int unitPriceBackend, String nombreProducto) {
        int unitPriceFrontend = cartItem.getPrecioUnitario();

        if (unitPriceFrontend <= 0) {
            return;
        }

        if (unitPriceFrontend != unitPriceBackend) {
            throw new IllegalArgumentException(
                    "Error de precio para '" + nombreProducto + "'. Recalculá el carrito e intentá nuevamente.");
        }
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
        EstadoPedido estado = pedido.getEstado();

        if (estado == EstadoPedido.CANCELADO) {
            throw new OrderCancelledException("El pedido ya está cancelado.");
        }
        if (estado == EstadoPedido.ENTREGADO) {
            throw new OrderAlreadyDeliveredException("No se puede cancelar un pedido ya entregado.");
        }
        if (estado != EstadoPedido.CREADO && estado != EstadoPedido.PAGADO) {
            throw new OrderException("Solo se pueden cancelar pedidos en estado CREADO o PAGADO.");
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
