package inspt_programacion2_kfc.frontend.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.models.products.CustomizacionEntity;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.products.TipoCustomizacion;
import inspt_programacion2_kfc.backend.services.products.CustomizacionesService;
import inspt_programacion2_kfc.frontend.models.Customizacion;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProductHelper {

    private final ObjectMapper objectMapper;
    private final CustomizacionesService customizacionesService;

    public ProductHelper(ObjectMapper objectMapper, CustomizacionesService customizacionesService) {
        this.objectMapper = objectMapper;
        this.customizacionesService = customizacionesService;
    }

    public void handleCustomizations(ProductoEntity producto, List<Customizacion> customizations) {
        if (customizations == null || customizations.isEmpty()) {
            return;
        }

        for (Customizacion dto : customizations) {
            Long id = dto.getId();
            String nombre = dto.getNombre();
            // Asegurar que el precio nunca sea negativo
            int priceModifier = Math.max(0, Objects.requireNonNullElse(dto.getPriceModifier(), 0));
            boolean enabled = Objects.requireNonNullElse(dto.getEnabled(), false);
            TipoCustomizacion tipo = parseTipo(dto.getTipo());
            String grupo = Objects.requireNonNullElse(dto.getGrupo(), "").trim();

            if (enabled && grupo.isEmpty()) {
                throw new IllegalArgumentException("El grupo es requerido para la customización: " + nombre);
            }

            if (id != null && id > 0) {
                Long customizationId = id;
                if (!enabled) {
                    customizacionesService.delete(customizationId);
                } else {
                    CustomizacionEntity existing = customizacionesService.findById(customizationId);
                    if (existing != null) {
                        existing.setNombre(nombre);
                        existing.setPriceModifier(priceModifier);
                        existing.setTipo(tipo);
                        existing.setGrupo(grupo);
                        customizacionesService.update(customizationId, existing);
                    }
                }
            } else if (id == null && enabled && nombre != null && !nombre.trim().isEmpty()) {
                // Nueva customización (id es null)
                CustomizacionEntity newCustomization = new CustomizacionEntity();
                newCustomization.setProducto(producto);
                newCustomization.setNombre(nombre);
                newCustomization.setPriceModifier(priceModifier);
                newCustomization.setTipo(tipo);
                newCustomization.setGrupo(grupo);
                customizacionesService.create(newCustomization);
            }
        }
    }

    public TipoCustomizacion parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return TipoCustomizacion.MULTIPLE;
        }
        try {
            return TipoCustomizacion.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TipoCustomizacion.MULTIPLE;
        }
    }

    public List<Customizacion> parseCustomizations(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Customizacion>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al parsear customizaciones JSON", e);
        }
    }

    public List<CustomizacionEntity> getCustomizacionesPorProducto(ProductoEntity producto) {
        return customizacionesService.findByProducto(producto);
    }
}
