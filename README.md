# 🌱 App2 - Sistema de Gestión Agrícola

![Java](https://img.shields.io/badge/Java-16%2B-orange)
![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-green)
![Licencia](https://img.shields.io/badge/Licencia-MIT-blue)

Aplicación desarrollada en **Java 16+** que implementa un sistema de gestión integral para cultivos, parcelas y actividades agrícolas. Este proyecto aplica principios sólidos de Programación Orientada a Objetos (POO) y utiliza persistencia de datos en formato CSV.

## ✨ Características principales

- Gestión completa del ciclo de vida de cultivos
- Administración de parcelas y sus asignaciones
- Seguimiento de actividades agrícolas (riego, fertilización, cosecha)
- Generación de reportes e indicadores de rendimiento
- Persistencia de datos mediante archivos CSV

## 👥 Integrantes del equipo

| Nombre         | Correo electrónico        | GitHub                                               |
| -------------- | ------------------------- | ---------------------------------------------------- |
| Cristobal Segu | csegu@alumnos.uai.cl      | [@Cseguuu](https://github.com/usuario)               |
| Diego Soler    | disoler@alumnos.uai.cl    | [@Dxeg0o](https://github.com/Dxeg0o)                 |
| Alonso Paniate | apaniate@alumnos.uai.cl   | [@Alonso0k](https://github.com/Alonso0k)             |
| Felipe Retamal | felretamal@alumnos.uai.cl | [@feliperetamalj](https://github.com/feliperetamalj) |

## 📝 Equipo académico

### Profesor

- Justo Vargas - [justo.vargas@edu.uai.cl](mailto:justo.vargas@edu.uai.cl)

### Ayudante

- Diego Duhalde - [dduhalde@alumnos.uai.cl](mailto:dduhalde@alumnos.uai.cl)

## 🚀 Instalación y ejecución

### Requisitos previos

- Java JDK 16 o superior
- Git
- Maven

### Pasos para compilar y ejecutar

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/<usuario>/App2.git
   cd App2
   ```

2. **Compilar y ejecutar con Maven**

   ```bash
   # (1) Limpia compilaciones previas
   mvn clean

   # (2) Compila todo el código
   mvn compile

   # (3) Arranca el GUI pasándole "cultivos.csv" como argumento
   mvn javafx:run
   ```

## 📂 Estructura del proyecto

A continuación se presenta la estructura general del proyecto, destacando los componentes principales:

```
App2/
├── src/
│   ├── models/          # Clases de dominio (Cultivo, Parcela, Actividad…)
│   ├── services/        # Lógica de negocio y manejo de CSV
│   └── ui/              # Menús / ventanas JavaFX
│       ├── ActividadRow.java     # Wrapper para mostrar Actividad en TableView
│       ├── ActividadWindow.java  # Ventana para gestión de actividades
│       ├── App2.java             # Clase principal con punto de entrada
│       ├── CultivoWindow.java    # Ventana para gestión de cultivos
│       ├── ParcelaWindow.java    # Ventana para gestión de parcelas
│       └── ReporteWindow.java    # Ventana para generación de reportes
├── bin/                 # Archivos compilados (.class)
├── docs/
│   ├── diagrama_clases.png
│   └── informe_diseno.pdf
├── cultivos.csv         # Archivo de persistencia de cultivos
├── actividades.csv      # Archivo de persistencia de actividades
└── README.md
```

## 📚 Funcionalidades e Interfaz Gráfica

El sistema cuenta con una interfaz gráfica desarrollada en JavaFX que ofrece las siguientes ventanas:

- **App2.java**: Punto de entrada de la aplicación y ventana principal del sistema
- **CultivoWindow.java**: Interfaz para la gestión de cultivos
- **ParcelaWindow.java**: Interfaz para la gestión de parcelas
- **ActividadWindow.java**: Interfaz para la gestión de actividades
- **ReporteWindow.java**: Interfaz para la generación de reportes y estadísticas
- **ActividadRow.java**: Clase auxiliar para mostrar actividades en un TableView

### Gestión de Cultivos

- **Listar cultivos**: Visualización completa del inventario de cultivos
- **Crear cultivos**: Registro de nuevas variedades con sus características
- **Editar cultivos**: Modificación de propiedades de cultivos existentes
- **Eliminar cultivos**: Borrado condicional (sólo si no existen actividades pendientes)

### Gestión de Parcelas

- **Listar parcelas**: Vista detallada incluyendo cultivos asignados
- **Agregar parcelas**: Registro de nuevas unidades de terreno
- **Editar parcelas**: Modificación de propiedades de parcelas existentes
- **Eliminar parcelas**: Borrado condicional (sólo si no hay cultivos activos)
- **Asignar cultivo**: Vinculación de cultivos a parcelas específicas

### Gestión de Actividades

- **Registrar actividades**: Creación de tareas como riego, fertilización o cosecha
- **Listar por cultivo**: Filtrado de actividades por tipo de cultivo
- **Eliminar actividades**: Borrado de tareas programadas
- **Marcar completadas**: Seguimiento del estado de las actividades

  |

### Flujo resumido

1. Al iniciar, leerCultivos() crea los objetos **Cultivo**.
2. Luego leerActividades() vincula cada actividad al cultivo correspondiente.
3. Cuando el usuario presiona **Guardar** o **Completar**, se llama a guardarCultivos() y guardarActividades() para sincronizar ambos archivos.
   Esto se hace, para poder guardar nuevas parcelas y guardar nuevas actividades o marcarlas como completadas, ya que al el .csv inicial estar centrado en los cultivos, sin estos .csv no era posible guardar estos nuevos datos sin agregar de forma simultanea un cultivo asociado.

### Búsquedas y Reportes

- **Búsqueda avanzada**: Localización de cultivos por nombre o variedad
- **Reportes dinámicos**: Generación de informes de cultivos activos, en riesgo o cosechados

## 📊 Informe de diseño

- [Informe de diseño (PDF)](docs/informe_diseno.pdf)

## 🙏 Agradecimientos

Agradecemos al profesor y ayudante por su orientación durante el desarrollo de este proyecto.

---

© 2025 | Desarrollado para el curso de Lenguajes y Paradigmas
