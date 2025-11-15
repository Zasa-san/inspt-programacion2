package inspt_programacion2_kfc.frontend.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.services.orders.PedidoService;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.services.ProductService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final PedidoService pedidoService;

    public CartController(ProductService productService, PedidoService pedidoService) {
        this.productService = productService;
        this.pedidoService = pedidoService;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            return (Map<Long, CartItem>) cartObj;
        }
        Map<Long, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        var productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("cartError", "Producto no encontrado.");
            return "redirect:/";
        }

        if (quantity < 1) {
            quantity = 1;
        }

        Map<Long, CartItem> cart = getCart(session);
        CartItem item = cart.get(productId);
        if (item == null) {
            item = new CartItem(productOpt.get(), quantity);
            cart.put(productId, item);
        } else {
            item.increment(quantity);
        }

        redirectAttrs.addFlashAttribute("cartMessage", "Producto agregado al carrito.");
        return "redirect:/";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("productId") Long productId,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Map<Long, CartItem> cart = getCart(session);
        if (cart.remove(productId) != null) {
            redirectAttrs.addFlashAttribute("cartMessage", "Producto eliminado del carrito.");
        }
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttrs) {
        Map<Long, CartItem> cart = getCart(session);
        cart.clear();
        redirectAttrs.addFlashAttribute("cartMessage", "Carrito vaciado.");
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttrs) {
        Map<Long, CartItem> cart = getCart(session);
        Collection<CartItem> items = cart.values();
        if (items.isEmpty()) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        try {
            pedidoService.crearPedidoDesdeCarrito(List.copyOf(items));
            cart.clear();
            redirectAttrs.addFlashAttribute("cartMessage", "Pedido registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("cartError", ex.getMessage());
        }

        return "redirect:/";
    }
}


