package inspt_programacion2_kfc.frontend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.frontend.models.NavLink;

@Service
public class NavbarService {

    public List<NavLink> getLinksForRoute(String route) {
        return switch (route) {
            case "index" ->
                getIndexLinks();
            case "login" ->
                getLoginLinks();
            default ->
                List.of();
        };
    }

    private List<NavLink> getIndexLinks() {
        return List.of(
                new NavLink("Inicio", "/"),
                new NavLink("Productos", "/"),
                new NavLink("Contacto", "/")
        );
    }

    private List<NavLink> getLoginLinks() {
        return List.of(
                new NavLink("Inicio", "/"),
                new NavLink("Productos", "/")
        );
    }
}
