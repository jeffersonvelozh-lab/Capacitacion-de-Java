import java.lang.String;

// JAVA <25 
public class HelloWorld {
    public static void main(String[] args) {
        // String no es dato primitivo, es un objeto
        String nombre = "Java";


        // Esto no es valido gracias al compiler
        String saludo = "Hola, " + " " + nombre + "!";

        System.out.println(saludo);

        // la inmutabilidad de String
        String say = "hello";
        say.toUpperCase();
        System.out.println(say); // hello

        String say2 = say.toUpperCase(); 
        System.out.println(say2); // HELLO

        // Unboxing: primitivo - objeto
        Integer num = 42; // Compiler crea num.intValue()

        int valor = num; 

        // Cuidado con el null
        Integer num2 = null;
        // int valor2 = num2; // NullPointerException

        System.out.println(valor);
        System.out.println(num2);
    }
}

// JAVA +25