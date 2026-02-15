package inspt_programacion2_kfc.frontend.helpers;

import java.util.List;

import org.springframework.stereotype.Component;

import inspt_programacion2_kfc.backend.models.pedidos.ItemPedido;
import inspt_programacion2_kfc.backend.models.pedidos.PedidoProducto;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.stock.Item;

@Component("itemPedidoHelper")
public class ItemPedidoHelper {

    public List<String> getCustomizacionesNombres(ItemPedido item) {
        if (item == null || item.getCustomizaciones() == null) {
            return List.of();
        }

        return item.getCustomizaciones().stream()
                .map(PedidoProducto::getIngrediente)
                .filter(ing -> ing != null && ing.getItem() != null)
                .map(Ingrediente::getItem)
                .map(Item::getName)
                .toList();
    }

    public boolean tieneCustomizaciones(ItemPedido item) {
        return item != null && item.getCustomizaciones() != null && !item.getCustomizaciones().isEmpty();
    }

}
