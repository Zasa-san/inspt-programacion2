package inspt_programacion2_kfc.frontend.utils;

public class PageMetadata {

    public static final String DEFAULT_TITLE = "Página de KFC";
    public static final String DEFAULT_DESCRIPTION = "Esta página no posee un descripción";

    private final String title;
    private final String description;

    public PageMetadata(String title, String description) {
        this.title = title != null && !title.isBlank() ? title : DEFAULT_TITLE;
        this.description = description != null && !description.isBlank() ? description : DEFAULT_DESCRIPTION;
    }

    public PageMetadata(String title) {
        this(title, null);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
