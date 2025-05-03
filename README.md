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

| Nombre         | Correo electrónico        | GitHub                                 |
| -------------- | ------------------------- | -------------------------------------- |
| Cristobal Segu | csegu@alumnos.uai.cl      | [@Cseguuu](https://github.com/usuario) |
| Diego Soler    | disoler@alumnos.uai.cl    | [@Dxeg0o](https://github.com/Dxeg0o)   |
| Alonso Paniate | apaniate@alumnos.uai.cl   | [@Alonso0k](https://github.com/Alonso0k) |
| Felipe Retamal | felretamal@alumnos.uai.cl | [@feliperetamalj](https://github.com/feliperetamalj) |

## 📝 Equipo académico

### Profesores

- María Loreto Arriagada - [loreto.arriagada.v@edu.uai.cl](mailto:loreto.arriagada.v@edu.uai.cl)
- Paulina González - [paulina.gonzalez.p@edu.uai.cl](mailto:paulina.gonzalez.p@edu.uai.cl)
- Justo Vargas - [justo.vargas@edu.uai.cl](mailto:justo.vargas@edu.uai.cl)

### Ayudante

- Diego Duhalde - [dduhalde@alumnos.uai.cl](mailto:dduhalde@alumnos.uai.cl)

## 🚀 Instalación y ejecución

### Requisitos previos

- Java JDK 16 o superior
- Git

### Pasos para compilar y ejecutar

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/<usuario>/App2.git
   cd App2
   ```


   # (1) Limpia compilaciones previas
>> mvn clean
>>
>> # (2) Compila todo el código
>> mvn compile
>>
>> # (3) Arranca el GUI pasándole “cultivos.csv” como argumento
>> mvn javafx:run
>>

## 📂 Estructura del proyecto

```
App2/
├── src/
│   ├── models/          # Clases de dominio (Cultivo, Parcela, Actividad…)
│   ├── services/        # Lógica de negocio y manejo de CSV
│   ├── ui/              # Menús / ventanas JavaFX
│   │   ├── ActividadRow.java   # Wrapper para mostrar Actividad en TableView
│   │   └── ActividadWindow.java
│   └── App2.java        # Clase principal con punto de entrada
├── bin/                 # Archivos compilados (.class)
├── docs/
│   ├── diagrama_clases.png
│   └── informe_diseno.pdf
├── cultivos.csv         # Archivo de persistencia de cultivos
├── actividades.csv      # Archivo de persistencia de actividades
└── README.md
```

## 📚 Funcionalidades

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
  Cada línea contiene:
- Tipo de registro (siempre "Cultivo")
- Nombre del cultivo
- Variedad
- Superficie (hectáreas)
- Código de parcela
- Fecha de siembra
- Estado (ACTIVO, EN_RIESGO, COSECHADO)
- Lista de actividades con fechas (formato JSON simple)

## 📦 Persistencia de Datos — `CSVHandler`

A partir de esta iteración la información se divide en **dos** archivos CSV para mejorar la escalabilidad y simplificar los filtros en la interfaz.

| Archivo          | Funciones de lectura/escritura                  | Columnas                                |
| ---------------- | ----------------------------------------------- | --------------------------------------- |
| `cultivos.csv`   | `leerCultivos()` / `guardarCultivos()`          | `nombre;variedad;superficie;parcela;fechaSiembra;estado` |
| `actividades.csv`| `leerActividades()` / `guardarActividades()`    | `cultivo;tipo;fecha;estado`             |

### Flujo resumido
1. Al iniciar, `leerCultivos()` crea los objetos **`Cultivo`**.  
2. Luego `leerActividades()` vincula cada actividad al cultivo correspondiente.  
3. Cuando el usuario presiona **Guardar** o **Completar**, se llama a `guardarCultivos()` y `guardarActividades()` para sincronizar ambos archivos.
Esto se hace, para poder guardar nuevas parcelas y guardar nuevas actividades o marcarlas como completadas, ya que al el .csv inicial estar centrado en los cultivos, sin estos .csv no era posible guardar estos nuevos datos sin agregar de forma simultanea un cultivo asociado.

## 3. Nuevos Requerimientos y Detalles de la Entrega

- **Lenguaje**: Java (versión 16 o superior, u OpenJDK 16+).
- **Grupos**: 4 o 5 personas (no se admitirán grupos de 6).
- **Fecha de entrega**: Sábado 3 de Mayo a las 23:59.
- Por cada día de atraso se descuenta 1 punto, comenzando a las 00:00 del día siguiente.
  - Ejemplo: si entregan a las 00:00 del día siguiente, la nota máxima es 6.0.

### Opcional: Diagrama de Clases

Deben entregar un diagrama de clases que represente su solución:

- Cardinalidades (ej. una parcela tiene N cultivos, un cultivo tiene M actividades, etc.)
- Herencia (si usan clases que extiendan de alguna clase base)
- Interfaces (si las usan)
- Composición o agregación (por ejemplo, una parcela contiene un conjunto de cultivos).

Se evaluará en particular el uso correcto de:

- Paquetes (organizar las clases en paquetes coherentes).
- Modificadores de Acceso (privado, público, protegido) y encapsulamiento.
- Herencia (si se crea una superclase ElementoAgricola o un patrón similar).
- Colecciones (ArrayList, List, Map, etc.).
- Interfaces/Clases abstractas (al menos un uso sensato).

## 4. Formato de Entrega (vía repositorio GitHub)

### Repositorio de trabajo

- Deberán crear un repositorio para el grupo que se llame App2 en GitHub (o el que indique el curso).
- Asegurarse que el repositorio sea privado al grupo de trabajo.
- En ese repositorio, agregar a todos los integrantes del grupo como colaboradores, y dar acceso a dicho repositorio al profesor y al ayudante.

### Commits balanceados y Pull Request

- Cada integrante del grupo debe tener aproximadamente la misma cantidad de commits.
- Se evaluará la participación equitativa a través del historial de commits.
- La entrega oficial por medio de WEBC indicando la URL del repo (o como indique la asignatura).

### Estructura del repositorio

- Código fuente en Java, organizado en paquetes (models, services, ui, etc.).
- Diagrama de clases (en formato imagen/PDF).
- Informe de diseño (PDF o Markdown) con:
  - Arquitectura (clases, paquetes, herencia, etc.).
  - Justificación del uso de colecciones y patrones de diseño (si aplican).
  - Manejo de modificadores de acceso.
  - Reflexiones finales / autoevaluación:
    - ¿Qué fue lo más desafiante de implementar en POO?
    - ¿Cómo controlaron la lectura y escritura de CSV?
    - ¿Qué aprendizajes surgieron del proyecto?
  - Explicación de uso de IA (si aplica):
    - ¿Qué tipo de ayuda proporcionó la herramienta?
    - ¿Cómo validaron o contrastaron las sugerencias?
- README explicando cómo compilar/ejecutar el programa, con info de cada integrante (nombre, correo, etc.).

### Compilación y ejecución

- Desde la raíz del proyecto, se debe poder compilar (por ejemplo, javac App2.java u otro comando, dependiendo de la estructura).
- Luego, ejecutar:
  ```bash
  java App2 cultivos.csv
  ```
- El programa mostrará un menú que permita realizar las acciones mencionadas (crear cultivo, registrar actividad, buscar, etc.).
- Al salir, se guardarán los cambios en cultivos.csv.

## 5. Rúbrica de Evaluación

| Criterio                                                                           | Peso     | Descripción                                                                                                                                                                                                                                                         |
| ---------------------------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Funcionamiento general                                                          | 30%      | <ul><li>El proyecto compila y se ejecuta correctamente.</li><li>Menús y submenús funcionan sin errores ni excepciones no controladas.</li><li>Guardado y lectura de cultivos.csv se realizan de forma coherente.</li></ul>                                          |
| 2. Paradigma Orientado a Objetos                                                   | 30%      | <ul><li>Uso adecuado de clases, encapsulamiento, herencia, interfaces y colecciones.</li><li>Organización en paquetes clara y lógica.</li><li>Aplicación de principios de OOP (mínima duplicación de código, coherencia en la jerarquía de clases, etc.).</li></ul> |
| 3. Informe de diseño y reflexiones finales                                         | 10%      | <ul><li><strong>Informe de diseño</strong>: explica la arquitectura, justifica las decisiones (7%).</li><li><strong>Reflexiones</strong>: aprendizajes, desafíos, etc. (5%).</li><li><strong>Uso de IA</strong>: transparencia y validación (3%).</li></ul>         |
| 4. Uso de Git (commits y pull request) / Organización del repositorio/Presentación | 20%      | <ul><li>Commits equilibrados entre integrantes (aporte individual visible).</li><li>Estructura del repositorio clara, con README que indique cómo compilar/ejecutar.</li></ul>                                                                                      |
| 5. Presentación                                                                    | 10%      | <ul><li>Presentar en clases la solución la app</li><li>Presentar la app funcionando.</li></ul>                                                                                                                                                                      |
| **Total**                                                                          | **100%** |                                                                                                                                                                                                                                                                     |

### Penalizaciones y Bonus

- **Atrasos**: Resta 1 punto al máximo posible por día de atraso (comenzando a las 00:00 del día siguiente).
- **Grupos con más de 5 integrantes**: penalización (no se admite grupo de 6).
- **Grupos con menos integrantes**: puede existir un pequeño bonus, según políticas de la asignatura.
- **Arquitectura y diagrama de clases**: puede existir un pequeño bonus, según políticas de la asignatura (Diagrama de clases con cardinalidades y relaciones (herencia, composición, etc.)).

## 6. Ejemplo de Uso

```bash
# (1) Clonar/forkear el repositorio
git clone https://github.com/<usuario>/App2.git

# (2) Compilar
cd App2
javac src/*.java

# (3) Ejecutar
java src/App2 cultivos.csv
```

### Búsquedas y Reportes

- **Búsqueda avanzada**: Localización de cultivos por nombre o variedad
- **Reportes dinámicos**: Generación de informes de cultivos activos, en riesgo o cosechados

### Persistencia de Datos

- Carga inicial desde archivo CSV
- Guardado automático de cambios al finalizar

## 📊 Diagramas y documentación

### Diagrama de clases

![Diagrama de Clases](docs/diagrama_clases.png)

Para ver el diagrama completo y la documentación detallada, consulte los siguientes archivos:

- [Informe de diseño (PDF)](docs/informe_diseno.pdf)
- [Reflexiones y autoevaluación](docs/reflexiones.pdf)

## 🤔 Reflexiones y aprendizajes

El proyecto ha presentado diversos desafíos técnicos que han sido abordados mediante:

- Aplicación de patrones de diseño adecuados
- Implementación rigurosa de principios SOLID
- Uso estratégico de herramientas de desarrollo

_Nota: La sección de reflexiones incluye detalles sobre el uso de IA como herramienta de apoyo durante el desarrollo._

## 🙏 Agradecimientos

Agradecemos al profesor y ayudante por su orientación durante el desarrollo de este proyecto.

---

© 2025 | Desarrollado para el curso de Lenguajes y Paradigmas
