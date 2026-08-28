package Herencia;

public class Empleado {
    private String nombre; 
    private double salario;

    public Empleado(String nombre, double salario) {
        setNombre(nombre);
        this.salario = salario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if(nombre != null && !nombre.isBlank()){
            this.nombre = nombre;
        }
    }

    public double getSalario() {
        return salario;
    }

    public void trabajar() {
        System.out.println("Trabajando...");
    }

}


