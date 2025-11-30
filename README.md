# CAPDOCS – Prototipo funcional

Este prototipo JavaFX (Java 21 + Maven) cubre todos los requisitos funcionales y no funcionales a nivel de demostración mínima. Incluye:

- RF01–RF03: Inicio de sesión con contraseñas Bcrypt y restricción por rol (ADMIN/OPERADOR)
- RF04: Gestión de usuarios (solo ADMIN)
- RF05–RF13: Crear Producción con ítems y descuento en una transacción atómica; rollback ante errores
- RF06–RF08: Selección de cliente y producto; técnicas opcionales (placeholder)
- RF09: Diseño – guarda ruta de archivo (placeholder)
- RF10: Descuento fijo
- RF14–RF18: CRUD de clientes y listado de productos
- RF16–RF17: Inventario por tallas (tabla `product_stock`) y actualización automática al crear producción
- RF19–RF20: Historial y estado de cuenta por cliente
- RF21–RF22: Placeholders para técnicas/costos (estructuras en BD y navegación)
- RF23: Dashboard con métricas básicas (pendientes, ventas hoy/mes)
- RF24–RF27: Registro de costos, rollback, navegación básica con stack (stub)

## Credenciales de acceso

Se crean automáticamente (si no existen) usuarios por defecto:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin   | pass123    | ADMIN |
| operador| pass123    | OPERADOR |

## Ejecutar

1. Requisitos: Java 21 y Maven.
2. Compilar y ejecutar:

```powershell
mvn clean javafx:run
```

Al iniciar, se crea la BD en `%USERPROFILE%/CapDocs/capdocs.db` y se asegura la existencia de los usuarios anteriores.

## Guía de uso rápida

1. Inicie la aplicación y utilice `admin / pass123` o `operador / pass123`.
2. Dashboard muestra métricas y producciones pendientes.
3. Barra lateral:
	- Clientes: CRUD completo, filtro y exportación Excel. Historial muestra saldo calculado vs registrado.
	- Producciones: Crear producción con cliente, producto, talla, cantidad, precio, técnica opcional, descuento y abono inicial. El stock se valida para evitar negativos y se descuenta automáticamente.
	- Técnicas: CRUD de técnicas con costo base; seleccionables al crear producción.
	- Ledger Costos: Visualización de entradas ABONO y DESCUENTO registradas por producción (auditoría financiera).
	- Usuarios (solo ADMIN): Alta y baja de usuarios con hashing BCrypt.
4. El botón "⟵ Atrás" permite volver a la vista anterior (stack de navegación interno).
5. El saldo pendiente del cliente se actualiza en cada producción (total - abono). Descuentos y abonos se registran en la tabla `costs`.
6. Para exportar clientes a Excel: en vista Clientes pulse "Exportar Excel".

## Módulos implementados

- Autenticación con BCrypt y roles.
- Navegación con historial (Back).
- Clientes, Usuarios, Técnicas CRUD.
- Producciones con transacción atómica y validación de stock.
- Inventario por tallas (`product_stock`).
- Ledger de costos (abonos, descuentos) para auditoría.
- Dashboard (pendientes, ventas hoy y mes, cola FIFO).

## Notas técnicas

- Si cambia el esquema, `DatabaseManager` añade columnas nuevas de forma segura.
- Las técnicas agregan `technique_id` en los ítems para futuros cálculos de costos avanzados.
- El diseño visual aplica tema oscuro profesional (`css/style.css`).

## Notas rápidas

- La contraseña se almacena con BCrypt; si existe un admin previo en texto plano, se migra en el arranque.
- Inventario por talla: tabla `product_stock`; al crear una producción se descuenta stock.
- Estado de cuenta: suma de `costo_total` menos abonos (tabla `costs` con `concepto='ABONO'`).
- Para poblar productos/clientes rápidamente, use los botones de cada vista.

## Estructura clave

- UI: `src/main/resources/fxml/*.fxml` y controladores en `com.orderlinkpos.ui`
- Persistencia: DAOs en `com.orderlinkpos.dao`
- Esquema: `src/main/resources/sql/schema.sql`

## Próximos pasos sugeridos

- CRUD completo de Técnicas y Costos
- Formularios de Producto e Inventario avanzado (tallas/stock)
- Subida de archivos de diseño y almacenamiento seguro
- Métricas adicionales y filtros
- Navegación con historial real (integrar `NavigationManager`)
