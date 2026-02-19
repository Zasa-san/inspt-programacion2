package inspt_programacion2_kfc.frontend.mapper;

import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductoDTOConverter {

    public static ProductoDTO mapToProductoDTO(ProductoEntity prodEntity) {
        if (prodEntity == null) {
            return null;
        }

        String img = prodEntity.getImgUrl();
        if (img == null || img.isBlank()) {
            img = AppConstants.DEFAULT_IMG_URL;
        }

        List<GrupoIngredienteDTO> grupos = new ArrayList<>();
        if (prodEntity.getGruposIngredientes() != null) {
            for (GrupoIngrediente grupoEntity : prodEntity.getGruposIngredientes()) {
                if (grupoEntity == null) {
                    continue;
                }

                GrupoIngredienteDTO grupoDTO = new GrupoIngredienteDTO(grupoEntity.getNombre(), grupoEntity.getTipo().name(),
                        getIngredienteDTOS(grupoEntity));

                grupos.add(grupoDTO);
            }
        }

        return new ProductoDTO(prodEntity.getId(), prodEntity.getName(), prodEntity.getDescription(), prodEntity.getPrecioBase(), img, grupos);
    }

    private static List<IngredienteDTO> getIngredienteDTOS(GrupoIngrediente grupoEntity) {
        List<IngredienteDTO> ingredientes = new ArrayList<>();
        if (grupoEntity.getIngredientes() != null) {
            for (Ingrediente ingredienteEntity : grupoEntity.getIngredientes()) {
                if (ingredienteEntity == null) {
                    continue;
                }

                IngredienteDTO ingredienteDTO = new IngredienteDTO(ingredienteEntity.getId(), ingredienteEntity.getItem().getId(), ingredienteEntity.getItem().getName(),
                        ingredienteEntity.getItem().getPrice(), ingredienteEntity.getCantidad(), ingredienteEntity.isSeleccionadoPorDefecto());
                ingredientes.add(ingredienteDTO);
            }
        }
        return ingredientes;
    }
}
