package inspt_programacion2_kfc.frontend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import inspt_programacion2_kfc.frontend.models.NavLink;

@Service
public class NavbarService {

    public List<NavLink> getLinksForRoute(String route) {
        return switch (route) {
            case "users" ->
                getUsersLinks();
            case "admin" ->
                getAdminLinks();
            case "vendedor" ->
                getVendedorLinks();
            default ->
                getIndexLinks();
        };
    }

    private List<NavLink> getIndexLinks() {
        return List.of(
                new NavLink("Inicio", "/"),
                new NavLink("Productos", "/#productos")
        );
    }

    private List<NavLink> getUsersLinks() {
        return List.of(
                new NavLink("Inicio", "/"),
                new NavLink("Usuarios", "/users")
        );
    }

    private List<NavLink> getAdminLinks() {
        return List.of(
                new NavLink("Inicio", "/"),
                new NavLink("Productos", "/#productos")
        );
    }

    private List<NavLink> getVendedorLinks() {
        return List.of(
                new NavLink("Inicio", "/")
        );
    }
}
