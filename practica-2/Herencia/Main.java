package Herencia;

public class Main {
    public static void main(String[] args) {
        Empleado[] empleados = new Empleado[3];
        empleados[0] = new Gerente("Ana", 3500, "IT");
        empleados[1] = new Desarrollador("Luis", 2800, "Java");
        empleados[2] = new Desarrollador("Carla", 2900, "Python");

        for (Empleado e : empleados) {
            System.out.print(e.getNombre() + ": ");
            e.trabajar();
        }
    }
}
