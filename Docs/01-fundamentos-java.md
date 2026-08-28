# Módulo 1: Fundamentos de Java

**Duración:** 4 horas  
**Objetivo:** Comprender el ecosistema Java, dominar tipos de datos, estructuras de control, métodos y arrays

---

## 1.1 El ecosistema Java (30 min)

### Objetivos
- Entender la diferencia entre JVM, JRE y JDK
- Comprender el modelo de compilación de Java
- Configurar el entorno de desarrollo

### Contenido teórico

#### JVM, JRE, JDK

```
┌─────────────────────────────────────────┐
│                 JDK                     │
│  ┌───────────────────────────────────┐  │
│  │              JRE                  │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │           JVM               │  │  │
│  │  │  ┌───────────────────────┐  │  │  │
│  │  │  │   Bytecode (.class)   │  │  │  │
│  │  │  └───────────────────────┘  │  │  │
│  │  │   Garbage Collector         │  │  │
│  │  │   Memory Management         │  │  │
│  │  └─────────────────────────────┘  │  │
│  │   Class Libraries                │  │
│  │   Runtime APIs                   │  │
│  └───────────────────────────────────┘  │
│   Compilador (javac)                    │
│   Debugger, JDB                         │
│   Documentación (javadoc)               │
└─────────────────────────────────────────┘
```

- **JVM (Java Virtual Machine):** Máquina virtual que ejecuta bytecode. Es la pieza central — convierte `.class` en instrucciones nativas del SO
- **JRE (Java Runtime Environment):** JVM + librerías de clases. Lo que necesita una app para correr
- **JDK (Java Development Kit):** JRE + herramientas de desarrollo (compilador, debugger, etc.)

#### Compilación vs Interpretación

Java es **compilada e interpretada**:

```
Código fuente (.java)
       │
       ▼ (javac - compilador)
Bytecode (.class)
       │
       ▼ (JVM - intérprete + JIT)
Código nativo del SO
```

- **Ventaja:** Portabilidad — el mismo `.class` corre en cualquier SO con JVM
- **JIT (Just-In-Time):** La JVM optimiza en runtime el código que más se ejecuta

#### El classpath

El classpath es donde Java busca clases. Puede ser:
- Directorios: `-cp ./bin`
- JARs: `-cp lib/mi-app.jar`
- Wildcards: `-cp "lib/*"`

**Peculiaridad:** El classpath se resuelve en orden — la primera clase encontrada gana. Esto puede causar conflictos de versiones (el famoso "classpath hell").

### Ejemplo parcial

```bash
# Verificar instalación
java -version
javac -version

# Compilar y ejecutar
javac Hello.java
java Hello

# Con classpath
javac -d out src/com/miapp/*.java
java -cp out com.miapp.Main
```

### Teoría en profundidad: la JVM

#### Modelo de memoria de la JVM

```
┌──────────────────────────────────────────────┐
│              JVM Memory Model                 │
├──────────────────────────────────────────────┤
│  HEAP (compartido por todos los hilos)       │
│  ├── Eden ─────────────────────────┐          │
│  │   (objetos nuevos)              │          │
│  │   ┌────────────────────────────┐│          │
│  │   │ Survivor S0 │ Survivor S1 ││          │
│  │   └────────────────────────────┘│          │
│  ├── Tenured / Old Generation      │          │
│  │   (objetos sobrevivientes)      │          │
│  └── Metaspace (clases, métodos,   │          │
│      bytecode — fuera del heap)    │          │
├──────────────────────────────────────────────┤
│  STACK (uno por hilo) — LIFO                │
│  ├── Frame: local variables                 │
│  ├── Frame: operand stack                   │
│  ├── Frame: reference to constant pool      │
│  └── Frame: return address                  │
├──────────────────────────────────────────────┤
│  PC Register (contador de programa)          │
├──────────────────────────────────────────────┤
│  Native Method Stack (C/C++ frames)          │
└──────────────────────────────────────────────┘
```

- **Heap:** Todos los objetos viven aquí. Es compartido entre hilos y gestionado por el Garbage Collector
- **Stack:** Cada hilo tiene su propio stack. Guarda valores primitivos y referencias a objetos (no los objetos)
- **Metaspace:** Desde Java 8 reemplazó a PermGen. Guarda metadatos de clases. Crece dinámicamente

**Peculiaridad:** Un objeto vive en el heap pero "se alcanza" desde la stack vía referencia. Si la referencia se pierde, el GC lo recoge.

#### Garbage Collector

Java no tiene `free()` ni `delete()`. El GC se encarga:

```
1. Objeto creado → Eden (young generation)
2. Minor GC → sobrevivientes pasan a Survivor
3. Tras N ciclos sin morir → promovido a Tenured
4. Major GC → limpia Tenured
5. Reclama memoria, compacta, actualiza referencias
```

**Algoritmos de GC:** Serial, Parallel, CMS (legacy), G1 (default desde Java 9), ZGC y Shenandoah (low-latency). Cada uno balancea *throughput* vs *pausas*.

**Peculiaridad de Stop-The-World:** La mayoría de GC detienen la app brevemente. ZGC/Shenandoah minimizan pausas con regiones coloreadas y hardware barriers.

#### ClassLoader

```
Bootstrap ClassLoader  → java.* (JRE core)
        ↓
Platform ClassLoader   → módulos JDK (Java 9+)
        ↓
Application ClassLoader → classpath de la app
        ↓
User-defined ClassLoader (custom, ej: web servers)
```

**Modelo de delegación:** Cada classloader pregunta primero a su padre. Solo si el padre no encuentra la clase, la carga él. Esto evita duplicados y mantiene seguridad (el core de Java no puede ser suplantado).

#### Bytecode: qué produce javac

```java
// Fuente
public int sumar(int a, int b) { return a + b; }

// Bytecode (javap -c Sumar)
  public int sumar(int, int);
    iload_1       // cargar a desde slot 1
    iload_2       // cargar b desde slot 2
    iadd          // sumar
    ireturn       // retornar
```

**Peculiaridad:** El bytecode es la representación intermedia. No es código máquina del SO — la JVM lo interpreta o lo compila JIT. Por eso el mismo `.class` corre en Windows, Linux y macOS.

#### JIT vs AOT

| Enfoque | Cómo funciona | Cuándo |
|---------|---------------|--------|
| Interprete | Ejecuta bytecode instrucción por instrucción | Arranque |
| C2 JIT | Compila métodos "calientes" a código nativo | Runtime |
| AOT (GraalVM) | Compila todo antes de ejecutar | Startups, serverless |

**C1 (client) vs C2 (server) JIT:** La JVM usa C1 para arranques rápidos y C2 para optimización agresiva. Tiered compilation (default) combina ambos.

### Ejercicio práctico

**Configurar el entorno:**

1. Instalar JDK 17+ (Adoptium o Oracle)
2. Verificar con `java -version`
3. Crear `Hello.java`:
   ```java
   public class Hello {
       public static void main(String[] args) {
           System.out.println("Hola, Java!");
       }
   }
   ```
4. Compilar: `javac Hello.java`
5. Ejecutar: `java Hello`

**Solución esperada:** El terminal imprime "Hola, Java!" sin errores.

---

## 1.2 Variables, tipos y operadores (45 min)

### Objetivos
- Diferenciar tipos primitivos de objetos
- Entender la inmutabilidad de String
- Dominar conversiones de tipos

### Contenido teórico

#### Tipos primitivos

Java tiene **8 tipos primitivos** — son los únicos que no son objetos:

| Tipo | Bytes | Rango | Default |
|------|-------|-------|---------|
| `byte` | 1 | -128 a 127 | 0 |
| `short` | 2 | -32,768 a 32,767 | 0 |
| `int` | 4 | -2^31 a 2^31-1 | 0 |
| `long` | 8 | -2^63 a 2^63-1 | 0L |
| `float` | 4 | Precisión decimal ~7 | 0.0f |
| `double` | 8 | Precisión decimal ~15 | 0.0 |
| `char` | 2 | Unicode 0 a 65,535 | '\u0000' |
| `boolean` | 1 | true/false | false |

**Peculiaridad:** Los primitivos se guardan en la **stack** (rápido), los objetos en el **heap** (garbage collected).

#### String no es primitivo

```java
String nombre = "Java";  // Objeto, no primitivo
```

`String` es una clase de `java.lang`. Pero tiene syntactic sugar:

```java
// Esto es válido gracias al compiler
String saludo = "Hola" + " " + "Mundo";  // El compiler crea StringBuilder
```

#### Inmutabilidad de String

```java
String s = "hello";
s.toUpperCase();  // No modifica s — retorna nuevo String
System.out.println(s);  // Imprime "hello"

String s2 = s.toUpperCase();  // Ahora s2 tiene "HELLO"
```

**Peculiaridad:** `String` es inmutable por seguridad (hashing, caching, hilos). Cada operación crea un nuevo objeto.

#### Autoboxing y Unboxing

```java
// Autoboxing: primitivo → objeto
Integer num = 42;  // Compiler crea Integer.valueOf(42)

// Unboxing: objeto → primitivo
int valor = num;   // Compiler crea num.intValue()

// Cuidado con null
Integer n = null;
int x = n;  // NullPointerException en runtime
```

### Ejemplo parcial

```java
// Variables
String nombre = "Maria";
int edad = 25;
double salario = 4500.50;
boolean activo = true;

// Operadores
int suma = 10 + 5;        // 15
double promedio = suma / 3.0;  // 5.0 (división con double)
int modulo = 10 % 3;      // 1

// Comparación
boolean esMayor = edad > 18;  // true
boolean iguales = nombre.equals("Maria");  // true — NO usar == para Strings
```

### Teoría en profundidad: tipos y representación binaria

#### Representación de enteros: complemento a dos

Java usa complemento a dos para enteros con signo. Un `byte` (8 bits):

```
 7 → 0000 0111
 1 → 0000 0001
 0 → 0000 0000
-1 → 1111 1111  (invertir 0000 0001 → 1111 1110, sumar 1 → 1111 1111)
-128 → 1000 0000  (extremo negativo del byte)
```

**Por qué:** La resta se convierte en suma (a - b = a + (-b)). El hardware suma, no resta. Overflow natural:
```
127 + 1 = -128   (desbordamiento silencioso)
```
Java no lanza error en overflow de enteros — simplemente envuelve. Usa `Math.addExact()` si necesitas detección.

#### Punto flotante: IEEE 754

```java
// float (32 bits): 1 signo + 8 exponente + 23 mantisa
// double (64 bits): 1 signo + 11 exponente + 52 mantisa

0.1 + 0.2 == 0.3  // false!
// 0.1 en binario es periódico: 0.000110011001100...
// Resultado: 0.30000000000000004
```

**Peculiaridad crítica:** Los decimales no son exactos en binario. Para dinero **nunca** uses `double`. Usa `BigDecimal`:

```java
BigDecimal a = new BigDecimal("0.1");
BigDecimal b = new BigDecimal("0.2");
a.add(b);  // 0.3 exacto

// Nunca BigDecimal(0.1) — usa String en el constructor
// new BigDecimal(0.1) → 0.1000000000000000055511151231257827021181583404541015625
```

#### El String Pool y la inmutabilidad

```java
String a = "hola";      // literal → String Pool (interning)
String b = "hola";      // mismo objeto del pool
a == b                  // true (misma referencia)

String c = new String("hola");  // NUEVO objeto en heap
a == c                  // false (referencias distintas)
a.equals(c)             // true (mismo contenido)

String d = ("ho" + "la").intern();  // forcé al pool
a == d                  // true
```

**Peculiaridad:** Los literales de String se cachean en el String Pool (parte del heap). La JVM **interna** los literales en compile-time. `new String()` evita el pool deliberadamente — mala práctica salvo casos raros.

**Coste de inmutabilidad:** Cada concatenación crea un objeto nuevo:

```java
String s = "";
for (int i = 0; i < 10000; i++) {
    s += i;   // 10,000 objetos String intermedios → O(n²)
}
// Usa StringBuilder en ciclos:
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);  // O(n), buffer mutable
}
```

El compiler optimiza `"a" + "b"` en una sola concatenación, pero en ciclos NO puede — de ahí `StringBuilder`.

#### Autoboxing: coste oculto

```java
// Cada autoboxing crea un objeto Integer
List<Integer> nums = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    nums.add(i);   // 1,000,000 objetos Integer
}

// Cache de Integer: -128 a 127
Integer x = 127; Integer y = 127;  x == y  // true (cache)
Integer p = 128; Integer q = 128;  p == q  // false (objetos distintos)
```

**Peculiaridad:** `Integer.valueOf()` cachea -128..127. Comparar Integers con `==` es bug sutil — siempre usa `.equals()` o desempaqueta.

### Ejercicio práctico

**Calculadora de tipos:**

1. Crear variables de cada tipo primitivo
2. Realizar conversiones entre ellos (int → double, double → int)
3. Demostrar la diferencia entre `==` y `.equals()` para Strings
4. Calcular el área de un rectángulo y el volumen de un cubo

**Solución esperada:** Programa que imprime resultados de cada operación sin errores de compilación.

---

## 1.3 Estructuras de control (45 min)

### Objetivos
- Dominar condicionales y ciclos
- Entender el switch moderno (Java 14+)
- Usar break, continue y labels

### Contenido teórico

#### if/else

```java
if (condicion1) {
    // ...
} else if (condicion2) {
    // ...
} else {
    // ...
}
```

#### switch moderno (Java 14+)

```java
// Antes (verbose)
String resultado;
switch (dia) {
    case "LUNES":
        resultado = "Inicio";
        break;
    case "VIERNES":
        resultado = "Casi fin";
        break;
    default:
        resultado = "Otro día";
}

// Ahora (moderno)
String resultado = switch (dia) {
    case "LUNES" -> "Inicio";
    case "VIERNES" -> "Casi fin";
    default -> "Otro día";
};

// Con lógica (yield)
String resultado = switch (dia) {
    case "LUNES", "MARTES", "MIERCOLES", "JUEVES" -> {
        System.out.println("Día laboral");
        yield "Trabajo";
    }
    case "VIERNES" -> "Casi fin";
    default -> "Descanso";
};
```

**Peculiaridad:** El switch expression retorna valor. Las ramas con `{}` necesitan `yield`.

#### Ciclos

```java
// for clásico
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// for-each (para arrays/colecciones)
String[] frutas = {"Manzana", "Pera", "Naranja"};
for (String fruta : frutas) {
    System.out.println(fruta);
}

// while
while (condicion) {
    // ...
}

// do-while (ejecuta al menos una vez)
do {
    // ...
} while (condicion);
```

#### Break, Continue y Labels

```java
// break: sale del ciclo
for (int i = 0; i < 10; i++) {
    if (i == 5) break;  // Se detiene en 5
}

// continue: salta a la siguiente iteración
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // Saltar pares
    System.out.println(i);  // Solo impares
}

// Labels: control de ciclos anidados
outer:
for (int i = 0; i < 5; i++) {
    for (int j = 0; j < 5; j++) {
        if (i * j > 6) break outer;  // Sale del ciclo externo
    }
}
```

### Ejemplo parcial

```java
// FizzBuzz clásico
for (int i = 1; i <= 100; i++) {
    if (i % 3 == 0 && i % 5 == 0) {
        System.out.println("FizzBuzz");
    } else if (i % 3 == 0) {
        System.out.println("Fizz");
    } else if (i % 5 == 0) {
        System.out.println("Buzz");
    } else {
        System.out.println(i);
    }
}
```

### Teoría en profundidad: estructuras de control en bytecode

#### El switch: tableswitch vs lookupswitch

El compiler genera **dos versiones** de switch según la densidad de casos:

```java
switch (x) {
    case 0: ... case 1: ... case 2: ... case 3: ...
    // Densos → tableswitch (tabla de salto, O(1))
}

switch (x) {
    case 10: ... case 500: ... case 5000: ...
    // Dispersos → lookupswitch (búsqueda, O(log n))
}
```

**Peculiaridad:** Para switches muy dispersos con pocos casos, JIT puede convertir a if-else chain si es más barato que la tabla.

#### Expression switch: por qué existe

```java
// Switch expression garantiza exhaustividad
int diaNumero = switch (dia) {
    case "LUN", "MAR" -> 1;
    case "MIE", "JUE" -> 2;
    case "VIE" -> 3;
    default -> 0;   // Obligatorio si no cubres todos los valores
};
```

**Características:** Retorna valor, no hace fall-through (no necesita `break`), es exhaustivo, y con `yield` permite bloques. El "arrow" `->` cambia la semántica: no hay *fall-through* como en el `case:` clásico.

#### Loops y optimizaciones del JIT

```java
// for clásico — el JIT hace loop unrolling
for (int i = 0; i < 4; i++) { op(i); }
// → op(0); op(1); op(2); op(3);  (desenrollado)

// Bounded loops con contador int son los más optimizables.
// while(true) + break interno también.
```

**Reglas de optimización:**
- El JIT detecta loops calientes (profiling counters)
- Hace *loop unrolling* para reducir saltos
- Elimina código muerto dentro del loop
- Si el loop nunca termina (bug), la app cuelga — no hay timeout automático

#### Ciclos infinitos y complejidad

```java
// O(n²) — evita anidar sin necesidad
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) { ... }
}

// O(n) — recorrido lineal
for (int i = 0; i < n; i++) { ... }
```

### Ejercicio práctico

**Juego de adivinanza:**

1. Generar número aleatorio entre 1 y 100
2. Dar al usuario 7 intentos
3. En cada intento: decir si es mayor o menor
4. Al final: revelar el número y decir si ganó o perdió
5. Usar `Scanner` para leer input del usuario

**Solución esperada:** Juego interactivo que guía al usuario hasta encontrar el número.

---

## 1.4 Métodos y pass-by-value (45 min)

### Objetivos
- Definir y usar métodos con diferentes firmas
- Entender pass-by-value en Java (incluso con objetos)
- Dominar sobrecarga de métodos

### Contenido teórico

#### Definición de métodos

```java
// Modificador retorno nombre(parametros) { cuerpo }
public static int sumar(int a, int b) {
    return a + b;
}

// Método sin retorno (void)
public static void imprimir(String mensaje) {
    System.out.println(mensaje);
}

// Métodos con múltiples parámetros
public static String formatear(String nombre, int edad, double salario) {
    return String.format("Nombre: %s, Edad: %d, Salario: %.2f", 
                         nombre, edad, salario);
}
```

#### Pass-by-value (la gran peculiaridad)

**Java SIEMPRE pasa por valor.** Nunca pasa referencias.

```java
public static void modificar(int numero) {
    numero = 100;  // No modifica el original
}

public static void modificarArray(int[] arr) {
    arr[0] = 999;  // SÍ modifica el contenido
}

public static void modificarString(String s) {
    s = "nuevo";   // No modifica el original (String es inmutable)
}

// Uso
int num = 5;
modificar(num);
System.out.println(num);  // Imprime 5 (no cambió)

int[] nums = {1, 2, 3};
modificarArray(nums);
System.out.println(nums[0]);  // Imprime 999 (sí cambió)

String texto = "hola";
modificarString(texto);
System.out.println(texto);  // Imprime "hola" (no cambió)
```

**¿Por qué?** Java copia el valor de la referencia. El objeto en el heap no se mueve, pero la referencia local apunta a la misma dirección.

#### Sobrecarga (Overloading)

Mismo nombre, diferentes parámetros:

```java
public static int sumar(int a, int b) { return a + b; }
public static double sumar(double a, double b) { return a + b; }
public static int sumar(int a, int b, int c) { return a + b + c; }

// El compiler decide cuál usar según los argumentos
sumar(1, 2);          // Usa sumar(int, int)
sumar(1.5, 2.5);     // Usa sumar(double, double)
sumar(1, 2, 3);       // Usa sumar(int, int, int)
```

**No confundir con overriding** — sobrecarga es en la misma clase, overriding es entre clases (herencia).

### Ejemplo parcial

```java
// Método utilitario completo
public class Calculadora {
    
    public static double calcularPromedio(double[] notas) {
        if (notas == null || notas.length == 0) {
            return 0;
        }
        double suma = 0;
        for (double nota : notas) {
            suma += nota;
        }
        return suma / notas.length;
    }
    
    public static boolean esAprobado(double promedio) {
        return promedio >= 6.0;
    }
    
    public static String formatearPromedio(double promedio) {
        return String.format("Promedio: %.2f - %s", 
            promedio, esAprobado(promedio) ? "Aprobado" : "Reprobado");
    }
}
```

### Teoría en profundidad: stack frames y recursión

#### El stack de llamadas (call stack)

Cada llamada a método crea un **stack frame**:

```
Stack (crece hacia abajo)
┌──────────────────────────────┐
│ main() frame                 │
│  local: args, a, b, resultado│
│  operand stack               │
│  return address              │
├──────────────────────────────┤
│ sumar() frame  ← nueva llamada
│  local: x, y                 │
│  operand stack               │
│  return address → main+3     │
└──────────────────────────────┘
```

- **Local variables:** Espacio reservado para los parámetros y variables locales del método
- **Operand stack:** Donde ocurren los cálculos (push/pop)
- **Return address:** Dónde seguir después de que el método termine

**Peculiaridad del pass-by-value:** Cuando llamas `sumar(x, y)`, se **copian los valores** a los slots locales del nuevo frame. Para objetos, se copia la referencia (el puntero), no el objeto. El objeto sigue en el heap, alcanzable desde ambos frames.

#### ¿Por qué "value" aunque parezca "reference"?

```java
public static void main(String[] args) {
    Persona p = new Persona("Ana");
    cambiarNombre(p);
    System.out.println(p.getNombre());  // "Maria"
}

public static void cambiarNombre(Persona q) {
    q.setNombre("Maria");   // Modifica el MISMO objeto
    q = new Persona("Otro"); // Reasigna SOLO la copia local
}
```

```
Antes:  main.p ──→ [Persona "Ana"] (heap)
        cambiarNombre.q ──→ mismo objeto (copia de la referencia)

        q.setNombre("Maria")  → [Persona "Maria"] (el objeto cambió)
        q = new Persona(...)  → q apunta a OTRO objeto, main.p no se entera
```

**Conclusión:** Java pasa **el valor de la referencia**. No puedes "reapuntar" la variable del llamador, pero sí mutar el objeto apuntado. Esto se llama *call-by-sharing*.

#### Recursión y StackOverflowError

```java
// Recursión sin caso base → desborda el stack
public static long factorial(int n) {
    return n * factorial(n - 1);   // ¡nunca para! → StackOverflowError
}

// Recursión correcta con caso base
public static long factorial(int n) {
    if (n <= 1) return 1;          // caso base
    return n * factorial(n - 1);   // caso recursivo
}
```

Cada frame ocupa memoria. ~10,000-30,000 frames suelen llenar el stack default (1MB). La recursión profunda falla con `StackOverflowError` — para iteraciones grandes usa loops o recursión de cola (aunque Java no la optimiza por defecto, el JIT a veces sí).

#### Varargs

```java
// Los varargs son un array en la firma del método
public static int sumar(int... numeros) {
    int total = 0;
    for (int n : numeros) total += n;
    return total;
}

sumar(1, 2, 3);      // array {1,2,3}
sumar(1, 2, 3, 4);   // array {1,2,3,4}
// En bytecode: sumar(int[] numeros) — el compiler empaqueta
```

### Ejercicio práctico

**Biblioteca de funciones:**

1. Crear métodos para: calcular área del círculo, perímetro del rectángulo, convertir Celsius a Fahrenheit
2. Crear sobrecarga: un método `convertir` que acepte Celsius→Fahrenheit O Kelvin→Celsius
3. Crear método `formatearMoneda` que acepte `double` o `int` y retorne string formateado

**Solución esperada:** Clase `CalculadoraUtilitaria` con todos los métodos funcionando.

---

## 1.5 Arrays (15 min)

### Objetivos
- Declarar e inicializar arrays
- Recorrer arrays con diferentes métodos
- Entender las limitaciones de los arrays

### Contenido teórico

#### Declaración e inicialización

```java
// Declaración
int[] numeros;

// Inicialización
numeros = new int[5];  // [0, 0, 0, 0, 0] — defaults

// Declaración + inicialización
int[] nums = {10, 20, 30, 40, 50};

// Inicialización con new
String[] frutas = new String[]{"Manzana", "Pera", "Naranja"};

// Array multidimensional
int[][] matriz = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
```

#### Acceso y modificación

```java
int[] nums = {10, 20, 30};

// Acceso por índice (empieza en 0)
int primero = nums[0];   // 10
int ultimo = nums[2];    // 30

// Modificación
nums[1] = 25;  // Ahora nums es {10, 25, 30}

// Longitud
int longitud = nums.length;  // 3 — NO es un método, es un campo
```

#### Recorrer arrays

```java
int[] nums = {10, 20, 30, 40, 50};

// for clásico
for (int i = 0; i < nums.length; i++) {
    System.out.println(nums[i]);
}

// for-each
for (int num : nums) {
    System.out.println(num);
}

// Java 8+ Streams
Arrays.stream(nums).forEach(System.out::println);
```

#### Limitaciones de arrays

```java
// Tamaño fijo — no se puede redimensionar
int[] arr = new int[5];
// arr[5] = 10;  // ArrayIndexOutOfBoundsException

// No hay métodos útiles
// Para agregar, eliminar, buscar → usa Collections (Módulo 3)

// Copia
int[] copia = Arrays.copyOf(arr, arr.length);
```

**Peculiaridad:** Los arrays en Java son objetos — tienen `.length` (campo, no método) y están en el heap.

### Ejemplo parcial

```java
import java.util.Arrays;

public class ArrayDemo {
    public static void main(String[] args) {
        int[] calificaciones = {85, 92, 78, 95, 88};
        
        // Buscar máximo
        int max = calificaciones[0];
        for (int cal : calificaciones) {
            if (cal > max) max = cal;
        }
        
        // Ordenar
        Arrays.sort(calificaciones);
        
        // Imprimir formateado
        System.out.println("Calificaciones ordenadas: " + 
                          Arrays.toString(calificaciones));
        System.out.println("Máximo: " + max);
    }
}
```

### Teoría en profundidad: arrays en memoria

#### Layout de un array en el heap

```
Array de primitivos int[4]:
┌──────────┬────┬────┬────┬────┐
│ header   │ 10 │ 20 │ 30 │ 40 │
│ (16 bytes)│    │    │    │    │
└──────────┴────┴────┴────┴────┘
   length = 4

Array de referencias String[3]:
┌──────────┬────────┬────────┬────────┐
│ header   │ → "A"  │ → "B"  │ → "C"  │
│ (16 bytes)│ ref    │ ref    │ ref    │
└──────────┴────────┴────────┴────────┘
   length = 3
```

**Peculiaridad:** El array es un objeto: tiene header (incluye `length` como campo) y el acceso `arr[i]` es *bounds-checked* por la JVM — acceder fuera de rango lanza `ArrayIndexOutOfBoundsException` (no comportamiento indefinido como en C).

#### Covarianza de arrays

```java
String[] strings = new String[3];
Object[] objetos = strings;      // ✅ Válido — arrays son covarianTES
objetos[0] = 42;                 // ✅ Compila...
                                 // ❌ ArrayStoreException en runtime
```

**Peculiaridad:** Los arrays son **covariantes** (a diferencia de los generics que son invariantes). Esto es una decisión histórica de diseño que permite el error de tipo en runtime. Los generics (Módulo 3) lo corrigieron siendo invariantes.

#### Matrices = arrays de arrays

```java
int[][] matriz = new int[3][];
matriz[0] = new int[2];   // fila de 2
matriz[1] = new int[5];   // fila de 5  ← ¡filas de distinta longitud!
matriz[2] = new int[3];

// No es una estructura rectangular — es un array de referencias a filas
// Memory: 1 array de 3 refs → 3 arrays int separados
```

#### Arrays.asList y las trampas

```java
int[] primitivos = {1, 2, 3};
Arrays.asList(primitivos);  // ⚠️ List<int[]> con UN elemento (el array)
// Con primitivos no funciona — usa Integer[]

String[] nombres = {"A", "B"};
List<String> lista = Arrays.asList(nombres);  // ✅
lista.set(0, "X");   // modifica el array subyacente
lista.add("C");      // ❌ UnsupportedOperationException (tamaño fijo)
```

### Ejercicio práctico

**Sistema de calificaciones:**

1. Crear array de 5 calificaciones (hardcoded)
2. Calcular promedio
3. Encontrar nota más alta y más baja
4. Contar cuántas son mayores al promedio
5. Ordenar las calificaciones de mayor a menor

**Solución esperada:** Programa que imprime estadísticas completas del array.

---

## Resumen del Módulo 1

### Conceptos clave

| Concepto | Descripción |
|---|---|
| JVM/JRE/JDK | Máquina virtual → Runtime → Development Kit |
| Compilación doble | `.java` → `.class` → código nativo |
| Tipos primitivos | 8 tipos: byte, short, int, long, float, double, char, boolean |
| String inmutabilidad | Cada operación crea nuevo String |
| Autoboxing | Conversión automática primitivo ↔ objeto |
| Pass-by-value | Java siempre copia el valor (incluso referencias) |
| Overloading | Mismo nombre, diferentes parámetros |
| Arrays | Tamaño fijo, `.length` campo, indexación desde 0 |

### Siguiente módulo
→ [Módulo 2: Programación Orientada a Objetos](02-poo-java.md)
