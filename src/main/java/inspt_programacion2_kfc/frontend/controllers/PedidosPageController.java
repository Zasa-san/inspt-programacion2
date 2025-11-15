package inspt_programacion2_kfc.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.services.orders.PedidoService;
import inspt_programacion2_kfc.frontend.services.NavbarService;
import inspt_programacion2_kfc.frontend.utils.PageMetadata;

@Controller
public class PedidosPageController {

    private final NavbarService navbarService;
    private final PedidoService pedidoService;

    public PedidosPageController(NavbarService navbarService, PedidoService pedidoService) {
        this.navbarService = navbarService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public String pedidosPage(Model model) {
        PageMetadata page = new PageMetadata("Pedidos", "Listado de pedidos registrados en el sistema");
        model.addAttribute("page", page);
        model.addAttribute("navLinks", navbarService.getLinksForRoute("vendedor"));

        var pedidos = pedidoService.findAll();
        model.addAttribute("pedidos", pedidos);

        return "pedidos/index";
    }

    @PostMapping("/pedidos/{id}/cancel")
    public String cancelarPedido(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            pedidoService.cancelarPedido(id);
            redirectAttrs.addFlashAttribute("successMessage", "Pedido " + id + " cancelado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pedidos";
    }
}


