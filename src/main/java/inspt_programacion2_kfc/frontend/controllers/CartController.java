package inspt_programacion2_kfc.frontend.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.helpers.CartHelper;
import inspt_programacion2_kfc.frontend.mapper.ProductoDTOConverter;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.CustomizacionSeleccionada;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.services.FrontProductoService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final FrontProductoService frontProductoService;
    private final CartHelper cartHelper;
    private final MovimientoStockService movimientoStockService;

    public CartController(FrontProductoService frontProductoService,
            CartHelper cartHelper,
            MovimientoStockService movimientoStockService) {
        this.frontProductoService = frontProductoService;
        this.cartHelper = cartHelper;
        this.movimientoStockService = movimientoStockService;
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

        if (productId == null) {
            redirectAttrs.addFlashAttribute("cartError", "Producto no encontrado.");
            return "redirect:/";
        }

        if (quantity < 1) {
            quantity = 1;
        }

        ProductoEntity productoEntity = frontProductoService.findProductoById(productId);
        if (productoEntity == null || !productoEntity.isAvailable()) {
            redirectAttrs.addFlashAttribute("cartError", "Producto no disponible.");
            return "redirect:/";
        }

        ProductoDTO productoDTO = ProductoDTOConverter.mapToProductoDTO(productoEntity);
        List<CustomizacionSeleccionada> customizacionesSeleccionadas;
        try {
            customizacionesSeleccionadas = cartHelper.parseCustomizaciones(customizacionesIdsJson, productoDTO);
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("cartError", ex.getMessage());
            return "redirect:/";
        }

        Map<String, CartItem> cart = getCart(session);

        int cantidadEnCarrito = cartHelper.calcularCantidadProductoEnCarrito(cart, productId);
        int stockDisponible = movimientoStockService.calcularStockProducto(productoEntity);
        int cantidadSolicitada = cantidadEnCarrito + quantity;

        if (stockDisponible <= 0) {
            redirectAttrs.addFlashAttribute("cartError", "Producto sin stock disponible.");
            return "redirect:/";
        }

        if (cantidadSolicitada > stockDisponible) {
            redirectAttrs.addFlashAttribute("cartError",
                    String.format("Stock insuficiente. Disponible para este producto: %d.", stockDisponible));
            return "redirect:/";
        }

        CartItem tempItem = new CartItem(productoDTO, quantity, customizacionesSeleccionadas);
        String cartKey = tempItem.getCartKey();

        CartItem item = cart.get(cartKey);

        if (item == null) {
            item = new CartItem(productoDTO, quantity, customizacionesSeleccionadas);
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
                redirectAttrs.addFlashAttribute("cartMessage", String.format("Se quitó una unidad de %s del carrito.", item.getProductoDTO().getName()));
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
