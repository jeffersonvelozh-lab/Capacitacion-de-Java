# Módulo 7: Testing y Buenas Prácticas

**Duración:** 2 horas  
**Objetivo:** Escribir tests unitarios y de integración, aplicar buenas prácticas

---

## 7.1 JUnit 5 + Mockito (45 min)

### Objetivos
- Escribir tests con JUnit 5
- Mockear dependencias con Mockito
- Verificar interacciones entre objetos

### Contenido teórico

#### Estructura de un test

```java
class ServicioProductoTest {
    
    private ServicioProducto servicio;
    private ProductoRepository repository;  // Mock
    
    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductoRepository.class);
        servicio = new ServicioProducto(repository);
    }
    
    @Test
    void deberiaCrearProducto() {
        // Arrange (preparar)
        Producto producto = new Producto("Laptop", 999.99);
        Producto guardado = new Producto(1L, "Laptop", 999.99);
        when(repository.save(producto)).thenReturn(guardado);
        
        // Act (ejecutar)
        Producto resultado = servicio.crear(producto);
        
        // Assert (verificar)
        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(999.99, resultado.getPrecio());
        verify(repository).save(producto);  // Verificar que se llamó
    }
    
    @Test
    void deberiaLanzarExcepcionCuandoNoExiste() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, 
            () -> servicio.obtenerPorId(999L));
    }
}
```

#### Anotaciones JUnit 5

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Una instancia por clase
class MiTest {
    
    @BeforeAll
    static void setUpClass() {
        // Se ejecuta una vez antes de todos los tests
    }
    
    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada test
    }
    
    @Test
    void test1() { }
    
    @Test
    @Disabled("Pendiente de implementar")  // Saltar test
    void test2() { }
    
    @AfterEach
    void tearDown() {
        // Se ejecuta después de cada test
    }
    
    @AfterAll
    static void tearDownClass() {
        // Se ejecuta una vez después de todos los tests
    }
}
```

#### Assertions

```java
// Básicas
assertEquals(esperado, real);
assertNotEquals(esperado, real);
assertTrue(condicion);
assertFalse(condicion);
assertNull(objeto);
assertNotNull(objeto);

// Con mensaje
assertEquals("Laptop", resultado.getNombre(), "El nombre debe ser Laptop");

// Arrays
assertArrayEquals(new int[]{1, 2, 3}, resultado);

// Exceptions
assertThrows(RecursoNoEncontradoException.class, 
    () -> servicio.obtenerPorId(999L));

// Lambda assertions (JUnit 5.8+)
assertAll("Producto",
    () -> assertEquals("Laptop", resultado.getNombre()),
    () -> assertEquals(999.99, resultado.getPrecio()),
    () -> assertTrue(resultado.isActivo())
);
```

#### Mockito — Mockear dependencias

```java
// Crear mock
ProductoRepository repository = Mockito.mock(ProductoRepository.class);

// Configurar comportamiento
when(repository.findById(1L)).thenReturn(Optional.of(new Producto("Laptop", 999.99)));
when(repository.findById(999L)).thenReturn(Optional.empty());
when(repository.save(any())).thenAnswer(invocation -> {
    Producto p = invocation.getArgument(0);
    p.setId(1L);
    return p;
});

// Verificar interacciones
verify(repository).save(producto);  // Se llamó una vez
verify(repository, never()).deleteById(any());  // Nunca se llamó
verify(repository, times(2)).findAll();  // Se llamó 2 veces

// Resetear mocks
reset(repository);
```

### Teoría en profundidad: la pirámide de test y el patrón AAA

#### La pirámide de test (Mike Cohn)

```
         ▲  ↑ velocidad de ejecución
        ╱ ╲
       ╱ e ╲        E2E (End-to-End): flujos completos en infra real
      ╱═════╲       pocos, lentos, frágiles — prueban integración del sistema
     ╱  s   ╲
    ╱═════════╲     Service/Integration: componentes + DB + HTTP
   ╱           ╲   algunos, más rápidos
  ╱═════════════╲
  unit tests        MUCHOS, instantáneos — prueban una unidad aislada
  ▼ número de tests
```

**Regla:** escribe muchos tests unitarios, algunos de integración, pocos E2E. Si tu pirámide está invertida (todo E2E), los builds son lentos y frágiles.

#### Las tres dimensiones de calidad

| Dimensión | Pregunta |
|-----------|----------|
| **Correctness** | ¿El código hace lo que debe? |
| **Regression safety** | ¿Un cambio rompe algo existente? |
| **Documentation** | ¿Los tests explican el comportamiento esperado? |

Un test bien escrito documenta el **contrato** de la unidad — el lector entiende qué debe cumplir sin leer la implementación.

#### El patrón AAA (Arrange-Act-Assert)

```
┌─────────────────────────────────────────────┐
│ Arrange: preparar el estado, mocks, inputs │
│                                             │
│ Act:      ejecutar el comportamiento        │
│             (UNA sola acción, no varias)    │
│                                             │
│ Assert:   verificar el resultado            │
│             (preferir asserts específicos)  │
└─────────────────────────────────────────────┘
```

**Señales de un test débil:**
- Sin Assert (o assert genérico como `assertNotNull`)
- Testea implementación, no comportamiento (cambia y se rompe sin necesidad)
- Múltiples Acts — divide en tests separados
- Assert demasiado laxo: `assertTrue(servicio.metodo() != null)`

#### F.I.R.S.T. — principios de un buen test

```
Fast      — corren en milisegundos (sin red, sin DB en unit tests)
Isolated  — un test no depende de otro; corre en cualquier orden
Repeatable— mismo resultado siempre (sin fechas fijas, sin aleatoriedad)
Self-verifying — assert automático, sin inspección manual
Timely    — se escriben cerca del código (TDD idealmente)
```

**Peculiaridad de la aleatoriedad y el tiempo:** `new Date()`, `System.currentTimeMillis()`, números aleatorios hacen tests no-repeatable. Inyecta relojes (Clock), fija valores, o usa `Instant.now(clock)`.

#### Test doubles: la teoría completa

| Double | Qué es | Uso |
|--------|--------|-----|
| **Dummy** | Objeto sin comportamiento | Solo para rellenar parámetros |
| **Fake** | Implementación simple funcional | En-memory repository, stub de API |
| **Stub** | Responde valores fijos | Controlar entrada |
| **Spy** | Envuelve el real, registra llamadas | Verificar + delegar |
| **Mock** | Expectativas + verificación | Comportamiento simulado controlado |

Mockito provee `mock()` (Mock), `spy()` (Spy), y `when().thenReturn()` (Stub). Los **Fakes** se escriben a mano (ej: `FakeProductoRepository implements ProductoRepository`).

### Ejemplo parcial

```java
@ExtendWith(MockitoExtension.class)
class ServicioPedidoTest {
    
    @Mock
    private ProductoRepository productoRepo;
    
    @Mock
    private PedidoRepository pedidoRepo;
    
    @InjectMocks
    private ServicioPedido servicio;
    
    @Test
    @DisplayName("Crear pedido reduce stock correctamente")
    void crearPedido_deberiaReducirStock() {
        // Arrange
        Producto producto = new Producto(1L, "Laptop", 999.99, 10);
        ItemPedido item = new ItemPedido(producto, 2);
        Pedido pedido = new Pedido(List.of(item));
        
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Pedido resultado = servicio.crearPedido(pedido);
        
        // Assert
        assertEquals(8, producto.getStock());  // 10 - 2 = 8
        verify(productoRepo).save(producto);
        verify(pedidoRepo).save(pedido);
    }
    
    @Test
    @DisplayName("Lanza excepción cuando stock es insuficiente")
    void crearPedido_stockInsuficiente_deberiaLanzarExcepcion() {
        // Arrange
        Producto producto = new Producto(1L, "Laptop", 999.99, 1);
        ItemPedido item = new ItemPedido(producto, 5);
        Pedido pedido = new Pedido(List.of(item));
        
        when(productoRepo.findById(1L)).thenReturn(Optional.of(producto));
        
        // Act & Assert
        assertThrows(StockInsuficienteException.class, 
            () -> servicio.crearPedido(pedido));
        
        verify(productoRepo, never()).save(any());
    }
}
```

### Ejercicio práctico

1. Crear test para `ServicioProducto`: crear, obtener, eliminar
2. Mockear `ProductoRepository` con `@Mock`
3. Inyectar mock con `@InjectMocks`
4. Verificar que se llama a `repository.save()` al crear
5. Verificar que se lanza excepción cuando no existe

**Solución esperada:** Suite de tests unitarios funcionando.

---

## 7.2 Spring Boot Test (45 min)

### Objetivos
- Usar `@SpringBootTest` para tests de integración
- Probar endpoints con `MockMvc`
- Configurar tests con `@DataJpaTest`

### Contenido teórico

#### `@SpringBootTest`

```java
@SpringBootTest
class AplicacionIntegrationTest {
    
    @Autowired
    private ApplicationContext context;
    
    @Test
    void contextoDeberiaCargar() {
        assertNotNull(context);
        assertTrue(context.containsBean("productoController"));
    }
    
    @Autowired
    private ProductoRepository repository;
    
    @Test
    void deberiaGuardarYRecuperarProducto() {
        Producto producto = new Producto("Test", 99.99);
        Producto guardado = repository.save(producto);
        
        Optional<Producto> encontrado = repository.findById(guardado.getId());
        
        assertTrue(encontrado.isPresent());
        assertEquals("Test", encontrado.get().getNombre());
    }
}
```

#### `@WebMvcTest` — Test de endpoints

```java
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioProducto servicio;  // Mock — no usa la BD real
    
    @Test
    void listarProductos_deberiaRetornar200() throws Exception {
        // Arrange
        when(servicio.listarTodos()).thenReturn(List.of(
            new Producto("Laptop", 999.99),
            new Producto("Mouse", 29.99)
        ));
        
        // Act & Assert
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nombre").value("Laptop"))
            .andExpect(jsonPath("$[1].nombre").value("Mouse"));
    }
    
    @Test
    void obtenerProducto_deberiaRetornar404CuandoNoExiste() throws Exception {
        // Arrange
        when(servicio.obtenerPorId(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(get("/api/productos/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void crearProducto_deberiaRetornar201() throws Exception {
        // Arrange
        Producto nuevo = new Producto("Teclado", 59.99);
        Producto creado = new Producto(1L, "Teclado", 59.99);
        when(servicio.crear(nuevo)).thenReturn(creado);
        
        // Act & Assert
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Teclado\",\"precio\":59.99}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("Teclado"));
    }
    
    @Test
    void crearProducto_deberiaRetornar400CuandoInvalido() throws Exception {
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"\",\"precio\":-1}"))
            .andExpect(status().isBadRequest());
    }
}
```

#### `@DataJpaTest` — Test de repositorio

```java
@DataJpaTest
class ProductoRepositoryTest {
    
    @Autowired
    private ProductoRepository repository;
    
    @Test
    void deberiaGuardarProducto() {
        Producto producto = new Producto("Laptop", 999.99);
        Producto guardado = repository.save(producto);
        
        assertNotNull(guardado.getId());
        assertEquals("Laptop", guardado.getNombre());
    }
    
    @Test
    void deberiaBuscarPorNombre() {
        repository.save(new Producto("Laptop", 999.99));
        repository.save(new Producto("Mouse", 29.99));
        
        List<Producto> resultados = repository.findByNombreContainingIgnoreCase("lap");
        
        assertEquals(1, resultados.size());
        assertEquals("Laptop", resultados.get(0).getNombre());
    }
    
    @Test
    void deberiaContarProductosActivos() {
        repository.save(new Producto("Activo", 10.0));
        repository.save(new Producto("Inactivo", 20.0));
        repository.save(new Producto("Otro", 30.0));
        
        long count = repository.countByActivoTrue();
        
        assertEquals(3, count);  // Todos activos por defecto
    }
}
```

#### Tipos de test

| Anotación | Qué prueba | Velocidad |
|-----------|-----------|-----------|
| `@SpringBootTest` | Todo el contexto | Lenta |
| `@WebMvcTest` | Solo controllers | Rápida |
| `@DataJpaTest` | Repositorios + BD | Media |
| `@JsonTest` | Serialización JSON | Muy rápida |
| Ninguna (JUnit puro) | Lógica aislada | Instantánea |

### Teoría en profundidad: test slicing y el contexto de Spring

#### ¿Por qué test slicing?

`@SpringBootTest` arranca el contexto COMPLETO: Tomcat, todos los beans, Flyway, autoconfiguraciones. Para una suite grande eso es minutos. **Test slicing** levanta solo la parte que se prueba:

| Slicing | Carga | Velocidad |
|---------|-------|-----------|
| `@WebMvcTest` | Controllers + MVC infra (no services reales) | Rápida |
| `@DataJpaTest` | Repositorios + Hibernate + DB embebida | Media |
| `@JsonTest` | Jackson + (de)serialización | Muy rápida |
| `@SpringBootTest` | Todo el contexto | Lenta |

**Peculiaridad:** `@WebMvcTest` NO carga los `@Service`. Por eso se usan `@MockBean` para simular las dependencias del controller. La cadena de testeo: unit (services) cubre la lógica; WebMvc cubre el mapeo HTTP; DataJpa cubre las queries.

#### Cómo funciona el context caching de Spring

```java
@SpringBootTest
class TestA { }   // contexto completo → cacheado

@SpringBootTest
class TestB { }   // MISMO contexto → reutilizado (no se recrea)

@WebMvcTest(ProductoController.class)
class TestC { }   // contexto distinto → nueva carga

@WebMvcTest(ProductoController.class)
class TestD { }   // mismo slice → reutilizado
```

Spring cachea los contextos por su configuración (anotaciones, properties). Ejecutar clases con contextos idénticos es casi gratis. Cada contexto distinto cuesta el arranque.

**Consejos de rendimiento:**
- Agrupa tests por tipo de contexto
- No varíes `@MockBean` entre clases del mismo slice (rompe el cache)
- Usa slices cuando no necesitas el contexto completo

#### Transactional rollback automático en @DataJpaTest

```java
@DataJpaTest
class ProductoRepositoryTest {
    @Autowired private ProductoRepository repo;

    @Test
    void t1() { repo.save(new Producto("A", 1.0)); }   // inserta
    @Test
    void t2() { assertEquals(0, repo.count()); }       // ✅ 0 — t1 se revirtió
}
```

Cada test corre dentro de una **transacción que se hace rollback** al final. La DB (embebida) queda limpia entre tests. Por defecto usa una DB en memoria (H2) salvo que configures lo contrario.

#### Probando con mock de beans (@MockBean vs @MockitoBean)

```java
// @MockBean — de spring-boot-test (en el contexto)
@WebMvcTest(ProductoController.class)
class C {
    @MockBean private ServicioProducto servicio;   // sustituye al bean real en el contexto
}

// @MockitoBean — preferido en Spring Boot 3.4+ (deprecado @MockBean)
// Mismo comportamiento, mejor gestión de recursos.
```

**Peculiaridad:** `@MockBean` sustituye el bean en el ApplicationContext. Los beans que dependan de él reciben el mock. `verify()` funciona igual que en unit tests.

### Ejemplo parcial

```java
@WebMvcTest(ProductoController.class)
class ProductoControllerApiTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ServicioProducto servicio;
    
    @Test
    void flujoCompleto_crear_listar_obtener() throws Exception {
        // Crear
        Producto nuevo = new Producto("Teclado", 59.99);
        Producto creado = new Producto(1L, "Teclado", 59.99);
        when(servicio.crear(nuevo)).thenReturn(creado);
        
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Teclado\",\"precio\":59.99}"))
            .andExpect(status().isCreated());
        
        // Listar
        when(servicio.listarTodos()).thenReturn(List.of(creado));
        
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Teclado"));
        
        // Obtener
        when(servicio.obtenerPorId(1L)).thenReturn(Optional.of(creado));
        
        mockMvc.perform(get("/api/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Teclado"));
    }
}
```

### Ejercicio práctico

1. Crear test `@WebMvcTest` para `ProductoController`
2. Probar GET, POST, PUT, DELETE
3. Probar validación (datos inválidos → 400)
4. Crear test `@DataJpaTest` para `ProductoRepository`
5. Ejecutar tests y verificar cobertura

**Solución esperada:** Tests de integración funcionando para controller y repository.

---

## 7.3 Buenas Prácticas y Arquitectura (30 min)

### Objetivos
- Aplicar arquitectura en capas
- Usar DTOs correctamente
- Seguir naming conventions

### Contenido teórico

#### Arquitectura en capas

```
┌─────────────────────────────────────────┐
│              Controller Layer           │
│  HTTP requests → ResponseEntity         │
│  @RestController                        │
├─────────────────────────────────────────┤
│              Service Layer              │
│  Lógica de negocio                      │
│  @Service + @Transactional              │
├─────────────────────────────────────────┤
│             Repository Layer            │
│  Acceso a datos                         │
│  @Repository + JPA                      │
├─────────────────────────────────────────┤
│              Entity Layer               │
│  Modelo de dominio                      │
│  @Entity (JPA)                          │
└─────────────────────────────────────────┘
```

**Regla:** Las capas **no se saltan**. Controller → Service → Repository. Nunca Controller → Repository.

#### DTOs — no exponer entidades

```java
// ❌ Mal — exponer entidad directamente
@RestController
public class ProductoController {
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return servicio.obtenerPorId(id);  // Expone campos internos
    }
}

// DTO de respuesta
public record ProductoResponse(
    Long id,
    String nombre,
    Double precio,
    String categoriaNombre
) {
    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(
            p.getId(),
            p.getNombre(),
            p.getPrecio(),
            p.getCategoria() != null ? p.getCategoria().getNombre() : null
        );
    }
}

// DTO de solicitud
public record ProductoRequest(
    @NotBlank String nombre,
    @Positive Double precio,
    String descripcion,
    Long categoriaId
) {}

// ✅ Bien — usar DTOs
@RestController
public class ProductoController {
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        Producto producto = servicio.obtenerPorId(id);
        return ProductoResponse.from(producto);
    }
    
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @RequestBody @Valid ProductoRequest request) {
        Producto creado = servicio.crear(request);
        return ResponseEntity.created(...)
            .body(ProductoResponse.from(creado));
    }
}
```

#### Naming conventions

```
Package structure:
com.taller.demo/
├── controller/      # Controllers REST
├── service/         # Lógica de negocio
├── repository/      # Repositorios JPA
├── model/           # Entidades JPA
├── dto/             # DTOs de request/response
├── exception/       # Excepciones personalizadas
├── config/          # Configuración
└── util/            # Utilidades

Classes:
- ProductoController (no ProductoApi, ProductoRest)
- ServicioProducto (no ProductoServicio, ProductoManager)
- ProductoRepository (no ProductoDao, ProductoRepo)
- Producto (entidad)
- ProductoRequest (DTO entrada)
- ProductoResponse (DTO salida)
- RecursoNoEncontradoException (excepción)

Methods:
- listarTodos() (no getAll, fetchAll)
- obtenerPorId(id) (no getById, findById)
- crear(producto) (no save, insert)
- actualizar(id, producto) (no update, modify)
- eliminar(id) (no delete, remove)
```

#### Checklist de buenas prácticas

```markdown
□ Controllers: solo manejar HTTP, delegar al service
□ Services: lógica de negocio, @Transactional
□ Repositories: acceso a datos, queries
□ DTOs: no exponer entidades directamente
□ Exceptions: @ControllerAdvice centralizado
□ Validation: @Valid en controllers
□ Tests: unitarios + integración
□ Naming: convenciones de Spring
□ Constructor injection: no field injection
□ Immutabilidad: records para DTOs, final para fields
```

### Teoría en profundidad: SOLID y arquitectura limpia

#### Los cinco principios SOLID

| Letra | Principio | Idea central |
|-------|-----------|--------------|
| S | Single Responsibility | Una clase, un motivo para cambiar |
| O | Open/Closed | Abierta a extensión, cerrada a modificación |
| L | Liskov Substitution | Subtipo sustituible sin romper |
| I | Interface Segregation | Interfaces pequeñas y específicas |
| D | Dependency Inversion | Depender de abstracciones, no concretos |

**S — Responsabilidad Única en práctica:**

```java
// ❌ Tres responsabilidades: negocio + persistencia + notificación
class PedidoService {
    void crearPedido() { ... validar ... }
    void guardarPedido() { ... SQL ... }
    void notificar() { ... email ... }
}

// ✅ Separado en capas con una responsabilidad cada una
@Service PedidoService      // lógica de negocio
@Repository PedidoRepo     // persistencia
@Component EmailNotifier   // notificación
```

**O — Open/Closed en Spring:** no modificas el servicio para agregar un canal de notificación — agregas un nuevo bean que implementa `Notificador`. El sistema está *cerrado a modificación* (el servicio no cambia) y *abierto a extensión* (nuevo bean). Esto es exactamente lo que viste con `List<Notificador>` inyectado.

**L — Liskov en Spring Data:** si tu service recibe `ProductoRepository` (interfaz), cualquier implementación (H2, MySQL, Testcontainers) es sustituible sin cambiar el caller.

**I — Segregación de interfaces:**

```java
// ❌ Interfaz gorda
interface RepositorioPedido {
    void guardar();
    void buscar();
    void exportarPdf();   // no todos los repos guardan PDF
}

// ✅ Interfaces pequeñas
interface Repositorio { void guardar(); void buscar(); }
interface Exportable { void exportarPdf(); }
```

**D — Inversión de dependencias:** lo viste en detalle en el Módulo 4 — la capa de alto nivel (service) depende de `RepositorioFactura` (abstracción), no de `MySQLRepositorioFactura` (concreto).

#### Arquitectura en capas vs hexagonal

```
Capa (layer) tradicional:
Controller → Service → Repository
  │            │           │
  HTTP      negocio      datos

Hexagonal (Ports & Adapters):
          ┌───────────────────────┐
  HTTP ───│  adapter (in)         │
  (rest)  │   │                   │
          │   ▼                   │
          │   Service (dominio)   │
          │   │  port            │
          │   ▼                   │
          │  adapter (out)        │─── MySQL
          └───────────────────────┘
```

**Peculiaridad:** en un monolito de Spring Boot, las "capas" son paquetes (`controller`, `service`, `repository`). La dirección de dependencia SIEMPRE hacia abajo (Hibernate no depende de Spring Web). Si `controller` importa el `repository`, violaste la capa.

#### El costo de no aplicar SOLID

| Síntoma | Principio violado |
|---------|-------------------|
| Clases de 1000+ líneas | S |
| Cambios en un sitio rompen otro | O |
| instanceof + cast en cascada | L |
| Interfaz con métodos que no aplican | I |
| `new Concreto()` dentro de la clase | D |

### Ejemplo parcial

```java
// Estructura completa del proyecto
com.taller.demo/
├── controller/
│   └── ProductoController.java
├── service/
│   ├── ServicioProducto.java
│   └── impl/
│       └── ServicioProductoImpl.java  (si hay múltiples impls)
├── repository/
│   └── ProductoRepository.java
├── model/
│   ├── Producto.java
│   └── Categoria.java
├── dto/
│   ├── ProductoRequest.java
│   └── ProductoResponse.java
├── exception/
│   ├── AppException.java
│   ├── RecursoNoEncontradoException.java
│   └── ManejadorErrores.java
└── config/
    └── OpenApiConfig.java
```

### Ejercicio práctico

1. Reorganizar el proyecto en capas
2. Crear DTOs `ProductoRequest` y `ProductoResponse`
3. Modificar controller para usar DTOs
4. Verificar que la entidad no se expone directamente
5. Aplicar naming conventions

**Solución esperada:** Proyecto bien estructurado siguiendo convenciones.

---

## Resumen del Módulo 7

### Conceptos clave

| Concepto | Descripción |
|---|---|
| JUnit 5 | Framework de testing para Java |
| `@Test` | Marca método como test |
| `@BeforeEach` / `@AfterEach` | Setup/teardown por test |
| Mockito | Mockear dependencias |
| `@Mock` | Crear mock |
| `@InjectMocks` | Inyectar mocks |
| `when().thenReturn()` | Configurar comportamiento |
| `verify()` | Verificar interacciones |
| `@SpringBootTest` | Test de integración completo |
| `@WebMvcTest` | Test de controllers |
| `@DataJpaTest` | Test de repositorios |
| `MockMvc` | Simular peticiones HTTP |
| DTOs | No exponer entidades |
| Arquitectura capas | Controller → Service → Repository |
| Naming | Convenciones de Spring |

### Siguiente módulo
→ [Proyecto Final Integrador](proyecto-final.md)
