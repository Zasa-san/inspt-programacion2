package inspt_programacion2_kfc.backend.services.products;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import inspt_programacion2_kfc.backend.exceptions.product.ProductException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductImageException;
import inspt_programacion2_kfc.backend.exceptions.product.ProductNotFoundException;
import inspt_programacion2_kfc.backend.models.constants.AppConstants;
import inspt_programacion2_kfc.backend.models.products.GrupoIngrediente;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.repositories.products.IngredienteRepository;
import inspt_programacion2_kfc.backend.repositories.products.ProductoRepository;
import inspt_programacion2_kfc.backend.services.files.FileUploadService;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FileUploadService fileUploadService;
    private final IngredienteRepository ingredienteRepository;

    public ProductoService(ProductoRepository productoRepository, FileUploadService fileUploadService, IngredienteRepository ingredienteRepository) {
        this.productoRepository = productoRepository;
        this.fileUploadService = fileUploadService;
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }

    public List<ProductoEntity> findAllAvailable() {
        return productoRepository.findByAvailableTrue();
    }

    public ProductoEntity findById(Long id) {
        if (id == null) {
            throw new ProductException("ID producto invalido.");
        }
        return productoRepository.findById(id).orElse(null);
    }

    public void create(String name, String description, List<GrupoIngrediente> grupoIngredientes, Integer precioBase, MultipartFile imageFile, String imgURL) {
        ProductoEntity productoEntity = new ProductoEntity();
        if (StringUtil.isBlank(name)) {
            throw new ProductException("Nombre producto invalido.");
        }
        if (CollectionUtils.isEmpty(grupoIngredientes)) {
            throw new ProductException("Grupo ingrediente invalido.");
        }

        productoEntity.setName(name);
        productoEntity.setDescription(description);

        configurarGrupos(productoEntity, grupoIngredientes);
        validarPrecioBaseMinimo(grupoIngredientes, precioBase);

        int precio;

        if (precioBase != null && precioBase > 0) {
            precio = precioBase;
        } else {
            precio = getPrecio(grupoIngredientes);
        }

        productoEntity.setPrecioBase(precio);

        if (imageFile != null) {
            setImagen(imageFile, false, productoEntity);
        } else if (imgURL != null) {
            productoEntity.setImgUrl(imgURL);
        }

        productoRepository.save(productoEntity);
    }

    private static int getPrecio(List<GrupoIngrediente> grupoIngredientes) {
        int precio = 0;

        for (GrupoIngrediente grupoIngrediente : grupoIngredientes) {
            for (Ingrediente ingrediente : grupoIngrediente.getIngredientes()) {
                if (ingrediente.isSeleccionadoPorDefecto()) {
                    precio += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
                }
            }
        }
        return precio;
    }

    public void delete(Long id) {
        if (id == null) {
            throw new ProductNotFoundException("ID no puede ser NULL.");
        }

        ProductoEntity producto = findById(id);
        if (producto == null) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }

        if (producto.getImgUrl() != null && !producto.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
            fileUploadService.deleteFile(producto.getImgUrl());
        }

        productoRepository.deleteById(id);
    }

    public void update(Long id, String name, String description, List<GrupoIngrediente> grupoIngredientes, Integer precioBase, MultipartFile imageFile, boolean removeImage) {
        if (id == null) {
            throw new ProductNotFoundException("ID no puede ser NULL.");
        }
        if (StringUtil.isBlank(name)) {
            throw new ProductException("Nombre producto invalido.");
        }
        if (CollectionUtils.isEmpty(grupoIngredientes)) {
            throw new ProductException("Grupo ingrediente invalido.");
        }

        ProductoEntity existing = findById(id);
        if (existing == null) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }

        existing.setName(name);
        existing.setDescription(description);

        existing.getGruposIngredientes().clear();
        configurarGrupos(existing, grupoIngredientes);
        validarPrecioBaseMinimo(grupoIngredientes, precioBase);

        if (precioBase != null && precioBase > 0) {
            existing.setPrecioBase(precioBase);
        } else {
            existing.setPrecioBase(getPrecio(grupoIngredientes));
        }

        setImagen(imageFile, removeImage, existing);

        productoRepository.save(existing);
    }

    private void setImagen(MultipartFile imageFile, boolean removeImage, ProductoEntity existing) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (existing.getImgUrl() != null && !existing.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
                    fileUploadService.deleteFile(existing.getImgUrl());
                }
                String imageUrl = fileUploadService.saveFile(imageFile, "products");
                existing.setImgUrl(imageUrl);
            } catch (IOException e) {
                throw new ProductImageException("Error al guardar la imagen del producto", e);
            }
        } else if (removeImage) {
            if (existing.getImgUrl() != null && !existing.getImgUrl().equals(AppConstants.DEFAULT_IMG_URL)) {
                fileUploadService.deleteFile(existing.getImgUrl());
            }
            existing.setImgUrl(AppConstants.DEFAULT_IMG_URL);
        }
    }

    public void toggleAvailability(Long id) {
        ProductoEntity producto = findById(id);
        if (producto != null) {
            producto.setAvailable(!producto.isAvailable());
            productoRepository.save(producto);
        }
    }

    public void recalcularPreciosProductosPorItem(Long itemId) {
        List<Ingrediente> ingredientes = ingredienteRepository.findByItemId(itemId);

        List<ProductoEntity> productosAfectados = ingredientes.stream()
                .map(ing -> ing.getGrupo().getProducto())
                .distinct()
                .toList();

        for (ProductoEntity producto : productosAfectados) {
            int nuevoPrecio = getPrecio(producto.getGruposIngredientes());
            producto.setPrecioBase(nuevoPrecio);
            productoRepository.save(producto);
        }
    }

    private void configurarGrupos(ProductoEntity producto, List<GrupoIngrediente> grupos) {
        for (GrupoIngrediente grupo : grupos) {
            grupo.setProducto(producto);
            producto.getGruposIngredientes().add(grupo);
            if (grupo.getIngredientes() != null) {
                for (Ingrediente ingrediente : grupo.getIngredientes()) {
                    ingrediente.setGrupo(grupo);
                }
            }
        }
    }

    private void validarPrecioBaseMinimo(List<GrupoIngrediente> grupos, Integer precioBase) {
        if (precioBase == null || precioBase <= 0 || CollectionUtils.isEmpty(grupos)) {
            return;
        }

        int costoBase = calcularCostoBaseIngredientes(grupos);
        if (precioBase < costoBase) {
            throw new ProductException("Precio base invalido: no puede ser menor al costo base de ingredientes (" + costoBase + ").");
        }
    }

    private int calcularCostoBaseIngredientes(List<GrupoIngrediente> grupos) {
        int total = 0;

        for (GrupoIngrediente grupo : grupos) {
            if (grupo == null || grupo.getTipo() == null || CollectionUtils.isEmpty(grupo.getIngredientes())) {
                continue;
            }

            List<Ingrediente> defaults = grupo.getIngredientes().stream()
                    .filter(i -> i != null && i.getItem() != null && i.isSeleccionadoPorDefecto())
                    .toList();

            if (grupo.getTipo() == GrupoIngrediente.TipoGrupo.OBLIGATORIO) {
                List<Ingrediente> base = defaults.isEmpty()
                        ? grupo.getIngredientes().stream().filter(i -> i != null && i.getItem() != null).toList()
                        : defaults;
                for (Ingrediente ingrediente : base) {
                    total += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
                }
                continue;
            }

            for (Ingrediente ingrediente : defaults) {
                total += ingrediente.getCantidad() * ingrediente.getItem().getPrice();
            }
        }

        return total;
    }
}
