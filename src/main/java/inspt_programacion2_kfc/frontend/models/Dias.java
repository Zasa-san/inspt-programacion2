package inspt_programacion2_kfc.frontend.models;

import lombok.Getter;

@Getter
public enum Dias {

    LUNES(1, "Lunes"),
    MARTES(2, "Martes"),
    MIERCOLES(3, "Miércoles"),
    JUEVES(4, "Jueves"),
    VIERNES(5, "Viernes"),
    SABADO(6, "Sábado"),
    DOMINGO(7, "Domingo");

    private final int valor;
    private final String nombre;

    Dias(int valor, String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }

    public static String numeroADia(int valor) {
        for (Dias dia : values()) {
            if (dia.valor == valor) {
                return dia.nombre;
            }
        }
        throw new IllegalArgumentException("Día inválido: " + valor);
    }
}
