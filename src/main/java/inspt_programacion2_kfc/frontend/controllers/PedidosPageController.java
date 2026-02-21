package inspt_programacion2_kfc.frontend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String pedidosPage(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "20") Integer size,
            @RequestParam(name = "estado", required = false) String estado,
            Model model) {
        PageMetadata pageMetadata = new PageMetadata("Pedidos", "Listado de pedidos registrados en el sistema");
        model.addAttribute("page", pageMetadata);

        int currentPage = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? Math.min(size, 200) : 20;

        EstadoPedido estadoFiltro = parseEstado(estado);

        Page<Pedido> pedidosPage = pedidoService.findPedidosPaginados(currentPage, pageSize, estadoFiltro);
        List<Pedido> pedidos = pedidosPage.getContent();

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
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", pedidosPage.getTotalPages());
        model.addAttribute("totalItems", pedidosPage.getTotalElements());
        model.addAttribute("hasPrevious", pedidosPage.hasPrevious());
        model.addAttribute("hasNext", pedidosPage.hasNext());
        model.addAttribute("estadoSeleccionado", estadoFiltro != null ? estadoFiltro.name() : "");

        return "pedidos/index";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping("/pedidos/{id}/cancel")
    public String cancelarPedido(
            @PathVariable Long id,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "estado", required = false) String estado,
            RedirectAttributes redirectAttrs) {
        try {
            pedidoService.cancelarPedido(id);
            redirectAttrs.addFlashAttribute("successMessage", "Pedido " + id + " cancelado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return buildPedidosRedirect(page, size, estado);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @PostMapping("/pedidos/{id}/entregar")
    public String entregarPedido(
            @PathVariable Long id,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "estado", required = false) String estado,
            RedirectAttributes redirectAttrs) {
        try {
            pedidoService.marcarEntregado(id);
            redirectAttrs.addFlashAttribute("successMessage", "Entrega del pedido " + id + " confirmada.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return buildPedidosRedirect(page, size, estado);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @PostMapping("/pedidos/{id}/pagar")
    public String pagarPedido(
            @PathVariable Long id,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "estado", required = false) String estado,
            RedirectAttributes redirectAttrs) {
        try {
            pedidoService.marcarComoPagado(id);
            redirectAttrs.addFlashAttribute("successMessage", "Pago del pedido " + id + " confirmado.");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return buildPedidosRedirect(page, size, estado);
    }

    private String buildPedidosRedirect(Integer page, Integer size, String estado) {
        int safePage = (page != null && page >= 0) ? page : 0;
        int safeSize = (size != null && size > 0) ? Math.min(size, 200) : 20;
        String estadoParam = parseEstado(estado) != null ? "&estado=" + parseEstado(estado).name() : "";
        return "redirect:/pedidos?page=" + safePage + "&size=" + safeSize + estadoParam;
    }

    private EstadoPedido parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoPedido.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
