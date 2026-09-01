# Módulo 3: Peculiaridades de Java como Lenguaje

**Duración:** 4 horas  
**Objetivo:** Dominar excepciones, generics, collections, lambdas y Optional

---

## 3.1 El sistema de excepciones (45 min)

### Objetivos
- Entender checked vs unchecked exceptions (la gran peculiaridad de Java)
- Usar `try-with-resources` correctamente
- Crear excepciones personalizadas

### Contenido teórico

#### Checked vs Unchecked

**La peculiaridad más famosa de Java:** Java tiene dos tipos de excepciones, y el compiler te obliga a manejar una de ellas.

```
Throwable
├── Error (NO se maneja — problemas del JVM)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception
    ├── RuntimeException (UNCHECKED — no estás obligado a manejar)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── IllegalArgumentException
    │   ├── ClassCastException
    │   └── ArithmeticException
    └── Checked EXCEPTION (OBLIGATORIO manejar con try-catch o declarar throws)
        ├── IOException
        ├── SQLException
        ├── FileNotFoundException
        └── ClassNotFoundException
```

```java
// Checked — DEBES manejar o declarar
public void leerArchivo() throws IOException {  // Declarar
    FileInputStream fis = new FileInputStream("archivo.txt");
}

// O manejar
public void leerArchivo() {
    try {
        FileInputStream fis = new FileInputStream("archivo.txt");
    } catch (IOException e) {  // Manejar
        System.out.println("Error leyendo archivo");
    }
}

// Unchecked — no estás obligado (pero puedes)
public void dividir(int a, int b) {
    int resultado = a / b;  // Puede lanzar ArithmeticException
    // No necesitas try-catch
}
```

**¿Por qué checked exceptions?** James Gosling (creador de Java) quería forzar a los programadores a manejar errores recuperables. Es controversial — otros lenguajes como C# y Python no lo hacen.

#### try-with-resources

```java
// Antes (verbose)
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("archivo.txt"));
    String linea = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Ahora (Java 7+) — AutoCloseable
try (BufferedReader br = new BufferedReader(new FileReader("archivo.txt"))) {
    String linea = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
}
// br se cierra automáticamente
```

**Peculiaridad:** `try-with-resources` funciona con cualquier clase que implemente `AutoCloseable`.

#### Custom Exceptions

```java
// Excepción checked
public class SaldoInsuficienteException extends Exception {
    private double saldoActual;
    private double montoSolicitado;
    
    public SaldoInsuficienteException(double saldoActual, double montoSolicitado) {
        super("Saldo insuficiente. Actual: " + saldoActual + ", Solicitado: " + montoSolicitado);
        this.saldoActual = saldoActual;
        this.montoSolicitado = montoSolicitado;
    }
    
    public double getSaldoActual() { return saldoActual; }
    public double getMontoSolicitado() { return montoSolicitado; }
}

// Excepción unchecked
public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

// Uso
public void retirar(double monto) throws SaldoInsuficienteException {
    if (monto > saldo) {
        throw new SaldoInsuficienteException(saldo, monto);
    }
    saldo -= monto;
}
```

### Ejemplo parcial

```java
// Manejo completo de excepciones
public class ServicioArchivo {
    
    public String leerPrimeraLinea(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            return br.readLine();
        }
    }
    
    public int dividir(int a, int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            System.out.println("Error: división por cero");
            return 0;
        }
    }
    
    public void procesarArchivos(String[] rutas) {
        for (String ruta : rutas) {
            try {
                String contenido = leerPrimeraLinea(ruta);
                System.out.println(ruta + ": " + contenido);
            } catch (FileNotFoundException e) {
                System.out.println("No encontrado: " + ruta);
            } catch (IOException e) {
                System.out.println("Error leyendo: " + ruta);
            }
        }
    }
}
```

### Teoría en profundidad: el mecanismo de excepciones

#### Cómo se implementa el try-catch en bytecode

El compiler genera una **exception table** por método:

```java
public int dividir(int a, int b) {
    try {
        return a / b;
    } catch (ArithmeticException e) {
        System.out.println("división por cero");
        return 0;
    }
}
```

```
Bytecode (simplificado):
  start_pc  end_pc  handler_pc  catch_type
  0         4       6           ArithmeticException

Si ocurre la excepción entre pc 0 y 4, salta al handler en pc 6.
Catch de Throwable → catch_type nulo (cualquier excepción).
```

**Peculiaridad:** lanzar una excepción es costoso: la JVM debe construir el stack trace (recorrer frames), y el try/catch NO es gratuito como en C++. El handler lookup se hace en runtime.

#### Checked vs unchecked: el debate de diseño

| Lenguaje | Postura |
|----------|---------|
| Java | Checked obligatorio (J. Gosling: forzar manejo de errores recuperables) |
| C#, Python, Ruby | Todo unchecked |
| Kotlin | Sin checked exceptions (decisión explícita de diseño) |

**Controversia:** los checked exceptions en interfaces complican la evolución (agregar una checked exception rompe todos los callers). Muchos frameworks (Spring, JPA) envuelven checked en unchecked: `SQLException` → `DataAccessException` (RuntimeException).

#### finally y try-with-resources: el orden de ejecución

```java
// ¿Qué imprime? (truco de entrevista)
public static String demo() {
    try {
        return "try";
    } finally {
        System.out.println("finally");  // se ejecuta SIEMPRE
    }
}
// Imprime "finally" y retorna "try".
// El finally corre ANTES de que el return se materialice en el caller.

// El problema del return en finally:
public static int demo2() {
    try { return 1; }
    finally { return 2; }   // ⚠️ SOBRESCRIBE el return del try
}
// Retorna 2 — nunca hagas return en finally
```

**try-with-resources** traduce a bytecode equivalente a try-finally con close(). La diferencia: `close()` se ejecuta ANTES que el bloque catch/finally del usuario y las excepciones suprimidas se agregan (método `addSuppressed`).

#### La supresión de excepciones

```java
try (Resource r = new Resource()) {  // close() lanza IOException
    throw new BusinessException();    // cuerpo lanza BusinessException
}
// El exception principal es BusinessException
// La IOException de close() queda SUPRIMIDA (accesible via getSuppressed())
```

**Peculiaridad:** cuando cuerpo y close() lanzan, el close() se suprime y el cuerpo es el principal. Antes de Java 7 se perdía información; con try-with-resources se preserva.

### Ejercicio práctico

1. Crear `SaldoInsuficienteException` (checked)
2. Crear `CuentaBancaria` que lance la excepción al intentar retirar más del saldo
3. Crear programa que intente varios retiros y maneje la excepción
4. Usar `try-with-resources` para leer un archivo y contar líneas

**Solución esperada:** Programa que maneja errores de saldo y archivos sin crashear.

---

## 3.2 Generics (45 min)

### Objetivos
- Entender por qué existen los generics
- Crear clases y métodos genéricos
- Comprender type erasure y wildcards

### Contenido teórico

#### El problema que resuelven

```java
// Sin generics — casting manual, propenso a errores
List lista = new ArrayList();
lista.add("texto");
lista.add(42);  // ¡Error de compilación detectado tarde!
String s = (String) lista.get(1);  // ClassCastException en runtime

// Con generics — type safety en compile-time
List<String> lista = new ArrayList<String>();
lista.add("texto");
// lista.add(42);  // ERROR de compilación — detectado INMEDIATAMENTE
String s = lista.get(0);  // No necesita casting
```

#### Clases genéricas

```java
public class Repository<T> {
    private List<T> elementos = new ArrayList<>();
    
    public void agregar(T elemento) {
        elementos.add(elemento);
    }
    
    public T obtener(int indice) {
        return elementos.get(indice);
    }
    
    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }
    
    public int contar() {
        return elementos.size();
    }
}

// Uso
Repository<String> repoStrings = new Repository<>();
Repository<Integer> repoNumeros = new Repository<>();

repoStrings.agregar("Hola");
repoNumeros.agregar(42);
// repoNumeros.agregar("texto");  // ERROR de compilación
```

#### Métodos genéricos

```java
public class Utilidades {
    // Método genérico — el tipo se infiere
    public static <T> List<T> filtrar(List<T> lista, Predicate<T> predicado) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : lista) {
            if (predicado.test(elemento)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }
}

// Uso — el tipo se infiere de los argumentos
List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6);
List<Integer> pares = Utilidades.filtrar(numeros, n -> n % 2 == 0);
// El compilador sabe que T es Integer
```

#### Type Erasure (peculiaridad)

**Los generics desaparecen en runtime.** El compiler los verifica y luego los elimina.

```java
// En compile-time
List<String> lista = new ArrayList<>();
lista.add("hello");

// En runtime — el tipo se "borra"
List lista2 = lista;  // Esto es lo que realmente existe
// No puedes hacer: lista.getClass() == List<String>.class
// Porque String ya no existe en runtime
```

**¿Por qué importa?**
- No puedes hacer `new T()` — no sabes qué clase es en runtime
- No puedes hacer `instanceof List<String>` — no existe el tipo parameterizado
- Los casts se insertan automáticamente por el compiler

#### Wildcards: PECS

**Producer Extends, Consumer Super:**

```java
// ¿Cuándo usar cada wildcard?

// Producer Extends — solo LEER
public static double sumar(List<? extends Number> lista) {
    double suma = 0;
    for (Number n : lista) {  // Number es el supertipo
        suma += n.doubleValue();
    }
    return suma;
}

List<Integer> enteros = List.of(1, 2, 3);
List<Double> decimales = List.of(1.5, 2.5, 3.5);

sumar(enteros);   // ✅ Integer extends Number
sumar(decimales);  // ✅ Double extends Number

// Consumer Super — solo ESCRIBIR
public static void agregarNumeros(List<? super Integer> lista) {
    lista.add(1);
    lista.add(2);
    // Number n = lista.get(0);  // ❌ No puedes leer como Number
}

List<Number> numeros = new ArrayList<>();
List<Object> objetos = new ArrayList<>();

agregarNumeros(numeros);   // ✅ Integer super Number
agregarNumeros(objetos);   // ✅ Integer super Object
```

### Ejemplo parcial

```java
// Repository genérico completo
public class Repository<T> {
    private final List<T> almacenamiento = new ArrayList<>();
    private final Function<T, String> extractorId;
    
    public Repository(Function<T, String> extractorId) {
        this.extractorId = extractorId;
    }
    
    public void guardar(T entidad) {
        almacenamiento.add(entidad);
    }
    
    public Optional<T> buscarPorId(String id) {
        return almacenamiento.stream()
            .filter(e -> extractorId.apply(e).equals(id))
            .findFirst();
    }
    
    public List<T> buscarTodos(Predicate<T> filtro) {
        return almacenamiento.stream()
            .filter(filtro)
            .toList();
    }
}
```

### Teoría en profundidad: type erasure y bridge methods

#### La erasure mecánica

```java
// Código fuente
class Caja<T> {
    private T contenido;
    public T get() { return contenido; }
    public void set(T contenido) { this.contenido = contenido; }
}

// Lo que realmente existe tras compilar (erasure):
class Caja {
    private Object contenido;
    public Object get() { return contenido; }
    public void set(Object contenido) { this.contenido = contenido; }
}

// Uso: Caja<String> c = new Caja<>(); c.set("hola");
// El compiler inserta cast: String s = (String) c.get();
```

**Regla de erasure:** los parámetros de tipo se reemplazan por su **límite superior** (`T` → `Object`, `T extends Number` → `Number`). Los casts se insertan en los puntos de uso.

#### Consecuencias prácticas de la erasure

```java
// 1. No puedes crear instancias del tipo genérico
public <T> T crear() {
    return new T();   // ❌ ERROR — T no existe en runtime
}

// 2. No puedes hacer instanceof parameterizado
if (x instanceof List<String>)   // ❌ ERROR — solo List<?>
if (x instanceof List<?>)        // ✅ correcto

// 3. No puedes crear arrays genéricos
List<String>[] arr = new List<String>[10];  // ❌ ERROR
// Solo: List<?>[] arr = new List<?>[10];

// 4. Los tipos static no conocen el parámetro
class Foo<T> {
    static T campo;  // ❌ ERROR — static no puede usar T
}
```

#### Bridge methods (métodos puente)

```java
class StringBox implements Box<String> {
    public void set(String s) { ... }   // método "real"
}

// El compiler genera un bridge para mantener el contrato de Box (Object):
// public void set(Object o) { set((String) o); }   // ← bridge sintético
```

El bridge existe porque el type erasure de la interfaz requiere `set(Object)`, pero la clase implementa `set(String)`. Son métodos sintéticos (no visibles en el código fuente) que delegan con cast. Esto es también por qué `getDeclaredMethods()` puede sorprender.

#### PECS — El principio completo

```
? extends T  (Producer) — solo puedes LEER: el tipo es T o un subtipo
  List<? extends Number> → get() devuelve Number, pero no puedes add()
  (no sabes si la lista es de Integer, Double, ...)

? super T  (Consumer) — solo puedes ESCRIBIR: el tipo es T o un supertipo
  List<? super Integer> → add(1) funciona, pero get() devuelve Object
  (no sabes el tipo concreto, solo que acepta Integer)

Sin wildcard (T) — puedes leer Y escribir
```

La regla mnemotécnica: **Producer Extends, Consumer Super**. Es la regla de varianza de Java: los generics son invariantes, el wildcard introduce covarianza (`? extends`) y contravarianza (`? super`).

### Ejercicio práctico

1. Crear `Par<A, B>` genérico que contenga dos valores
2. Crear `Utilidades` con métodos genéricos: `filtrar`, `transformar`, `encontrar`
3. Crear `Repository<T>` con búsqueda por criterio
4. Probar con diferentes tipos (String, Integer, objetos propios)

**Solución esperada:** Clases genéricas funcionando con múltiples tipos.

---

## 3.3 Collections Framework (60 min)

### Objetivos
- Distinguir `List`, `Set` y `Map`
- Conocer las implementaciones más importantes
- Dominar la Stream API para procesamiento de datos

### Contenido teórico

#### La jerarquía de Collections

```
Collection
├── List (ordenada, duplicados)
│   ├── ArrayList  — array dinámico (rápido acceso por índice)
│   ├── LinkedList — lista doblemente enlazada (rápido insert/eliminar)
│   └── Vector     — thread-safe (legacy, usar ArrayList)
├── Set (sin duplicados)
│   ├── HashSet      — hash table (rápido, sin orden)
│   ├── LinkedHashSet — hash + linked list (orden de inserción)
│   └── TreeSet      — árbol rojo-negro (ordenado)
└── Queue (FIFO)
    ├── PriorityQueue — cola de prioridad
    └── Deque         — doble extremo
        ├── ArrayDeque — array circular (mejor que Stack)
        └── LinkedList — también implementa Deque

Map (key-value, no es Collection)
├── HashMap       — hash table (rápido, sin orden)
├── LinkedHashMap — hash + linked list (orden de inserción)
├── TreeMap       — árbol (ordenado por key)
├── Hashtable     — thread-safe (legacy)
└── ConcurrentHashMap — thread-safe (concurrente)
```

#### List — ArrayList vs LinkedList

```java
// ArrayList — mejor para lectura y acceso por índice
List<String> arrayList = new ArrayList<>();
arrayList.add("A");      // O(1) amortizado
arrayList.get(0);        // O(1)
arrayList.remove(0);     // O(n) — tiene que mover elementos

// LinkedList — mejor para inserción/eliminación frecuente
List<String> linkedList = new LinkedList<>();
linkedList.add("A");     // O(1)
linkedList.get(0);       // O(n) — tiene que recorrer
linkedList.remove(0);    // O(1)
```

**Regla práctica:** Usa `ArrayList` por defecto. Solo usa `LinkedList` si insertas/eliminas mucho al inicio.

#### Set — sin duplicados

```java
// HashSet — más rápido, sin orden
Set<String>.HashSet = new HashSet<>();
HashSet.add("A");
HashSet.add("A");  // Ignorado — duplicado
System.out.println(HashSet.size());  // 1

// TreeSet — ordenado
Set<Integer> treeSet = new TreeSet<>();
treeSet.add(3);
treeSet.add(1);
treeSet.add(2);
System.out.println(treeSet);  // [1, 2, 3] — ordenado

// LinkedHashSet — orden de inserción
Set<String> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add("C");
linkedHashSet.add("A");
linkedHashSet.add("B");
System.out.println(linkedHashSet);  // [C, A, B] — orden de inserción
```

#### Map — key-value

```java
// HashMap — el más usado
Map<String, Integer> edades = new HashMap<>();
edades.put("Ana", 25);
edades.put("Bob", 30);
edades.put("Ana", 26);  // Sobrescribe el anterior

Integer edadAna = edades.get("Ana");  // 26
boolean existe = edades.containsKey("Ana");  // true
boolean tieneValor = edades.containsValue(30);  // true

// Iterar
for (Map.Entry<String, Integer> entry : edades.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Java 8+
edades.forEach((nombre, edad) -> System.out.println(nombre + ": " + edad));
```

#### Streams (la gran herramienta)

```java
List<String> nombres = List.of("Ana", "Bob", "Carlos", "Diana", "Eve");

// filter — filtrar
List<String> largos = nombres.stream()
    .filter(n -> n.length() > 3)
    .toList();  // [Carlos, Diana]

// map — transformar
List<Integer> longitudes = nombres.stream()
    .map(String::length)
    .toList();  // [3, 3, 6, 5, 3]

// reduce — agregar
int totalLetras = nombres.stream()
    .map(String::length)
    .reduce(0, Integer::sum);  // 20

// collect — recopilar
Map<Integer, List<String>> porLongitud = nombres.stream()
    .collect(Collectors.groupingBy(String::length));
// {3: [Ana, Bob, Eve], 5: [Diana], 6: [Carlos]}

// sorted — ordenar
List<String> ordenados = nombres.stream()
    .sorted()
    .toList();  // [Ana, Bob, Carlos, Diana, Eve]

// distinct — eliminar duplicados
List<String> conDuplicados = List.of("A", "B", "A", "C");
List<String> sinDuplicados = conDuplicados.stream()
    .distinct()
    .toList();  // [A, B, C]
```

**Peculiaridad:** `List.of()`, `Set.of()`, `Map.of()` crean colecciones **inmutables**. No puedes agregar elementos.

### Ejemplo parcial

```java
// Procesamiento de ventas con Streams
public class AnalisisVentas {
    
    public static void main(String[] args) {
        List<Venta> ventas = List.of(
            new Venta("Ana", "Electrónica", 1500),
            new Venta("Bob", "Ropa", 200),
            new Venta("Ana", "Ropa", 300),
            new Venta("Carlos", "Electrónica", 800),
            new Venta("Diana", "Libros", 150)
        );
        
        // Total vendido por categoría
        Map<String, Double> totalPorCategoria = ventas.stream()
            .collect(Collectors.groupingBy(
                Venta::getCategoria,
                Collectors.summingDouble(Venta::getMonto)
            ));
        
        // Top 2 ventas
        List<Venta> topVentas = ventas.stream()
            .sorted(Comparator.comparingDouble(Venta::getMonto).reversed())
            .limit(2)
            .toList();
        
        // Promedio de venta
        double promedio = ventas.stream()
            .mapToDouble(Venta::getMonto)
            .average()
            .orElse(0);
    }
}
```

### Teoría en profundidad: estructuras de datos subyacentes

#### HashMap por dentro

```
HashMap (capacidad inicial 16, load factor 0.75)

Índice  Bucket (lista enlazada, luego árbol rojo-negro si >8)
  0     → Entry(k1,v1) → Entry(k2,v2)
  1     → Entry(k3,v3)
  2     (vacío)
  ...

1. hashCode(key) → hash
2. Índice = hash & (capacidad-1)   (bitmask, asume capacidad potencia de 2)
3. Si colisión → se inserta en la cadena
4. Si bucket > 8 elementos → se convierte a TreeNode (árbol balanceado)
5. Si tamaño > capacidad × 0.75 → resize (duplica capacidad, rehash)
```

**Coste esperado:** O(1) amortizado para get/put. **Peor caso:** O(log n) con árbol, O(n) si hashCode está mal implementado (todas las claves al mismo bucket).

**Peculiaridad:** por eso el contrato equals/hashCode es crítico. Un hashCode pobre degrada HashMap a lista enlazada → O(n).

#### Complejidad de las implementaciones

| Estructura | get/contains | add/put | remove | Orden |
|------------|-------------|---------|--------|-------|
| `ArrayList` | O(1) por índice | O(1) amortizado | O(n) | inserción |
| `LinkedList` | O(n) | O(1) extremos | O(1) si hay ref | inserción |
| `HashSet`/`HashMap` | O(1) | O(1) | O(1) | ninguno |
| `LinkedHashSet`/`Map` | O(1) | O(1) | O(1) | inserción |
| `TreeSet`/`TreeMap` | O(log n) | O(log n) | O(log n) | natural |
| `PriorityQueue` | O(1) peek | O(log n) | O(log n) peek | prioridad |

**TreeMap/TreeSet** usan árbol rojo-negro: insertar/eliminar/buscar O(log n), siempre ordenado por el `Comparable` o `Comparator` de la clave.

#### Streams: lazy evaluation

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

nums.stream()
    .filter(n -> { System.out.println("filter: " + n); return n % 2 == 0; })
    .map(n -> { System.out.println("map: " + n); return n * n; })
    .limit(2)
    .toList();
// Salida:
// filter: 1
// filter: 2
// map: 2
// filter: 3
// filter: 4
// map: 4
```

**Peculiaridad:** los streams son **perezosos**. Las operaciones intermedias (`filter`, `map`) no procesan nada hasta que una terminal (`toList`, `forEach`, `reduce`) las dispara, y procesan **elemento a elemento**, no etapa por etapa. `limit(2)` corta el pipeline apenas hay 2 resultados — nunca procesa el 5+.

#### Streams: de una sola pasada

```java
Stream<Integer> s = List.of(1, 2, 3).stream();
List<Integer> a = s.toList();   // ✅ consume el stream
List<Integer> b = s.toList();   // ❌ IllegalStateException: stream already consumed

// Un stream NO es reutilizable ni almacenable — es un iterador de un solo uso.
```

#### Streams paralelos — precaución

```java
// stream.parallel() divide el trabajo en hilos (ForkJoinPool)
list.stream().parallel().map(f).reduce(...);

// Riesgos:
// 1. Orden no garantizado en resultados no ordenados
// 2. Estados compartidos → condiciones de carrera
// 3. Sobrecarga para listas pequeñas
// Regla: solo usar si la operación es costosa y la lista grande.
```

### Ejercicio práctico

1. Crear lista de 10 productos con nombre, categoría y precio
2. Filtrar productos con precio > 100
3. Agrupar por categoría
4. Calcular precio promedio por categoría
5. Encontrar el producto más caro
6. Ordenar por precio descendente

**Solución esperada:** Pipeline completo de procesamiento de datos con Streams.

---

## 3.4 Lambdas y Functional Interfaces (45 min)

### Objetivos
- Entender la sintaxis lambda
- Usar las functional interfaces principales
- Aplicar method references

### Contenido teórico

#### ¿Qué es una lambda?

Una lambda es una **función anónima** — código sin nombre que se puede pasar como dato.

```java
// Antes (anonymous class)
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hola");
    }
};

// Ahora (lambda)
Runnable r = () -> System.out.println("Hola");

// Con parámetros
Comparator<String> comp = (a, b) -> a.length() - b.length();

// Con cuerpo complejo
Comparator<String> comp2 = (a, b) -> {
    int longitudA = a.length();
    int longitudB = b.length();
    return longitudA - longitudB;
};
```

#### Functional Interfaces

**Peculiaridad:** Java no tiene first-class functions. Pero las simula con interfaces que tienen un solo método abstracto (`@FunctionalInterface`).

```java
// Predicate<T> — retorna boolean
Predicate<String> esLargo = s -> s.length() > 5;
esLargo.test("Hello");  // false
esLargo.test("Hello World");  // true

// Function<T, R> — transforma T en R
Function<String, Integer> aLongitud = String::length;
aLongitud.apply("Hello");  // 5

// Consumer<T> — ejecuta acción, no retorna nada
Consumer<String> imprimir = System.out::println;
imprimir.accept("Hola");

// Supplier<T> — provee un valor, no toma nada
Supplier<List<String>> crearLista = ArrayList::new;
List<String> lista = crearLista.get();

// UnaryOperator<T> — transforma T en T
UnaryOperator<String> mayusculas = String::toUpperCase;
mayusculas.apply("hello");  // "HELLO"

// BinaryOperator<T> — toma dos T, retorna T
BinaryOperator<Integer> sumar = Integer::sum;
sumar.apply(3, 5);  // 8
```

#### Method References

```java
// Lambda                    → Method Reference
(s) -> System.out.println(s)  → System.out::println
(s) -> s.length()             → String::length
(s) -> Integer.parseInt(s)    → Integer::parseInt
(n) -> new ArrayList(n)       → ArrayList::new

// Uso con streams
List<String> nombres = List.of("Ana", "Bob", "Carlos");

// Con lambda
nombres.stream().map(n -> n.toUpperCase()).toList();

// Con method reference
nombres.stream().map(String::toUpperCase).toList();
```

#### Composición de funciones

```java
Function<Integer, Integer> duplicar = n -> n * 2;
Function<Integer, Integer> sumarDiez = n -> n + 10;

// Componer: primero duplicar, luego sumar 10
Function<Integer, Integer> procesar = duplicar.andThen(sumarDiez);
procesar.apply(5);  // (5 * 2) + 10 = 20

// Componer: primero sumar 10, luego duplicar
Function<Integer, Integer> procesarInverso = duplicar.compose(sumarDiez);
procesarInverso.apply(5);  // (5 + 10) * 2 = 30

// Predicate composition
Predicate<String> esLargo = s -> s.length() > 5;
Predicate<String> empiezaConA = s -> s.startsWith("A");

Predicate<String> largoYEmpiezaA = esLargo.and(empiezaConA);
Predicate<String> largoOEmpiezaA = esLargo.or(empiezaConA);
Predicate<String> noEsLargo = esLargo.negate();
```

### Ejemplo parcial

```java
// Pipeline completo con lambdas
public class ProcesadorDatos {
    
    public static void main(String[] args) {
        List<String> palabras = List.of("Java", "Spring", "Boot", 
                                        "Hibernate", "JPA", "REST");
        
        // 1. Filtrar palabras largas
        // 2. Convertir a mayúsculas
        // 3. Ordenar
        // 4. Unir con coma
        String resultado = palabras.stream()
            .filter(p -> p.length() > 4)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.joining(", "));
        
        System.out.println(resultado);
        // BOOT, HIBERNATE, SPRING
    }
}
```

### Teoría en profundidad: lambdas e invokedynamic

#### Cómo se implementan las lambdas (Java 8+)

**Peculiaridad:** una lambda NO es una clase anónima. Se implementa con `invokedynamic` + el mecanismo de *lambda metafactory*:

```java
// Fuente
Supplier<String> s = () -> "hola";

// Lo que pasa:
// 1. El compiler genera un método privado estático (o de instancia) "lambda body"
//    private static String lambda$main$0() { return "hola"; }
// 2. invokedynamic genera en runtime una instancia de la functional interface
//    (usa LambdaMetafactory → genera la clase dinámicamente)
```

**Ventajas sobre las clases anónimas:**
- No se crea un `.class` por lambda (menos bytecode)
- Las lambdas sin captura son **singleton** (se cachean) — `s == s` puede ser true
- Captura de variables: solo `effectively final` (no se puede mutar la variable capturada)

```java
// Captura
int x = 10;
Runnable r = () -> System.out.println(x);  // x debe ser effectively final
// x = 20;  // ❌ ERROR: no se puede reasignar (necesitaría mutable holder)
```

#### Effectively final — por qué la restricción

Las variables capturadas se copian al lambda. Si el lambda pudiera mutarlas, sería una variable compartida con semántica confusa (no es una referencia real al frame del caller). Java exige `effectively final` para garantizar que la copia es consistente. Para estados mutables usa un holder:

```java
int[] contador = {0};   // truco del holder
Runnable r = () -> contador[0]++;

// Mejor: AtomicInteger o una clase mutable
AtomicInteger contador = new AtomicInteger();
```

#### Functional interfaces: la teoría

Una functional interface tiene **exactamente un método abstracto**:

```java
@FunctionalInterface
interface Operacion {
    double calcular(double a, double b);   // el único método abstracto
    default double duplicar(double a) { return calcular(a, a); }
    static double identity(double a) { return a; }
}

// defaults y static no cuentan — por eso la interfaz sigue siendo functional
```

**Herencia de functional interfaces:**
```java
interface A { void m(); }
interface B extends A { default void m() {} }   // B ya no es functional (no tiene abstract)
```

#### El patrón Strategy (lambdas lo vuelven trivial)

```java
// Estrategias de descuento como lambdas
enum TipoCliente {
    REGULAR, VIP, PREMIUM
}

Map<TipoCliente, UnaryOperator<Double>> descuentos = Map.of(
    TipoCliente.REGULAR, precio -> precio * 0.95,
    TipoCliente.VIP,     precio -> precio * 0.85,
    TipoCliente.PREMIUM, precio -> precio * 0.70
);

double aplicarDescuento(TipoCliente t, double precio) {
    return descuentos.get(t).apply(precio);
}
```

### Ejercicio práctico

1. Crear `Predicate<Integer>` para verificar si un número es par
2. Crear `Function<String, String>` que capitalice la primera letra
3. Crear `Consumer<String>` que imprima el texto entre comillas
4. Usar `compose` y `andThen` para crear pipelines de transformación
5. Aplicar lambdas a un Stream de objetos

**Solución esperado:** Pipeline funcional completo usando lambdas y method references.

---

## 3.5 Optional y null safety (15 min)

### Objetivos
- Entender por qué `Optional` existe
- Usar los métodos principales de `Optional`
- Eliminar null checks manuales

### Contenido teórico

#### El problema de `null`

```java
// Esto puede fallar en cualquier momento
String nombre = obtenerNombre();  // Puede retornar null
int longitud = nombre.length();   // NullPointerException

// Defensivo — verbose
if (nombre != null) {
    int longitud = nombre.length();
}

// Con Optional — limpio
Optional<String> nombreOpt = Optional.ofNullable(obtenerNombre());
int longitud = nombreOpt.map(String::length).orElse(0);
```

**Peculiaridad:** Tony Hoare llamó a `null` "the billion-dollar mistake". Java lo heredó de C++ y nunca lo eliminó. `Optional` es el remedio parcial.

#### Crear Optionals

```java
// Valor no nulo
Optional<String> opt1 = Optional.of("Hola");

// Valor puede ser nulo
Optional<String> opt2 = Optional.ofNullable(null);
Optional<String> opt3 = Optional.ofNullable("Valor");

// Vacío
Optional<String> opt4 = Optional.empty();
```

#### Usar Optionals

```java
Optional<String> nombre = Optional.of("Ana");

// isPresent / ifPresent
if (nombre.isPresent()) {
    System.out.println(nombre.get());
}
nombre.ifPresent(System.out::println);  // Más limpio

// orElse — valor por defecto
String valor = nombre.orElse("Desconocido");

// orElseThrow — excepción si está vacío
String seguro = nombre.orElseThrow(() -> 
    new RuntimeException("Nombre requerido"));

// map — transformar
Optional<Integer> longitud = nombre.map(String::length);
// Optional<Integer> que contiene 3

// flatMap — cuando la transformación retorna Optional
Optional<String> resultado = nombre.flatMap(n -> 
    Optional.of(n.toUpperCase()));
// Optional<String> que contiene "ANA"

// filter — condicional
Optional<String> filtrado = nombre.filter(n -> n.startsWith("A"));
// Optional<String> que contiene "Ana"

Optional<String> filtrado2 = nombre.filter(n -> n.startsWith("B");
// Optional<String> vacío
```

#### Encadenar Optionals

```java
public Optional<String> obtenerCiudadDelUsuario(Usuario usuario) {
    return Optional.ofNullable(usuario)
        .map(Usuario::getDireccion)
        .map(Direccion::getCiudad)
        .filter(ciudad -> !ciudad.isBlank());
}

// Sin Optional — 4 null checks
public String obtenerCiudadLimpio(Usuario usuario) {
    if (usuario == null) return "";
    Direccion dir = usuario.getDireccion();
    if (dir == null) return "";
    String ciudad = dir.getCiudad();
    if (ciudad == null || ciudad.isBlank()) return "";
    return ciudad;
}
```

### Ejemplo parcial

```java
// Repository que retorna Optional
public Optional<Producto> buscarPorId(Long id) {
    // Puede encontrar o no
    return repository.findById(id);
}

// Uso limpio
productoRepository.buscarPorId(1L)
    .ifPresent(p -> System.out.println(p.getNombre()));

// Con transformación
String nombreMayusculas = productoRepository.buscarPorId(1L)
    .map(Producto::getNombre)
    .map(String::toUpperCase)
    .orElse("NO ENCONTRADO");
```

### Teoría en profundidad: Optional y la teoría del null

#### Por qué null es "the billion-dollar mistake"

Tony Hoare, inventor de los null references (1965), admitió: *"I call it my billion-dollar mistake"*. El costo: millones de bugs NullPointerException a nivel mundial.

```
Dimensiones del problema:
1. Ambigüedad: null significa "no hay valor" Y "error" a la vez
2. No localizado: el null se crea en un lado, explota en otro (NPE)
3. Invisible en el tipo: String puede ser null pero el tipo no lo dice
4. Sin chequeo en compile-time
```

**La alternativa de otros lenguajes:**
- **Kotlin:** tipos con nullabilidad en el sistema de tipos (`String?` — no compila si no manejas null)
- **Rust/Option:** `Option<T>` con pattern matching obligatorio
- **Java:** `Optional<T>` como *wrapper* (no enforced por el compiler)

#### Cuándo usar (y NO usar) Optional

```java
// ✅ Correcto: retorno de métodos que pueden no tener resultado
Optional<Producto> findById(Long id);

// ✅ Correcto: campos en streams
list.stream().filter(...).findFirst();   // retorna Optional

// ❌ NO como campo de entidad — JPA/Jackson no lo serializan bien
class Producto {
    Optional<String> descripcion;   // mala práctica
}

// ❌ NO como parámetro de método
void procesar(Optional<String> nombre) { }   // obliga al caller a envolver

// ❌ NO para colecciones — usa listas vacías
Optional<List<Producto>> productos;  // mejor: List<Producto> (vacía = sin datos)
```

**Regla de oro:** Optional es un tipo de retorno. Nunca campo, nunca parámetro.

#### Monad-ish: encadenamiento

`Optional` sigue el patrón de una mónada (de la programación funcional):

```
T → Optional<T>        (of / ofNullable / empty)
T → U                  (map: Optional<T> → Optional<U>)
T → Optional<U>        (flatMap: evita Optional<Optional<U>>)
Optional<T> → boolean  (filter / isPresent)

Leyes monádicas que cumple:
  m.flatMap(f).flatMap(g) == m.flatMap(x -> f(x).flatMap(g))   (asociatividad)
  m.flatMap(of) == m                                          (identidad izq)
  of(x).flatMap(f) == f(x)                                    (identidad der)
```

No necesitas saber la teoría de mónadas, pero entender `map` vs `flatMap` es esencial:

```java
// map: si la función devuelve Optional, queda Optional<Optional<...>>
Optional<String> opt = Optional.of("Hola");
Optional<Integer> len1 = opt.map(String::length);   // Optional<Integer> ✅

Optional<String> maybe = opt.flatMap(s -> buscar(s));  // buscar() devuelve Optional
// flatMap aplana — sin flatMap quedaría Optional<Optional<String>>
```

### Ejercicio práctico

1. Crear método `buscarUsuario` que retorne `Optional<Usuario>`
2. Usar `map` para obtener el email del usuario
3. Usar `filter` para verificar que el email no esté vacío
4. Usar `orElse` para提供er un valor por defecto
5. Refactorizar código existente con null checks usando Optional

**Solución esperado:** Código sin null checks manuales, usando Optional encadenado.

---

## Resumen del Módulo 3

### Conceptos clave

| Concepto | Descripción |
|---|---|
| Checked exceptions | Compiler obliga a manejar (peculiaridad Java) |
| `try-with-resources` | Auto-closeable, limpio y seguro |
| Generics | Type safety en compile-time, type erasure en runtime |
| PECS | Producer Extends, Consumer Super |
| `ArrayList` | Array dinámico, mejor para lectura |
| `HashMap` | Key-value, el más usado |
| Streams | `filter`, `map`, `reduce`, `collect` |
| Lambdas | Funciones anónimas, `->` |
| `@FunctionalInterface` | Interfaz con un solo método abstracto |
| Method references | `::` — syntactic sugar para lambdas |
| `Optional` | Remedio a null, encadenable |

### Siguiente módulo
→ [Módulo 4: Introducción a Spring Boot](04-springboot-intro.md)
