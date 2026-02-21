package inspt_programacion2_kfc.backend.services.stock;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inspt_programacion2_kfc.backend.exceptions.stock.ItemAlreadyExistsException;
import inspt_programacion2_kfc.backend.exceptions.stock.ItemInUseException;
import inspt_programacion2_kfc.backend.exceptions.stock.ItemNotFoundException;
import inspt_programacion2_kfc.backend.models.products.Ingrediente;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.models.stock.Item;
import inspt_programacion2_kfc.backend.repositories.products.IngredienteRepository;
import inspt_programacion2_kfc.backend.repositories.stock.ItemRepository;
import inspt_programacion2_kfc.backend.services.products.ProductoService;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final IngredienteRepository ingredienteRepository;
    private final ProductoService productoService;

    public ItemService(ItemRepository itemRepository, IngredienteRepository ingredienteRepository, ProductoService productoService) {
        this.itemRepository = itemRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.productoService = productoService;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        if (id == null) {
            throw new ItemNotFoundException("ID de item inválido.");
        }
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item no encontrado con ID: " + id));
    }

    @Transactional
    public Item create(String name, String descripcion, int price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del item es requerido.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        if (itemRepository.existsByNameIgnoreCase(name.trim())) {
            throw new ItemAlreadyExistsException("Ya existe un item con el nombre: " + name);
        }

        Item item = new Item();
        item.setName(name.trim());
        item.setDescripcion(descripcion);
        item.setPrice(price);

        return itemRepository.save(item);
    }

    @Transactional
    public Item update(Long id, String name, String descripcion, int price) {
        if (id == null) {
            throw new ItemNotFoundException("ID de item inválido.");
        }

        Item existing = findById(id);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del item es requerido.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        String nombreTrimmed = name.trim();
        if (!existing.getName().equalsIgnoreCase(nombreTrimmed)) {
            if (itemRepository.existsByNameIgnoreCase(nombreTrimmed)) {
                throw new ItemAlreadyExistsException("Ya existe otro item con el nombre: " + nombreTrimmed);
            }
        }

        boolean preciosCambiaron = existing.getPrice() != price;

        existing.setName(nombreTrimmed);
        existing.setDescripcion(descripcion);
        existing.setPrice(price);

        Item updated = itemRepository.save(existing);

        if (preciosCambiaron) {
            productoService.recalcularPreciosProductosPorItem(id);
        }

        return updated;
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new ItemNotFoundException("ID de item inválido.");
        }

        Item item = findById(id);

        List<Ingrediente> ingredientes = ingredienteRepository.findByItemId(id);

        List<ProductoEntity> productosEnUso = ingredientes.stream()
                .map(ing -> ing.getGrupo().getProducto())
                .distinct()
                .toList();

        if (!productosEnUso.isEmpty()) {
            List<String> nombresProductos = productosEnUso.stream()
                    .map(ProductoEntity::getName)
                    .collect(Collectors.toList());

            String productosStr = String.join(", ", nombresProductos);

            throw new ItemInUseException(
                    "No se puede eliminar el item '" + item.getName() + "' porque está en uso por los siguientes productos: " + productosStr,
                    nombresProductos
            );
        }

        itemRepository.deleteById(id);
    }

}
