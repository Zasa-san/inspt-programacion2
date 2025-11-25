package inspt_programacion2_kfc.backend.models.products;

public enum Size {
    SMALL,
    MEDIUM,
    LARGE;

    @Override
    public String toString() {
        return name();
    }

}
