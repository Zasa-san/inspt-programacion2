package inspt_programacion2_kfc.frontend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.frontend.helpers.CartHelper;
import inspt_programacion2_kfc.frontend.mapper.ProductoDTOConverter;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;

@Service
public class CartService {

    private final FrontProductoService frontProductoService;
    private final MovimientoStockService movimientoStockService;
    private final CartHelper cartHelper;

    public CartService(FrontProductoService frontProductoService, MovimientoStockService movimientoStockService, CartHelper cartHelper) {
        this.frontProductoService = frontProductoService;
        this.movimientoStockService = movimientoStockService;
        this.cartHelper = cartHelper;
    }

    public void addToCart(Long productId, int quantity, String ingredientesIdsJson, Map<String, CartItem> cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Carrito invalido.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Producto invalido.");
        }

        ProductoEntity productoEntity = frontProductoService.findProductoById(productId);
        ProductoDTO productoDTO = ProductoDTOConverter.mapToProductoDTO(productoEntity);
        if (productoDTO == null) {
            throw new IllegalArgumentException("Producto no encontrado.");
        }

        List<Long> requestedIds = cartHelper.parseIngredientesIdsJson(ingredientesIdsJson);
        List<Long> ingredientesIds = cartHelper.completarIngredientesSeleccionados(productoDTO, requestedIds);

        CartItem toAdd = new CartItem(productoDTO, quantity, ingredientesIds);
        String cartKey = toAdd.getCartKey();

        // Validar stock por ingrediente considerando el carrito actual + este agregado.
        Map<Long, Integer> requeridosPorItemId = new HashMap<>();
        for (CartItem existing : cart.values()) {
            cartHelper.acumularRequeridosPorItemId(requeridosPorItemId, existing);
        }
        cartHelper.acumularRequeridosPorItemId(requeridosPorItemId, toAdd);

        for (Map.Entry<Long, Integer> entry : requeridosPorItemId.entrySet()) {
            Long itemId = entry.getKey();
            int requerido = entry.getValue();
            int stockActual = movimientoStockService.calcularStockItem(itemId);
            if (stockActual < requerido) {
                throw new IllegalArgumentException("Producto o ingredientes sin stock.");
            }
        }

        CartItem item = cart.get(cartKey);
        if (item == null) {
            cart.put(cartKey, toAdd);
        } else {
            item.increment(quantity);
        }
    }
}
