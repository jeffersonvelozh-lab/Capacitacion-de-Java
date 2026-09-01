# Módulo 4: Introducción a Spring Boot

**Duración:** 4 horas  
**Objetivo:** Comprender IoC, DI, estructura de Spring Boot y configuración

---

## 4.1 ¿Qué es Spring y por qué Spring Boot? (30 min)

### Objetivos
- Entender el problema que resuelve Spring
- Diferenciar Spring Framework de Spring Boot
- Comprender Inversión de Control y Dependency Injection

### Contenido teórico

#### El problema: acoplamiento excesivo

```java
// Sin Spring — todo acoplado
public class ServicioPedido {
    private MySQLConexion conexion;  // ¡Atado a MySQL!
    private EmailService email;       // ¡Atado a EmailService!
    
    public ServicioPedido() {
        this.conexion = new MySQLConexion();  // Hardcoded
        this.email = new EmailService();       // Hardcoded
    }
}

// Problemas:
// 1. No puedes cambiar MySQL por PostgreSQL
// 2. No puedes mockear para tests
// 3. No puedes reutilizar con otro email service
```

#### Inversión de Control (IoC)

El objeto **no crea** sus dependencias — alguien más se las provee.

```java
// Con IoC — el objeto recibe sus dependencias
public class ServicioPedido {
    private final ConexionBD conexion;  // Interface
    private final Notificador notificador;  // Interface
    
    public ServicioPedido(ConexionBD conexion, Notificador notificador) {
        this.conexion = conexion;
        this.notificador = notificador;
    }
}

// Quien crea decide qué implementación usar
ConexionBD conexion = new PostgreSQLConexion();
Notificador notificador = new EmailNotificador();
ServicioPedido servicio = new ServicioPedido(conexion, notificador);
```

#### Dependency Injection (DI)

Spring implementa IoC: **crea los objetos y los inyecta automáticamente.**

```java
// Spring crea la instancia y la inyecta
@Service
public class ServicioPedido {
    private final ConexionBD conexion;
    private final Notificador notificador;
    
    // Spring llama a este constructor automáticamente
    public ServicioPedido(ConexionBD conexion, Notificador notificador) {
        this.conexion = conexion;
        this.notificador = notificador;
    }
}

// Spring decide:
// - MySQLConexion o PostgreSQLConexion según configuración
// - EmailNotificador o SMSNotificador según configuración
```

#### Spring Framework vs Spring Boot

| Aspecto | Spring Framework | Spring Boot |
|---------|-----------------|-------------|
| Configuración | Explícita (XML o Java) | Convención sobre configuración |
| Arranque | Manual setup | `main()` con un click |
| Dependencias | Gestionar manualmente | Starter packs automáticos |
| Servidor | Configurar externamente | Embedded Tomcat/Jetty |
| Properties | Múltiples archivos | `application.properties` |
| **Filosofía** | Flexible pero verbose | Simple pero opinionated |

**Spring Boot = Spring Framework + opiniones sensatas + autoconfiguración.**

#### Novedades de Spring Boot 4 (sobre Spring Framework 7)

| Cambio | Impacto |
|--------|---------|
| Base: Spring Framework 7 + Jakarta EE 11 | APIs actualizadas (`jakarta.*`) |
| **API versioning** de primera clase | Versionar endpoints sin custom headers |
| `RestClient` como cliente REST estándar | Reemplaza a `RestTemplate` |
| Modularización de starters | Menos dependencias no usadas |
| Mejoras en AOT + GraalVM | Startups más rápidos |
| Requiere **Java 17+**, soporta **Java 25** (LTS) | Compatible con el JDK más reciente |

**Java 25 (LTS)** trae consolidado lo mejor de versiones recientes: virtual threads (21), structured concurrency y scoped values (finales en 25), switch pattern matching (21), records y sealed classes como estándar.

### Ejemplo parcial

```java
// Sin Spring — configuración manual
public class AppConfig {
    public static void main(String[] args) {
        ConexionBD conexion = new MySQLConexion("jdbc:mysql://...");
        EmailService email = new EmailService("smtp.gmail.com");
        ServicioPedido servicio = new ServicioPedido(conexion, email);
        // Usar servicio...
    }
}

// Con Spring Boot — todo automático
@SpringBootApplication
public class MiApp {
    public static void main(String[] args) {
        SpringApplication.run(MiApp.class, args);
        // ¡Listo! Spring configuró todo
    }
}
```

### Teoría en profundidad: IoC y el principio de inversión de dependencias

#### Los 5 niveles de desacoplamiento

| Nivel | Técnica | Acoplamiento |
|-------|---------|--------------|
| 1 | `new` hardcodeado en el método | Alto |
| 2 | `new` en el constructor | Alto |
| 3 | Abstract factory / Service Locator | Medio |
| 4 | Constructor injection (manual) | Bajo |
| 5 | DI container (Spring) | Mínimo |

Spring lleva el nivel 5: el container decide el *wiring* completo. Tu código ni siquiera sabe que existe Spring en las capas de dominio.

#### Dependency Inversion Principle (DIP) — la teoría

**SOLID, letra D:** los módulos de alto nivel no deben depender de los de bajo nivel; ambos deben depender de abstracciones.

```java
// ❌ Violación del DIP: servicio de alto nivel depende de concreto de bajo nivel
class ServicioFactura {
    private MySQLRepositorioFactura repo;   // concreto

    ServicioFactura() { repo = new MySQLRepositorioFactura(); }
}

// ✅ Cumple DIP: depende de abstracción, inyectada desde fuera
class ServicioFactura {
    private final RepositorioFactura repo;  // interfaz (abstracción)

    ServicioFactura(RepositorioFactura repo) { this.repo = repo; }
    // El container provee MySQLRepositorioFactura O MongoRepositorioFactura
}
```

#### Inversion of Control en sentido amplio

IoC es un principio más general que DI:

```
Flujo tradicional:
  tu código → controla todo → crea dependencias → ejecuta

Flujo con IoC:
  el container → crea objetos → inyecta → tu código solo usa las entradas
```

Otros ejemplos de IoC que ya conoces:
- **Callbacks / eventos:** el framework te llama, no al revés
- **Templates method (JUnit):** JUnit controla el ciclo, tu solo defines `@Test`
- **Servlets:** el contenedor invoca `doGet()/doPost()`
- **Spring MVC:** el framework invoca tus `@Controller` methods

#### Service Locator vs DI — por qué DI gana

| Aspecto | Service Locator | Dependency Injection |
|---------|-----------------|----------------------|
| Dónde se resuelve | Dentro de la clase | Fuera, al construir |
| Testabilidad | Difícil (acopla al locator) | Fácil (inyectas mocks) |
| Visibilidad de deps | Oculta | Explícita en firma |
| Estado | Puede ser static/global | Instancia, sin global |
| Spring | Legacy (`@Autowired` field) | Recomendado (constructor) |

**Peculiaridad:** Spring soporta ambos, pero el patrón recomendado en la comunidad (y documentación oficial) es constructor injection porque hace las dependencias explícitas y el código es inmutable y testeable sin el container.

### Ejercicio práctico

1. Comparar una app Java pura con Spring Boot
2. Identificar las dependencias en ambos casos
3. Crear un diagrama de acoplamiento vs inyección

**Solución esperada:** Documento comparativo mostrando ventajas de DI.

---

## 4.2 Estructura de un proyecto Spring Boot (45 min)

### Objetivos
- Generar proyecto con Spring Initializr
- Entender la estructura de directorios
- Conocer `pom.xml` y starter packs

### Contenido teórico

#### Spring Initializr

**URL:** https://start.spring.io

Opciones recomendadas para el taller:
- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 4.x (última estable)
- **Group:** `com.taller`
- **Artifact:** `demo`
- **Packaging:** Jar
- **Java:** 17

#### Estructura de directorios

```
demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── taller/
│   │   │           └── demo/
│   │   │               └── DemoApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/          # Archivos estáticos (CSS, JS)
│   │       └── templates/       # Templates (Thymeleaf)
│   └── test/
│       └── java/
│           └── com/
│               └── taller/
│                   └── demo/
│                       └── DemoApplicationTests.java
├── pom.xml                      # Dependencias Maven
├── mvnw                         # Maven wrapper
├── mvnw.cmd                     # Maven wrapper (Windows)
└── .gitignore
```

#### `@SpringBootApplication`

```java
@SpringBootApplication  // Equivale a:
public class DemoApplication {
    // @Configuration — permite definir beans
    // @EnableAutoConfiguration — configura automáticamente
    // @ComponentScan — busca componentes en este paquete y subpaquetes
    
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

**Peculiaridad:** Un solo hacer que hace todo. `@SpringBootApplication` es un meta-annotation que combina 3 anotaciones.

#### `pom.xml` y Starter Packs

```xml
<dependencies>
    <!-- Spring Boot Starter Web — incluye Tomcat, Spring MVC, Jackson -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Boot Starter Data JPA — incluye Hibernate, Spring Data -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- H2 — base de datos en memoria -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok — reduce boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Peculiaridad:** `spring-boot-starter-web` trae: Tomcat embebido, Spring MVC, Jackson (JSON), validación, y más. Un solo `<dependency>`.

### Ejemplo parcial

```bash
# Generar proyecto con curl
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.0 \
  -d baseDir=demo \
  -d groupId=com.taller \
  -d artifactId=demo \
  -d dependencies=web,data-jpa,h2,lombok \
  -o demo.zip

# Descomprimir
unzip demo.zip
cd demo
./mvnw spring-boot:run
```

### Teoría en profundidad: arranque y autoconfiguración

#### Qué hace SpringApplication.run() paso a paso

```
SpringApplication.run(Clase, args)
├── 1. Determina el tipo de app (web reactive / servlet / none)
├── 2. Prepara SpringApplicationRunListeners
├── 3. Prepara el Environment (properties, profiles, args)
├── 4. Imprime el banner
├── 5. Crea el ApplicationContext (AnnotationConfigServletWebServerApplicationContext)
├── 6. Registra BeanFactoryPostProcessors
│     └── ConfigurationClassPostProcessor (procesa @Configuration)
├── 7. Registra BeanPostProcessors
├── 8. Ejecuta los "beans factory post processors" → procesa
│     └── @ComponentScan, @Import, @EnableAutoConfiguration
├── 9. Crea los singleton beans (perezosamente según dependencias)
├── 10. Publica ContextRefreshedEvent
├── 11. Ejecuta runners (CommandLineRunner, ApplicationRunner)
└── 12. Devuelve el contexto listo
```

#### Cómo funciona @EnableAutoConfiguration

```
@SpringBootApplication
├── @SpringBootConfiguration        (= @Configuration)
├── @EnableAutoConfiguration
│     └── importa AutoConfigurationImportSelector
│           └── carga META-INF/spring/org.springframework.boot.autoconfigure.
│               AutoConfiguration.imports  (en los JARs de los starters)
│                 ├── ...DataSourceAutoConfiguration
│                 ├── ...JpaRepositoriesAutoConfiguration
│                 ├── ...WebMvcAutoConfiguration
│                 └── ... (cientos de candidatas)
└── @ComponentScan (escanea tu paquete y subpaquetes)
```

**Peculiaridad clave:** cada clase de auto-configuración tiene condiciones `@ConditionalOn*`:

```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)          // ¿hay JAR de datasource?
@ConditionalOnMissingBean(DataSource.class)    // ¿ya definiste uno?
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
    // Spring crea un DataSource automáticamente SOLO si:
    // 1. La clase DataSource está en el classpath (dependencia presente)
    // 2. Tú no definiste tu propio DataSource
    // 3. Hay propiedades spring.datasource.* configuradas
}
```

**Resultado:** "convención sobre configuración" se implementa con condiciones. Si agregas `spring-boot-starter-data-jpa`, aparecen ~30 auto-configuraciones nuevas que se activan según tu classpath.

#### Dependencias transitivas y el classpath

```
Maven (pom.xml) → resuelve árbol de dependencias
spring-boot-starter-web
  └── spring-boot-starter
  └── spring-web
  └── spring-webmvc
  └── tomcat-embed-core     (el servidor embebido)
  └── jackson-databind      (JSON)
  └── spring-boot-starter-tomcat

Conflictos → Maven elige la versión "más cercana" al proyecto raíz.
Spring Boot BOM (bill of materials) fija versiones compatibles de todo.
```

**Peculiaridad del conflicto de versiones:** si dos librerías traen la misma clase en versiones distintas, gana la que está "más cerca" en el árbol de dependencias — a veces en silencio, causando bugs de runtime difíciles de diagnosticar. `mvn dependency:tree` es tu herramienta.

### Ejercicio práctico

1. Crear proyecto en Spring Initializr con: Web, JPA, H2, Lombok
2. Importar en IntelliJ IDEA
3. Ejecutar `./mvnw spring-boot:run`
4. Verificar que arranca en `http://localhost:8080`
5. Explorar la estructura de directorios

**Solución esperada:** Proyecto funcionando con servidor embebido arrancando.

---

## 4.3 Container y Dependency Injection (60 min)

### Objetivos
- Entender el ApplicationContext
- Usar `@Component`, `@Service`, `@Repository`, `@Controller`
- Aplicar constructor injection
- Comprender el proxying de Spring

### Contenido teórico

#### El ApplicationContext

El ApplicationContext es el **contenedor central** de Spring. Crea, almacena y gestiona todos los beans.

```java
@SpringBootApplication
public class MiApp {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MiApp.class, args);
        
        // Listar todos los beans
        String[] beans = ctx.getBeanDefinitionNames();
        for (String bean : beans) {
            System.out.println(bean);
        }
    }
}
```

```
ApplicationContext
├── BeanFactory (almacén de beans)
├── Environment (properties, profiles)
├── EventPublisher (publicar eventos)
└── ResourceLoader (cargar archivos)
```

#### Anotaciones de stereotyping

```java
// @Component — genérico, cualquier clase
@Component
public class MiComponente {
    // ...
}

// @Service — lógica de negocio
@Service
public class ServicioPedido {
    // ...
}

// @Repository — acceso a datos
@Repository
public class ProductoRepository {
    // ...
}

// @Controller — controlador web
@Controller
public class ProductoController {
    // ...
}

// @RestController — controlador REST (combina @Controller + @ResponseBody)
@RestController
public class ProductoRestController {
    // ...
}
```

**¿Para qué las diferencias?** Son **semanticamente** diferentes — Spring las usa para comportamiento especial. `@Repository` convierte excepciones JDBC en `DataAccessException`.

#### Constructor injection (la práctica recomendada)

```java
@Service
public class ServicioPedido {
    private final ProductoRepository productoRepo;
    private final Notificador notificador;
    
    // Constructor injection — INMUTABLE, TESTABLE, CLARO
    public ServicioPedido(ProductoRepository productoRepo, 
                          Notificador notificador) {
        this.productoRepo = productoRepo;
        this.notificador = notificador;
    }
}

// Si solo hay un constructor, @Autowired es opcional (Spring 4.3+)
```

**¿Por qué constructor injection?**
- **Inmutabilidad:** `final` fields
- **Testabilidad:** puedes pasar mocks al constructor
- **Claridad:** dependencias visibles en la firma
- **Seguridad:** no puede ser null después de construido

```java
// ❌ Field injection — no recomendado
@Service
public class ServicioPedido {
    @Autowired
    private ProductoRepository productoRepo;  // Private — no puede ser final
    
    // Problems:
    // - No inmutable
    // - Difícil de testear (necesitas reflection)
    // - Oculta dependencias
}

// ❌ Setter injection — solo para dependencias opcionales
@Service
public class ServicioPedido {
    private ProductoRepository productoRepo;
    
    @Autowired
    public void setProductoRepo(ProductoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }
}
```

#### Proxying de Spring

**Peculiaridad:** Cuando Spring inyecta un bean, el objeto que recibes **no es el original** — es un proxy.

```java
@Service
public class ServicioPedido {
    @Transactional
    public void crearPedido(Pedido pedido) {
        // Spring intercepta esta llamada
        // 1. Abre transacción
        // 2. Ejecuta el método real
        // 3. Cierra transacción (commit o rollback)
    }
}

// Lo que recibes:
// - Referencia: ServicioPedido$$SpringCGLIB$$0
// - No es ServicioPedido real — es un proxy
// - El proxy agrega comportamiento (transactions, security, etc.)
```

**¿Por qué importa?**
- Los `@Transactional` funcionan por proxy — si llamas internamente, no pasa por el proxy
- Los mocks en tests deben ser del tipo correcto

### Teoría en profundidad: el bean lifecycle y los proxies

#### Ciclo de vida completo de un bean

```
1. Spring lee la definición del bean (BeanDefinition)
2. Instancia: crea el objeto (constructor o factory)
3. Población de propiedades (@Autowired, setters)
4. Aware interfaces (si implementas):
     BeanNameAware, BeanFactoryAware, ApplicationContextAware, ...
5. BeanPostProcessors (antes) — ej: @PostConstruct runner, AOP proxies
6. @PostConstruct
7. Inicializadores: InitializingBean.afterPropertiesSet(), @Bean(initMethod=...)
8. BeanPostProcessors (después)
9. Bean LISTO para uso
   ──────────────────────────
10. @PreDestroy
11. DisposableBean.destroy(), @Bean(destroyMethod=...)
```

Los `BeanPostProcessor` son el mecanismo central de extensión: Spring AOP, transacciones, y el proxying se inyectan en el paso 5/8.

#### Bean scopes

| Scope | Descripción | Cómo se provee |
|-------|-------------|----------------|
| `singleton` (default) | UNA instancia por contexto | Un objeto compartido |
| `prototype` | NUEVA instancia en cada inyección | Nueva en cada getBean/inject |
| `request` | Una por petición HTTP | Scope web |
| `session` | Una por sesión HTTP | Scope web |
| `application` | Una por ServletContext | Scope web |

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CarritoCompra {
    // una instancia nueva por inyección
}

// Peculiaridad: prototype NO es gestionado tras la creación
// (no se le aplican @PreDestroy ni destroy lifecycle)
// Spring no sabe cuándo "muere" un prototype
```

#### Proxies: JDK vs CGLIB

**¿Qué es un proxy?** Un objeto que "envuelve" al bean real y agrega comportamiento antes/después de cada llamada.

```
Llamada → Proxy → interceptor (@Transactional, @Secured) → método real
              └── agregar comportamiento ──┘
```

| Aspecto | JDK Dynamic Proxy | CGLIB |
|---------|-------------------|-------|
| Base | Interfaz obligatoria | Subclase |
| Nombre de clase | `$Proxy0` | `$$EnhancerBySpringCGLIB` |
| Funciona con | Interfaces | Clases concretas |
| Desde Spring Boot | Default si hay interfaz | Default si no hay interfaz |
| Restricción | No proxies métodos públicos de clase concreta | No puede proxy métodos `final` |

**Peculiaridad crítica del autoproxy (self-invocation):**

```java
@Service
public class ServicioA {
    @Transactional
    public void metodoA() {
        metodoB();   // ← llamada INTERNA, NO pasa por el proxy
    }
    @Transactional
    public void metodoB() { ... }
}

// El contexto inyecta el PROXY, no el bean.
// Las llamadas externas → proxy → @Transactional funciona.
// Las llamadas internas (this.metodoB) → directo al bean real → SIN transacción.
```

**Soluciones:** inyectar el proxy de sí mismo (`@Lazy` self-injection), o separar en otro bean. Este es un error clásico de entrevista.

#### El ciclo de los @PostConstruct con proxies

```java
@Service
public class Servicio {
    @PostConstruct
    public void init() {
        // Se ejecuta sobre el OBJETO REAL, antes de que exista el proxy
        // que lo envuelve. Aquí los campos ya están inyectados.
    }
}
```

### Ejemplo parcial

```java
// Servicio completo con DI
@Service
public class ServicioNotificacion {
    private final List<CanalNotificacion> canales;
    
    public ServicioNotificacion(List<CanalNotificacion> canales) {
        // Spring inyecta TODAS las implementaciones de CanalNotificacion
        this.canales = canales;
    }
    
    public void notificar(String mensaje) {
        for (CanalNotificacion canal : canales) {
            canal.enviar(mensaje);
        }
    }
}

// Spring inyecta:
// - EmailNotificacion
// - SMSNotificacion
// - PushNotificacion
// Automáticamente, sin configuración explícita
```

### Ejercicio práctico

1. Crear interfaz `Notificador` con método `enviar(mensaje)`
2. Crear 3 implementaciones: `EmailNotificador`, `SMSNotificador`, `PushNotificador`
3. Crear `ServicioNotificacion` que reciba `List<Notificador>` por constructor
4. Inyectar y probar que funciona
5. Agregar logging para ver qué notificadores se usan

**Solución esperado:** Servicio que usa todos los notificadores inyectados automáticamente.

---

## 4.4 Configuración en Spring Boot (45 min)

### Objetivos
- Usar `application.properties` y `application.yml`
- Inyectar propiedades con `@Value` y `@ConfigurationProperties`
- Manejar profiles para diferentes ambientes

### Contenido teórico

#### `application.properties` vs `application.yml`

```properties
# application.properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
app.mensaje=Hola desde Spring Boot
```

```yaml
# application.yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
app:
  mensaje: Hola desde Spring Boot
```

**Peculiaridad:** Spring Boot carga propiedades en orden específico:
1. `application.properties` en `src/main/resources`
2. Variables de entorno
3. Command line arguments
4. Perfiles específicos

#### `@Value` — inyectar propiedades

```java
@Component
public class Configuracion {
    @Value("${app.mensaje}")
    private String mensaje;
    
    @Value("${server.port:8080}")  // Default value si no existe
    private int puerto;
    
    @Value("${app.timeout:30}")
    private int timeout;
    
    public void mostrarConfig() {
        System.out.println(mensaje + " en puerto " + puerto);
    }
}
```

#### `@ConfigurationProperties` — objetos de configuración

```java
@ConfigurationProperties(prefix = "app.email")
public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean ssl;
    
    // Getters y setters (o usa @Data de Lombok)
}

// application.yml
app:
  email:
    host: smtp.gmail.com
    port: 587
    username: usuario@gmail.com
    password: ${EMAIL_PASSWORD}  # Variable de entorno
    ssl: true
```

**Ventaja sobre `@Value`:** Tipado, validación, groups,Relaxed binding.

#### Profiles

```properties
# application-dev.properties (desarrollo)
spring.datasource.url=jdbc:h2:mem:devdb
spring.h2.console.enabled=true
logging.level.root=DEBUG

# application-prod.properties (producción)
spring.datasource.url=jdbc:mysql://localhost:3306/proddb
spring.h2.console.enabled=false
logging.level.root=INFO
```

```yaml
# application.yml (base)
spring:
  profiles:
    active: dev  # Default profile

---
spring:
  config:
    activate:
      on-profile: dev
server:
  port: 8080

---
spring:
  config:
    activate:
      on-profile: prod
server:
  port: 443
```

**Activar profile:**
```bash
# Command line
java -jar app.jar --spring.profiles.active=prod

# Variable de entorno
export SPRING_PROFILES_ACTIVE=prod

# En application.properties
spring.profiles.active=dev
```

### Teoría en profundidad: el Environment y la resolución de propiedades

#### Orden de precedencia de PropertySource

Spring consulta fuentes en orden de precedencia (de mayor a menor). La primera que tenga la propiedad gana:

```
1. Command line args              --server.port=8081
2. SPRING_APPLICATION_JSON        (variable de entorno JSON)
3. Java System properties         -Dserver.port=8081
4. OS environment variables      SERVER_PORT=8081
5. application-{profile}.yml     application-prod.yml
6. application-{profile}.properties
7. application.yml
8. application.properties
9. @PropertySource de @Configuration
10. Defaults del código
```

**Peculiaridad de nombrado:** las variables de entorno usan *relaxed binding*: `SPRING_DATASOURCE_URL` se mapea a `spring.datasource.url`. Es una conversión de nombres tolerante (`SERVER_PORT`, `server.port`, `serverPort`).

#### Cómo se resuelve una propiedad en runtime

```java
// @Value("${app.timeout}")
// 1. Busca "app.timeout" en el Environment (orden de precedencia)
// 2. Si no existe → usa el default tras ':' ("${app.timeout:30}")
// 3. Si no hay default → error al arrancar (si la inyección es obligatoria)
```

#### Profiles: activación y composición

```yaml
spring:
  config:
    activate:
      on-profile: dev   # YAML con --- (multi-documento)
server:
  port: 8080

---
spring:
  config:
    activate:
      on-profile: prod
server:
  port: 443
```

```java
// Activar profiles en código
@Configuration
@Profile("dev")
public class DevConfig {
    // solo se activa con el profile dev
}

// Combinación: @Profile("dev & !test") o @Profile("prod | staging")
```

**Peculiaridad:** `application-{profile}.properties` sobreescribe las del archivo base. El profile activo se elige por: `SPRING_PROFILES_ACTIVE`, `--spring.profiles.active=`, o `spring.profiles.active=`.

#### Configuración encriptada y secrets

```properties
# Nunca commits secretos en claro
spring.datasource.password=${DB_PASSWORD}   # desde variable de entorno

# O con Jasypt (jasypt-spring-boot-starter):
app.secret=ENC(encryptedValue)
```

### Ejemplo parcial

```java
@Configuration
@ConfigurationProperties(prefix = "app.database")
@Validated
public class DatabaseConfig {
    @NotBlank
    private String url;
    
    @NotBlank
    private String username;
    
    @Min(1)
    @Max(65535)
    private int port;
    
    private boolean poolEnabled = true;
    
    // Getters y setters
}

// Uso
@Service
public class ServicioDatos {
    private final DatabaseConfig config;
    
    public ServicioDatos(DatabaseConfig config) {
        this.config = config;
    }
    
    public Connection getConnection() {
        // Usar config.getUrl(), config.getPort(), etc.
    }
}
```

### Ejercicio práctico

1. Crear `app.config` con propiedades personalizadas
2. Crear clase `AppConfig` con `@ConfigurationProperties`
3. Inyectar con `@Value` en un servicio
4. Crear profiles `dev` y `prod` con diferentes configuraciones
5. Probar cambiando el profile activo

**Solución esperado:** App configurable con profiles funcionando.

---

## 4.5 Ciclo de vida y eventos (30 min)

### Objetivos
- Entender las fases de arranque de Spring
- Usar `@PostConstruct` y `@PreDestroy`
- Publicar y escuchar eventos

### Contenido teórico

#### Fases de arranque

```
1. SpringApplication.run()
   │
   ├── 2. Crear ApplicationContext
   │
   ├── 3. Registrar BeanFactoryPostProcessors
   │   (modificar beans antes de crearlos)
   │
   ├── 4. Registrar BeanPostProcessors
   │   (proxy, autowiring, etc.)
   │
   ├── 5. Crear beans (instancia + configurar)
   │
   ├── 6. @PostConstruct
   │   (post-inicialización)
   │
   ├── 7. Bean listo para usar
   │
   └── 8. Aplicación corriendo
```

#### `@PostConstruct` y `@PreDestroy`

```java
@Component
public class ServicioCache {
    private Map<String, Object> cache = new HashMap<>();
    
    @PostConstruct  // Se ejecuta DESPUÉS de crear el bean
    public void init() {
        System.out.println("Cargando cache inicial...");
        cache.put("version", "1.0");
        cache.put(" startup", System.currentTimeMillis());
    }
    
    @PreDestroy  // Se ejecuta ANTES de destruir el bean
    public void cleanup() {
        System.out.println("Limpiando cache...");
        cache.clear();
    }
    
    public Object get(String key) {
        return cache.get(key);
    }
}
```

**Peculiaridad:** `@PostConstruct` se ejecuta **después** de la inyección de dependencias. Puedes usar todas las dependencias inyectadas.

#### Application Events

```java
// 1. Crear evento
public class ProductoCreadoEvent {
    private final Producto producto;
    private final Instant timestamp;
    
    public ProductoCreadoEvent(Producto producto) {
        this.producto = producto;
        this.timestamp = Instant.now();
    }
    
    // Getters
}

// 2. Publicar evento
@Service
public class ServicioProducto {
    private final ApplicationEventPublisher publisher;
    
    public ServicioProducto(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    public Producto crear(Producto producto) {
        Producto guardado = repository.save(producto);
        publisher.publishEvent(new ProductoCreadoEvent(guardado));
        return guardado;
    }
}

// 3. Escuchar evento
@Component
public class NotificadorProducto {
    @EventListener
    public void onProductoCreado(ProductoCreadoEvent event) {
        System.out.println("Nuevo producto: " + event.getProducto().getNombre());
    }
}

// 4. Escuchar async (en otro hilo)
@Component
public class AuditoriaProducto {
    @Async
    @EventListener
    public void onProductoCreado(ProductoCreadoEvent event) {
        // Se ejecuta en hilo separado
        auditService.registrar(event);
    }
}
```

### Teoría en profundidad: el modelo de eventos de Spring

#### La jerarquía de eventos de contexto

```
ApplicationEvent (clase base)
├── ContextRefreshedEvent     — el contexto terminó de refrescarse
├── ContextStartedEvent       — se llamó start()
├── ContextStoppedEvent       — se llamó stop()
├── ContextClosedEvent        — se está cerrando
└── (tus eventos custom)

publicación:  ApplicationEventPublisher.publishEvent(event)
entrega:      @EventListener (síncrono) o @Async @EventListener (asíncrono)
```

**Peculiaridad:** por defecto `publishEvent` es **síncrono** y en el **mismo hilo**: el publisher se bloquea hasta que todos los listeners terminan. Para desacoplar el rendimiento usa `@Async` (requiere `@EnableAsync`).

#### El patrón Observer / Pub-Sub

Spring Events implementa el patrón **Observer** (o Pub-Sub con múltiples listeners):

```
Publisher                    Listeners
ServicioProducto             ┌─────────────────────┐
  │ publishEvent(ProductoCreado) │ NotificadorEmail  │  ← listener A
  └────────────────────────────→│ Auditoria         │  ← listener B
                                │ CacheInvalidator  │  ← listener C
                                └─────────────────────┘
El publisher no conoce a los listeners (bajo acoplamiento).
```

**Beneficio vs llamada directa:** el servicio de negocio no se acopla a notificaciones, auditoría o cache. Puedes agregar listeners sin tocar el dominio.

#### Orden y filtrado de listeners

```java
@Component
public class ManejadorEventos {
    @EventListener
    @Order(1)                      // controlar el orden de ejecución
    public void primero(ProductoCreado e) { ... }

    @EventListener
    @Order(2)
    public void segundo(ProductoCreado e) { ... }

    // Listener condicional: solo si la condición se cumple
    @EventListener(condition = "#event.producto.precio > 1000")
    public void soloCaros(ProductoCreado e) { ... }
}
```

#### Ventajas de eventos en arquitecturas monolíticas

```
Problema clásico:
ServicioPedido → directo → EmailService, AuditoriaService, NotificationService
  (acoplamiento + latencia en la petición)

Con eventos:
ServicioPedido → publishEvent(PedidoCreado) → return
  EmailService, Auditoria, Notification  (listeners, opcionalmente async)
  → la petición HTTP no espera a los listeners
  → menos acoplamiento, mejor rendimiento
```

**Peculiaridad de @TransactionalEventListener:** si usas `@TransactionalEventListener(phase = AFTER_COMMIT)`, el listener solo corre si la transacción del publisher hizo commit. Evita notificar con datos que luego se revierten.

### Ejemplo parcial

```java
@Component
public class Inicializador implements CommandLineRunner {
    
    private final ProductoRepository repository;
    
    public Inicializador(ProductoRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Se ejecuta al arrancar la app
        System.out.println("Cargando datos iniciales...");
        
        repository.save(new Producto("Laptop", 999.99));
        repository.save(new Producto("Mouse", 29.99));
        repository.save(new Producto("Teclado", 59.99));
        
        System.out.println("Datos cargados: " + repository.count() + " productos");
    }
}
```

### Ejercicio práctico

1. Crear `@Component` con `@PostConstruct` que imprima mensaje
2. Crear `@Component` con `@PreDestroy` que limpie recursos
3. Crear evento `ConfiguracionCambiadaEvent`
4. Publicar evento al cambiar una propiedad
5. Escuchar evento y actualizar cache

**Solución esperado:** Ciclo de vida completo con eventos funcionando.

---

## Resumen del Módulo 4

### Conceptos clave

| Concepto | Descripción |
|---|---|
| IoC | El objeto no crea sus dependencias |
| DI | Spring inyecta las dependencias automáticamente |
| `@SpringBootApplication` | Meta-annotation: `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| `@Component`/`@Service``@Repository` | Anotaciones de stereotyping |
| Constructor injection | Recomendado: inmutable, testable, claro |
| Proxying | Spring intercepta llamadas para agregar comportamiento |
| `@Value` | Inyectar propiedades individuales |
| `@ConfigurationProperties` | Objetos de configuración tipados |
| Profiles | `dev`, `prod`, `test` — diferentes configuraciones |
| `@PostConstruct` | Post-inicialización |
| `@PreDestroy` | Pre-destrucción |
| Application Events | Publicar/escuchar eventos |

### Siguiente módulo
→ [Módulo 5: APIs REST con Spring Boot](05-apis-rest.md)
