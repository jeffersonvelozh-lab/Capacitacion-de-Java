
public class Persona {

    private String nombre;
    private int edad;
    private String genero;

    // Constructor default
    public Persona() {
        this.nombre = "No especificado";
        this.edad = 0;
        this.genero = "No especificado";
    }

    // Constructor parametrizado
    public Persona(String nombre, int edad, String genero) {
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
    }

    // Constructor de copia
    public Persona(Persona otra) {
        this.nombre = otra.nombre;
        this.edad = otra.edad;
        this.genero = otra.genero;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    // Método que determina si la persona es mayor de edad
    public boolean esMayorDeEdad() {
        return this.edad >= 18;
    }

    public static void main(String[] args) {

        // Usamos los 3 tipos de constructores
        Persona p1 = new Persona("Jefferson", 24, "Masculino"); // parametrizado
        Persona p2 = new Persona("Maria", 16, "Femenino"); // parametrizado
        Persona p3 = new Persona(p1); // copia de p1
        p3.setNombre("Carlos"); // cambiamos el nombre de p3                                    
        p3.setEdad(15); // cambiamos la edad de p3

        Persona[] personas = { p1, p2, p3 };

        System.out.println("\n=== Detalle de todas las personas ===");
        for (Persona p : personas) {
            String estado = p.esMayorDeEdad() ? "Mayor de edad" : "Menor de edad";
            System.out.println(p.nombre + " -> " + estado);
        }
    }
}
