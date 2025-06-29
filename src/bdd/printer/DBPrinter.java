package bdd.printer;

import java.util.List;

public class DBPrinter {

    private DBPrinter(){}

    public static <T> void print(String name, List<T> data) {
        System.out.println("Visualizando registros en " + name + "...\n");
        System.out.println(name + "\n{");
        data.forEach(item -> System.out.println(item.toString() + ","));
        System.out.println("}\n");
    }

    public static <T> void print(String name, Object data) {
        System.out.println("Visualizando producto '" + name + "'...");
        System.out.println(data + "\n");
    }
}
