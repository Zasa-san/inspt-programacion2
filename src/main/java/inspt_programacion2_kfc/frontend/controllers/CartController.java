package inspt_programacion2_kfc.frontend.controllers;

import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.helpers.CartHelper;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;
import inspt_programacion2_kfc.frontend.models.Producto;
import inspt_programacion2_kfc.frontend.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final MovimientoStockService movimientoStockService;
    private final CartHelper cartHelper;
    
    public CartController(ProductService productService, MovimientoStockService movimientoStockService, CartHelper cartHelper) {
        this.productService = productService;
        this.movimientoStockService = movimientoStockService;
        this.cartHelper = cartHelper;
    }


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

        int stockDisponible = movimientoStockService.calcularStockProducto(productId);

        if (stockDisponible <= 0) {
            redirectAttrs.addFlashAttribute("cartError", "Producto sin stock disponible.");
            return "redirect:/";
        }

        Map<String, CartItem> cart = getCart(session);
        // Calcular cantidad TOTAL de este producto en el carrito (todas las variantes)
        int totalProductoEnCarrito = cartHelper.calcularCantidadProductoEnCarrito(cart, productId);
        int totalRequested = totalProductoEnCarrito + quantity;

        if (totalRequested > stockDisponible) {
            redirectAttrs.addFlashAttribute("cartError", "Stock insuficiente.");
            return "redirect:/";
        }

        // Parsear customizaciones seleccionadas
        List<CustomizacionSeleccionada> customizacionesSeleccionadas = cartHelper.parseCustomizaciones(customizacionesIdsJson, producto);

        // Crear CartItem temporal para obtener la clave
        CartItem tempItem = new CartItem(producto, quantity, customizacionesSeleccionadas);
        String cartKey = tempItem.getCartKey();
        
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
