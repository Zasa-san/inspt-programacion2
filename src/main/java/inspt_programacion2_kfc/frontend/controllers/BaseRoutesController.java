package inspt_programacion2_kfc.frontend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inspt_programacion2_kfc.backend.models.products.ProductoEntity;
import inspt_programacion2_kfc.backend.services.stock.MovimientoStockService;
import inspt_programacion2_kfc.frontend.mapper.ProductoDTOConverter;
import inspt_programacion2_kfc.frontend.models.CartItem;
import inspt_programacion2_kfc.frontend.models.ProductoDTO;
import inspt_programacion2_kfc.frontend.services.FrontProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BaseRoutesController {

    @Autowired
    private FrontProductoService frontProductoService;

    @Autowired
    private MovimientoStockService movimientoStockService;

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

    @GetMapping({ "/", "/index" })
    public String index(Model model, HttpSession session) {
        PageMetadata page = new PageMetadata("Inicio", "Página pública para que el cliente vea el menú y su carrito");
        model.addAttribute("page", page);

        List<ProductoEntity> productosEntities = frontProductoService.findAllAvailable();

        List<ProductoDTO> products = productosEntities.stream()
                .map(ProductoDTOConverter::mapToProductoDTO)
                .toList();
        model.addAttribute("products", products);

        // JSON por producto para que el modal renderice grupos/ingredientes sin serializar entidades JPA.
        Map<Long, String> gruposJsonMap = new HashMap<>();
        for (ProductoDTO p : products) {
            try {
                String json = objectMapper.writeValueAsString(p.getGruposIngredientes());
                gruposJsonMap.put(p.getId(), json);
            } catch (JsonProcessingException e) {
                gruposJsonMap.put(p.getId(), "[]");
            }
        }
        model.addAttribute("gruposJsonMap", gruposJsonMap);

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
