package inspt_programacion2_kfc.backend.utils;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;


public class PedidoUtils {

    public static ItemPedido mapItemPedido(CartItemDto cartItem, ProductoEntity producto) {
        ItemPedido item = new ItemPedido();
        item.setProducto(producto);
        item.setQuantity(cartItem.getQuantity());

        // Usar precio unitario del carrito (incluye extras) o el del producto si no está definido
        int precioUnitario = cartItem.getPrecioUnitario() > 0
                ? cartItem.getPrecioUnitario()
                : producto.getPrice();
        item.setUnitPrice(precioUnitario);
        item.setSubtotal(precioUnitario * cartItem.getQuantity());

        // Guardar customizaciones seleccionadas como JSON
        item.setCustomizacionesJson(cartItem.getCustomizacionesJson());
        return item;
    }
}
