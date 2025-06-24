package items;

public interface UsaIngredientes {

    /**
     * @param ingrediente agregar un ingrediente al producto y suma al precio
     * base si es necesario
     */
    public void agregar(Item ingrediente);

    /**
     * @param ingrediente quita un ingrediente del producto y resta al precio
     * base si es necesario
     */
    public void quitar(Item ingrediente);
}
