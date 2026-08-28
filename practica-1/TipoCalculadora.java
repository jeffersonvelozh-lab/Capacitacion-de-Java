import java.util.Scanner;

public class TipoCalculadora {

    public static void main(String[] args) {


        // =========================================================
        // 2. CONVERSIONES ENTRE TIPOS
        // =========================================================
        System.out.println("\n=== 2. CONVERSIONES ===");
 
        // int -> double
        int enteroOriginal = 10;
        double comoDouble = enteroOriginal;
        System.out.println("int -> double (implicita): " + enteroOriginal + " -> " + comoDouble);
 
        // double -> int
        // no redondea la parte decimal, la trunca
        double decimalOriginal = 9.99;
        int comoInt = (int) decimalOriginal;
        System.out.println("double -> int (explicita, trunca): " + decimalOriginal + " -> " + comoInt);
 
        // Ejemplo extra: si quieres redondear en vez de truncar
        int redondeado = (int) Math.round(decimalOriginal);
        System.out.println("double -> int con Math.round: " + decimalOriginal + " -> " + redondeado);
 
        // =========================================================
        // 3. == vs .equals() PARA STRINGS
        // =========================================================
        System.out.println("\n=== 3. == vs .equals() ===");
 
        // Caso A: literales -> Java los guarda en el "String pool" y reutiliza
        // el mismo objeto, así que == puede dar true "por casualidad"
        String s1 = "hola";
        String s2 = "hola";
        System.out.println("s1 == s2 (literales):        " + (s1 == s2));       // true
        System.out.println("s1.equals(s2):                " + s1.equals(s2));    // true
 
        // Caso B: creados con 'new' -> son objetos distintos en memoria
        String s3 = new String("hola");
        String s4 = new String("hola");
        System.out.println("s3 == s4 (con new):           " + (s3 == s4));       // false
        System.out.println("s3.equals(s4):                " + s3.equals(s4));    // true
 
        // Conclusion: == compara REFERENCIAS (misma direccion de memoria)
        //             .equals() compara CONTENIDO (mismos caracteres)
        // Regla practica: para Strings, usa SIEMPRE .equals()


        // =========================================================
        // 4. CALCULADORA: AREA DE RECTANGULO Y VOLUMEN DE CUBO
        // =========================================================
        System.out.println("\n ");
        System.out.println(" ======================================= ");
        System.out.println(" ====== 4. CALCULADORA GEOMETRICA ======");
        System.out.println(" ======================================= ");

        double base;
        double altura;
        double lado;

        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== El area de una rectangulo ===");
        System.out.print("Ingresa la base del rectangulo: ");
        base = sc.nextDouble();
        System.out.print("Ingresa la altura del rectangulo: ");
        altura = sc.nextDouble();
        double areaRectangulo = base * altura;
        System.out.println("Area del rectangulo: " + areaRectangulo);

        System.out.println("\n ======================================= ");

        System.out.println("\n=== El Volumen de un cubo ===");
        System.out.print("Ingresa el lado del cubo: ");
        lado = sc.nextDouble();
        double volumenCubo = Math.pow(lado, 3);
        System.out.println("Volumen del cubo: " + volumenCubo);

        sc.close();
    }

}