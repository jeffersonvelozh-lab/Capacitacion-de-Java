# Módulo 6: Acceso a Datos con Spring Boot

**Duración:** 3 horas  
**Objetivo:** Dominar Spring Data JPA, relaciones, transacciones y migraciones

---

## 6.1 Spring Data JPA (60 min)

### Objetivos
- Entender JPA y por qué existe
- Crear entidades con `@Entity`
- Usar `JpaRepository` para CRUD automático

### Contenido teórico

#### ¿Qué es JPA?

JPA (Java Persistence API) es un **estándar Java** para mapear objetos a tablas de base de datos (ORM).

```
Java Object                    Database Table
┌──────────────────┐          ┌──────────────────┐
│ Producto         │    ↔     │ productos        │
│   id: Long       │          │   id: BIGINT     │
│   nombre: String │          │   nombre: VARCHAR│
│   precio: Double │          │   precio: DOUBLE │
└──────────────────┘          └──────────────────┘
```

**Peculiaridad:** JPA es una **especificación**. Hibernate es la implementación más usada. Spring Data JPA simplifica aún más.

#### Crear una entidad

```java
@Entity
@Table(name = "productos")  // Opcional si el nombre coincide
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
    
    // Constructor vacío (requerido por JPA)
    public Producto() {}
    
    // Constructor con campos
    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }
    
    // Getters y setters (o usa @Data de Lombok)
}
```

#### Anotaciones de entidad

| Anotación | Uso |
|-----------|-----|
| `@Entity` | Marca como entidad JPA |
| `@Table` | Nombre de la tabla |
| `@Id` | Clave primaria |
| `@GeneratedValue` | Generación automática del ID |
| `@Column` | Configuración de columna |
| `@CreationTimestamp` | Fecha automática al crear |
| `@UpdateTimestamp` | Fecha automática al actualizar |

#### `JpaRepository`

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // ¡Spring genera la implementación automáticamente!
    
    // Métodos derivados del nombre
    List<Producto> findByNombre(String nombre);
    List<Producto> findByPrecioBetween(Double min, Double max);
    List<Producto> findByActivoTrue();
    List<Producto> findByNombreContainingIgnoreCase(String busqueda);
    Optional<Producto> findByNombreAndPrecio(String nombre, Double precio);
    
    // Contar
    long countByActivoTrue();
    
    // Eliminar
    void deleteByActivoFalse();
    
    // JPQL personalizado
    @Query("SELECT p FROM Producto p WHERE p.precio > :precio")
    List<Producto> encontrarCaros(@Param("precio") Double precio);
    
    // SQL nativo
    @Query(value = "SELECT * FROM productos WHERE nombre LIKE %:busqueda%", 
           nativeQuery = true)
    List<Producto> buscarPorNombre(@Param("busqueda") String busqueda);
}
```

**Peculiaridad:** Spring Data genera la implementación en **tiempo de ejecución**. Solo defines el método y el nombre — Spring crea la query.

#### Convención de nombres

```
 findBy Nombre
       ↓
 find    By    Nombre
 │       │      │
 │       │      └── Campo de la entidad
 │       └── Palabra clave (And, Or, Between, etc.)
 └── Prefijo (find, read, query, count, delete)

 Ejemplos:
 findByNombre → WHERE nombre = ?
 findByPrecioGreaterThan → WHERE precio > ?
 findByNombreAndPrecio → WHERE nombre = ? AND precio = ?
 findByNombreContaining → WHERE nombre LIKE %?%
 findByPrecioBetween → WHERE precio BETWEEN ? AND ?
 findByActivoTrue → WHERE activo = TRUE
 findByNombreOrderByPrecioDesc → WHERE nombre = ? ORDER BY precio DESC
```

### Teoría en profundidad: ORM, persistence context y ciclo de vida de entidades

#### ¿Qué resuelve realmente un ORM?

```
Capa de dominio (Java)              Base de datos (SQL)
┌──────────────────────┐           ┌──────────────────────┐
│ List<Producto>       │           │ SELECT * FROM        │
│ producto.setPrecio() │   mapping │ productos            │
│ producto.getNombre() │  ──────►  │ UPDATE productos     │
└──────────────────────┘           └──────────────────────┘

Problemas que resuelve JPA:
1. Impedance mismatch: objetos ≠ filas (herencia, relaciones, colecciones)
2. Código repetitivo: getResultSet, close connection, try-finally
3. Cambio de DB: cambiar MySQL→PostgreSQL sin reescribir la capa de datos
4. Cache: entidades en memoria (first-level cache)
```

#### Los 4 estados del ciclo de vida JPA

```
        new Producto()                 entityManager.persist()
   ┌──────────────────┐  persist()   ┌──────────────────────┐
   │    TRANSIENT     │─────────────►│        MANAGED       │
   │ (nuevo, sin ID)  │              │ (en persistence ctx) │
   └──────────────────┘              │  sincronizado con DB │
            ▲                        └──────────────────────┘
            │                                   │
            │          merge() (detached)      │  flush() → INSERT/UPDATE
            │          o persist() (transient) │
   ┌──────────────────┐                        ▼
   │     DETACHED     │◄──────────────────────┐┌──────────────────────┐
   │ (ya no en ctx,   │   entityManager.clear()││        REMOVED       │
   │  con ID)         │◄──────────────────────││ (marcado para DELETE)│
   └──────────────────┘                       └──────────────────────┘
```

| Estado | En el contexto | Con ID | Cambios se persisten |
|--------|----------------|--------|----------------------|
| **Transient** | No | No | No |
| **Managed** | Sí | Sí | Sí (dirty checking en flush) |
| **Detached** | No | Sí | No (se necesita merge) |
| **Removed** | Sí | Sí | Se elimina al flush |

**Peculiaridad del dirty checking:** con `repository.save(entity)` y la entidad *managed*, Spring/Hibernate compara el estado en el flush y genera el UPDATE automáticamente. No necesitas `save` explícito para que el cambio se persista.

#### Persistence Context y first-level cache

```
PersistenceContext (por transacción / EntityManager)
├── Cache de entidades (first-level cache) — clave: clase + id
├── Identity map: mismo id → misma instancia de entidad
├── Change tracking (snapshot del estado inicial)
└── Está atado al ciclo de @Transactional

ProductoRepository.findById(1L)   // 1 query a DB, entidad cacheadas
producto.setPrecio(150)
ProductoRepository.findById(1L)   // 0 queries — misma instancia del cache
// al flush → UPDATE (dirty checking)
```

**Identity map:** dentro de una transacción, la misma fila SIEMPRE mapea a la misma instancia Java. Evita divergencias.

#### Cuándo no usar JPA / considerar JDBC

| Caso | Recomendación |
|------|---------------|
| CRUD de entidades simples | JPA + Spring Data (perfecto) |
| Reportes complejos, joins masivos, agregaciones | JPA con `@Query` JPQL, o **JdbcTemplate** para SQL directo |
| Queries dinámicas muy específicas del motor | `nativeQuery` o JdbcTemplate |
| Procesamiento batch de millones de filas | JPA batch con flush controlado, o JDBC puro |

**Peculiaridad:** JPA abstrae el SQL pero no lo elimina. Saber SQL sigue siendo esencial para optimizar (índices, planes de ejecución, joins).

### Ejemplo parcial

```java
@Service
public class ServicioProducto {
    
    private final ProductoRepository repository;
    
    public ServicioProducto(ProductoRepository repository) {
        this.repository = repository;
    }
    
    public List<Producto> listarTodos() {
        return repository.findAll();  // SELECT * FROM productos
    }
    
    public Optional<Producto> obtenerPorId(Long id) {
        return repository.findById(id);  // SELECT * WHERE id = ?
    }
    
    public Producto crear(Producto producto) {
        return repository.save(producto);  // INSERT
    }
    
    public Producto actualizar(Long id, Producto datos) {
        Producto existente = repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        
        existente.setNombre(datos.getNombre());
        existente.setPrecio(datos.getPrecio());
        existente.setDescripcion(datos.getDescripcion());
        
        return repository.save(existente);  // UPDATE
    }
    
    public boolean eliminar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);  // DELETE
            return true;
        }
        return false;
    }
    
    public List<Producto> buscarPorCategoria(String categoria) {
        return repository.findByNombreContainingIgnoreCase(categoria);
    }
}
```

### Ejercicio práctico

1. Crear entidad `Producto` con todas las anotaciones
2. Crear `ProductoRepository` con métodos derivados
3. Crear `ServicioProducto` con CRUD completo
4. Probar: crear, listar, buscar por nombre, actualizar, eliminar

**Solución esperada:** CRUD funcional con Spring Data JPA.

---

## 6.2 Consultas y relaciones (60 min)

### Objetivos
- Crear consultas JPQL personalizadas
- Definir relaciones entre entidades
- Manejar ciclos JSON en relaciones

### Contenido teórico

#### JPQL vs SQL

```java
// JPQL — usa nombres de clase, no tablas
@Query("SELECT p FROM Producto p WHERE p.precio > :precio")
List<Producto> encontrarCaros(@Param("precio") Double precio);

// SQL nativo — usa tablas y columnas reales
@Query(value = "SELECT * FROM productos WHERE precio > :precio", 
       nativeQuery = true)
List<Producto> encontrarCaros(@Param("precio") Double precio);

// JPQL con ordenamiento
@Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.precio DESC")
List<Producto> encontrarActivosOrdenados();

// JPQL con agregación
@Query("SELECT p.categoria.nombre, COUNT(p) FROM Producto p GROUP BY p.categoria.nombre")
List<Object[]> contarPorCategoria();
```

#### Relaciones

```java
// ONE-TO-MANY: Una categoría tiene muchos productos
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY)
    private List<Producto> productos;
}

// MANY-TO-ONE: Muchos productos pertenecen a una categoría
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}

// MANY-TO-MANY: Pedidos y productos
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "pedido_producto",
        joinColumns = @JoinColumn(name = "pedido_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<Producto> productos;
}
```

#### Fetch types

```java
// LAZY (default para collections) — carga bajo demanda
@OneToMany(fetch = FetchType.LAZY)
private List<Producto> productos;
// Solo carga cuando accedes a .getProductos()

// EAGER (default para @ManyToOne) — carga inmediatamente
@ManyToOne(fetch = FetchType.EAGER)
private Categoria categoria;
// Carga cuando cargas el Producto

// Peculiaridad: N+1 problem
// Si cargas 100 productos y cada uno tiene eager loading de categoría,
// haces 101 queries (1 para productos + 100 para categorías)
// Solución: Usa LAZY + JOIN FETCH
```

#### `@JsonManagedReference` y `@JsonBackReference`

```java
// Sin esto, Jackson entra en ciclo infinito:
// Producto → Categoria → Productos → Categoria → ...

@Entity
public class Categoria {
    @OneToMany(mappedBy = "categoria")
    @JsonManagedReference  // "adelante" — se serializa
    private List<Producto> productos;
}

@Entity
public class Producto {
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonBackReference  // "atrás" — NO se serializa
    private Categoria categoria;
}

// Resultado JSON:
// Producto: {"id": 1, "nombre": "Laptop", "categoria": null}  ← sin referencia circular
// Categoria: {"id": 1, "nombre": "Electrónica", "productos": [...]}
```

### Teoría en profundidad: lazy loading, N+1 y fetch strategies

#### Cómo funciona el lazy loading (proxies de Hibernate)

```java
@Entity
public class Producto {
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria;   // NO se carga al leer Producto
}

// Al hacer: productoRepository.findAll();
// → SELECT * FROM productos  (sin JOIN)
// El campo categoria es un PROXY de Hibernate (subclase Categoria$$HibernateProxy)

// Cuando accedes: producto.getCategoria().getNombre()
// → DISPARA: SELECT * FROM categorias WHERE id = ?
// (solo si el persistence context sigue abierto)
```

**Regla de oro:** el acceso a un proxy lazy SOLO funciona dentro de una transacción activa (persistence context abierto). Fuera de ella → `LazyInitializationException`.

#### El problema N+1

```
Sin JOIN FETCH:
SELECT * FROM productos;                    // 1 query
for (cada producto) {
    producto.getCategoria()                 // N queries (una por producto)
}
// Total: 1 + N queries  ← N+1 problem

Con JOIN FETCH:
SELECT p FROM Producto p JOIN FETCH p.categoria;   // 1 query con JOIN
// Total: 1 query
```

**Peculiaridad:** Spring Data no te protege del N+1. Métodos como `findAll()`, `findById()` cargan perezoso por defecto. La solución:
- `@Query("SELECT p FROM Producto p JOIN FETCH p.categoria")`
- `@EntityGraph(attributePaths = "categoria")` — Spring Data, más expresivo

```java
@EntityGraph(attributePaths = {"categoria", "items"})
@Query("SELECT p FROM Producto p WHERE p.activo = true")
List<Producto> encontrarActivosConDependencias();
```

#### FetchType — cuándo cada uno

| FetchType | Colección (`@OneToMany`) | Un solo (`@ManyToOne`) |
|-----------|--------------------------|------------------------|
| EAGER | Carga todo con JOIN | Carga todo (default) |
| LAZY | Default | Explícito |

**Regla práctica:** casi siempre LAZY. EAGER en `@ManyToOne` es default pero genera joins innecesarios si no necesitas la relación en cada lectura. La elección es entre queries y memoria.

#### Relaciones: la tabla de joins y el mapeo

**ManyToMany** siempre crea una **tabla de unión**:

```
pedidos              pedido_producto              productos
┌──────┬───────┐    ┌───────────┬────────────┐    ┌──────┬────────┐
│ id   │ total │    │ pedido_id │ producto_id │    │ id   │ nombre │
├──────┼───────┤    ├───────────┼────────────┤    ├──────┼────────┤
│ 1    │ 150   │    │ 1         │ 1          │    │ 1    │ Laptop │
│ 2    │ 300   │    │ 1         │ 3          │    │ 2    │ Mouse  │
└──────┴───────┘    │ 2         │ 2          │    │ 3    │ Tecla  │
                    └───────────┴────────────┘    └──────┴────────┘
```

**Peculiaridad:** `mappedBy` marca el lado "inverso" (no dueño de la relación). Solo el lado dueño mantiene el FK. El lado `mappedBy` es de solo lectura para el mapeo.

#### Cascade — control del ciclo de persistencia

```java
@OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
private List<Producto> productos;

// Al persistir la Categoria, se persisten sus productos también.
// Tipos: PERSIST, MERGE, REMOVE, REFRESH, DETACH, ALL

// ⚠️ Cascada REMOVE peligrosa: borrar una Categoria borra sus Productos
// ⚠️ orphanRemoval = true: quitar un item de la lista lo borra de la DB
```

### Ejemplo parcial

```java
// Repository con consultas complejas
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // JOIN FETCH para evitar N+1
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria WHERE p.activo = true")
    List<Producto> encontrarActivosConCategoria();
    
    // Subconsulta
    @Query("SELECT p FROM Producto p WHERE p.precio > " +
           "(SELECT AVG(p2.precio) FROM Producto p2)")
    List<Producto> encontrarMasCarosQuePromedio();
    
    // Consulta con múltiples parámetros
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax)")
    List<Producto> buscarConFiltros(
        @Param("nombre") String nombre,
        @Param("precioMin") Double precioMin,
        @Param("precioMax") Double precioMax);
}
```

### Ejercicio práctico

1. Crear entidad `Categoria` con relación `@OneToMany`
2. Modificar `Producto` con `@ManyToOne` hacia `Categoria`
3. Crear consultas JPQL personalizadas
4. Solucionar problema N+1 con JOIN FETCH
5. Probar con datos relacionados

**Solución esperada:** Modelo relacional funcional con consultas optimizadas.

---

## 6.3 Transacciones (30 min)

### Objetivos
- Entender por qué `@Transactional` importa
- Conocer propagación de transacciones
- Aplicar rollback correcto

### Contenido teórico

#### ¿Qué es una transacción?

Una transacción es una **unidad de trabajo** que debe ser atómica — todo o nada.

```java
// Sin transacción — puede quedar en estado inconsistente
public void transferir(Long origenId, Long destinoId, double monto) {
    Cuenta origen = cuentaRepo.findById(origenId).get();
    Cuenta destino = cuentaRepo.findById(destinoId).get();
    
    origen.setSaldo(origen.getSaldo() - monto);  // ✅ OK
    cuentaRepo.save(origen);
    
    // ¡ERROR AQUÍ! — El dinero se perdió
    destino.setSaldo(destino.getSaldo() + monto);  // ❌ Nunca se ejecuta
    cuentaRepo.save(destino);
}

// Con transacción — todo o nada
@Transactional
public void transferir(Long origenId, Long destinoId, double monto) {
    Cuenta origen = cuentaRepo.findById(origenId).get();
    Cuenta destino = cuentaRepo.findById(destinoId).get();
    
    origen.setSaldo(origen.getSaldo() - monto);
    cuentaRepo.save(origen);
    
    // Si falla aquí, se revierte TODO (incluyendo el save anterior)
    destino.setSaldo(destino.getSaldo() + monto);
    cuentaRepo.save(destino);
}
```

#### `@Transactional`

```java
@Service
public class ServicioPedido {
    
    @Transactional  // Abre transacción, hace commit al final, rollback si excepción
    public Pedido crearPedido(Pedido pedido) {
        // 1. Validar stock
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(producto.getNombre());
            }
        }
        
        // 2. Reservar stock
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepo.save(producto);
        }
        
        // 3. Guardar pedido
        return pedidoRepo.save(pedido);
        
        // Si todo OK → COMMIT (stock reducido + pedido guardado)
        // Si excepción → ROLLBACK (todo vuelve al estado anterior)
    }
}
```

#### Propagación de transacciones

```java
// REQUIRED (default) — usa transacción existente o crea nueva
@Transactional(propagation = Propagation.REQUIRED)
public void metodoA() {
    metodoB();  // Usa la misma transacción
}

@Transactional
public void metodoB() {
    // Misma transacción que metodoA
}

// REQUIRES NEW — siempre crea nueva transacción
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logOperacion() {
    // Transacción separada — si metodoA falla, el log persiste
}

// NEVER — no debe tener transacción
@Transactional(propagation = Propagation.NEVER)
public void soloLectura() {
    // Lanza excepción si hay transacción activa
}
```

#### Rollback en excepciones

```java
// Rollback automático en unchecked exceptions
@Transactional
public void metodo() {
    // RuntimeException → ROLLBACK automático
    // Exception (checked) → COMMIT (no rollback)
}

// Forzar rollback en checked exceptions
@Transactional(rollbackFor = Exception.class)
public void metodo() throws Exception {
    // Ahora también hace rollback en checked exceptions
}

// No hacer rollback en ciertas excepciones
@Transactional(noRollbackFor = PocoImportanteException.class)
public void metodo() {
    // PocoImportanteException no causa rollback
}
```

### Teoría en profundidad: ACID y niveles de aislamiento

#### Los cuatro pilares de las transacciones

| Propiedad | Qué garantiza | Violación |
|-----------|---------------|-----------|
| **Atomicity** | Todo o nada | El dinero sale pero no llega |
| **Consistency** | El estado siempre es válido (invariantes) | Stock negativo |
| **Isolation** | Transacciones no se interfieren | Lectura de datos a medias |
| **Durability** | Lo confirmado sobrevive | Crash pierde datos |

**Consistency** en este contexto no es lo mismo que consistencia de C.A.P. (aunque comparten espíritu). Es: las reglas de negocio y restricciones de DB se mantienen al inicio y fin de la transacción.

#### Niveles de aislamiento (SQL estándar)

El nivel define qué **fenómenos de concurrencia** se permiten:

| Nivel | Dirty read | Non-repeatable read | Phantom read |
|-------|-----------|--------------------|--------------|
| READ UNCOMMITTED | ❌ permitido | ❌ | ❌ |
| READ COMMITTED | ✅ evitado | ❌ | ❌ |
| REPEATABLE READ | ✅ | ✅ | ❌ |
| SERIALIZABLE | ✅ | ✅ | ✅ |

```
Dirty read:      leer un dato que otra transacción aún no confirmó (y puede revertir)
Non-repeatable:  la misma lectura da resultados distintos (otra tx confirmó UPDATE)
Phantom read:    la misma query devuelve filas nuevas (otra tx insertó)
```

```java
// Default en MySQL: REPEATABLE READ
// Default en PostgreSQL: READ COMMITTED
// Default en H2: READ COMMITTED

@Transactional(isolation = Isolation.SERIALIZABLE)
public void operacionCritica() {
    // Máxima protección, peor concurrencia (locks globales)
}
```

#### Cómo lo implementa la DB (locks)

```
Mecanismos:
- Lock compartido (S): varias lecturas simultáneas
- Lock exclusivo (X): una escritura, bloquea lecturas
- Two-phase locking (2PL): adquiero todos los locks, luego libero
- MVCC (Multi-Version Concurrency Control): cada tx ve un snapshot
  (PostgreSQL, MySQL InnoDB usan MVCC — las lecturas NO bloquean escrituras)

Con MVCC, REPEATABLE READ se logra sin locks globales:
cada transacción trabaja sobre su versión del dato (snapshot).
```

#### Propagación — la matriz completa

| Propagation | Comportamiento |
|-------------|----------------|
| `REQUIRED` (default) | Usa la tx existente o crea una |
| `REQUIRES_NEW` | Suspende la actual, crea una nueva, la actual reanuda |
| `NESTED` | Punto de salvamento (savepoint): rollback parcial |
| `MANDATORY` | Error si no hay tx activa |
| `SUPPORTS` | Tx si hay, sino ejecuta sin tx |
| `NOT_SUPPORTED` | Suspende tx, ejecuta sin tx |
| `NEVER` | Error si hay tx activa |

**REQUIRES_NEW típico:** logs y auditoría que deben persistir aunque falle la transacción principal (rollback de la principal no revierte el log).

#### readOnly y el flujo del commit/rollback

```java
@Transactional(readOnly = true)
public List<Producto> listar() {
    // 1. Hibernate desactiva dirty checking (mejor rendimiento)
    // 2. Algunas DB (PostgreSQL) optimizan la query
    // 3. No persiste nada
}
```

```
@Transactional (proxy)                                   DB
   │  begin transaction ───────────────────────────► BEGIN
   │  (se ejecuta el método real)
   │  método termina sin excepción ─────────────────► COMMIT
   │  (o lanza RuntimeException → ROLLBACK)

NOTA: la transacción comienza en el PROXY (self-invocation no aplica).
ROLLBACK solo en RuntimeException/Error por defecto — usa rollbackFor=Exception.class
para checked exceptions.
```

### Ejemplo parcial

```java
@Service
@Transactional
public class ServicioPedido {
    
    private final PedidoRepository pedidoRepo;
    private final ProductoRepository productoRepo;
    
    public ServicioPedido(PedidoRepository pedidoRepo, 
                          ProductoRepository productoRepo) {
        this.pedidoRepo = pedidoRepo;
        this.productoRepo = productoRepo;
    }
    
    public Pedido crearPedido(Pedido pedido) {
        // Validar stock
        for (ItemPedido item : pedido.getItems()) {
            Producto prod = productoRepo.findById(item.getProducto().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", 
                    item.getProducto().getId()));
            
            if (prod.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(prod.getNombre());
            }
        }
        
        // Reservar stock y guardar pedido
        for (ItemPedido item : pedido.getItems()) {
            Producto prod = productoRepo.findById(item.getProducto().getId()).get();
            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepo.save(prod);
        }
        
        return pedidoRepo.save(pedido);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarLog(String mensaje) {
        // Transacción separada para logs
    }
}
```

### Ejercicio práctico

1. Crear servicio de transferencia bancaria con `@Transactional`
2. Simular error a mitad de la transferencia
3. Verificar que el rollback funciona
4. Crear método con `REQUIRES_NEW` para logs

**Solución esperada:** Transacciones funcionando correctamente con rollback.

---

## 6.4 H2 y Flyway (30 min)

### Objetivos
- Configurar H2 para desarrollo
- Implementar migraciones con Flyway
- Versionar el esquema de base de datos

### Contenido teórico

#### H2 — Base de datos en memoria

```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola web (acceso: /h2-console)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Ventaja de H2:** Arranca en memoria, no necesita instalación, ideal para desarrollo y tests.

#### Flyway — Migraciones versionadas

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```
src/main/resources/db/migration/
├── V1__create_tables.sql
├── V2__add_initial_data.sql
├── V3__add_producto_descripcion.sql
└── V4__create_pedido_table.sql
```

```sql
-- V1__create_tables.sql
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500)
);

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    descripcion VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    categoria_id BIGINT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- V2__add_initial_data.sql
INSERT INTO categorias (nombre, descripcion) VALUES 
('Electrónica', 'Dispositivos electrónicos'),
('Ropa', 'Prendas de vestir'),
('Libros', 'Libros y publicaciones');

INSERT INTO productos (nombre, precio, categoria_id) VALUES 
('Laptop', 999.99, 1),
('Mouse', 29.99, 1),
('Camiseta', 19.99, 2),
('El Principito', 15.99, 3);

-- V3__add_producto_descripcion.sql
ALTER TABLE productos ADD COLUMN descripcion VARCHAR(500);
```

**Peculiaridad:** Flyway ejecuta migraciones en orden (V1, V2, V3...) y las registra en una tabla `flyway_schema_history`. No ejecuta la misma migración dos veces.

### Teoría en profundidad: Flyway y el versionado del esquema

#### ¿Qué problema resuelven las migraciones?

```
Sin migraciones:
- La DB de dev tiene tablas que prod no tiene
- Nadie sabe qué scripts correr en orden
- "funciona en mi máquina" — el esquema nunca coincide
- Alteraciones manuales indocumentadas

Con migraciones (Flyway):
- El esquema ES código versionado
- Cada entorno aplica el mismo conjunto de migraciones
- Hay una fuente única de verdad: el directorio de migraciones
```

#### El ciclo de Flyway

```
1. Flyway arranca con Spring Boot
2. Crea la tabla flyway_schema_history (si no existe)
3. Lee src/main/resources/db/migration/*.sql en orden
4. Compara checksums: si una migración aplicada cambió → ERROR (bloquea)
5. Aplica las pendientes, registra la fila por cada una
6. Ignora las ya aplicadas (idempotente)

Tabla flyway_schema_history:
│ version │ description     │ success │ checksum │
│ 1       │ create_tables   │ true    │ 12345    │
│ 2       │ add_initial_data│ true    │ 67890    │
```

#### La regla de oro de Flyway

**Una vez aplicada, una migración NUNCA se modifica.** Si necesitas cambiar, crea `V4__...`, no edites `V2`. Por eso el checksum: si editas una aplicada, Flyway aborta el arranque (`Validate failed`). Te protege de divergencias entre entornos.

**Convención de nombres:**

```
V1__create_tables.sql          ← version + __ + descripción
V1_1__fix_typo.sql             ← sub-versiones
V2__add_productos.sql
R__repeatable.sql              ← se re-ejecuta si cambia (checksum distinto)
```

#### Múltiples entornos y datos

```
DEV:      H2 en memoria + flyway → esquema fresco en cada arranque
TEST:     H2 / Testcontainers + flyway → mismo esquema que prod
STAGING:  PostgreSQL + flyway
PROD:     PostgreSQL + flyway  → aplica solo pendientes, sin tocar las aplicadas
```

**Peculiaridad:** `ddl-auto` de Hibernate (`update`) es para prototipos, NO para producción. Con Flyway, pon `spring.jpa.hibernate.ddl-auto=validate` — Hibernate verifica que su mapeo coincide con el esquema de Flyway y falla temprano si hay mismatch.

### Ejemplo parcial

```bash
# Flyway ejecuta automáticamente al arrancar Spring Boot
# La app arranca y el esquema está listo

# Consola H2: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# User: sa
# Password: (vacío)
```

### Ejercicio práctico

1. Crear migración `V1__create_tables.sql` con tablas `categorias` y `productos`
2. Crear migración `V2__add_initial_data.sql` con datos de ejemplo
3. Crear migración `V3__add_pedido_table.sql` con tabla `pedidos`
4. Arrancar la app y verificar en consola H2
5. Modificar una migración y verificar que Flyway detecta el cambio

**Solución esperada:** Esquema versionado con Flyway funcionando.

---

## Resumen del Módulo 6

### Conceptos clave

| Concepto | Descripción |
|---|---|
| JPA | Java Persistence API — ORM estándar |
| `@Entity` | Marca clase como entidad JPA |
| `@Id` + `@GeneratedValue` | Clave primaria auto-generada |
| `JpaRepository` | CRUD automático + queries derivadas |
| Métodos derivados | `findByNombre`, `findByPrecioBetween`, etc. |
| JPQL | Queries con nombres de clase, no tablas |
| `@OneToMany` / `@ManyToOne` | Relaciones entre entidades |
| `@ManyToMany` | Relación muchos a muchos |
| Fetch LAZY vs EAGER | Carga bajo demanda vs inmediata |
| N+1 problem | Queries excesivas por eager loading |
| `@Transactional` | Atómica: todo o nada |
| Propagación | REQUIRED, REQUIRES NEW, NEVER |
| H2 | DB en memoria para desarrollo |
| Flyway | Migraciones versionadas del esquema |

### Siguiente módulo
→ [Módulo 7: Testing y Buenas Prácticas](07-testing.md)
