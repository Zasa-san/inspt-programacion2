package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.frontend.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.services.ProductService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class RouteController {

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @Autowired
    private ProductService productService;

    @Autowired
    private MovimientoStockService movimientoStockService;

    @Autowired
    private ProductoService productoService;

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
        List<Producto> products = productService.findAll();
        model.addAttribute("products", products);

        // Calculate stock for all available products
        List<ProductoEntity> productosEntities = productoService.findAllAvailable();
        Map<Long, Integer> stockMap = movimientoStockService.calcularStockParaProductos(productosEntities);
        model.addAttribute("stockMap", stockMap);

        // Carrito actual en sesión
        List<CartItem> cartItems = getCartItems(session);
        int cartTotal = cartItems.stream().mapToInt(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Authentication authentication, HttpServletRequest request,
            HttpServletResponse response) {
        //TODO TESTEAR BIEN LOGUEO Y RETROCESO
        if (isAuthenticated(authentication) && request.getParameter("justLoggedOut") == null) {
            logoutHandler.logout(request, response, authentication);
            return "redirect:/login?justLoggedOut=1";
        }

        PageMetadata page = new PageMetadata("Iniciar sesión");
        model.addAttribute("page", page);
        return "login";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        PageMetadata page = new PageMetadata("Acceso denegado");
        model.addAttribute("page", page);
        return "access-denied";
    }

}
