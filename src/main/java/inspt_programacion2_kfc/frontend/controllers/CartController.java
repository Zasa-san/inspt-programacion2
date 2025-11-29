package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.Customizacion;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;
import inspt_programacion2_kfc.frontend.models.Producto;
import inspt_programacion2_kfc.frontend.services.ProductService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final MovimientoStockService movimientoStockService;
    private final ObjectMapper objectMapper;

    public CartController(ProductService productService, MovimientoStockService movimientoStockService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.movimientoStockService = movimientoStockService;
        this.objectMapper = objectMapper;
    }

    /**
     * Ahora el carrito usa String como clave (cartKey) para diferenciar
     * el mismo producto con diferentes customizaciones.
     */
    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            return (Map<String, CartItem>) cartObj;
        }
        Map<String, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            @RequestParam(name = "customizacionesIds", required = false) String customizacionesIdsJson,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Producto producto = productService.findById(productId);
        if (producto == null) {
            redirectAttrs.addFlashAttribute("cartError", "Producto no encontrado.");
            return "redirect:/";
        }

        if (quantity < 1) {
            quantity = 1;
        }

        // Check available stock
        int availableStock = movimientoStockService.calcularStockProducto(productId);

        // Parsear customizaciones seleccionadas
        List<CustomizacionSeleccionada> customizacionesSeleccionadas = parseCustomizaciones(customizacionesIdsJson, producto);

        // Crear CartItem temporal para obtener la clave
        CartItem tempItem = new CartItem(producto, quantity, customizacionesSeleccionadas);
        String cartKey = tempItem.getCartKey();

        Map<String, CartItem> cart = getCart(session);
        
        // Calcular cantidad TOTAL de este producto en el carrito (todas las variantes)
        int totalProductoEnCarrito = calcularCantidadProductoEnCarrito(cart, productId);
        int totalRequested = totalProductoEnCarrito + quantity;

        if (availableStock <= 0) {
            redirectAttrs.addFlashAttribute("cartError", "Producto sin stock disponible.");
            return "redirect:/";
        }

        if (totalRequested > availableStock) {
            redirectAttrs.addFlashAttribute("cartError", "Stock insuficiente.");
            return "redirect:/";
        }
        
        CartItem item = cart.get(cartKey);

        if (item == null) {
            item = new CartItem(producto, quantity, customizacionesSeleccionadas);
            cart.put(cartKey, item);
        } else {
            item.increment(quantity);
        }

        redirectAttrs.addFlashAttribute("cartMessage", "Producto agregado al carrito.");
        return "redirect:/";
    }

    /**
     * Calcula la cantidad total de un producto en el carrito,
     * sumando todas las variantes (diferentes customizaciones).
     */
    private int calcularCantidadProductoEnCarrito(Map<String, CartItem> cart, Long productId) {
        return cart.values().stream()
                .filter(item -> item.getProducto().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Parsea el JSON de IDs de customizaciones y las convierte a CustomizacionSeleccionada
     * usando los datos actuales del producto.
     */
    private List<CustomizacionSeleccionada> parseCustomizaciones(String json, Producto producto) {
        List<CustomizacionSeleccionada> result = new ArrayList<>();
        
        if (json == null || json.trim().isEmpty()) {
            return result;
        }

        try {
            List<Long> ids = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            
            for (Long customId : ids) {
                // Buscar la customización en el producto
                for (Customizacion c : producto.getCustomizaciones()) {
                    if (c.getId().equals(customId)) {
                        result.add(new CustomizacionSeleccionada(c.getId(), c.getNombre(), c.getPriceModifier()));
                        break;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            // Si hay error parseando, retornar lista vacía
        }

        return result;
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("cartKey") String cartKey,
            HttpSession session,
            RedirectAttributes redirectAttrs) {
        Map<String, CartItem> cart = getCart(session);
        CartItem item = cart.get(cartKey);

        if (item != null) {
            int newQuantity = item.getQuantity() - 1;

            if (newQuantity <= 0) {
                cart.remove(cartKey);
                redirectAttrs.addFlashAttribute("cartMessage", "Producto eliminado del carrito.");
            } else {
                item.setQuantity(newQuantity);
                redirectAttrs.addFlashAttribute("cartMessage", String.format("Se quitó una unidad de %s del carrito.", item.getProducto().getName()));
            }
        }
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttrs) {
        Map<String, CartItem> cart = getCart(session);
        cart.clear();
        redirectAttrs.addFlashAttribute("cartMessage", "Carrito vaciado.");
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttrs) {
        Map<String, CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        return "redirect:/checkout";
    }
}
