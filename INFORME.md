# Sistema de Gestión para Cadena de Comida Rápida (KFC)

**Nombre de la materia:** Programación II

**Docente:** Miguel Silva

**Integrantes del grupo:** 
- Castellano Mauricio
- Schneider Bruno
- Borrajo Santiago

**Curso y año:** [2.603] - 2025

**Título del proyecto:** Sistema de Gestión para Cadena de Comida Rápida - KFC

---

## 2. Link al Repositorio de GitHub

**Repositorio:** [https://github.com/Zasa-san/inspt-programacion2](https://github.com/Zasa-san/inspt-programacion2)

El proyecto está completo y es ejecutable siguiendo las instrucciones del README.md

---

## 3. Descripción General del Proyecto

### 3.1 Tema Elegido

Sistema de gestión integral para una cadena de comida rápida (KFC), que permite administrar productos, gestionar pedidos, controlar stock y administrar usuarios con diferentes niveles de acceso.

### 3.2 Objetivos del Sistema

- **Gestión de Productos:** Permitir la administración completa del catálogo de productos (CRUD), incluyendo nombre, descripción, precio y disponibilidad.
- **Control de Pedidos:** Facilitar la creación, seguimiento y gestión de pedidos de clientes con diferentes estados.
- **Administración de Stock:** Registrar movimientos de entrada y salida de productos, vinculados a pedidos y con motivos específicos.
- **Seguridad y Usuarios:** Implementar un sistema de autenticación y autorización basado en roles para proteger funcionalidades sensibles.
- **Interfaz de Usuario:** Proporcionar una interfaz web intuitiva y responsive para diferentes tipos de usuarios.

### 3.3 Descripción de los Actores y sus Roles

El sistema cuenta con tres roles principales de usuarios:

#### **ROLE_ADMIN (Administrador)**
- Acceso total al sistema
- Gestión completa de usuarios (crear, modificar, eliminar)
- Administración de productos (CRUD completo)
- Gestión de pedidos
- Control y visualización de stock
- Configuración del sistema

#### **ROLE_VENDEDOR (Vendedor)**
- Gestión de pedidos (visualizar y modificar estado)
- Consulta de historial de pedidos
- Sin acceso a gestión de productos ni usuarios

#### **ROLE_SOPORTE (Soporte)**
- Visualización y gestión de stock
- Registro de movimientos de inventario
- Gestión de pedidos
- Sin acceso a administración de productos ni usuarios

#### **Cliente (sin autenticación)**
- Visualización del catálogo de productos en la página principal
- Agregado de productos al carrito de compras
- Realización de pedidos (checkout)
- Sin acceso a funcionalidades administrativas

---

## 4. Arquitectura y Desarrollo

### 4.1 Descripción de las Capas del Sistema

El proyecto implementa una arquitectura en capas basada en Spring Boot, siguiendo el patrón MVC (Model-View-Controller) con una clara separación entre backend y frontend:

#### **Capa de Backend**
Ubicada en `src/main/java/inspt_programacion2_kfc/backend/`

- **Models:** Entidades JPA que representan el modelo de datos
- **Repositories:** Interfaces para acceso a datos (Spring Data JPA)
- **Services:** Lógica de negocio y operaciones del sistema
- **Exceptions:** Manejo de excepciones personalizadas

#### **Capa de Frontend**
Ubicada en `src/main/java/inspt_programacion2_kfc/frontend/`

- **Controllers:** Controladores que manejan las peticiones HTTP y renderizan vistas
- **Models:** DTOs específicos para la capa de presentación
- **Services:** Servicios auxiliares para la interfaz de usuario
- **Utils:** Utilidades como generación de metadatos de página

#### **Capa de Seguridad**
Ubicada en `src/main/java/inspt_programacion2_kfc/security/`

- **SecurityConfig:** Configuración de Spring Security
- **AppUserDetailsService:** Servicio de autenticación de usuarios

#### **Capa de Configuración**
Ubicada en `src/main/java/inspt_programacion2_kfc/config/`

- **WebConfig:** Configuración de recursos estáticos y handlers
- **DataLoaderCli:** Cargador de datos iniciales

#### **Capa de Presentación**
Ubicada en `src/main/resources/`

- **Templates Thymeleaf:** Vistas HTML con templates
- **Static Resources:** CSS (Bulma Framework), JavaScript, imágenes
- **Application Properties:** Configuración de la aplicación

### 4.2 Explicación de la Estructura del Proyecto

```
inspt-programacion2-kfc/
│
├── src/main/java/inspt_programacion2_kfc/
│   ├── backend/
│   │   ├── exceptions/          # Excepciones personalizadas
│   │   ├── models/              # Modelos de dominio
│   │   │   ├── auth/            # Modelos de autenticación
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── orders/          # Entidades de pedidos
│   │   │   ├── products/        # Entidades de productos
│   │   │   ├── stock/           # Entidades de stock
│   │   │   └── users/           # Entidades de usuarios
│   │   ├── repositories/        # Repositorios JPA
│   │   └── services/            # Servicios de negocio
│   │
│   ├── config/                  # Configuraciones generales
│   ├── frontend/                # Capa de presentación
│   │   ├── controllers/         # Controladores MVC
│   │   ├── models/              # DTOs de vista
│   │   ├── services/            # Servicios de frontend
│   │   └── utils/               # Utilidades de frontend
│   │
│   └── security/                # Configuración de seguridad
│
├── src/main/resources/
│   ├── application.properties   # Configuración de Spring Boot
│   ├── static/                  # Recursos estáticos
│   │   ├── css/                 # Estilos CSS
│   │   ├── img/                 # Imágenes
│   │   └── js/                  # JavaScript
│   └── templates/               # Plantillas Thymeleaf
│       ├── fragments/           # Fragmentos reutilizables
│       ├── pedidos/             # Vistas de pedidos
│       ├── products/            # Vistas de productos
│       ├── stock/               # Vistas de stock
│       └── users/               # Vistas de usuarios
│
├── db/                          # Scripts de base de datos
├── uploads/                     # Archivos subidos (imágenes)
└── pom.xml                      # Configuración Maven
```

### 4.3 Descripción de las Tecnologías y Frameworks Utilizados

#### **Backend**

- **Spring Boot 3.5.7:** Framework principal para el desarrollo de la aplicación
- **Spring Data JPA:** Para el mapeo objeto-relacional y acceso a datos
- **Spring Security:** Gestión de autenticación y autorización
- **Hibernate:** Implementación de JPA para persistencia
- **MySQL Connector/J:** Driver para conexión con base de datos MySQL/MariaDB
- **Lombok:** Reducción de código boilerplate con anotaciones
- **Jakarta Validation:** Validación de datos
- **Java 21:** Lenguaje de programación

#### **Frontend**

- **Thymeleaf:** Motor de plantillas para renderizado del lado del servidor
- **Bulma CSS 0.9.4:** Framework CSS moderno y responsive
- **jQuery 3.7.1:** Librería JavaScript para manipulación del DOM
- **FontAwesome:** Iconos vectoriales

#### **Servidor de Aplicaciones**

- **Jetty:** Contenedor de servlets embebido (alternativa a Tomcat)

#### **Base de Datos**

- **MariaDB 10.4.32+:** Sistema de gestión de bases de datos relacional

#### **Herramientas de Construcción**

- **Maven:** Gestión de dependencias y construcción del proyecto
- **Maven Wrapper:** Ejecuta Maven sin instalación global

### 4.4 Explicación de las Clases Principales y sus Responsabilidades

#### **Backend - Modelos**

**User.java**
```java
@Entity
@Table(name = "users")
public class User implements UserDetails
```
- Representa un usuario del sistema
- Implementa `UserDetails` de Spring Security para integración con autenticación
- Campos principales: `id`, `username`, `password`, `enabled`, `role`
- Relación con `Role` enum que define los roles del sistema
- Responsabilidad: Almacenar credenciales y datos de usuario

**ProductoEntity.java**
```java
@Entity
@Table(name = "productos")
public class ProductoEntity
```
- Representa un producto del catálogo
- Campos: `id`, `name`, `description`, `price` (en centavos), `imgUrl`, `available`
- Responsabilidad: Almacenar información de productos disponibles para venta

**Pedido.java**
```java
@Entity
@Table(name = "pedidos")
public class Pedido
```
- Representa un pedido realizado en el sistema
- Campos: `id`, `createdAt`, `estado`, `total`, `customerName`, `creadoPor`
- Relación OneToMany con `ItemPedido`
- Relación ManyToOne con `User` (creador del pedido)
- Responsabilidad: Gestionar información de pedidos y su estado

**ItemPedido.java**
- Representa un item dentro de un pedido
- Relación ManyToOne con `Pedido` y `ProductoEntity`
- Campos: `id`, `cantidad`, `precioUnitario`, `subtotal`
- Responsabilidad: Almacenar detalles de cada producto en un pedido

**MovimientoStock.java**
```java
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock
```
- Representa un movimiento de inventario
- Campos: `id`, `producto`, `tipo`, `cantidad`, `fecha`, `motivo`, `pedidoId`
- Relación ManyToOne con `ProductoEntity`
- Responsabilidad: Registrar entradas y salidas de stock

**Role.java**
```java
public enum Role {
    ROLE_SOPORTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN;
}
```
- Enum que define los roles disponibles en el sistema
- Métodos auxiliares para integración con Spring Security
- Responsabilidad: Definir niveles de acceso

#### **Backend - Servicios**

**ProductoService**
- CRUD completo de productos
- Validaciones de negocio
- Gestión de imágenes asociadas
- Responsabilidad: Lógica de negocio para productos

**PedidoService**
- Creación y gestión de pedidos
- Cambio de estados de pedidos
- Cálculo de totales
- Integración con gestión de stock
- Responsabilidad: Lógica de negocio para pedidos

**UserService**
- CRUD de usuarios
- Encriptación de contraseñas
- Validación de datos de usuario
- Responsabilidad: Gestión de usuarios del sistema

**StockService**
- Registro de movimientos de stock
- Consulta de inventario
- Responsabilidad: Control de inventario

**FileUploadService**
- Gestión de archivos subidos (imágenes de productos)
- Almacenamiento en sistema de archivos
- Eliminación de archivos
- Responsabilidad: Manejo de archivos multimedia

#### **Security**

**SecurityConfig.java**
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig
```
- Configuración de Spring Security
- Define reglas de autorización por URL
- Configuración de login y logout
- Manejo de acceso denegado
- Responsabilidad: Seguridad de la aplicación

Reglas principales:
- `/`, `/index`, `/login`, `/cart/**`, `/checkout/**`: Acceso público
- `/users/**`: Usuarios autenticados
- `/products/**`: Solo ADMIN
- `/stock/**`: ADMIN y SOPORTE
- `/pedidos/**`: ADMIN, VENDEDOR y SOPORTE

**AppUserDetailsService.java**
- Implementa `UserDetailsService` de Spring Security
- Carga usuarios desde la base de datos para autenticación
- Responsabilidad: Integración de usuarios con Spring Security

#### **Frontend - Controllers**

**ProductsPageController.java**
- Maneja todas las operaciones de la interfaz de productos
- Endpoints: listar, crear, editar, eliminar productos
- Gestión de imágenes de productos
- Responsabilidad: Controlador de vistas de productos

**CheckoutController.java**
- Gestión del proceso de checkout
- Creación de pedidos desde el carrito
- Integración con sesión HTTP para carrito
- Responsabilidad: Proceso de compra

**PedidosPageController.java**
- Visualización de pedidos
- Cambio de estados de pedidos
- Filtrado y búsqueda
- Responsabilidad: Gestión de interfaz de pedidos

**CartController.java**
- Gestión del carrito de compras en sesión
- Añadir, actualizar, eliminar items
- Responsabilidad: Carrito de compras en sesión

**UsersPageController.java**
- CRUD de usuarios desde interfaz web
- Responsabilidad: Administración de usuarios

**StockPageController.java**
- Visualización de movimientos de stock
- Registro de nuevos movimientos
- Responsabilidad: Interfaz de gestión de inventario

### 4.5 Capturas y Ejemplos de Código Relevantes

#### Ejemplo 1: Configuración de Seguridad

```java
@Bean
public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index", "/login", "/access-denied", 
                "/css/**", "/js/**", "/img/**", "/uploads/**", 
                "/favicon.ico", "/cart/**", "/checkout/**").permitAll()
            .requestMatchers("/users/**").authenticated()
            .requestMatchers("/products/**").hasRole(Role.ROLE_ADMIN.getRoleName())
            .requestMatchers("/stock/**").hasAnyRole(
                Role.ROLE_ADMIN.getRoleName(), 
                Role.ROLE_SOPORTE.getRoleName())
            .requestMatchers("/pedidos/**").hasAnyRole(
                Role.ROLE_ADMIN.getRoleName(), 
                Role.ROLE_VENDEDOR.getRoleName(), 
                Role.ROLE_SOPORTE.getRoleName())
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            .accessDeniedHandler(new AccessDeniedHandlerImpl() {
                {
                    setErrorPage("/access-denied");
                }
            })
        )
        .logout(withDefaults());

    return http.build();
}
```

#### Ejemplo 2: Modelo de Entidad con Relaciones

```java
@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.CREADO;

    @Column(nullable = false)
    private int total;

    @Column(nullable = true)
    private String customerName;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creadoPor;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }
}
```

#### Ejemplo 3: Configuración de Base de Datos

```properties
# application.properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:inspt_programacion2_kfc}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.upload-dir=uploads
spring.servlet.multipart.max-file-size=15MB
spring.servlet.multipart.max-request-size=15MB
```

---

## 5. Casos de Uso y Diagramas

### 5.1 Diagrama de Casos de Uso

```
                          Sistema KFC
    ┌─────────────────────────────────────────────────────┐
    │                                                     │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Productos (CRUD)         │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Usuarios (CRUD)          │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │               [ADMIN]────────────────┐              │
    │                    │                 │              │
    │                    │                 │              │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Gestión de Stock                    │◄──────┤
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │               [SOPORTE]                             │
    │                                                     │
    │  ┌─────────────────────────────────────────┐       │
    │  │   Gestión de Pedidos (Ver y Modificar)  │       │
    │  └─────────────────────────────────────────┘       │
    │              ▲           ▲           ▲              │
    │              │           │           │              │
    │         [ADMIN]     [VENDEDOR]  [SOPORTE]          │
    │                                                     │
    │                                                     │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Ver Catálogo de Productos           │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Agregar Productos al Carrito        │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │  ┌─────────────────────────────────────────┐       │
    │  │     Realizar Pedido (Checkout)          │       │
    │  └─────────────────────────────────────────┘       │
    │                    ▲                                │
    │                    │                                │
    │    [CLIENTE sin autenticación]                      │
    │                                                     │
    └─────────────────────────────────────────────────────┘
```

### 5.2 Descripción de Casos de Uso

#### **CU-01: Gestión de Productos (ADMIN)**
- **Actor:** Administrador
- **Descripción:** El administrador puede crear, leer, actualizar y eliminar productos del catálogo
- **Precondiciones:** Usuario autenticado con rol ADMIN
- **Flujo Principal:**
  1. El administrador accede a `/products`
  2. Visualiza el listado de productos existentes
  3. Puede crear nuevo producto con nombre, descripción, precio e imagen
  4. Puede editar productos existentes
  5. Puede eliminar productos
  6. Puede marcar productos como disponibles/no disponibles

#### **CU-02: Gestión de Usuarios (ADMIN)**
- **Actor:** Administrador
- **Descripción:** El administrador gestiona usuarios del sistema
- **Precondiciones:** Usuario autenticado con rol ADMIN
- **Flujo Principal:**
  1. Accede a `/users`
  2. Visualiza lista de usuarios
  3. Crea nuevos usuarios asignando username, password y rol
  4. Edita usuarios existentes
  5. Elimina usuarios
  6. Habilita/deshabilita usuarios

#### **CU-03: Ver Catálogo (CLIENTE)**
- **Actor:** Cliente (no autenticado)
- **Descripción:** Visualización del catálogo de productos disponibles
- **Precondiciones:** Ninguna
- **Flujo Principal:**
  1. El cliente accede a la página principal `/`
  2. Visualiza productos disponibles con imagen, nombre, descripción y precio
  3. Puede ver detalles de cada producto

#### **CU-04: Agregar al Carrito (CLIENTE)**
- **Actor:** Cliente (no autenticado)
- **Descripción:** El cliente agrega productos al carrito de compras
- **Precondiciones:** Ninguna
- **Flujo Principal:**
  1. El cliente visualiza el catálogo
  2. Selecciona cantidad deseada de un producto
  3. Hace clic en "Agregar al carrito"
  4. El producto se añade a la sesión del carrito
  5. Puede modificar cantidades o eliminar items del carrito

#### **CU-05: Realizar Pedido - Checkout (CLIENTE)**
- **Actor:** Cliente (no autenticado) o Usuario autenticado
- **Descripción:** Completar la compra de los productos en el carrito
- **Precondiciones:** Tener al menos un item en el carrito
- **Flujo Principal:**
  1. El cliente accede a `/checkout`
  2. Revisa los productos y el total
  3. Selecciona método de pago:
     - "Pagar en caja": Registra pedido en estado CREADO
     - "Pagar ahora": Ingresa datos de tarjeta (demo) y registra pedido en estado PAGADO
  4. Confirma el pedido
  5. El sistema crea el pedido con el estado correspondiente
  6. Se registran movimientos de stock (salida) por cada producto
  7. El carrito se vacía

#### **CU-06: Gestión de Pedidos (VENDEDOR, SOPORTE, ADMIN)**
- **Actor:** Vendedor, Soporte, Administrador
- **Descripción:** Visualización y gestión de pedidos del sistema
- **Precondiciones:** Usuario autenticado con rol autorizado
- **Flujo Principal:**
  1. El usuario accede a `/pedidos`
  2. Visualiza lista de todos los pedidos
  3. Puede ver detalles de cada pedido (items, total, cliente, fecha)
  4. Puede cambiar el estado del pedido (CREADO → PAGADO → ENTREGADO o CANCELADO)

#### **CU-07: Gestión de Stock (SOPORTE, ADMIN)**
- **Actor:** Soporte, Administrador
- **Descripción:** Visualización y registro de movimientos de inventario
- **Precondiciones:** Usuario autenticado con rol SOPORTE o ADMIN
- **Flujo Principal:**
  1. El usuario accede a `/stock`
  2. Visualiza historial de movimientos de stock
  3. Puede registrar nuevos movimientos (ENTRADA o SALIDA)
  4. Especifica producto, tipo, cantidad y motivo
  5. El sistema registra el movimiento con fecha actual

#### **CU-08: Autenticación (TODOS LOS USUARIOS)**
- **Actor:** Usuario registrado
- **Descripción:** Login en el sistema
- **Precondiciones:** Tener credenciales válidas
- **Flujo Principal:**
  1. El usuario accede a `/login`
  2. Ingresa username y password
  3. El sistema valida las credenciales
  4. Si son válidas, establece sesión autenticada
  5. Redirige a página principal
  6. El usuario tiene acceso a funcionalidades según su rol

---

## 6. Base de Datos

### 6.1 Diagrama Entidad-Relación

```
┌─────────────────────┐
│       USERS         │
├─────────────────────┤
│ PK  id             │
│     username        │◄──────────┐
│     password        │           │
│     enabled         │           │
│     role            │           │
└─────────────────────┘           │
                                  │
                                  │ user_id (FK)
                                  │
┌─────────────────────┐    ┌─────────────────────┐
│     PRODUCTOS       │    │      PEDIDOS        │
├─────────────────────┤    ├─────────────────────┤
│ PK  id             │    │ PK  id             │
│     name            │    │     created_at      │
│     description     │    │     estado          │
│     price           │    │     total           │
│     img_url         │    │     customer_name   │
│     available       │    │ FK  user_id         │
└─────────────────────┘    └─────────────────────┘
         │                           │
         │                           │
         │ producto_id (FK)          │ pedido_id (FK)
         │                           │
         │         ┌─────────────────────────┐
         └────────►│    ITEM_PEDIDO          │◄────┘
                   ├─────────────────────────┤
                   │ PK  id                 │
                   │ FK  pedido_id          │
                   │ FK  producto_id        │
                   │     cantidad            │
                   │     precio_unitario     │
                   │     subtotal            │
                   └─────────────────────────┘
                   
         ┌─────────────────────────┐
         │  MOVIMIENTOS_STOCK      │
         ├─────────────────────────┤
         │ PK  id                 │
         │ FK  producto_id        │
         │     tipo                │
         │     cantidad            │
         │     fecha               │
         │     motivo              │
         │     pedido_id           │
         └─────────────────────────┘
                   ▲
                   │
                   │ producto_id (FK)
                   │
         ┌─────────┘
         │
┌─────────────────────┐
│     PRODUCTOS       │
└─────────────────────┘
```

### 6.2 Descripción de las Tablas y Relaciones

#### **Tabla: USERS**
- **Descripción:** Almacena información de usuarios del sistema
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `username` (VARCHAR, UNIQUE): Nombre de usuario para login
  - `password` (VARCHAR): Contraseña encriptada con BCrypt
  - `enabled` (BOOLEAN): Si el usuario está activo
  - `role` (VARCHAR): Rol del usuario (ROLE_ADMIN, ROLE_VENDEDOR, ROLE_SOPORTE)
- **Relaciones:**
  - Uno a muchos con PEDIDOS (un usuario puede crear múltiples pedidos)

#### **Tabla: PRODUCTOS**
- **Descripción:** Catálogo de productos disponibles para venta
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `name` (VARCHAR): Nombre del producto
  - `description` (TEXT): Descripción detallada
  - `price` (INT): Precio en centavos (ej: 5500 = $55.00)
  - `img_url` (VARCHAR): Ruta de la imagen del producto
  - `available` (BOOLEAN): Si el producto está disponible para venta
- **Relaciones:**
  - Uno a muchos con ITEM_PEDIDO
  - Uno a muchos con MOVIMIENTOS_STOCK

#### **Tabla: PEDIDOS**
- **Descripción:** Registro de pedidos realizados en el sistema
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `created_at` (TIMESTAMP): Fecha y hora de creación
  - `estado` (VARCHAR): Estado actual (CREADO, PAGADO, ENTREGADO, CANCELADO)
  - `total` (INT): Total del pedido en centavos
  - `customer_name` (VARCHAR): Nombre del cliente
  - `user_id` (BIGINT, FK): Usuario que creó el pedido (puede ser NULL si es cliente no autenticado)
- **Relaciones:**
  - Muchos a uno con USERS (creador del pedido)
  - Uno a muchos con ITEM_PEDIDO (items del pedido)

#### **Tabla: ITEM_PEDIDO**
- **Descripción:** Detalle de productos incluidos en cada pedido
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `pedido_id` (BIGINT, FK): Referencia al pedido
  - `producto_id` (BIGINT, FK): Referencia al producto
  - `cantidad` (INT): Cantidad del producto
  - `precio_unitario` (INT): Precio unitario al momento de la compra (en centavos)
  - `subtotal` (INT): Subtotal (cantidad × precio_unitario)
- **Relaciones:**
  - Muchos a uno con PEDIDOS
  - Muchos a uno con PRODUCTOS

#### **Tabla: MOVIMIENTOS_STOCK**
- **Descripción:** Registro de entradas y salidas de inventario
- **Campos:**
  - `id` (BIGINT, PK): Identificador único
  - `producto_id` (BIGINT, FK): Producto afectado
  - `tipo` (VARCHAR): Tipo de movimiento (ENTRADA, SALIDA)
  - `cantidad` (INT): Cantidad del movimiento
  - `fecha` (TIMESTAMP): Fecha y hora del movimiento
  - `motivo` (VARCHAR): Descripción del motivo
  - `pedido_id` (BIGINT, nullable): Referencia al pedido si el movimiento es por venta
- **Relaciones:**
  - Muchos a uno con PRODUCTOS

### 6.3 Ejemplo de Datos Cargados

#### Usuarios por defecto:
```sql
INSERT INTO users (username, password, enabled, role) VALUES
('admin', '$2a$10$...', 1, 'ROLE_ADMIN'),
('vendedor1', '$2a$10$...', 1, 'ROLE_VENDEDOR'),
('soporte1', '$2a$10$...', 1, 'ROLE_SOPORTE');
```

#### Productos de ejemplo:
```sql
INSERT INTO productos (name, description, price, img_url, available) VALUES
('Combo Clásico', 'Pollo frito con papas y bebida', 1200, '/uploads/products/combo-clasico.jpg', 1),
('Alitas Picantes', '8 alitas con salsa BBQ', 850, '/uploads/products/alitas.jpg', 1),
('Hamburguesa XXL', 'Hamburguesa doble con queso', 950, '/uploads/products/hamburguesa.jpg', 1);
```
---

## 7. Manual de Usuario

### 7.1 Acceso al Sistema

**URL:** `http://localhost:8080`

**Credenciales por defecto:**
- Admin: `admin` / `admin`
- Vendedor: `vendedor1` / `vendedor123`
- Soporte: `soporte1` / `soporte123`

### 7.2 Flujo para Clientes sin Autenticación

#### Realizar un Pedido

1. **Ver catálogo:** Acceder a la página principal y visualizar productos disponibles con imagen, nombre, descripción y precio
2. **Agregar al carrito:** Seleccionar cantidad y hacer clic en "Agregar al Carrito"
3. **Gestionar carrito:** En `/cart` visualizar items, modificar cantidades o eliminar productos
4. **Checkout:** En `/checkout` revisar el resumen y seleccionar método de pago:
   - **Pagar en caja:** Registra pedido en estado CREADO para pagar al retirar
   - **Pagar ahora:** Ingresar datos de tarjeta (demo) y registra pedido en estado PAGADO
5. **Confirmación:** El sistema registra el pedido y vacía el carrito

### 7.3 Flujos para Usuarios Autenticados

Los usuarios autenticados acceden a funcionalidades según sus permisos:

#### 7.3.1 Gestión de Usuarios (ADMIN)
**Ruta:** `/users`

- **Listar:** Visualizar tabla con username, rol y estado
- **Crear:** Completar formulario con username, password, rol (ADMIN/VENDEDOR/SOPORTE) y estado
- **Editar:** Modificar datos de usuario existente (password opcional)
- **Eliminar:** Confirmar eliminación del usuario

#### 7.3.2 Gestión de Productos (ADMIN)
**Ruta:** `/products`

- **Listar:** Visualizar tabla con imagen, nombre, descripción, precio y disponibilidad
- **Crear:** Completar formulario con nombre, descripción, precio, imagen (JPG/PNG, máx 15MB) y disponibilidad
- **Editar:** Modificar datos del producto y opcionalmente cambiar imagen
- **Eliminar:** Confirmar eliminación permanente del producto

#### 7.3.3 Gestión de Pedidos (ADMIN, VENDEDOR, SOPORTE)
**Ruta:** `/pedidos`

- **Listar:** Visualizar todos los pedidos con número, fecha, cliente, total y estado
- **Ver detalles:** Acceder a items del pedido con cantidades y precios
- **Cambiar estado:** Actualizar estado del pedido (CREADO → PAGADO → ENTREGADO o CANCELADO)

#### 7.3.4 Gestión de Stock (ADMIN, SOPORTE)
**Ruta:** `/stock`

- **Listar movimientos:** Visualizar historial con producto, tipo (ENTRADA/SALIDA), cantidad, fecha, motivo y pedido relacionado
- **Registrar movimiento:** Seleccionar producto, tipo, ingresar cantidad y motivo
- **Consultar inventario:** Ver balance actual por producto (entradas menos salidas)

---

## 8. Conclusiones

### 8.1 Objetivos Alcanzados

El sistema desarrollado cumple con los objetivos planteados:

✅ **Sistema completo de gestión** para una cadena de comida rápida
✅ **Arquitectura en capas** bien definida con separación de responsabilidades
✅ **Seguridad robusta** implementada con Spring Security
✅ **Gestión de roles** con permisos diferenciados
✅ **Interfaz responsive** y moderna utilizando Bulma CSS
✅ **Base de datos relacional** con integridad referencial
✅ **Funcionalidades CRUD completas** para todas las entidades principales
✅ **Sistema de carrito de compras** funcional en sesión HTTP
✅ **Control de inventario** con trazabilidad de movimientos

### 8.2 Tecnologías Implementadas

- ✅ Java 21 con Spring Boot 3.5.7
- ✅ Spring Data JPA con Hibernate
- ✅ Spring Security con autenticación basada en formularios
- ✅ Thymeleaf como motor de plantillas
- ✅ MySQL/MariaDB como base de datos
- ✅ Maven para gestión de dependencias
- ✅ Jetty como servidor de aplicaciones embebido
- ✅ Bulma CSS para diseño responsive
- ✅ Lombok para reducción de código boilerplate