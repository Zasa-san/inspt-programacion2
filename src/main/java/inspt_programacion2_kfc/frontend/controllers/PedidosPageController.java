package inspt_programacion2_kfc.frontend.controllers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import inspt_programacion2_kfc.backend.models.pedidos.EstadoPedido;
import inspt_programacion2_kfc.backend.models.pedidos.Pedido;
import inspt_programacion2_kfc.backend.services.pedidos.PedidoService;
import inspt_programacion2_kfc.frontend.helpers.ItemPedidoHelper;

@Controller
public class PedidosPageController {

    private final PedidoService pedidoService;
    private final ItemPedidoHelper itemPedidoHelper;

    public PedidosPageController(PedidoService pedidoService, ItemPedidoHelper itemPedidoHelper) {
        this.pedidoService = pedidoService;
        this.itemPedidoHelper = itemPedidoHelper;
    }

    @ModelAttribute("CREADO")
    public EstadoPedido creado() {
        return EstadoPedido.CREADO;
    }

    @ModelAttribute("PAGADO")
    public EstadoPedido pagado() {
        return EstadoPedido.PAGADO;
    }

    @ModelAttribute("ENTREGADO")
    public EstadoPedido entregado() {
        return EstadoPedido.ENTREGADO;
    }

    @ModelAttribute("CANCELADO")
    public EstadoPedido cancelado() {
        return EstadoPedido.CANCELADO;
    }

    @GetMapping("/pedidos")
    public String pedidosPage(Model model) {
        PageMetadata page = new PageMetadata("Pedidos", "Listado de pedidos registrados en el sistema");
        model.addAttribute("page", page);

        List<Pedido> pedidos = pedidoService.findAllPedidos().stream()
                .sorted(Comparator.comparing(Pedido::getCreatedAt).reversed())
                .toList();

        Map<Long, String> nombresProductoPorItem = new HashMap<>();
        Map<Long, List<String>> customizacionesPorItem = new HashMap<>();

        for (Pedido pedido : pedidos) {
            if (pedido.getItems() == null) {
                continue;
            }
            pedido.getItems().forEach(item -> {
                nombresProductoPorItem.put(item.getId(), itemPedidoHelper.getNombreProducto(item));
                if (itemPedidoHelper.tieneCustomizaciones(item)) {
                    customizacionesPorItem.put(item.getId(), itemPedidoHelper.getCustomizacionesNombres(item));
                }
            });
        }

        if (pedidos.isEmpty()) {
            model.addAttribute("pedidos", List.of());
        } else {
            model.addAttribute("pedidos", pedidos);
        }

        model.addAttribute("nombresProductoPorItem", nombresProductoPorItem);
        model.addAttribute("customizacionesPorItem", customizacionesPorItem);

        return "pedidos/index";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @PostMapping("/pedidos/{id}/entregar")
    public String entregarPedido(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            pedidoService.marcarEntregado(id);
            redirectAttrs.addFlashAttribute("successMessage", "Entrega del pedido " + id + " confirmada.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping("/pedidos/{id}/pagar")
    public String pagarPedido(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            pedidoService.marcarComoPagado(id);
            redirectAttrs.addFlashAttribute("successMessage", "Pago del pedido " + id + " confirmado.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pedidos";
    }
}
