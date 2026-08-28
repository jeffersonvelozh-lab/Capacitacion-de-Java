package Herencia;

public class Gerente extends Empleado {
    private String departamento;

    public Gerente(String nombre, double salario, String departamento) {
        super(nombre, salario);
        setDepartamento(departamento);
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        if (departamento != null && !departamento.isBlank()) {
            this.departamento = departamento;
        }
    }

    @Override
    public void trabajar() {
        System.out.println("Supervisando el departamento de " + departamento);
    }
}
