package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.products.ProductoService;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.Producto;
import inspt_programacion2_kfc.frontend.services.ProductService;
import jakarta.servlet.http.HttpSession;

@Controller
public class BaseRoutesController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MovimientoStockService movimientoStockService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCartItems(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            Map<String, CartItem> cart = (Map<String, CartItem>) cartObj;
            return new ArrayList<>(cart.values());
        }
        return List.of();
    }

    @GetMapping({"/", "/index"})
    public String index(Model model, HttpSession session) {
        PageMetadata page = new PageMetadata("Inicio", "Página pública para que el cliente vea el menú y su carrito");
        model.addAttribute("page", page);

        List<Producto> products = productService.findAll();
        model.addAttribute("products", products);

        // Generar JSON de customizaciones por producto
        Map<Long, String> customizacionesJsonMap = new HashMap<>();
        for (Producto p : products) {
            if (p.tieneCustomizaciones()) {
                try {
                    String json = objectMapper.writeValueAsString(p.getCustomizaciones());
                    customizacionesJsonMap.put(p.getId(), json);
                } catch (JsonProcessingException e) {
                    customizacionesJsonMap.put(p.getId(), "[]");
                }
            }
        }
        model.addAttribute("customizacionesJsonMap", customizacionesJsonMap);

        List<ProductoEntity> productosEntities = productoService.findAllAvailable();
        Map<Long, Integer> stockMap = movimientoStockService.calcularStockParaProductos(productosEntities);
        model.addAttribute("stockMap", stockMap);

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
