# Módulo 5: Construcción de APIs REST con Spring Boot

**Duración:** 4 horas  
**Objetivo:** Crear APIs REST completas con validación, errores y documentación

---

## 5.1 Fundamentos REST (30 min)

### Objetivos
- Entender los principios de REST
- Conocer los HTTP methods y status codes
- Comprender serialización JSON con Jackson

### Contenido teórico

#### ¿Qué es REST?

REST (Representational State Transfer) es un estilo arquitectónico:

| Principio | Descripción |
|-----------|-------------|
| Stateless | Cada petición contiene toda la info necesaria |
| Resource-based | Todo es un recurso (URL) |
| Uniform interface | Métodos HTTP estándar |
| Client-server | Separación de responsabilidades |
| Cacheable | Respuestas pueden ser cacheadas |

#### HTTP Methods

| Method | Uso | Idempotente | Request Body |
|--------|-----|-------------|--------------|
| `GET` | Leer recurso | ✅ | ❌ |
| `POST` | Crear recurso | ❌ | ✅ |
| `PUT` | Reemplazar recurso | ✅ | ✅ |
| `PATCH` | Actualizar parcial | ✅ | ✅ |
| `DELETE` | Eliminar recurso | ✅ | ❌ |

#### Status Codes

| Code | Significado | Uso |
|------|-------------|-----|
| 200 | OK | GET exitoso, PUT exitoso |
| 201 | Created | POST exitoso |
| 204 | No Content | DELETE exitoso |
| 400 | Bad Request | Error de validación |
| 401 | Unauthorized | No autenticado |
| 403 | Forbidden | No autorizado |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Conflicto (duplicado) |
| 500 | Internal Server Error | Error del servidor |

#### JSON y Jackson

```json
{
    "id": 1,
    "nombre": "Laptop",
    "precio": 999.99,
    "activo": true,
    "categoria": {
        "id": 1,
        "nombre": "Electrónica"
    }
}
```

**Peculiaridad:** Spring Boot usa **Jackson** para convertir JSON↔Java automáticamente. Anotaciones de Jackson:

```java
public class Producto {
    @JsonProperty("producto_id")
    private Long id;
    
    @JsonIgnore
    private String密码;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCreacion;
}
```

### Teoría en profundidad: REST como arquitectura

#### Los seis constraints de REST (Fielding, 2000)

Roy Fielding definió REST en su tesis doctoral. Una API es "RESTful" solo si cumple:

| Constraint | Qué exige | Consecuencia |
|-----------|-----------|--------------|
| 1. Client-Server | Separación de responsabilidades | UI y API evolucionan solas |
| 2. Stateless | Cada request tiene todo el contexto | Escalabilidad horizontal |
| 3. Cacheable | Las respuestas declaran cachabilidad | Rendimiento |
| 4. Uniform Interface | Mismos verbos, recursos, convenciones | Simplicidad |
| 5. Layered System | Jerarquía de capas intermedias | Seguridad, balanceo |
| 6. Code on Demand (opcional) | Código ejecutable descargable | Raramente usado |

**Peculiaridad:** la mayoría de las "APIs REST" modernas son **REST-like** — usan HTTP + JSON con verbos y recursos, pero omiten HATEOAS (hypermedia) y a veces cache. REST puro exige que el cliente navegue por links, no por URLs hardcodeadas.

#### Idempotencia: la propiedad fundamental

```
Definición: ejecutar la operación N veces tiene el mismo efecto que una vez.

GET    /productos/1     → idempotente (no cambia estado)
PUT    /productos/1     → idempotente (mismo resultado siempre)
DELETE /productos/1     → idempotente (deleting algo inexistente = ok, 204)
POST   /productos       → NO idempotente (crea N copias si se repite N veces)
PATCH  /productos/1     → Depende del contrato (a veces no idempotente)
```

**Consecuencia práctica:** los retries son seguros con GET/PUT/DELETE. Con POST, un retry duplica recursos → usa idempotency keys (`Idempotency-Key` header) o haz POST + validación de duplicados.

#### Statelessness y la sesión

```
❌ Stateful: el servidor recuerda "el usuario X está en el paso 3"
   → cada request depende de la memoria del servidor
   → no escala horizontalmente (sticky sessions)

✅ Stateless: cada request lleva todo
   → Authorization: Bearer <JWT>
   → el servidor no guarda sesión
   → cualquier instancia puede responder
```

**Peculiaridad:** el statelessness no significa que el cliente no tenga estado (cliente puede cachear, autenticarse, etc.). Significa que el **servidor** no guarda estado de sesión entre requests.

#### Diseño de recursos y convenciones

```
Recursos son SUSTANTIVOS (nunca verbos):

✅ /productos                ❌ /getProductos
✅ /productos/{id}           ❌ /producto/obtener
✅ /productos/{id}/pedidos   ❌ /pedidosDeProducto

Colecciones en plural. Anidación solo hasta 2 niveles.
Filtros como query params: ?categoria=5&precioMin=100
```

### Ejemplo parcial

```java
// Mapeo HTTP method → operación CRUD
// GET    /api/productos       → listar todos
// GET    /api/productos/{id}  → obtener uno
// POST   /api/productos       → crear
// PUT    /api/productos/{id}  → actualizar
// DELETE /api/productos/{id}  → eliminar
```

### Ejercicio práctico

1. Diseñar endpoints para un CRUD de `Producto`
2. Definir status codes para cada operación
3. Crear diagrama de las URLs

**Solución esperada:** Documento de diseño de la API.

---

## 5.2 Controllers y endpoints (60 min)

### Objetivos
- Crear controllers con `@RestController`
- Mapear path variables, query params y request body
- Retornar respuestas con `ResponseEntity`

### Contenido teórico

#### `@RestController`

```java
@RestController
@RequestMapping("/api/productos")  // Prefijo común
public class ProductoController {
    
    private final ServicioProducto servicio;
    
    public ProductoController(ServicioProducto servicio) {
        this.servicio = servicio;
    }
    
    // GET /api/productos
    @GetMapping
    public List<Producto> listar() {
        return servicio.listarTodos();
    }
    
    // GET /api/productos/{id}
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return servicio.obtenerPorId(id);
    }
    
    // POST /api/productos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody Producto producto) {
        return servicio.crear(producto);
    }
    
    // PUT /api/productos/{id}
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, 
                               @RequestBody Producto producto) {
        return servicio.actualizar(id, producto);
    }
    
    // DELETE /api/productos/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
```

#### Path Variables

```java
// GET /api/productos/42
@GetMapping("/{id}")
public Producto obtener(@PathVariable Long id) {
    // id = 42
}

// Múltiples path variables
// GET /api/categorias/5/productos/10
@GetMapping("/categorias/{catId}/productos/{prodId}")
public Producto obtenerDeCategoria(
        @PathVariable Long catId,
        @PathVariable Long prodId) {
    // ...
}
```

#### Query Parameters

```java
// GET /api/productos?categoria=electronica&precioMin=100
@GetMapping
public List<Producto> buscar(
        @RequestParam String categoria,
        @RequestParam(defaultValue = "0") double precioMin,
        @RequestParam(required = false) String busqueda) {
    // categoria = "electronica"
    // precioMin = 100.0
    // busqueda = null (no se proporcionó)
}
```

#### Request Body

```java
// POST /api/productos
// Body: {"nombre": "Mouse", "precio": 29.99}
@PostMapping
public Producto crear(@RequestBody Producto producto) {
    // Spring convierte JSON → Producto automáticamente
    return servicio.crear(producto);
}

// Con validación
@PostMapping
public Producto crear(@RequestBody @Valid Producto producto) {
    // Valida antes de crear
    return servicio.crear(producto);
}
```

#### ResponseEntity

```java
@GetMapping("/{id}")
public ResponseEntity<Producto> obtener(@PathVariable Long id) {
    Optional<Producto> producto = servicio.obtenerPorId(id);
    
    if (producto.isPresent()) {
        return ResponseEntity.ok(producto.get());  // 200
    } else {
        return ResponseEntity.notFound().build();  // 404
    }
}

@PostMapping
public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
    Producto creado = servicio.crear(producto);
    return ResponseEntity
        .created(URI.create("/api/productos/" + creado.getId()))
        .body(creado);  // 201 + Location header
}
```

### Teoría en profundidad: el pipeline MVC de Spring

#### DispatcherServlet y el flujo de una petición

```
HTTP Request
   │
   ▼
Tomcat (servlet container)
   │
   ▼
DispatcherServlet (front controller)
   │
   ├── HandlerMapping → encuentra el @RequestMapping correcto
   │
   ├── HandlerAdapter → invoca el método del controller
   │     ├── resuelve @PathVariable, @RequestParam, @RequestBody
   │     ├── convierte JSON→Object (HttpMessageConverter)
   │     ├── valida (@Valid) → MethodArgumentNotValidException
   │     └── ejecuta el método
   │
   ├── HandlerInterceptor (pre/post processing)
   │
   ├── el controller retorna ResponseEntity / objeto
   │
   ├── HttpMessageConverter → serializa a JSON (Jackson)
   │
   └── HTTP Response
```

**Peculiaridad del front controller pattern:** DispatcherServlet es un *front controller* — un punto único de entrada que centraliza el despacho. Todo pasa por él. Por eso `@ControllerAdvice` funciona a nivel global: intercepta excepciones de cualquier handler.

#### HttpMessageConverter — la magia de la conversión

```java
// Jackson convierte automáticamente:
//   JSON body   →  Java object   (@RequestBody)
//   Java object →  JSON response

// Registrado por orden de prioridad:
//   StringHttpMessageConverter      (text/plain)
//   MappingJackson2HttpMessageConverter (application/json)
//   ByteArrayHttpMessageConverter   (application/octet-stream)
//   ...

// Configurar Jackson:
spring.jackson.serialization.indent-output=true
spring.jackson.property-naming-strategy=SNAKE_CASE   // campoNombre → campo_nombre
spring.jackson.default-property-inclusion=non_null    // no serializar nulls
```

#### Content negotiation

```
Accept: application/json   → devuelve JSON
Accept: application/xml    → devuelve XML (si hay converter)
Accept: */*                → default (JSON en Spring Boot)

El cliente pide el formato; el servidor negocia el medio (content negotiation).
```

#### Anotaciones de binding — el detalle

```java
// @RequestBody — cuerpo completo (JSON/XML) → objeto
// @ModelAttribute — form-encoded o query params → objeto
// @PathVariable — {id} de la URL
// @RequestParam — ?clave=valor
// @RequestHeader — Header HTTP → parámetro
// @CookieValue — cookie → parámetro
// @RequestPart — multipart (archivos)

// Ejemplo combinado:
@PostMapping("/{id}/upload")
public String subir(@PathVariable Long id,
                    @RequestParam("descripcion") String desc,
                    @RequestPart("archivo") MultipartFile archivo) { ... }
```

### Ejemplo parcial

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    private final ServicioProducto servicio;
    
    public ProductoController(ServicioProducto servicio) {
        this.servicio = servicio;
    }
    
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Producto> productos = servicio.listar(PageRequest.of(page, size));
        return ResponseEntity.ok(productos.getContent());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return servicio.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody @Valid Producto producto) {
        Producto creado = servicio.crear(producto);
        return ResponseEntity
            .created(URI.create("/api/productos/" + creado.getId()))
            .body(creado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid Producto producto) {
        return servicio.actualizar(id, producto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (servicio.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### Ejercicio práctico

1. Crear `ProductoController` con CRUD completo
2. Usar `@PathVariable` para obtener por ID
3. Usar `@RequestParam` para filtrar por categoría
4. Retornar `ResponseEntity` con status codes correctos
5. Probar con curl o Postman

**Solución esperada:** API REST funcional con todos los endpoints.

---

## 5.3 Validación de datos (45 min)

### Objetivos
- Aplicar validación con Bean Validation (JSR 380)
- Crear validadores personalizados
- Manejar errores de validación

### Contenido teórico

#### Bean Validation (JSR 380)

**Peculiaridad:** Bean Validation es un **estándar Java** (JSR 380), no de Spring. Spring lo integra.

```java
public class Producto {
    @NotNull(message = "El nombre es requerido")
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "Nombre: 2-100 caracteres")
    private String nombre;
    
    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser positivo")
    @DecimalMax(value = "999999.99", message = "Precio máximo: 999,999.99")
    private Double precio;
    
    @Email(message = "Email inválido")
    private String emailProveedor;
    
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fechaCreacion;
    
    @Size(max = 500, message = "Descripción máxima: 500 caracteres")
    private String descripcion;
}
```

#### Validaciones disponibles

| Anotación | Uso |
|-----------|-----|
| `@NotNull` | No puede ser null |
| `@NotBlank` | No puede ser null, vacío o solo espacios |
| `@NotEmpty` | No puede ser null o vacío |
| `@Size(min, max)` | Tamaño (String, Collection, Array) |
| `@Min`, `@Max` | Valor mínimo/máximo |
| `@Positive`, `@Negative` | positivo/negativo |
| `@PositiveOrZero`, `@NegativeOrZero` | >= 0, <= 0 |
| `@Email` | Formato de email |
| `@Pattern(regex)` | Expresión regular |
| `@Past`, `@Future` | Fecha pasada/futura |
| `@PastOrPresent`, `@FutureOrPresent` | Fecha pasada/presente/futura |
| `@DecimalMin`, `@DecimalMax` | Valor decimal |
| `@Digits(integer, fraction)` | Dígitos |

#### Validación en el controller

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody @Valid Producto producto) {
        // Si la validación falla, Spring lanza MethodArgumentNotValidException
        // El error se maneja en el @ControllerAdvice (sección 5.4)
        Producto creado = servicio.crear(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId()))
            .body(creado);
    }
}
```

#### Validadores personalizados

```java
// 1. Crear la anotación
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrecioValidator.class)
public @interface PrecioValido {
    String message() default "Precio inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Crear el validador
public class PrecioValidator implements ConstraintValidator<PrecioValido, Double> {
    
    @Override
    public boolean isValid(Double precio, ConstraintValidatorContext context) {
        if (precio == null) return true;  // @NotNull se encarga
        return precio > 0 && precio < 1000000;
    }
}

// 3. Usar
public class Producto {
    @PrecioValido
    private Double precio;
}
```

### Teoría en profundidad: Bean Validation (JSR 380) a fondo

#### Arquitectura del estándar

```
JSR 380 (Bean Validation 2.0) — ESPECIFICACIÓN
├── API: jakarta.validation
│     └── Validator, ConstraintValidator, ConstraintDescriptor
├── Implementación (proveedor):
│     └── Hibernate Validator (el default de Spring Boot)
└── Integración en Spring:
      └── LocalValidatorFactoryBean (bean "validator" del contexto)
```

**Peculiaridad:** no es de Spring — es un estándar Jakarta. Spring solo lo *integra*: cuando un `@Valid` en el controller falla, Spring captura la excepción y la mapea. Puedes usar el mismo estándar en cualquier framework Java.

#### Grupos de validación

```java
// Un mismo DTO valida distinto según el caso de uso
public interface OnCreate {}
public interface OnUpdate {}

public class Producto {
    @Null(groups = OnCreate.class)          // crear: id nulo
    @NotNull(groups = OnUpdate.class)       // actualizar: id requerido
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String nombre;
}

// Uso:
@PostMapping
public void crear(@Validated(OnCreate.class) @RequestBody Producto p) { }
@PutMapping("/{id}")
public void actualizar(@Validated(OnUpdate.class) @RequestBody Producto p) { }
```

#### Validación en cascada

```java
public class PedidoRequest {
    @Valid                       // cascada: valida los campos de ClienteRequest
    @NotNull
    private ClienteRequest cliente;

    @NotEmpty
    @Valid                       // cascada en colección: valida cada item
    private List<ItemRequest> items;
}

public class ItemRequest {
    @NotNull @Positive
    private Long productoId;
    @NotNull @Min(1)
    private Integer cantidad;
}
```

#### Constraints compuestos (custom de Hibernate)

```java
// Hibernate Validator agrega constraints propios:
@Length(min = 2, max = 100)     // similar a @Size
@NotEmpty                       // para String/Collection/Map
@Range(min = 1, max = 100)      // numérico
@URL                            // URL válida
@CreditCardNumber               // valida algoritmo Luhn
```

#### Rendimiento de la validación

- La validación corre en el thread de la petición → evita validaciones costosas repetidas
- Usa constraints declarativos en lugar de `if` dispersos en la lógica
- Los `message` con `{placeholder}` y `MessageFormat` tienen coste solo al fallar
- Para datasets grandes, considera validación manual + exceptions

### Ejemplo parcial

```java
// DTO con validación completa
public record ProductoRequest(
    @NotBlank(message = "Nombre requerido")
    @Size(min = 2, max = 100)
    String nombre,
    
    @NotNull(message = "Precio requerido")
    @Positive
    Double precio,
    
    @Size(max = 500)
    String descripcion,
    
    @NotNull
    Long categoriaId
) {}

// Uso en controller
@PostMapping
public ResponseEntity<Producto> crear(
        @RequestBody @Valid ProductoRequest request) {
    Producto producto = servicio.crear(request);
    return ResponseEntity.created(...).body(producto);
}
```

### Ejercicio práctico

1. Agregar validaciones a la entidad `Producto`
2. Crear DTO `ProductoRequest` con validaciones
3. Usar `@Valid` en el controller
4. Crear validador personalizado `@PrecioValido`
5. Probar enviando datos inválidos y verificar errores

**Solución esperada:** API que rechaza datos inválidos con mensajes claros.

---

## 5.4 Manejo de errores global (45 min)

### Objetivos
- Crear `@ControllerAdvice` para manejo centralizado
- Definir `@ExceptionHandler` para cada tipo de error
- Retornar respuestas de error consistentes

### Contenido teórico

#### `@ControllerAdvice`

```java
@RestControllerAdvice
public class ManejadorErrores {
    
    // Error de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException ex) {
        
        List<String> errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", errores));
    }
    
    // Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }
    
    // Error genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
    }
}
```

#### Response de error consistente

```java
public record ErrorResponse(
    String codigo,
    List<String> mensajes,
    Instant timestamp
) {
    public ErrorResponse(String codigo, List<String> mensajes) {
        this(codigo, mensajes, Instant.now());
    }
    
    public ErrorResponse(String codigo, String mensaje) {
        this(codigo, List.of(mensaje));
    }
}

// Respuesta JSON:
// {
//     "codigo": "VALIDATION_ERROR",
//     "mensajes": ["nombre: El nombre es requerido", "precio: El precio debe ser positivo"],
//     "timestamp": "2024-01-15T10:30:00Z"
// }
```

#### Excepciones personalizadas

```java
// Excepción base
public class AppException extends RuntimeException {
    private final String codigo;
    
    public AppException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
    
    public String getCodigo() { return codigo; }
}

// Excepción específica
public class RecursoNoEncontradoException extends AppException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super("NOT_FOUND", recurso + " con id " + id + " no encontrado");
    }
}

// Excepción de conflicto
public class ConflictoException extends AppException {
    public ConflictoException(String mensaje) {
        super("CONFLICT", mensaje);
    }
}
```

### Teoría en profundidad: manejo de errores y RFC 9457

#### ¿Por qué un formato consistente?

Problemas sin un formato de error estándar:
- Cada framework retorna estructuras distintas → el cliente parsea 5 formatos
- Sin códigos máquina → el cliente hace string matching de mensajes
- Mensajes en distinto idioma que el cliente espera
- Sin detalles para debugging

**Solución modernas:**
- **RFC 7807 / RFC 9457 (Problem Details for HTTP APIs):** estándar IETF para errores HTTP
- Cada problema es un documento JSON con campos estándar: `type`, `title`, `status`, `detail`, `instance`

#### Estructura de un Problem Details

```json
{
    "type": "https://ejemplo.com/errores/precio-invalido",
    "title": "Precio inválido",
    "status": 400,
    "detail": "El precio debe ser positivo",
    "instance": "/api/productos",
    "errores": ["campo: precio", "campo: stock"],
    "timestamp": "2024-01-15T10:30:00Z"
}
```

| Campo | Significado |
|-------|-------------|
| `type` | URI que identifica la categoría del error (documentable) |
| `title` | Mensaje corto legible |
| `status` | HTTP status code |
| `detail` | Explicación específica de esta instancia |
| `instance` | URI del recurso que causó el error |
| (extras) | Campos propios para errores específicos |

#### La decisión de la pila: exceptions vs @ControllerAdvice

```
Lógica de negocio:
  throw new RecursoNoEncontradoException(id)      ← dominio
      ↓ (propaga, sin try-catch dispersos)
@ControllerAdvice:
  @ExceptionHandler(RecursoNoEncontradoException.class)
  → traduce a ProblemDetails + status 404                     ← HTTP

Separación: el dominio no sabe HTTP. El advice no sabe lógica.
```

**Peculiaridad:** evita exponer `e.printStackTrace()` o stack traces al cliente. Los logs internos los tiene el servidor; el cliente recibe solo el `detail` sanitizado. Nunca filtres datos internos (SQL, rutas de archivos, credenciales).

#### Errores comunes de diseño

```java
// ❌ Cliente necesita string matching
throw new RuntimeException("PRODUCTO_NOT_FOUND");

// ✅ Código de error estructurado + HTTP status correcto
throw new RecursoNoEncontradoException("Producto", id);
// → 404, ProblemDetails con type=".../not-found"

// ❌ Status 500 para errores de cliente (validación → 400, no encontrado → 404)
// ❌ Attrapar todo con Exception.class sin log (esconde bugs)
```

### Ejemplo parcial

```java
@RestControllerAdvice
public class ManejadorErrores {
    
    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException ex) {
        
        log.warn("Error de validación: {}", ex.getMessage());
        
        List<String> errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .sorted()
            .toList();
        
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", errores));
    }
    
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcion(Exception ex) {
        
        log.error("Error interno", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", 
                  "Error interno del servidor"));
    }
}
```

### Ejercicio práctico

1. Crear `ErrorResponse` consistente
2. Crear excepciones personalizadas: `RecursoNoEncontradoException`, `ConflictoException`
3. Crear `@ControllerAdvice` con handlers para cada excepción
4. Probar enviando datos inválidos y verificar respuestas
5. Probar buscar recurso inexistente

**Solución esperada:** API con respuestas de error consistentes y manejadas centralizadamente.

---

## 5.5 Documentación de la API (30 min)

### Objetivos
- Configurar SpringDoc OpenAPI
- Documentar endpoints con anotaciones
- Acceder a Swagger UI

### Contenido teórico

#### SpringDoc OpenAPI

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

```properties
# application.properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

**Acceder:** `http://localhost:8080/swagger-ui.html`

#### Anotaciones de documentación

```java
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD de productos")
public class ProductoController {
    
    @Operation(summary = "Listar productos", 
               description = "Obtiene la lista de todos los productos con paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        // ...
    }
    
    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id) {
        // ...
    }
    
    @Operation(summary = "Crear producto")
    @PostMapping
    public ResponseEntity<Producto> crear(
            @RequestBody @Schema(description = "Datos del producto") 
            ProductoRequest request) {
        // ...
    }
}
```

#### Schema personalizado

```java
@Schema(description = "Producto del catálogo")
public record ProductoRequest(
    @Schema(description = "Nombre del producto", example = "Laptop", 
            requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
    @NotBlank String nombre,
    
    @Schema(description = "Precio en USD", example = "999.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive Double precio,
    
    @Schema(description = "Descripción opcional", example = "Laptop gaming")
    @Size(max = 500) String descripcion,
    
    @Schema(description = "ID de la categoría", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Long categoriaId
) {}
```

### Teoría en profundidad: OpenAPI — el contrato de la API

#### ¿Qué es OpenAPI (Swagger)?

OpenAPI es un **estándar** (inicialmente Swagger 2.0, ahora OpenAPI 3.x) para describir APIs de forma machine-readable. Es el "WSDL de las APIs REST".

```
API real → OpenAPI Spec (JSON/YAML) → herramientas
                       │
                       ├── Swagger UI      → documentación interactiva
                       ├── Swagger Editor  → editar el spec
                       ├── generators     → clientes/servidores en 40+ lenguajes
                       └── validators     → contract testing
```

**Peculiaridad:** el spec ES el contrato. El cliente puede generarse (OpenAPI Generator / TypeScript, Dart, etc.) directamente desde `v3/api-docs`. Si tu API cambia el schema, el cliente regenerado se rompe — la documentación y el código quedan en sincronía.

#### Estructura del spec

```yaml
openapi: 3.0.0
info:
  title: API de Productos
  version: 1.0.0
paths:
  /api/productos:
    get:
      summary: Listar productos
      parameters:
        - name: page
          in: query
          schema: { type: integer }
      responses:
        '200':
          description: Lista de productos
          content:
            application/json:
              schema:
                type: array
                items: { $ref: '#/components/schemas/Producto' }
components:
  schemas:
    Producto:
      type: object
      properties:
        id: { type: integer, format: int64 }
        nombre: { type: string }
```

SpringDoc genera este YAML automáticamente a partir de tus anotaciones y DTOs.

#### Anotaciones equivalentes (Java ↔ Spec)

| Java (SpringDoc) | OpenAPI |
|------------------|---------|
| `@Operation(summary, description)` | `paths...get.summary` |
| `@ApiResponse(responseCode)` | `paths...responses` |
| `@Parameter(description, example)` | `paths...parameters` |
| `@Schema(description, example, required)` | `components.schemas` |
| `@Tag(name, description)` | `tags` |
| `@SecurityRequirement` | `security` |

#### Por qué documentar el contrato con precisión

```java
@Schema(description = "Precio en USD, positivo", 
        example = "999.99", minimum = "0")
@Positive
private Double precio;
```

El schema bien anotado permite:
- **Contract testing** (cliente vs servidor)
- **Mock servers** generados del spec
- **Client generation** sin consultar al equipo backend
- Validación de ejemplos en la doc interactiva

### Ejemplo parcial

```java
// Configuración OpenAPI
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Productos")
                .description("API para gestionar productos del catálogo")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Taller Spring Boot")
                    .email("taller@example.com")));
    }
}
```

### Ejercicio práctico

1. Agregar dependencia SpringDoc
2. Anotar todos los endpoints del controller
3. Crear schema personalizado para el DTO
4. Configurar información de la API (título, descripción)
5. Acceder a Swagger UI y probar los endpoints

**Solución esperada:** API documentada con Swagger UI funcional.

---

## Resumen del Módulo 5

### Conceptos clave

| Concepto | Descripción |
|---|---|
| REST | Stateful, resource-based, uniform interface |
| HTTP Methods | GET, POST, PUT, PATCH, DELETE |
| Status Codes | 200, 201, 204, 400, 404, 500 |
| `@RestController` | Controller REST (combina @Controller + @ResponseBody) |
| `@PathVariable` | Parámetro de URL `/productos/{id}` |
| `@RequestParam` | Query parameter `?page=0` |
| `@RequestBody` | Cuerpo de la petición (JSON → Java) |
| `ResponseEntity` | Controlar status code, headers, body |
| Bean Validation | JSR 380 — estándar Java para validación |
| `@ControllerAdvice` | Manejo centralizado de errores |
| `@ExceptionHandler` | Manejar excepciones específicas |
| SpringDoc OpenAPI | Documentación automática con Swagger |

### Siguiente módulo
→ [Módulo 6: Acceso a Datos](06-acceso-datos.md)
