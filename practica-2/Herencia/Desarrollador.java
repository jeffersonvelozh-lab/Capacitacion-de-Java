package Herencia;

public class Desarrollador extends Empleado{
    private String lenguaje;

    public Desarrollador(String nombre, double salario, String lenguaje) {
        super(nombre, salario);
        setLenguaje(lenguaje);
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        if (lenguaje != null && !lenguaje.isBlank()) {
            this.lenguaje = lenguaje;
        }
    }

    @Override
    public void trabajar() {
        System.out.println("Desarrollando en " + lenguaje);
    }
}
