package items;

public interface UsaIngredientes {

    /**
     * @param ingrediente agregar un ingrediente al producto
     */
    public void agregar(Ingrediente ingrediente);

    /**
     * @param ingrediente quita un ingrediente del producto
     */
    public void quitar(Ingrediente ingrediente);
}
