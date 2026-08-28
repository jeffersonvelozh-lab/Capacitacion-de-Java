# Módulo 2: Programación Orientada a Objetos en Java

**Duración:** 4 horas  
**Objetivo:** Dominar clases, encapsulamiento, herencia, polimorfismo e interfaces

---

## 2.1 Clases y objetos (45 min)

### Objetivos
- Definir clases con atributos y métodos
- Entender constructores y el keyword `this`
- Diferenciar clase vs objeto vs instancia

### Contenido teórico

#### ¿Qué es una clase?

Una clase es un **plano** (blueprint) para crear objetos. Define qué datos tiene y qué puede hacer.

```java
public class Persona {
    // Atributos (estado)
    String nombre;
    int edad;
    
    // Métodos (comportamiento)
    void presentar() {
        System.out.println("Soy " + nombre + " y tengo " + edad + " años");
    }
}
```

#### Crear objetos

```java
// La clase es el plano, el objeto es la casa
Persona persona1 = new Persona();  // instancia
persona1.nombre = "Ana";           // acceder al atributo
persona1.edad = 25;
persona1.presentar();              // llamar al método
```

**Peculiaridad:** `new` reserva memoria en el heap y retorna la referencia (guardada en la stack).

#### Constructores

```java
public class Persona {
    String nombre;
    int edad;
    
    // Constructor por defecto (si no defines ninguno)
    // public Persona() {}
    
    // Constructor parametrizado
    public Persona(String nombre, int edad) {
        this.nombre = nombre;  // 'this' distingue atributo de parámetro
        this.edad = edad;
    }
    
    // Constructor copia
    public Persona(Persona otra) {
        this.nombre = otra.nombre;
        this.edad = otra.edad;
    }
}

// Uso
Persona ana = new Persona("Ana", 25);
Persona copia = new Persona(ana);
```

#### El keyword `this`

`this` se refiere al objeto actual:

```java
public class Persona {
    String nombre;
    
    public Persona(String nombre) {
        this.nombre = nombre;  // this.nombre = atributo, nombre = parámetro
    }
    
    public Persona setNombre(String nombre) {
        this.nombre = nombre;
        return this;  // Permite encadenar: persona.setNombre("Ana").setEdad(25)
    }
}
```

### Ejemplo parcial

```java
public class CuentaBancaria {
    private String titular;
    private double saldo;
    
    public CuentaBancaria(String titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
    }
    
    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
        }
    }
    
    public boolean retirar(double monto) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            return true;
        }
        return false;
    }
    
    public void mostrarSaldo() {
        System.out.println("Titular: " + titular + ", Saldo: $" + saldo);
    }
}
```

### Teoría en profundidad: anatomía de un objeto en memoria

#### Layout de un objeto en el heap

```
Objeto Persona en heap:
┌──────────────────────────────┐
│ Header de objeto (Mark word) │  ← estado de lock, hashCode identity, edad GC
│ Class pointer                │  ← referencia al Class<Persona> (Metaspace)
│ Campos (instance fields):    │
│   nombre ──→ String (heap)   │
│   edad   (4 bytes)           │
└──────────────────────────────┘
```

**Mark word (8 bytes):** guarda bits del lock (synchronized), el hashCode identity (solo cuando se calcula), y la edad del objeto para el GC (generacional). Estos bits se comparten — por eso `identityHashCode` se calcula una sola vez.

**Class pointer (4 bytes comprimido):** apunta a los metadatos de clase en Metaspace. Todos los objetos de `Persona` comparten el mismo `Class<Persona>`.

#### Referencias y el ciclo de vida

```
Persona p = new Persona("Ana");
        │
        ▼
stack: p ──────→ heap: [Persona header | nombre→"Ana" | edad]
                    │
                    └── String "Ana" en heap (o pool si es literal)

p = null;  // la referencia se pierde
            // → objeto elegible para GC (no alcanzable desde las raíces)
```

**Raíces GC (roots):** variables locales de la stack, variables static, JNI references, hilos activos. Cualquier objeto alcanzable desde una raíz NO se recolecta.

#### ¿Qué es un objeto "igual"?

```java
Persona p1 = new Persona("Ana", 25);
Persona p2 = new Persona("Ana", 25);

p1 == p2    // false — comparan REFERENCIAS (dirección de memoria)
p1.equals(p2) // false POR DEFECTO — Object.equals usa ==
```

Para que `equals` funcione por contenido hay que sobreescribirlo (se ve en la teoría de la sección de igualdad más adelante).

#### Constructores: el orden de ejecución

```java
class Abuelo { Abuelo() { System.out.println("1"); } }
class Padre extends Abuelo { Padre() { System.out.println("2"); } }
class Hijo extends Padre { Hijo() { System.out.println("3"); } }

new Hijo();
// Imprime: 1 → 2 → 3
// Los constructores se encadenan de arriba hacia abajo.
// Cada constructor llama implícitamente a super() al inicio.
```

**Regla:** si el constructor hijo no llama explícitamente a `super(...)`, el compiler inserta `super()` (constructor sin args). Si el padre no tiene constructor sin args → error de compilación.

#### Inicializadores de instancia y static

```java
class Demo {
    static int contador = 0;           // 1. static fields (orden textual)
    static { contador = 100; }         // 2. static initializer block

    String nombre = "x";               // 3. instance fields
    { System.out.println("block"); }   // 4. instance initializer block

    Demo() { System.out.println("ctor"); }  // 5. constructor
}

// Orden de carga de clase:
//   static fields + static blocks (una sola vez, cuando se carga la clase)
// Orden por instancia:
//   instance fields + instance blocks (en orden textual) → constructor body
```

### Ejercicio práctico

1. Crear clase `Persona` con nombre, edad y género
2. Crear 3 constructores: default, parametrizado y copia
3. Crear método `esMayorDeEdad()` que retorne boolean
4. Crear array de 3 personas y mostrar cuáles son mayores de edad

**Solución esperada:** Clase funcional con 3 instancias y verificación de edad.

---

## 2.2 Encapsulamiento (30 min)

### Objetivos
- Aplicar modificadores de acceso correctamente
- Entender por qué getters/setters son convención en Java
- Proteger el estado interno de un objeto

### Contenido teórico

#### Modificadores de acceso

| Modificador | Clase | Paquete | Subclase | Mundo |
|-------------|-------|---------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| default (sin modifier) | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

**Peculiaridad:** Java no tiene `internal` como C#. El scope por defecto es el **paquete**.

#### Getters y setters

```java
public class CuentaBancaria {
    private String titular;    // private — solo accesible dentro de la clase
    private double saldo;      // private
    
    // Getter
    public String getTitular() {
        return titular;
    }
    
    // Setter con validación
    public void setTitular(String titular) {
        if (titular != null && !titular.isBlank()) {
            this.titular = titular;
        }
    }
    
    // Getter solo lectura (no tiene setter)
    public double getSaldo() {
        return saldo;
    }
    
    // Método que modifica estado de forma controlada
    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
        }
    }
}
```

**¿Por qué no hacer todo público?**
- Control: puedes validar antes de modificar
- Flexibilidad: puedes cambiar la implementación sin romper código externo
- Seguridad: evitas que se ponga saldo negativo directamente

### Ejemplo parcial

```java
// Mal diseño — todo público
public class PersonaPublica {
    public String nombre;
    public int edad;
}

// Buen diseño — encapsulado
public class PersonaPrivada {
    private String nombre;
    private int edad;
    
    public PersonaPrivada(String nombre, int edad) {
        this.nombre = nombre;
        setEdad(edad);  // Validar en el constructor también
    }
    
    public String getNombre() { return nombre; }
    
    public int getEdad() { return edad; }
    
    public void setEdad(int edad) {
        if (edad >= 0 && edad <= 150) {
            this.edad = edad;
        }
        // Si no es válido, no cambia (silencioso)
    }
}
```

### Teoría en profundidad: encapsulamiento y el contrato equals/hashCode

#### Por qué la encapsulación: invariantes de clase

Un **invariante** es una condición que siempre debe cumplirse. Sin encapsulamiento se rompe:

```java
// Invariante: edad ∈ [0, 150]
public class Persona {
    public int edad;  // cualquiera puede poner 999

    // Con getter/setter validado, el invariante se protege en UN solo lugar
    private int edad2;
    public void setEdad(int edad2) {
        if (edad2 < 0 || edad2 > 150)
            throw new IllegalArgumentException("Edad inválida: " + edad2);
        this.edad2 = edad2;
    }
}
```

**Principio:** exponer la mínima superficie necesaria. Los campos internos son detalle de implementación — el acceso a ellos es contrato público.

#### Defensive copying

```java
// Campo mutable expuesto por el getter
public class Cuenta {
    private final List<String> movimientos = new ArrayList<>();

    public List<String> getMovimientos() {
        return movimientos;              // ❌ el llamador puede mutar la lista interna
    }

    public List<String> getMovimientosSeguro() {
        return new ArrayList<>(movimientos);  // ✅ copia defensiva (inmutable view: List.copyOf)
    }
}
```

**Peculiaridad:** los `record` de Java (Java 16+) son inmutables por diseño:

```java
public record Persona(String nombre, int edad) {
    // fields son private final, getters automáticos (nombre(), edad()),
    // equals/hashCode/toString automáticos, constructor canónico
}
```

#### El contrato equals/hashCode (crítico)

**Regla de oro:** si sobreescribes `equals`, DEBES sobreescribir `hashCode`. Violarlo rompe HashMap, HashSet y otras colecciones.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;                  // misma referencia
    if (o == null || getClass() != o.getClass()) return false;  // tipo
    Persona persona = (Persona) o;
    return edad == persona.edad
        && Objects.equals(nombre, persona.nombre);
}

@Override
public int hashCode() {
    return Objects.hash(nombre, edad);   // mismo campos que equals
}
```

**Contrato formal:**
1. **Reflexivo:** `x.equals(x)` → true
2. **Simétrico:** `x.equals(y)` == `y.equals(x)`
3. **Transitivo:** `x.equals(y)` y `y.equals(z)` → `x.equals(z)`
4. **Consistente:** si los campos no cambian, el resultado no cambia
5. **hashCode:** `x.equals(y)` → `x.hashCode() == y.hashCode()`

**Peculiaridad:** dos objetos pueden tener el mismo hashCode sin ser iguales (colisión). Pero objetos iguales SIEMPRE deben tener el mismo hashCode.

### Ejercicio práctico

1. Refactorizar `CuentaBancaria` del ejercicio anterior: hacer atributos `private`
2. Agregar getters para titular y saldo
3. Agregar setter para titular con validación (no vacío, no nulo)
4. El saldo solo debe modificarse con `depositar()` y `retirar()`

**Solución esperada:** Clase donde no se puede acceder directamente al saldo.

---

## 2.3 Herencia (45 min)

### Objetivos
- Crear jerarquías de clases con `extends`
- Entender constructor chaining con `super`
- Usar `@Override` correctamente
- Comprender la limitación de herencia simple

### Contenido teórico

#### La palabra clave `extends`

```java
public class Empleado {
    String nombre;
    double salario;
    
    public Empleado(String nombre, double salario) {
        this.nombre = nombre;
        this.salario = salario;
    }
    
    public void trabajar() {
        System.out.println(nombre + " está trabajando");
    }
}

public class Gerente extends Empleado {
    String departamento;
    
    public Gerente(String nombre, double salario, String departamento) {
        super(nombre, salario);  // Llama al constructor de Empleado
        this.departamento = departamento;
    }
    
    public void gestionar() {
        System.out.println(nombre + " gestiona " + departamento);
    }
}

// Uso
Gerente g = new Gerente("Ana", 5000, "IT");
g.trabajar();    // Heredado de Empleado
g.gestionar();   // Propio de Gerente
```

#### Constructor chaining

```java
public class Animal {
    String nombre;
    
    public Animal(String nombre) {
        this.nombre = nombre;
        System.out.println("Animal creado: " + nombre);
    }
}

public class Perro extends Animal {
    String raza;
    
    public Perro(String nombre, String raza) {
        super(nombre);  // DEBE ser la primera línea
        this.raza = raza;
        System.out.println("Perro creado: " + raza);
    }
}

// Uso
Perro p = new Perro("Rex", "Labrador");
// Imprime:
// Animal creado: Rex
// Perro creado: Labrador
```

**Peculiaridad:** `super()` debe ser la **primera línea** del constructor. No puedes poner nada antes.

#### `@Override`

```java
public class Empleado {
    public void trabajar() {
        System.out.println("Trabajando...");
    }
}

public class Desarrollador extends Empleado {
    @Override  // Opcional pero recomendado — el compiler verifica que exista en el padre
    public void trabajar() {
        System.out.println("Escribiendo código...");
    }
}
```

#### Herencia simple (peculiaridad de Java)

Java **no permite herencia múltiple** de clases:

```java
// ESTO NO COMPILA
public class Hijo extends Padre1, Padre2 {
    // ERROR
}
```

**¿Por qué?** El "diamond problem" — si ambos padres tienen el mismo método, ¿cuál se usa?

**Solución:** Usa interfaces (sección 2.5) para comportamiento múltiple.

### Ejemplo parcial

```java
public class Figura {
    protected String nombre;
    
    public Figura(String nombre) {
        this.nombre = nombre;
    }
    
    public double calcularArea() {
        return 0;  // Base — cada hija lo sobreescribe
    }
    
    @Override
    public String toString() {
        return nombre + " (área: " + calcularArea() + ")";
    }
}

public class Circulo extends Figura {
    private double radio;
    
    public Circulo(double radio) {
        super("Círculo");
        this.radio = radio;
    }
    
    @Override
    public double calcularArea() {
        return Math.PI * radio * radio;
    }
}

public class Rectangulo extends Figura {
    private double base, altura;
    
    public Rectangulo(double base, double altura) {
        super("Rectángulo");
        this.base = base;
        this.altura = altura;
    }
    
    @Override
    public double calcularArea() {
        return base * altura;
    }
}
```

### Teoría en profundidad: herencia y dispatch de métodos

#### Method tables y dynamic dispatch

Cada clase tiene una **tabla de métodos** (vtable) generada en runtime:

```
Clase Animal:
  vtable: [hacerSonido → Animal.hacerSonido]
          [toString     → Object.toString]

Clase Perro extends Animal:
  vtable: [hacerSonido → Perro.hacerSonido]  ← sobreescribió
          [toString     → Object.toString]

Llamada: a.hacerSonido();
La JVM busca en la vtable del TIPO REAL del objeto → Perro.hacerSonido
```

**Peculiaridad:** la resolución es *late binding* (runtime). El compiler genera `invokevirtual` (no sabe qué clase concreta es). Esto da polimorfismo pero tiene coste: no es directo como un `invokestatic`, requiere lookup en la tabla.

#### `final` y la anulación de dispatch

```java
public class Animal {
    public final void respirar() { }  // NO se puede sobreescribir
}

// El compiler puede hacer inline de métodos final (sabe exactamente cuál ejecuta)
// Los métodos static y private también son "no polimórficos" (early binding)
```

#### El problema del diamond

```
       Animal
      /      \
   Perro     Gato
      \      /
     PerroGato  ← ¿hereda ladrar() de Perro o miar() de Gato?
```

Java elige **herencia única**: evita la ambigüedad. Pero permite múltiples interfaces:

```java
class PerroGato implements Ladra, Mia  {
    // El compiler obliga a resolver colisiones explícitamente
}
```

#### Composición sobre herencia (Favor Composition over Inheritance)

```java
// ❌ Herencia mal usada
class Pila extends ArrayList { ... }   // hereda métodos que no aplican

// ✅ Composición: usar una colección interna
class Pila {
    private final List<String> elementos = new ArrayList<>();
    public void push(String s) { elementos.add(s); }
    public String pop() { return elementos.remove(elementos.size()-1); }
}
```

**Regla LSP (Liskov):** una subclase debe poder sustituir a su padre sin romper el comportamiento esperado. Si `Pila extends ArrayList`, hereda `add(int, ...)` que rompe la semántica de pila.

### Ejercicio práctico

1. Crear clase `Empleado` con nombre, salario y método `trabajar()`
2. Crear `Gerente` que herede de `Empleado` (agregar departamento)
3. Crear `Desarrollador` que herede de `Empleado` (agregar lenguaje)
4. Sobreescribir `trabajar()` en cada subclase con comportamiento diferente
5. Crear un array de `Empleado` y ejecutar `trabajar()` en cada uno

**Solución esperada:** Polimorfismo funcional — cada empleado trabaja a su manera.

---

## 2.4 Polimorfismo (45 min)

### Objetivos
- Entender polimorfismo de compilación (overloading) y ejecución (overriding)
- Usar `instanceof` y casting de objetos
- Aplicar polimorfismo en diseños reales

### Contenido teórico

#### Overloading vs Overriding

| Aspecto | Overloading | Overriding |
|---------|-------------|------------|
| Ubicación | Misma clase | Diferentes clases (herencia) |
| Nombre | Igual | Igual |
| Parámetros | Diferentes | Mismos |
| Retorno | Puede diferir | Debe ser igual o subtipo |
| Anotación | Ninguna | `@Override` |
| Resolución | Compile-time | Runtime |

#### Polimorfismo de ejecución

```java
public class Animal {
    public void hacerSonido() {
        System.out.println("...");
    }
}

public class Perro extends Animal {
    @Override
    public void hacerSonido() {
        System.out.println("Guau!");
    }
}

public class Gato extends Animal {
    @Override
    public void hacerSonido() {
        System.out.println("Miau!");
    }
}

// Polimorfismo en acción
Animal a1 = new Perro();  // Referencia Animal, objeto Perro
Animal a2 = new Gato();   // Referencia Animal, objeto Gato

a1.hacerSonido();  // Imprime "Guau!" — se ejecuta el método de Perro
a2.hacerSonido();  // Imprime "Miau!" — se ejecuta el método de Gato
```

**Peculiaridad:** Java decide **en runtime** qué método ejecutar, no en compile-time.

#### `instanceof` y casting

```java
Animal a = new Perro();

// Verificar tipo antes de castear
if (a instanceof Perro) {
    Perro p = (Perro) a;  // Downcasting — de Animal a Perro
    p.hacerSonido();
}

// Java 16+ pattern matching
if (a instanceof Perro p) {
    p.hacerSonido();  // 'p' ya está declarado
}
```

#### Sistema de pagos (ejemplo completo)

```java
public interface Paggable {
    boolean pagar(double monto);
    String getTipo();
}

public class TarjetaCredito implements Paggable {
    private double limite;
    private double usado;
    
    @Override
    public boolean pagar(double monto) {
        if (usado + monto <= limite) {
            usado += monto;
            return true;
        }
        return false;
    }
    
    @Override
    public String getTipo() { return "Tarjeta de Crédito"; }
}

public class Efectivo implements Paggable {
    private double disponible;
    
    @Override
    public boolean pagar(double monto) {
        if (disponible >= monto) {
            disponible -= monto;
            return true;
        }
        return false;
    }
    
    @Override
    public String getTipo() { return "Efectivo"; }
}

// Uso polimórfico
Paggable[] metodos = {
    new TarjetaCredito(1000),
    new Efectivo(500)
};

for (Paggable metodo : metodos) {
    System.out.println("Intentando pagar con " + metodo.getTipo());
    boolean exito = metodo.pagar(200);
    System.out.println("¿Éxito? " + exito);
}
```

### Ejemplo parcial

```java
// Casting seguro
public static void procesarAnimal(Animal a) {
    if (a instanceof Perro p) {
        p.ladrar();  // Método específico de Perro
    } else if (a instanceof Gato g) {
        g.miar();    // Método específico de Gato
    } else {
        a.hacerSonido();  // Método genérico
    }
}
```

### Teoría en profundidad: polimorfismo y tipos

#### Static vs dynamic typing en Java

Java es **estáticamente tipado** (tipos se verifican en compile-time) pero con **dynamic dispatch** (qué método corre se decide en runtime):

```java
Animal a = new Perro();
a.hacerSonido();
// Compile-time: a es Animal → se llama Animal.hacerSonido (firma válida)
// Runtime: el objeto real es Perro → se ejecuta Perro.hacerSonido
```

**Peculiaridad:** Java NUNCA rebaja el tipo implícitamente. `a.ladrar()` no compila aunque el objeto real sea Perro — el compiler solo conoce el tipo estático `Animal`.

#### Upcasting vs Downcasting

```java
// Upcasting: implícito, siempre seguro (subir por la jerarquía)
Animal a = new Perro();          // ✅ implícito

// Downcasting: explícito, requiere instanceof
if (a instanceof Perro perro) {
    perro.ladrar();              // ✅ seguro con pattern matching
}

// Sin verificar → ClassCastException
Perro p = (Perro) new Animal();  // ❌ Animal no es Perro
```

**Java 16+ pattern matching:** `a instanceof Perro perro` declara la variable automáticamente. También `switch` con pattern matching en Java 21.

#### Covarianza en retornos

```java
class Animal { Animal producir() { return new Animal(); } }
class Perro extends Animal {
    @Override
    Perro producir() { return new Perro(); }  // ✅ covarianza: subtipo de Animal
}

// Retorno covariante: puedes devolver un subtipo más específico.
// Parámetros NO son contravariantes en Java (invariantes).
// @Override Perro comer(Perro p) — ❌ no sobreescribe Animal.comer(Animal)
```

### Ejercicio práctico

1. Crear interfaz `Paggable` con `pagar(monto)` y `getTipo()`
2. Implementar: `TarjetaCredito`, `Efectivo`, `Transferencia`
3. Crear `ProcesadorPagos` que acepte `Paggable` y procese un pago
4. Probar con cada tipo de pago

**Solución esperada:** Sistema que procesa diferentes métodos de pago de forma polimórfica.

---

## 2.5 Clases abstractas e interfaces (45 min)

### Objetivos
- Diferenciar `abstract class` de `interface`
- Entender `default` methods en interfaces
- Usar `sealed` classes (Java 17+)
- Decidir cuándo usar cada una

### Contenido teórico

#### Clases abstractas

```java
public abstract class Figura {
    protected String nombre;
    
    public Figura(String nombre) {
        this.nombre = nombre;
    }
    
    // Método abstracto — NO tiene implementación
    public abstract double calcularArea();
    
    // Método concreto — SÍ tiene implementación
    public void describir() {
        System.out.println("Soy un " + nombre + " con área " + calcularArea());
    }
}

// NO se puede instanciar
// Figura f = new Figura("test");  // ERROR

public class Circulo extends Figura {
    private double radio;
    
    public Circulo(double radio) {
        super("Círculo");
        this.radio = radio;
    }
    
    @Override
    public double calcularArea() {
        return Math.PI * radio * radio;
    }
}
```

**Cuándo usar:** Cuando tienes código común entre subclases pero necesitas que cada una implemente ciertos métodos.

#### Interfaces

```java
public interface Paggable {
    // Método abstracto (por defecto)
    boolean pagar(double monto);
    
    // Default method (Java 8+) — tiene implementación
    default String getTipo() {
        return this.getClass().getSimpleName();
    }
    
    // Static method
    static Paggable desde(String tipo) {
        return switch (tipo) {
            case "TARJETA" -> new TarjetaCredito();
            case "EFECTIVO" -> new Efectivo();
            default -> throw new IllegalArgumentException("Tipo desconocido");
        };
    }
}

// Implementar
public class TarjetaCredito implements Paggable {
    @Override
    public boolean pagar(double monto) {
        // lógica...
        return true;
    }
    // getTipo() viene del default
}
```

**Peculiaridad:** Las interfaces pueden tener `default` methods desde Java 8. Esto permite agregar métodos sin romper implementaciones existentes.

#### Abstract class vs Interface

| Aspecto | Abstract Class | Interface |
|---------|---------------|-----------|
| Herencia múltiple | ❌ Una sola | ✅ Muchas |
| Constructores | ✅ Sí | ❌ No |
| Atributos | Cualquier tipo | Solo `public static final` |
| Métodos | Abstractos + concretos | Abstractos + default + static |
| Estado (atributos con estado) | ✅ Sí | ❌ No |
| Cuándo usar | "es un tipo de..." | "puede hacer..." |

#### `sealed` classes (Java 17+)

```java
public sealed class Forma permits Circulo, Rectangulo, Triangulo {
    // Solo estas 3 subclases pueden extenderla
}

public final class Circulo extends Forma { }      // final: no se puede extender más
public non-sealed class Rectangulo extends Forma { } // non-sealed: abierto
public sealed class Triangulo extends Forma permits Escaleno { } // sealed: aún más restrictivo
```

**Peculiaridad:** Java originalmente no tenía enums ni sealed classes. Son adiciones modernas para modelar dominios con tipos fijos.

### Ejemplo parcial

```java
// Ejemplo completo de interfaz y clase abstracta

public abstract class Empleado {
    protected String nombre;
    protected double salarioBase;
    
    public Empleado(String nombre, double salarioBase) {
        this.nombre = nombre;
        this.salarioBase = salarioBase;
    }
    
    public abstract double calcularSalario();
    
    public void mostrarInfo() {
        System.out.println(nombre + " - Salario: $" + calcularSalario());
    }
}

public interface Bonificable {
    double calcularBonificacion();
    
    default double aplicarBonificacion() {
        return calcularSalario() + calcularBonificacion();
    }
}

public class Desarrollador extends Empleado implements Bonificable {
    private String lenguaje;
    
    public Desarrollador(String nombre, double salario, String lenguaje) {
        super(nombre, salario);
        this.lenguaje = lenguaje;
    }
    
    @Override
    public double calcularSalario() {
        return salarioBase;
    }
    
    @Override
    public double calcularBonificacion() {
        return salarioBase * 0.10;  // 10% bono
    }
}
```

### Teoría en profundidad: abstract vs interface — historia y semántica

#### Semántica "es-un" vs "puede-hacer"

- **Clase abstracta:** describe un **ser** (es-un). Define esencia común: estado (campos), comportamiento compartido, y contrato parcial. Solo puede haber UNA (herencia única)
- **Interface:** describe una **capacidad** (puede-hacer). No tiene estado (solo constantes), define contrato de comportamiento. Puede haber MUCHAS

```java
abstract class Animal {          // es-un Animal
    String nombre;               // estado OK
    abstract void moverse();
}

interface Volador {              // puede volar
    void volar();
}
interface Nadador {              // puede nadar
    void nadar();
}

class Pato extends Animal implements Volador, Nadador {
    void moverse() { ... }
    public void volar() { ... }
    public void nadar() { ... }
}
```

#### La evolución histórica de las interfaces

| Versión | Cambio en interfaces |
|---------|---------------------|
| Java 1.0 | Solo métodos abstractos y constantes |
| Java 8 | `default` methods + `static` methods |
| Java 9 | `private` methods (helpers de defaults) |
| Java 16 | `sealed` interfaces |

**`default` methods resuelven la adición retroactiva:** antes de Java 8, agregar un método a una interfaz pública rompía todos los implementadores. Con `default`, la implementación existe por defecto.

#### Métodos privados en interfaces (Java 9+)

```java
interface Saludo {
    default void saludar(String nombre) {
        validar(nombre);           // helper privado compartido
        System.out.println("Hola " + nombre);
    }
    default void despedir(String nombre) {
        validar(nombre);
        System.out.println("Adiós " + nombre);
    }
    private void validar(String n) {   // solo para uso interno de defaults
        if (n == null) throw new IllegalArgumentException();
    }
}
```

#### `sealed` classes y el modelado de dominios

```java
// Antes: para modelar "un valor es o esto o aquello" se usaba instanceof+else
// Java 17+ sealed permite exhaustividad en switch

sealed interface Forma permits Circulo, Rectangulo {
    double area();
}

record Circulo(double radio) implements Forma {
    public double area() { return Math.PI * radio * radio; }
}
record Rectangulo(double base, double altura) implements Forma {
    public double area() { return base * altura; }
}

double calcular(Forma f) {
    return switch (f) {           // Java 21: switch sobre sealed es exhaustivo
        case Circulo c -> c.area();
        case Rectangulo r -> r.area();
        // No hay default: el compiler sabe que no hay más subtipos
    };
}
```

**Peculiaridad:** `permits` restringe quién puede implementar. Los subtipos deben ser `final`, `sealed`, o `non-sealed`. Esto habilita *exhaustive pattern matching* — el compiler verifica que cubriste todos los casos.

### Ejercicio práctico

1. Crear clase abstracta `Empleado` con `calcularSalario()` abstracto
2. Crear interfaz `Bonificable` con `calcularBonificacion()` y default `aplicarBonificacion()`
3. Crear `Gerente`, `Desarrollador`, `Disenador` que extiendan `Empleado` e implementen `Bonificable`
4. Cada uno calcula salario y bonificación diferente
5. Crear array de `Empleado` y mostrar salario + bonificación de cada uno

**Solución esperada:** Jerarquía completa con interfaces y clases abstractas funcionando juntas.

---

## Resumen del Módulo 2

### Conceptos clave

| Concepto | Descripción |
|---|---|
| Clase | Plano para crear objetos |
| Objeto | Instancia de una clase |
| Constructor | Método especial para inicializar objetos |
| `this` | Referencia al objeto actual |
| Encapsulamiento | Proteger atributos con `private` + getters/setters |
| Herencia | `extends` — reutilizar código de la clase padre |
| `@Override` | Sobreescribir métodos de la clase padre |
| Herencia simple | Solo una clase padre (peculiaridad Java) |
| Polimorfismo | Un tipo de referencia, múltiples comportamientos |
| `instanceof` | Verificar tipo en runtime |
| Clase abstracta | No se instancia, tiene métodos abstractos + concretos |
| Interfaz | Contrato de comportamiento, `default` methods |
| `sealed` | Controlar qué clases pueden extender (Java 17+) |

### Siguiente módulo
→ [Módulo 3: Peculiaridades de Java](03-peculiaridades-java.md)
