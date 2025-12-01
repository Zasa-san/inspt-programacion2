package inspt_programacion2_kfc.frontend.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.models.orders.ItemPedido;

@Component("itemPedidoHelper")
public class ItemPedidoHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getCustomizacionesNombres(ItemPedido item) {
        if (item == null || item.getProducto() == null || StringUtils.isBlank(item.getCustomizacionesJson())) {
            return List.of();
        }

        String customizacionesJson = item.getCustomizacionesJson();

        try {
            List<Map<String, Object>> customList = objectMapper.readValue(
                    customizacionesJson,
                    new TypeReference<List<Map<String, Object>>>() {
            });
            List<String> nombres = new ArrayList<>();
            for (Map<String, Object> custom : customList) {
                Object nombre = custom.get("nombre");
                if (nombre != null) {
                    nombres.add(nombre.toString());
                }
            }
            return nombres;
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    public boolean tieneCustomizaciones(ItemPedido item) {
        return item != null
                && item.getProducto() != null
                && (StringUtils.isNotBlank(item.getCustomizacionesJson()) || "[]".equals(item.getCustomizacionesJson()));
    }

}
