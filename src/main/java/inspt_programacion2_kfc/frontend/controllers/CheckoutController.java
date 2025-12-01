package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.dto.order.CartItemDto;
import inspt_programacion2_kfc.backend.models.orders.EstadoPedido;
import inspt_programacion2_kfc.backend.services.orders.PedidoService;
import inspt_programacion2_kfc.frontend.helpers.CheckoutHelper;
import inspt_programacion2_kfc.frontend.models.CartItem;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    private final PedidoService pedidoService;
    private final CheckoutHelper checkoutHelper;

    public CheckoutController(PedidoService pedidoService, CheckoutHelper checkoutHelper) {
        this.pedidoService = pedidoService;
        this.checkoutHelper = checkoutHelper;
    }

    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof Map) {
            return (Map<String, CartItem>) cartObj;
        }
        return null;
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        PageMetadata page = new PageMetadata("Checkout", "Elegí cómo querés pagar tu pedido");
        model.addAttribute("page", page);

        Map<String, CartItem> cart = getCart(session);
        List<CartItem> cartItems = cart != null ? new ArrayList<>(cart.values()) : List.of();
        int cartTotal = cartItems.stream().mapToInt(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        return "checkout";
    }

    @PostMapping("/checkout/pagar-en-caja")
    public String pagarEnCaja(HttpSession session, RedirectAttributes redirectAttrs) {
        Map<String, CartItem> cart = getCart(session);
        if (cart == null) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        Collection<CartItem> items = cart.values();
        if (items.isEmpty()) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        try {
            List<CartItemDto> dtoItems = items.stream()
                    .map(checkoutHelper::toCartItemDto)
                    .toList();
            pedidoService.crearPedidoDesdeCarrito(dtoItems, EstadoPedido.CREADO);
            cart.clear();
            redirectAttrs.addFlashAttribute("cartMessage", "Pedido registrado. Pagá en caja al retirar.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("cartError", ex.getMessage());
            return "redirect:/checkout";
        }

        return "redirect:/";
    }

    @PostMapping("/checkout/pagar-ahora")
    public String pagarAhora(
            @RequestParam("cardholderName") String cardholderName,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("expiry") String expiry,
            @RequestParam("cvv") String cvv,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        if (cardholderName == null || cardholderName.isBlank()
                || cardNumber == null || cardNumber.isBlank()
                || expiry == null || expiry.isBlank()
                || cvv == null || cvv.isBlank()) {
            redirectAttrs.addFlashAttribute("paymentError", "Debes completar todos los datos de pago.");
            return "redirect:/checkout";
        }

        Map<String, CartItem> cart = getCart(session);
        if (cart == null) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        Collection<CartItem> items = cart.values();
        if (items.isEmpty()) {
            redirectAttrs.addFlashAttribute("cartError", "No hay productos en el carrito.");
            return "redirect:/";
        }

        try {
            List<CartItemDto> dtoItems = items.stream()
                    .map(checkoutHelper::toCartItemDto)
                    .toList();
            pedidoService.crearPedidoDesdeCarrito(dtoItems, EstadoPedido.PAGADO);
            cart.clear();
            redirectAttrs.addFlashAttribute("cartMessage", "Pago realizado y pedido registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("cartError", ex.getMessage());
            return "redirect:/checkout";
        }

        return "redirect:/";
    }

}
