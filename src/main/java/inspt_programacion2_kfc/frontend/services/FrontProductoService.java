package inspt_programacion2_kfc.frontend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.ItemService;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;

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

    public List<ProductoEntity> findAllAvailable() {
        return productoService.findAllAvailable();
    }

    public List<ProductoEntity> findAll() {
        return productoService.findAll();
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

    private List<GrupoIngrediente> parseGruposIngredientes(String gruposJson) {

        if (gruposJson == null || gruposJson.trim().isEmpty()) {
            throw new ProductException("Debe cargar al menos un grupo de ingredientes.");
        }

        List<GrupoIngredienteDTO> grupoIngredientesDTO;

        try {
            grupoIngredientesDTO = objectMapper.readValue(gruposJson, new TypeReference<>() {
            });
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
            int seleccionadosPorDefecto = 0;

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

                Integer cantidadDTO = ingrediente.getCantidad();
                int cantidad = cantidadDTO != null ? cantidadDTO : 1;

                if (cantidad < 0) {
                    throw new ProductException("Cantidad invalida (no puede ser negativa) en el grupo: " + nombre);
                }

                nuevoIngrediente.setCantidad(cantidad);

                boolean seleccionado = ingrediente.getSeleccionadoPorDefecto() != null
                        && ingrediente.getSeleccionadoPorDefecto();

                if (seleccionado) {
                    seleccionadosPorDefecto++;
                }

                nuevoIngrediente.setSeleccionadoPorDefecto(seleccionado);

                grupo.getIngredientes().add(nuevoIngrediente);
            }

            if (tipo == GrupoIngrediente.TipoGrupo.OPCIONAL_UNICO && seleccionadosPorDefecto > 1) {
                throw new ProductException("En el grupo '" + nombre + "' solo puede haber un ingrediente seleccionado por defecto.");
            }

            grupos.add(grupo);
        }

        return grupos;
    }

    private static GrupoIngrediente getGrupoIngrediente(GrupoIngredienteDTO ingredienteDTO, GrupoIngrediente.TipoGrupo tipo, String nombre) {
        if (tipo == null) {
            throw new ProductException("Tipo de grupo invalido para: " + nombre);
        }

        if (ingredienteDTO.getIngredientes() == null || ingredienteDTO.getIngredientes().isEmpty()) {
            throw new ProductException("El grupo '" + nombre + "' debe tener ingredientes.");
        }

        GrupoIngrediente grupo = new GrupoIngrediente();

        grupo.setNombre(nombre);
        grupo.setTipo(tipo);
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

}
