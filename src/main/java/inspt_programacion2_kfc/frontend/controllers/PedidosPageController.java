package inspt_programacion2_kfc.frontend.controllers;

import java.util.ArrayList;
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

import inspt_programacion2_kfc.backend.models.orders.EstadoPedido;
import inspt_programacion2_kfc.backend.models.orders.ItemPedido;
import inspt_programacion2_kfc.backend.models.orders.Pedido;
import inspt_programacion2_kfc.backend.services.orders.PedidoService;
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

        List<ItemPedido> items = pedidoService.findAll();

        Map<Long, Pedido> pedidosMap = new HashMap<>();
        Map<Long, List<String>> customizacionesPorItem = new HashMap<>();

        for (ItemPedido item : items) {
            Pedido pedido = item.getPedido();
            if (pedido != null && pedido.getId() != null && !pedidosMap.containsKey(pedido.getId())) {
                pedidosMap.put(pedido.getId(), pedido);
            }

            if (itemPedidoHelper.tieneCustomizaciones(item)) {
                customizacionesPorItem.put(item.getId(), itemPedidoHelper.getCustomizacionesNombres(item));
            }
        }

        List<Pedido> pedidos = new ArrayList<>(pedidosMap.values());
        model.addAttribute("pedidos", pedidos);
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
