package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.ItemService;
import inspt_programacion2_kfc.frontend.models.productos.GrupoIngredienteDTO;
import inspt_programacion2_kfc.frontend.models.productos.IngredienteDTO;

@Controller
public class ProductsPageController {

    private final ProductoService productoService;
    private final ItemService itemService;
    private final ObjectMapper objectMapper;

    public ProductsPageController(ProductoService productoService, ItemService itemService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.itemService = itemService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        PageMetadata page = new PageMetadata("Productos", "Administración de productos");
        model.addAttribute("page", page);

        List<ProductoEntity> productos = productoService.findAll();
        model.addAttribute("products", productos);
        return "products/index";
    }

    @GetMapping("/products/new")
    public String newProductPage(Model model) {
        PageMetadata page = new PageMetadata("Nuevo producto");
        model.addAttribute("page", page);

        model.addAttribute("product", new ProductoEntity());
        return "products/new";
    }

    @PostMapping("/products/new")
    public String createProductPage(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(name = "precio") Integer precio,
            @RequestParam(name = "gruposJson") String gruposJson,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttrs) {

        try {
            List<GrupoIngrediente> grupos = parseGruposIngredientes(gruposJson);
            productoService.create(name, description, grupos, precio, imageFile, null);
            redirectAttrs.addFlashAttribute("successMessage", "Producto creado correctamente.");
        } catch (ProductImageException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/new";
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al crear producto: " + e.getMessage());
            return "redirect:/products/new";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error inesperado: " + e.getMessage());
            return "redirect:/products/new";
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        PageMetadata page = new PageMetadata("Editar producto");
        model.addAttribute("page", page);

        ProductoEntity producto = productoService.findById(id);

        if (producto == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/products";
        }

        model.addAttribute("product", producto);

        return "products/edit";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(name = "precio") Integer precio,
            @RequestParam(name = "gruposJson") String gruposJson,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) Boolean removeImage,
            RedirectAttributes redirectAttrs) {

        try {
            boolean removeImageValue = removeImage != null && removeImage;
            List<GrupoIngrediente> grupos = parseGruposIngredientes(gruposJson);

            productoService.update(id, name, description, grupos, precio, imageFile, removeImageValue);

            redirectAttrs.addFlashAttribute("successMessage", "Producto actualizado correctamente.");
        } catch (ProductNotFoundException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        } catch (ProductImageException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al guardar la imagen: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar producto: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        }

        return "redirect:/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
        } catch (ProductNotFoundException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/toggle-availability/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.toggleAvailability(id);
            redirectAttrs.addFlashAttribute("successMessage", "Disponibilidad actualizada correctamente.");
        } catch (ProductException e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar disponibilidad: " + e.getMessage());
        }
        return "redirect:/products";
    }

    private List<GrupoIngrediente> parseGruposIngredientes(String gruposJson) {

        if (gruposJson == null || gruposJson.trim().isEmpty()) {
            throw new ProductException("Debe cargar al menos un grupo de ingredientes.");
        }

        List<GrupoIngredienteDTO> grupoIngredientesDTO;

        try {
            grupoIngredientesDTO = objectMapper.readValue(gruposJson, new TypeReference<List<GrupoIngredienteDTO>>() {
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
