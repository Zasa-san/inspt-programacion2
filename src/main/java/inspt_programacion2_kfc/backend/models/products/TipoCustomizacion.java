package inspt_programacion2_kfc.backend.models.products;

/**
 * Define cómo se puede seleccionar una customización en el carrito.
 */
public enum TipoCustomizacion {
    
    /**
     * Solo se puede seleccionar UNA customización de este tipo por producto.
     * Ejemplo: Tamaño (pequeño, mediano, grande) - solo uno a la vez.
     */
    UNICA,
    
    /**
     * Se pueden seleccionar MÚLTIPLES customizaciones de este tipo.
     * Ejemplo: Extras (queso, bacon, huevo) - varios a la vez.
     */
    MULTIPLE
}

