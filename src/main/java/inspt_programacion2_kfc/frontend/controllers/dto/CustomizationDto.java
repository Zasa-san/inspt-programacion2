package inspt_programacion2_kfc.frontend.controllers.dto;

public class CustomizationDto {

    private String id;
    private String nombre;
    private Integer priceModifier;
    private Boolean enabled;
    private String tipo; // "UNICA" o "MULTIPLE"

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPriceModifier() {
        return priceModifier;
    }

    public void setPriceModifier(Integer priceModifier) {
        this.priceModifier = priceModifier;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
