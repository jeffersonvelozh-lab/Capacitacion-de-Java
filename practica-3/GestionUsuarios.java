import java.util.Optional;

public class GestionUsuarios {

    // Clase de ejemplo para el modelo de Usuario
    public static class Usuario {
        private String email;

        public Usuario(String email) {
            this.email = email;
        }

        public String getEmail() {
            return this.email;
        }
    }

    // 1. Método buscarUsuario que retorna Optional<Usuario>
    public Optional<Usuario> buscarUsuario(String id) {
        // Simulación: si el id es "123" devuelve usuario, si no, devuelve vacío
        if ("123".equals(id)) {
            return Optional.of(new Usuario("ejemplo@correo.com"));
        }
        return Optional.empty(); // caso de usuario no encontrado
    }

    // Método principal para ejecutar los puntos 2, 3 y 4 en cadena
    public String obtenerEmailSeguro(String id) {
        return buscarUsuario(id)
            .map(Usuario::getEmail)                  // 2. Obtener el email
            .filter(email -> !email.trim().isEmpty()) // 3. Verificar que no esté vacío
            .orElse("correo.defecto@correo.com");    // 4. Proveer valor por defecto
    }

    // Método main para ejecutar las pruebas
    public static void main(String[] args) {
        GestionUsuarios sistema = new GestionUsuarios();

        System.out.println("--- EJECUTANDO PRUEBAS DE OPTIONAL ---");

        // Caso 1: El usuario existe y tiene un email válido
        String resultado1 = sistema.obtenerEmailSeguro("123");
        System.out.println("Resultado ID 123 (Existe): " + resultado1);

        // Caso 2: El usuario existe pero su email está vacío (falla el filter)
        String resultado2 = sistema.obtenerEmailSeguro("456");
        System.out.println("Resultado ID 456 (Email vacio): " + resultado2);

        // Caso 3: El usuario no existe (buscarUsuario devuelve Optional.empty())
        String resultado3 = sistema.obtenerEmailSeguro("789");
        System.out.println("Resultado ID 789 (No existe): " + resultado3);
    }
}

