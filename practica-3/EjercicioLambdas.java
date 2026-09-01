import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EjercicioLambdas {
    public static void main(String[] args) {
        List<String> nombres = List.of("  ana", "PEDRO  ", "maria", "luis ", "  SOFIA");

        Predicate<Integer> esPar = n -> n % 2 == 0;
        Function<String, String> quitarEspacios = String::trim;
        Function<String, String> aMinusculas = String::toLowerCase;
        Function<String, String> capitalizar = texto -> texto.substring(0, 1).toUpperCase() + texto.substring(1);
        Consumer<String> imprimirEntreComillas = texto -> System.out.println("\"" + texto + "\"");

        // Pipeline: trim -> minúsculas -> capitalizar
        Function<String, String> normalizar = quitarEspacios
                .andThen(aMinusculas)
                .andThen(capitalizar);

        List<String> resultado = nombres.stream()
                .map(normalizar)
                .filter(nombre -> nombre.length() > 3)   // lambda como Predicate inline
                .sorted()
                .collect(Collectors.toList());

        resultado.forEach(imprimirEntreComillas);

        // Bonus: usando el Predicate de números pares sobre las longitudes
        long cantidadConLongitudPar = resultado.stream()
                .map(String::length)
                .filter(esPar)
                .count();

        System.out.println("Nombres con longitud par: " + cantidadConLongitudPar);
    }
}
