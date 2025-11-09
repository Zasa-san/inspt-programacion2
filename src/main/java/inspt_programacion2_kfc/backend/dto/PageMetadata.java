package inspt_programacion2_kfc.backend.dto;

/**
 * DTO para encapsular metadatos de página (title y description). Se utiliza
 * para pasar información al head de los templates.
 */
public class PageMetadata {

    private String title;
    private String description;

    public PageMetadata(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public PageMetadata(String title) {
        this(title, "Sistema de gestión KFC INSPT");
    }

    public PageMetadata() {
        this("INSPT KFC", "Sistema de gestión KFC INSPT");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
