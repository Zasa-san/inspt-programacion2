package items.productos;

import items.Item;

//concepto
public class Bacon extends Hamburguesa {

    public Bacon() {
        super("Bacon", 9000F);
        agregar(new Item("Bacon", 200F));
    }


}
