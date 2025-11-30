# CAPDEPOT (Antigravity) – Especificación Integral Adaptada a la Rúbrica Semestral

## 1. Título del Proyecto
CAPDEPOT: Sistema local integrado para gestión de Inventario, Ventas (POS), Costos, Producción Ligera y Repositorio de Documentos.

## 2. Identificación del Problema / Necesidad (PIN)
Pequeñas y medianas unidades de confección / comercialización carecen de una solución local unificada que:
- Controle inventario en tiempo real (insumos y productos) incluyendo mermas.
- Gestione punto de venta (efectivo vs tarjeta) con cierre diario y trazabilidad.
- Calcule costos detallados (materiales, mano de obra, técnicas) y márgenes.
- Registre producción básica por etapas y eficiencia.
- Centralice documentación (diseños, facturas, fichas técnicas) con subida/bajada controlada.
- Genere indicadores para decisiones rápidas sin depender de soluciones complejas externas.

## 3. Alcance (Estructura de datos, listas, pilas, colas según rúbrica)
El sistema operará completamente de forma local, manteniendo:
- Persistencia estructurada en almacenamiento local (archivos estructurados y/o base de datos local embebida).  
- Estructuras de datos internas para procesamiento: listas ordenadas (inventario), colas (procesamiento de órdenes), pilas (deshacer acciones críticas limitadas), árboles o índices para búsqueda acelerada.  
- Módulos: Inventario, Ventas/POS (Carrito/Caja, Descuentos/Cupones), Costos & Ledger, Producción, Clientes, Técnicas, Archivo/Documentos, Auditoría, Dashboard analítico, Configuración, Seguridad.

## 4. Objetivo General
Desarrollar un sistema local robusto y modular que integre inventario, ventas, costos, producción y documentación, optimizando la toma de decisiones y mejorando la eficiencia operativa.

## 5. Objetivos Específicos
1. Implementar control de inventario con movimientos atómicos y alertas de mínimos.  
2. Registrar transacciones de venta (efectivo/tarjeta) con cálculo de impuestos y descuentos.  
3. Calcular costos unitarios y márgenes por producto, técnica y pedido con ledger histórico versionado.  
4. Orquestar flujo de producción por etapas con tiempos estimados vs reales.  
5. Gestionar repositorio de archivos (subir/bajar) para diseños, facturas y fichas técnicas con control de permisos.  
6. Proporcionar panel de indicadores (rotación, merma, ventas, productividad, uso de técnicas).  
7. Garantizar auditoría integral de acciones críticas y accesos.  
8. Asegurar interfaz moderna, accesible, con validación inmediata y navegación consistente.  
9. Diseñar estrategia de pruebas multi-nivel que garantice confiabilidad y mantenibilidad.  
10. Documentar trazabilidad entre requerimientos, casos de uso y pruebas para la rúbrica académica.

## 6. Actores y Roles
- Administrador: Configuración, roles, auditoría, backups.  
- Cajero/Ventas: Procesar transacciones, aplicar descuentos, cierre diario.  
- Inventario: Registrar entradas/salidas, ajustes, mermas.  
- Producción: Actualizar etapas, tiempos reales, marcar finalización.  
- Analista de Costos: Recalcular fórmulas, simular escenarios.  
- Usuario Comercial: Gestor de clientes y pedidos personalizados.  
- Auditor/Supervisor: Revisar historial y consistencia.  

## 7. Requerimientos Funcionales Detallados
### 7.1 Inventario
- Crear/editar ítems con categoría, unidad, costo base, stock inicial.
- Movimientos: entrada (compra), salida (producción/venta), ajuste (merma, corrección), todos transaccionales y auditados.
- Umbrales mínimos con generación de alertas y recomendación de reposición.
- Registro de merma por causa (defecto, desperdicio, error), impacto en costos.

### 7.2 Ventas / POS
- Carrito: agregar productos, aplicar cupones, validar stock, totalizar impuestos.
- Pagos: efectivo vs tarjeta (simulación de comisiones), control de cambio.
- Emisión de comprobante local imprimible y archivo digital asociado.
- Cierre diario: consolidación de ventas, resumen de métodos de pago, discrepancias.
- Cupones/descuentos configurables por tipo (porcentaje, monto fijo, combo).  

### 7.3 Clientes & Pedidos
- Alta/edición de clientes (datos mínimos necesarios).  
- Pedidos personalizados con lista de items y técnicas aplicadas.  
- Estado pedido: nuevo, preparado, en producción, listo, entregado, anulado.  
- Histórico de pedidos y búsquedas por rango temporal, cliente, técnica.

### 7.4 Técnicas
- Registro de técnicas con parámetros (tiempo base, costo adicional, requerimientos).  
- Asociación a personalizaciones; impacto en coste unitario y productividad.  
- Estadísticas de uso (frecuencia, margen promedio generado).

### 7.5 Producción
- Orden de producción vinculada a pedido o lote interno.
- Etapas configurables: preparación, técnica, control calidad, finalización.
- Registro de tiempos estimados vs reales; cálculo de desviación.
- Pausa/reanudación con causa documentada.

### 7.6 Costos & Ledger
- Costos directos (materiales) e indirectos (energía, mano de obra estimada, desgaste).  
- Fórmulas versionadas (cada cambio genera entrada en ledger).  
- Cálculo de precio sugerido basado en margen objetivo y coste actual.
- Simulador: variación de insumo clave → impacto en margen global y precio.

### 7.7 Archivo / Documentos
- Subir: diseños, facturas, fichas técnicas, reportes PDF/CSV.  
- Bajar: acceso controlado según rol; registro de descargas (auditoría).  
- Etiquetado y metadatos (tipo, fecha, referencia a pedido/técnica).  
- Versionado simple (última vs histórica).  

### 7.8 Dashboard & Reportes
- KPIs: rotación inventario, merma %, margen medio, ventas día, ventas vs objetivo, productividad (tiempo estimado vs real), uso técnicas.  
- Reportes exportables filtrables (inventario, ventas, producción, costos, auditoría).  
- Visualizaciones dinámicas (barras, líneas, proporciones) con agregaciones locales.

### 7.9 Seguridad & Roles
- Inicio de sesión con credenciales cifradas; política de complejidad configurable.  
- Gestión granular de permisos por módulo y acción (ver, crear, editar, eliminar, descargar archivo).  
- Bloqueo tras intentos fallidos consecutivos; desbloqueo manual.  

### 7.10 Auditoría
- Eventos: login, logout, creación/edición/eliminación de entidad, movimientos inventario, transacciones venta, cambios de fórmula de costo, subida/bajada de archivo.  
- Nivel de severidad (info, warning, critical).  
- Filtros por usuario, rango temporal, módulo.

### 7.11 Configuración
- Parámetros: impuesto estándar, comisión tarjeta, moneda, margen objetivo, rutas de almacenamiento local, política de backups.  
- Backups manuales y programados; verificación de integridad.

### 7.12 Notificaciones / Alertas
- Alerta stock bajo, margen deteriorado, alta merma, desviación tiempo producción > umbral.
- Centro de alertas con priorización.

### 7.13 Funcionalidades de Valor Agregado (sin salir del contexto)
- Simulación de cambio de coste insumo clave (impacto en precios/márgenes).  
- Seguimiento de mermas con causa raíz y recomendación de acción correctiva.  
- Sugerencia dinámica de precio mínimo rentable según margen objetivo.  
- Indicador de dispersión de tiempos de producción por etapa (eficiencia).  
- Repositorio documental con búsqueda por metadatos y vista previa rápida.  
- Mini panel “salud financiera” (tendencia márgenes últimos N días).  
- Registro de carga de trabajo por usuario (producción y ventas).  

## 8. Requerimientos No Funcionales
1. Rendimiento: operaciones CRUD < 300 ms en dataset medio; reportes con agregaciones optimizadas vía precálculos y estructuras indexadas.  
2. Usabilidad: navegación consistente (sidebar + barra superior), mensajes claros, estados vacíos ilustrativos.  
3. Accesibilidad: enfoque visible, contraste adecuado, soporte teclado, etiquetas en iconos.  
4. Seguridad: hashing robusto, sanitización entradas, control de autorización centralizado.  
5. Confiabilidad: transacciones para ventas, movimientos inventario y ledger; recuperación ante fallo parcial.  
6. Escalabilidad local: separación por capas posibilitando futura externalización de persistencia.  
7. Mantenibilidad: módulos independientes con contratos (interfaces) y pruebas unitarias asociadas.  
8. Portabilidad: ejecución en múltiples sistemas locales sin dependencias externas pesadas.  
9. Integridad: validaciones estrictas (no stock negativo, márgenes recalculados tras cambio costo).  
10. Observabilidad: logs estructurados y métricas internas (latencia promedio, errores por módulo).  
11. Privacidad: mínimo dato personal cliente, ocultamiento parcial en reportes generales.  
12. Resiliencia: reintentos controlados en operaciones críticas (escritura ledger, backup).  

## 9. Arquitectura Lógica
Capas: Presentación, Aplicación (Servicios), Dominio (Entidades y Reglas), Persistencia (Repositorios), Infraestructura (configuración, almacenamiento, archivos), Seguridad, Auditoría, Integración Documental.

Flujos clave:  
Venta → Servicio Ventas → Validación Inventario → Cálculo Costos/Margen → Persistencia Transacción → Ledger (si aplica) → Auditoría → Actualización Dashboard.

Producción → Servicio Producción → Validaciones Dominio (etapas, tiempos) → Persistencia → Cálculo Desviación → Alertas → Auditoría.

Archivos → Servicio Documentos → Verificación Permisos → Almacenamiento local → Registro Auditoría.

## 10. Arquitectura Física Local
Componentes: Ejecutable principal, almacenamiento de datos estructurados, directorio de archivos (repositorio documental), carpeta logs rotativos, módulo backup (exportación snapshot), configuraciones parametrizadas. Separación clara entre datos estructurados y binarios.

## 11. Modelo de Datos (Conceptual Resumido)
Entidades: Usuario, Rol, Permiso, Cliente, Pedido, ItemPedido, Producto, MovimientoInventario, Tecnica, OrdenProduccion, EtapaProduccion, Costo, FormulaCostoVersion, LedgerCostos, TransaccionVenta, Cupon, ArchivoDocumento, AuditoriaEvento, AlertaSistema.

Relaciones:
- Usuario – Rol (N:M)  
- Pedido – Cliente (N:1)  
- ItemPedido – Pedido (N:1), ItemPedido – Producto (N:1), ItemPedido – Tecnica (opcional)  
- MovimientoInventario – Producto (N:1)  
- OrdenProduccion – Pedido (1:1 o 1:N según modalidad)  
- EtapaProduccion – OrdenProduccion (N:1)  
- TransaccionVenta – Usuario (N:1), TransaccionVenta – Pedido (opcional si venta directa)  
- ArchivoDocumento – Pedido/Tecnica/TransaccionVenta (polimórfico referencial)  
- LedgerCostos – Producto / Tecnica / FormulaCostoVersion (referencias)  
- AlertaSistema – Usuario (ack por usuario responsable)  

Integridad: restricciones para evitar stock negativo, fórmulas inactivas usadas, etapas fuera de orden, cupon expirado.

## 12. Diseño UX/UI
Pantallas: Login, Dashboard (tarjetas KPIs + alertas), Inventario (tabla + detalle + movimientos), Ventas (carrito + checkout + cierre diario), Clientes, Pedidos (cronología + items), Producción (vista en progreso, etapas), Técnicas, Costos & Ledger, Documentos (explorador + filtros), Auditoría (filtros), Configuración, Reportes.
Principios: Minimalismo, jerarquía visual clara, iconografía semántica, feedback inmediato, modo claro/oscuro, resaltado de acciones primarias, accesibilidad básica.

## 13. Seguridad y Privacidad
- Autenticación local segura (hash + salt).  
- Políticas: expiración sesión inactiva, bloqueo tras intentos fallidos.  
- Control de acceso centralizado por acción y módulo.  
- Auditoría inmutable (solo append).  
- Gestión de archivos con validación tipo y tamaño; prevención sobreescritura accidental (versionado).  
- Minimización datos cliente (solo nombre, contacto esencial).  

## 14. Estrategia de Pruebas
1. Unitarias: reglas de costos, validaciones stock, cálculo margen, transición etapa producción.  
2. Integración: flujo pedido → producción → venta → ledger.  
3. UI/interacción: formularios, mensajes error, accesibilidad básica.  
4. Rendimiento: generación reportes sobre dataset medio, simulador costo.  
5. Regresión: suite antes de versionado.  
6. Seguridad: pruebas negativas (descarga archivo sin permiso, cupon expirado).  
7. Datos: transacciones y consistencia tras fallo simulado.  

## 15. Mantenimiento y Evolución
- Versionado semántico (X.Y.Z).  
- Changelog por release.  
- Backups: política rota diaria/semana.  
- Refactor periódico (limpieza módulos, revisión duplicaciones).  
- Documentación viva: actualizar casos de uso y modelo datos con cambios relevantes.  
- Métricas para priorizar mejoras (features menos usadas, cuellos de botella).  

## 16. Mapeo a Rúbrica Semestral
| Rúbrica | Sección | Notas |
|---------|---------|-------|
| Título Proyecto | 1 | Claro y descriptivo |
| Problema / PIN | 2 | Necesidad bien delimitada |
| Alcance | 3 | Incluye estructuras de datos requeridas |
| Objetivo General | 4 | Alineado a integración modular |
| Objetivos Específicos | 5 | Medibles y trazables |
| Requisitos Funcionales | 7 | Módulos ampliados valor agregado |
| Requisitos No Funcionales | 8 | Cobertura integral (rendimiento, seguridad, etc.) |
| Arquitectura | 9–10 | Lógica y física local separadas |
| Modelo Datos | 11 | Relaciones e integridad clave |
| Diseño UI | 12 | Principios modernos y accesibilidad |
| Seguridad | 13 | Políticas + protección datos |
| Estrategia Pruebas | 14 | Multinivel con negativos |
| Mantenimiento | 15 | Plan de evolución y soporte |
| Validación Usuarios | (Previsto) | Pruebas con roles internos (mínimo 4) |
| Cronograma | (Adjuntar) | Hitos: análisis, prototipo, integración, pruebas, cierre |
| Valor Económico | KPIs | Margen, rotación, simulación costos |
| Valor Social/Ambiental | Extensión | Minimización desperdicios (merma), eficiencia |

## 17. KPIs y Métricas
- Rotación inventario (días promedio).  
- Merma (%) vs uso total.  
- Margen promedio por día/semana.  
- Desviación tiempo producción (varianza).  
- Tasa éxito cupones vs ventas totales.  
- Frecuencia técnica más rentable.  
- Descargas de documentos críticos (indicador uso).  

## 18. Riesgos y Mitigaciones
| Riesgo | Mitigación |
|--------|------------|
| Inconsistencias stock en concurrencia local | Transacciones y bloqueo lógico por operación |
| Fórmulas costo desactualizadas | Versionado + alerta cambio pendiente recalcular |
| Crecimiento no estructurado módulo documentos | Metadatos obligatorios + índice de búsqueda |
| Margen erosionado sin alerta | Umbral margen mínimo → alerta automática |
| Uso excesivo cupones abusivos | Reglas de límite por periodo y auditoría |

## 19. Validación y Cronograma (Esqueleto)
Fases sugeridas (semanas):
1–2: Afinar requerimientos y modelo datos.
3–5: Implementar núcleo Inventario + Usuarios/Roles.
6–8: Ventas/POS + Costos inicial.
9–10: Producción y Técnicas.
11–12: Documentos + Auditoría.
13–14: Dashboard, Reportes, Simulador costos.
15: Pruebas rendimiento, accesibilidad, regresión.
16: Cierre, documentación, validación con usuarios.

## 20. Próximas Ampliaciones (Opcionales)
- Forecast simple de demanda (promedios móviles).  
- Panel de sostenibilidad (indicador merma vs reutilización).  
- Motor de recomendaciones de precio dinámico por elasticidad simple.  

## 21. Conclusión
La especificación cubre el mínimo exigido por la rúbrica y añade funcionalidades de alto valor sin salirse del contexto académico ni del objetivo del programa. Proporciona trazabilidad clara y base sólida para evaluación.

---
¿Deseas ahora un documento separado con Casos de Uso formales o un diagrama entidad-relación detallado? Indica la prioridad y lo genero.
