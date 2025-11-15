package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.services.ProductService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;
import jakarta.servlet.http.HttpSession;

@Controller
public class RouteController {

    @Autowired
    private ProductService productService;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCartItems(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            Map<Long, CartItem> cart = (Map<Long, CartItem>) cartObj;
            return new ArrayList<>(cart.values());
        }
        return List.of();
    }

    @GetMapping({"/", "/index"})
    public String index(Model model, HttpSession session) {
        PageMetadata page = new PageMetadata("Inicio", "Página pública para que el cliente vea el menú y su carrito");
        model.addAttribute("page", page);

        // Productos disponibles para el cliente
        model.addAttribute("products", productService.findAll());

        // Carrito actual en sesión
        List<CartItem> cartItems = getCartItems(session);
        int cartTotal = cartItems.stream().mapToInt(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        PageMetadata page = new PageMetadata("Iniciar sesión");
        model.addAttribute("page", page);
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        PageMetadata page = new PageMetadata("Acceso denegado");
        model.addAttribute("page", page);
        return "access-denied";
    }

}
