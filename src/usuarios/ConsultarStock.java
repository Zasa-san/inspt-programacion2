package usuarios;

import bdd.DBEntry;

interface ConsultarStock {

    /**
     * @param id del producto a consultar
     */
    public DBEntry consultarStock(int id);
}
