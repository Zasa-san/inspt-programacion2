package inspt_programacion2_kfc.frontend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.ItemService;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FrontProductoService {

    private final ProductoService productoService;
    private final ItemService itemService;
    private final ObjectMapper objectMapper;

    public FrontProductoService(ProductoService productoService, ItemService itemService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.itemService = itemService;
        this.objectMapper = objectMapper;
    }

    public List<ProductoEntity> findAll() {
        return productoService.findAllAvailable();
    }

    public ProductoEntity findProductoById(Long id) {
        return productoService.findById(id);
    }

    public void delete(Long id) {
        productoService.delete(id);
    }

    public void toggleAvailability(Long id) {
        productoService.toggleAvailability(id);
    }

    public void create(String nombre, String descripcion, String gruposJson, Integer precioBase, MultipartFile imageFile) {
        productoService.create(nombre, descripcion, parseGruposIngredientes(gruposJson), precioBase, imageFile, null);
    }

    public void update(Long id, String nombre, String descripcion, String gruposJson, Integer precioBase, MultipartFile imageFile, boolean removeImage) {
        productoService.update(id, nombre, descripcion, parseGruposIngredientes(gruposJson), precioBase, imageFile, removeImage);
    }

    public List<GrupoIngrediente> parseGruposIngredientes(String gruposJson) {

        if (gruposJson == null || gruposJson.trim().isEmpty()) {
            throw new ProductException("Debe cargar al menos un grupo de ingredientes.");
        }

        List<GrupoIngredienteDTO> grupoIngredientesDTO;

        try {
            grupoIngredientesDTO = objectMapper.readValue(gruposJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new ProductException("Error al parsear los grupos de ingredientes.", e);
        }

        if (grupoIngredientesDTO == null || grupoIngredientesDTO.isEmpty()) {
            throw new ProductException("Debe cargar al menos un grupo de ingredientes.");
        }

        List<GrupoIngrediente> grupos = new ArrayList<>();

        for (GrupoIngredienteDTO ingredienteDTO : grupoIngredientesDTO) {
            String nombre = ingredienteDTO.getNombre();

            if (nombre.isEmpty()) {
                throw new ProductException("Nombre de grupo invalido.");
            }

            GrupoIngrediente.TipoGrupo tipo = parseTipoGrupo(ingredienteDTO.getTipo());

            GrupoIngrediente grupo = getGrupoIngrediente(ingredienteDTO, tipo, nombre);

            for (IngredienteDTO ingrediente : ingredienteDTO.getIngredientes()) {
                if (ingrediente == null || ingrediente.getItemId() == null) {
                    throw new ProductException("Ingrediente invalido en el grupo: " + nombre);
                }

                Ingrediente nuevoIngrediente = new Ingrediente();

                try {
                    nuevoIngrediente.setItem(itemService.findById(ingrediente.getItemId()));
                } catch (RuntimeException e) {
                    throw new ProductException("Item no encontrado para el grupo: " + nombre, e);
                }

                int cantidad = ingrediente.getCantidad() != null ? ingrediente.getCantidad() : 1;

                if (cantidad <= 0) {
                    throw new ProductException("Cantidad invalida en el grupo: " + nombre);
                }

                nuevoIngrediente.setCantidad(cantidad);

                boolean seleccionado = ingrediente.getSeleccionadoPorDefecto() != null
                        && ingrediente.getSeleccionadoPorDefecto();
                nuevoIngrediente.setSeleccionadoPorDefecto(seleccionado);

                grupo.getIngredientes().add(nuevoIngrediente);
            }

            grupos.add(grupo);
        }

        return grupos;
    }

    private static GrupoIngrediente getGrupoIngrediente(GrupoIngredienteDTO ingredienteDTO, GrupoIngrediente.TipoGrupo tipo, String nombre) {
        if (tipo == null) {
            throw new ProductException("Tipo de grupo invalido para: " + nombre);
        }

        int minSeleccion = ingredienteDTO.getMinSeleccion() != null ? ingredienteDTO.getMinSeleccion() : 0;
        int maxSeleccion = ingredienteDTO.getMaxSeleccion() != null ? ingredienteDTO.getMaxSeleccion() : 0;

        if (minSeleccion < 0 || maxSeleccion < 0 || minSeleccion > maxSeleccion) {
            throw new ProductException("Rangos invalidos para el grupo: " + nombre);
        }

        if (ingredienteDTO.getIngredientes() == null || ingredienteDTO.getIngredientes().isEmpty()) {
            throw new ProductException("El grupo '" + nombre + "' debe tener ingredientes.");
        }

        GrupoIngrediente grupo = new GrupoIngrediente();

        grupo.setNombre(nombre);
        grupo.setTipo(tipo);
        grupo.setMinSeleccion(minSeleccion);
        grupo.setMaxSeleccion(maxSeleccion);
        return grupo;
    }

    private GrupoIngrediente.TipoGrupo parseTipoGrupo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return null;
        }
        try {
            return GrupoIngrediente.TipoGrupo.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    public ProductoDTO mapToProductoDTO(ProductoEntity prodEntity) {
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

                GrupoIngredienteDTO grupoDTO = new GrupoIngredienteDTO(grupoEntity.getNombre(), grupoEntity.getTipo().name(), grupoEntity.getMinSeleccion(),
                        grupoEntity.getMaxSeleccion(), getIngredienteDTOS(grupoEntity));

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
