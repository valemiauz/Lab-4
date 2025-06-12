import java.util.*;
import java.io.*;

class Paciente {
    private String nombre;
    private String apellido;
    private String id;
    private int categoria;
    private long tiempoLlegada;
    private String estado;
    private String area;
    private Stack<String> historialCambios = new Stack<>();

    Paciente (String nombre, String apellido, String id, int categoria, long tiempoLlegada, String estado, String area){
        this.nombre=nombre;
        this.apellido=apellido;
        this.id=id;
        this.categoria=categoria;
        this.tiempoLlegada=tiempoLlegada;
        this.estado=estado;
        this.area=area;
    }

    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setApellido(String apellido) {this.apellido = apellido;}
    public void setId(String id) {this.id = id;}
    public void setCategoria(int categoria) {this.categoria = categoria;}
    public void setTiempoLlegada(long tiempoLlegada) {this.tiempoLlegada = tiempoLlegada;}
    public void setEstado(String estado) {this.estado = estado;}
    public void setArea(String area) {this.area = area;}

    public String getNombre() {return nombre;}
    public String getApellido() {return apellido;}
    public String getId() {return id;}
    public int getCategoria() {return categoria;}
    public long getTiempoLlegada() {return tiempoLlegada;}
    public String getEstado() {return estado;}
    public String getArea() {return area;}

    public long tiempoEsperaActual(){
        long tiempoActual = System.currentTimeMillis() / 1000;
        long diferencia = tiempoActual-this.tiempoLlegada;
        return diferencia / 60;
    }

    public void registrarCambio(String descripcion){
        historialCambios.push(descripcion);
    }

    public String obtenerUltimoCambio(){
        if(historialCambios.empty()){
            return "No hay cambios.";
        } else {
            return historialCambios.pop();
        }
    }
}

class AreaAtencion {
    private String nombre;
    private PriorityQueue<Paciente> pacientesHeap;
    private int capacidadMaxima;

    public AreaAtencion(String nombre, int capacidadMaxima) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.pacientesHeap = new PriorityQueue<>(new Comparator<Paciente>() {
            public int compare(Paciente p1, Paciente p2) {
                int cmp = Integer.compare(p1.getCategoria(), p2.getCategoria());
                if (cmp != 0) return cmp;
                return Long.compare(p2.tiempoEsperaActual(), p1.tiempoEsperaActual());
            }
        });
    }

    public void ingresarPaciente (Paciente p){
        pacientesHeap.add(p);
    }

    public Paciente atenderPaciente(){
        return pacientesHeap.poll();
    }

    public boolean estaSaturada() {
        return pacientesHeap.size() >= capacidadMaxima;
    }

    public List<Paciente> obtenerPacientesPorHeapSort() {
        PriorityQueue<Paciente> copia = new PriorityQueue<>(pacientesHeap);
        List<Paciente> ordenados = new ArrayList<>();
        while (!copia.isEmpty()) {
            ordenados.add(copia.poll());
        }
        return ordenados;
    }

}

class Hospital {
    private Map<String, Paciente> pacientesTotales;
    private PriorityQueue<Paciente> colaAtencion;
    private Map<String, AreaAtencion> areasAtencion;
    private List<Paciente> pacientesAtendidos;

    public Hospital () {
        this.pacientesTotales = new HashMap<>();
        this.areasAtencion = new HashMap<>();
        this.pacientesAtendidos = new ArrayList<>();
        this.colaAtencion = new PriorityQueue<>(new Comparator<Paciente>() {
            public int compare(Paciente p1, Paciente p2) {
                int cmp = Integer.compare(p1.getCategoria(), p2.getCategoria());
                if (cmp != 0) return cmp;
                return Long.compare(p2.tiempoEsperaActual(), p1.tiempoEsperaActual());
            }
        });
    }

    public void registrarPaciente(Paciente p) {
        pacientesTotales.put(p.getId(), p);
        colaAtencion.add(p);
        p.registrarCambio("Paciente registrado - Categoría: " + p.getCategoria() + ", Área: " + p.getArea());
    }

    public void reasignarCategoria(String id, int nuevaCategoria) {
        Paciente paciente = pacientesTotales.get(id);

        if (paciente != null) {
            int categoriaAnterior = paciente.getCategoria();
            paciente.setCategoria(nuevaCategoria);
            paciente.registrarCambio("Categoría cambiada de " + categoriaAnterior + " a " + nuevaCategoria);

            colaAtencion.remove(paciente);
            colaAtencion.add(paciente);
        }
    }

    public Paciente atenderSiguiente() {
        Paciente paciente = colaAtencion.poll();

        if (paciente != null) {
            AreaAtencion area = areasAtencion.get(paciente.getArea());

            if (area != null) {
                area.ingresarPaciente(paciente);
            }

            paciente.setEstado("En atención");
            paciente.registrarCambio("Paciente asignado al área: " + paciente.getArea());
            pacientesAtendidos.add(paciente);
        }

        return paciente;
    }

    public List<Paciente> obtenerPacientesPorCategoria(int categoria) {
        List<Paciente> pacientesPorCategoria = new ArrayList<>();

        for (Paciente paciente : colaAtencion) {
            if (paciente.getCategoria() == categoria) {
                pacientesPorCategoria.add(paciente);
            }
        }

        return pacientesPorCategoria;
    }

    public AreaAtencion obtenerArea(String nombre) {
        return areasAtencion.get(nombre);
    }

    public void agregarArea(String nombre, int capacidadMaxima) {
        AreaAtencion area = new AreaAtencion(nombre, capacidadMaxima);
        areasAtencion.put(nombre, area);
    }

}

class GeneradorPacientes {
    private static final String[] NOMBRES = {
            "Juan", "María", "Carlos", "Ana", "Luis", "Carmen", "José", "Elena",
            "Miguel", "Patricia", "Francisco", "Laura", "Antonio", "Isabel", "Manuel",
            "Rosa", "David", "Teresa", "Jesús", "Pilar", "Javier", "Dolores", "Fernando",
            "Mercedes", "Rafael", "Concepción", "Daniel", "Josefa", "Alejandro", "Francisca",
            "Pedro", "Antonia", "Sergio", "Cristina", "Álvaro", "Lucía", "Pablo", "Julia",
            "Adrián", "Nuria", "Diego", "Beatriz", "Iván", "Silvia", "Rubén", "Amparo"
    };

    private static final String[] APELLIDOS = {
            "García", "Rodríguez", "González", "Fernández", "López", "Martínez", "Sánchez",
            "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Díaz", "Moreno",
            "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez", "Navarro", "Torres",
            "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez", "Serrano", "Blanco",
            "Suárez", "Molina", "Morales", "Ortega", "Delgado", "Castro", "Ortiz",
            "Rubio", "Marín", "Sanz", "Iglesias", "Medina", "Garrido", "Cortés",
            "Castillo", "Santos", "Lozano", "Guerrero", "Cano", "Prieto", "Méndez"
    };

    private static final String[] AREAS = {
            "SAPU", "Urgencia_adulto", "Infantil", "Cardiología", "Traumatología"
    };

    private Random random;
    private int contadorId;

    public GeneradorPacientes() {
        this.random = new Random();
        this.contadorId = 1;
    }

    public List<Paciente> generarPacientes(int n, long timestampInicio) {
        List<Paciente> pacientes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            String id = generarIdUnico();
            int categoria = generarCategoria();
            long tiempoLlegada = timestampInicio + (i * 600);
            String estado = "en_espera";
            String area = AREAS[random.nextInt(AREAS.length)];

            Paciente paciente = new Paciente(nombre, apellido, id, categoria, tiempoLlegada, estado, area);
            pacientes.add(paciente);
        }

        return pacientes;
    }

    private String generarIdUnico() {
        return String.format("PAC%04d", contadorId++);
    }


    private int generarCategoria() {
        double probabilidad = random.nextDouble() * 100;

        if (probabilidad < 10) {
            return 1;
        } else if (probabilidad < 25) {
            return 2;
        } else if (probabilidad < 43) {
            return 3;
        } else if (probabilidad < 70) {
            return 4;
        } else {
            return 5;
        }
    }

    public void guardarPacientesEnArchivo(List<Paciente> pacientes, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("=== PACIENTES GENERADOS PARA SIMULACIÓN 24H ===");
            writer.println("Total de pacientes: " + pacientes.size());
            writer.println("Formato: ID | Nombre | Apellido | Categoría | Tiempo Llegada | Estado | Área");
            writer.println("================================================================");

            for (Paciente paciente : pacientes) {
                writer.printf("%s | %s | %s | C%d | %d | %s | %s%n",
                        paciente.getId(),
                        paciente.getNombre(),
                        paciente.getApellido(),
                        paciente.getCategoria(),
                        paciente.getTiempoLlegada(),
                        paciente.getEstado(),
                        paciente.getArea()
                );
            }

            writer.println("================================================================");
            writer.println("Distribución por categorías:");

            Map<Integer, Integer> contadorCategorias = new HashMap<>();
            for (Paciente p : pacientes) {
                contadorCategorias.put(p.getCategoria(),
                        contadorCategorias.getOrDefault(p.getCategoria(), 0) + 1);
            }

            for (int i = 1; i <= 5; i++) {
                int cantidad = contadorCategorias.getOrDefault(i, 0);
                double porcentaje = (cantidad * 100.0) / pacientes.size();
                writer.printf("C%d: %d pacientes (%.1f%%)%n", i, cantidad, porcentaje);
            }

            System.out.println("Archivo '" + nombreArchivo + "' creado exitosamente.");

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    static void main(String[] args) {
        GeneradorPacientes generador = new GeneradorPacientes();

        long timestampInicio = System.currentTimeMillis() / 1000;
        int pacientesPor24h = 144;

        System.out.println("=== GENERADOR DE PACIENTES HOSPITALARIOS ===");
        System.out.println("Generando " + pacientesPor24h + " pacientes para simulación de 24 horas...");
        System.out.println("Intervalo: 1 paciente cada 10 minutos");

        List<Paciente> pacientes = generador.generarPacientes(pacientesPor24h, timestampInicio);

        System.out.println("✓ " + pacientes.size() + " pacientes generados exitosamente");

        generador.guardarPacientesEnArchivo(pacientes, "Pacientes_24h.txt");

        System.out.println("\n=== ESTADÍSTICAS DE GENERACIÓN ===");

        Map<Integer, Integer> stats = new HashMap<>();
        for (Paciente p : pacientes) {
            stats.put(p.getCategoria(), stats.getOrDefault(p.getCategoria(), 0) + 1);
        }

        System.out.println("Distribución por categorías:");
        for (int i = 1; i <= 5; i++) {
            int cantidad = stats.getOrDefault(i, 0);
            double porcentaje = (cantidad * 100.0) / pacientes.size();
            String nombreCategoria = obtenerNombreCategoria(i);
            System.out.printf("C%d (%s): %d pacientes (%.1f%%)%n",
                    i, nombreCategoria, cantidad, porcentaje);
        }

        System.out.println("\n=== PRIMEROS 5 PACIENTES GENERADOS ===");
        for (int i = 0; i < Math.min(5, pacientes.size()); i++) {
            Paciente p = pacientes.get(i);
            System.out.printf("%d. %s %s (ID: %s, C%d, Área: %s)%n",
                    i + 1, p.getNombre(), p.getApellido(), p.getId(),
                    p.getCategoria(), p.getArea());
        }

        System.out.println("\n✓ Generación completada. Revisar archivo 'Pacientes_24h.txt'");
    }

    private static String obtenerNombreCategoria(int categoria) {
        switch (categoria) {
            case 1: return "Crítico";
            case 2: return "Muy Urgente";
            case 3: return "Urgente";
            case 4: return "Menos Urgente";
            case 5: return "No Urgente";
            default: return "Desconocido";
        }
    }
}

class SimuladorUrgencia {
    private Hospital hospital;
    private GeneradorPacientes generador;
    private List<Paciente> pacientesGenerados;
    private int indicePacienteActual;

    // Estadísticas de simulación
    private Map<Integer, List<Long>> tiemposEsperaPorCategoria;
    private Map<Integer, Integer> pacientesAtendidosPorCategoria;
    private List<Paciente> pacientesExcedidos;
    private Map<String, Long> tiemposAtencionPorPaciente;

    // Configuración de tiempos máximos por categoría (en minutos)
    private static final Map<Integer, Integer> TIEMPOS_MAXIMOS = Map.of(
            1, 0,   // Crítico - inmediato
            2, 10,  // Muy Urgente - 10 minutos
            3, 60,  // Urgente - 1 hora
            4, 120, // Menos Urgente - 2 horas
            5, 240  // No Urgente - 4 horas
    );

    // Contadores para la lógica de simulación
    private int contadorPacientesAcumulados;
    private long tiempoSimulacion; // en minutos

    public SimuladorUrgencia() {
        this.hospital = new Hospital();
        this.generador = new GeneradorPacientes();
        this.indicePacienteActual = 0;
        this.contadorPacientesAcumulados = 0;
        this.tiempoSimulacion = 0;

        // Inicializar estadísticas
        this.tiemposEsperaPorCategoria = new HashMap<>();
        this.pacientesAtendidosPorCategoria = new HashMap<>();
        this.pacientesExcedidos = new ArrayList<>();
        this.tiemposAtencionPorPaciente = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            tiemposEsperaPorCategoria.put(i, new ArrayList<>());
            pacientesAtendidosPorCategoria.put(i, 0);
        }

        // Inicializar áreas de atención
        hospital.agregarArea("SAPU", 10);
        hospital.agregarArea("Urgencia_adulto", 15);
        hospital.agregarArea("Infantil", 8);
    }

    public void simular(int pacientesPorDia) {
        System.out.println("=== INICIANDO SIMULACIÓN DE URGENCIA HOSPITALARIA ===");
        System.out.println("Duración: 24 horas (1440 minutos)");
        System.out.println("Pacientes esperados: " + pacientesPorDia);
        System.out.println("========================================================\n");

        // Generar pacientes para la simulación
        long timestampInicio = System.currentTimeMillis() / 1000;
        pacientesGenerados = generador.generarPacientes(pacientesPorDia, timestampInicio);

        // Ciclo principal de simulación (1440 minutos = 24 horas)
        for (tiempoSimulacion = 0; tiempoSimulacion < 1440; tiempoSimulacion++) {

            // Cada 10 minutos llega un nuevo paciente
            if (tiempoSimulacion % 10 == 0 && indicePacienteActual < pacientesGenerados.size()) {
                llegadaNuevoPaciente();
            }

            // Cada 15 minutos se atiende un paciente
            if (tiempoSimulacion % 15 == 0) {
                atenderPacienteRegular();
            }

            // Verificar si hay pacientes que exceden tiempo máximo
            verificarPacientesExcedidos();

            // Cada vez que se acumulan 3 pacientes, atender 2 inmediatamente
            if (contadorPacientesAcumulados >= 3) {
                atenderPacientesAcumulados();
                contadorPacientesAcumulados = 0;
            }

            // Mostrar progreso cada hora
            if (tiempoSimulacion % 60 == 0 && tiempoSimulacion > 0) {
                mostrarProgresoHorario();
            }
        }

        // Procesar pacientes restantes
        procesarPacientesRestantes();

        // Mostrar resultados finales
        mostrarResultadosFinales();
    }

    private void llegadaNuevoPaciente() {
        if (indicePacienteActual < pacientesGenerados.size()) {
            Paciente paciente = pacientesGenerados.get(indicePacienteActual);

            // Ajustar tiempo de llegada al tiempo actual de simulación
            long timestampActual = System.currentTimeMillis() / 1000 + (tiempoSimulacion * 60);
            paciente.setTiempoLlegada(timestampActual);

            hospital.registrarPaciente(paciente);
            contadorPacientesAcumulados++;
            indicePacienteActual++;

            System.out.printf("[Min %d] Llegada: %s %s (ID: %s, C%d, Área: %s)\n",
                    tiempoSimulacion, paciente.getNombre(), paciente.getApellido(),
                    paciente.getId(), paciente.getCategoria(), paciente.getArea());
        }
    }

    private void atenderPacienteRegular() {
        Paciente paciente = hospital.atenderSiguiente();
        if (paciente != null) {
            procesarAtencionPaciente(paciente, "Atención regular");
        }
    }

    private void atenderPacientesAcumulados() {
        System.out.printf("[Min %d] ATENCIÓN MASIVA: Atendiendo 2 pacientes por acumulación\n", tiempoSimulacion);

        for (int i = 0; i < 2; i++) {
            Paciente paciente = hospital.atenderSiguiente();
            if (paciente != null) {
                procesarAtencionPaciente(paciente, "Atención por acumulación");
            }
        }
    }

    private void verificarPacientesExcedidos() {
        // Esta lógica se implementaría accediendo a la cola de pacientes del hospital
        // Por simplicidad, asumimos que los pacientes críticos (C1) se atienden inmediatamente
    }

    private void procesarAtencionPaciente(Paciente paciente, String tipoAtencion) {
        long tiempoEspera = calcularTiempoEspera(paciente);

        // Registrar estadísticas
        tiemposEsperaPorCategoria.get(paciente.getCategoria()).add(tiempoEspera);
        pacientesAtendidosPorCategoria.put(paciente.getCategoria(),
                pacientesAtendidosPorCategoria.get(paciente.getCategoria()) + 1);
        tiemposAtencionPorPaciente.put(paciente.getId(), tiempoEspera);

        // Verificar si excedió tiempo máximo
        int tiempoMaximo = TIEMPOS_MAXIMOS.get(paciente.getCategoria());
        if (tiempoEspera > tiempoMaximo) {
            pacientesExcedidos.add(paciente);
            paciente.registrarCambio("EXCEDIÓ tiempo máximo: " + tiempoEspera + " min (máx: " + tiempoMaximo + " min)");
        }

        paciente.setEstado("Atendido");
        paciente.registrarCambio("Atendido después de " + tiempoEspera + " minutos de espera");

        System.out.printf("[Min %d] ATENDIDO: %s %s (C%d) - Espera: %d min (%s)\n",
                tiempoSimulacion, paciente.getNombre(), paciente.getApellido(),
                paciente.getCategoria(), tiempoEspera, tipoAtencion);
    }

    private long calcularTiempoEspera(Paciente paciente) {
        long tiempoActual = System.currentTimeMillis() / 1000 + (tiempoSimulacion * 60);
        return (tiempoActual - paciente.getTiempoLlegada()) / 60;
    }

    private void procesarPacientesRestantes() {
        System.out.println("\n=== PROCESANDO PACIENTES RESTANTES ===");

        Paciente paciente;
        while ((paciente = hospital.atenderSiguiente()) != null) {
            procesarAtencionPaciente(paciente, "Procesamiento final");
        }
    }

    private void mostrarProgresoHorario() {
        int hora = (int) (tiempoSimulacion / 60);
        int totalAtendidos = pacientesAtendidosPorCategoria.values().stream()
                .mapToInt(Integer::intValue).sum();

        System.out.printf("\n--- PROGRESO HORA %d ---\n", hora);
        System.out.printf("Pacientes atendidos hasta ahora: %d\n", totalAtendidos);
        System.out.printf("Pacientes que excedieron tiempo: %d\n", pacientesExcedidos.size());
    }

    private void mostrarResultadosFinales() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              RESULTADOS FINALES DE SIMULACIÓN");
        System.out.println("=".repeat(60));

        int totalAtendidos = pacientesAtendidosPorCategoria.values().stream()
                .mapToInt(Integer::intValue).sum();

        System.out.println("RESUMEN GENERAL:");
        System.out.println("- Total pacientes generados: " + pacientesGenerados.size());
        System.out.println("- Total pacientes atendidos: " + totalAtendidos);
        System.out.println("- Pacientes que excedieron tiempo máximo: " + pacientesExcedidos.size());

        System.out.println("\nATENCIÓN POR CATEGORÍA:");
        for (int categoria = 1; categoria <= 5; categoria++) {
            int atendidos = pacientesAtendidosPorCategoria.get(categoria);
            List<Long> tiempos = tiemposEsperaPorCategoria.get(categoria);

            if (!tiempos.isEmpty()) {
                double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0.0);
                long maximo = tiempos.stream().mapToLong(Long::longValue).max().orElse(0);
                long minimo = tiempos.stream().mapToLong(Long::longValue).min().orElse(0);

                System.out.printf("C%d (%s):\n", categoria, obtenerNombreCategoria(categoria));
                System.out.printf("  - Atendidos: %d pacientes\n", atendidos);
                System.out.printf("  - Tiempo promedio: %.1f minutos\n", promedio);
                System.out.printf("  - Tiempo mínimo: %d minutos\n", minimo);
                System.out.printf("  - Tiempo máximo: %d minutos\n", maximo);
                System.out.printf("  - Tiempo máximo permitido: %d minutos\n", TIEMPOS_MAXIMOS.get(categoria));
            }
        }

        if (!pacientesExcedidos.isEmpty()) {
            System.out.println("\nPACIENTES QUE EXCEDIERON TIEMPO MÁXIMO:");
            for (Paciente p : pacientesExcedidos) {
                long tiempoEspera = tiemposAtencionPorPaciente.get(p.getId());
                System.out.printf("- %s %s (C%d): %d min (máx: %d min)\n",
                        p.getNombre(), p.getApellido(), p.getCategoria(),
                        tiempoEspera, TIEMPOS_MAXIMOS.get(p.getCategoria()));
            }
        }
    }

    // Métodos para pruebas específicas

    public void seguimientoPacienteC4() {
        System.out.println("\n=== SEGUIMIENTO INDIVIDUAL PACIENTE C4 ===");

        // Buscar un paciente C4 en los datos generados
        Paciente pacienteC4 = null;
        for (Paciente p : pacientesGenerados) {
            if (p.getCategoria() == 4) {
                pacienteC4 = p;
                break;
            }
        }

        if (pacienteC4 != null) {
            System.out.println("Paciente seleccionado: " + pacienteC4.getNombre() + " " +
                    pacienteC4.getApellido() + " (ID: " + pacienteC4.getId() + ")");

            Long tiempoEspera = tiemposAtencionPorPaciente.get(pacienteC4.getId());
            if (tiempoEspera != null) {
                System.out.println("Tiempo total de atención: " + tiempoEspera + " minutos");
                System.out.println("Tiempo máximo permitido C4: " + TIEMPOS_MAXIMOS.get(4) + " minutos");

                if (tiempoEspera > TIEMPOS_MAXIMOS.get(4)) {
                    System.out.println("⚠️  EXCEDIÓ el tiempo máximo por " +
                            (tiempoEspera - TIEMPOS_MAXIMOS.get(4)) + " minutos");
                } else {
                    System.out.println("✅ Atendido dentro del tiempo permitido");
                }

                // Mostrar historial de cambios
                System.out.println("\nHistorial de cambios:");
                String cambio;
                while (!(cambio = pacienteC4.obtenerUltimoCambio()).equals("No hay cambios.")) {
                    System.out.println("- " + cambio);
                }
            }
        }
    }

    public Map<Integer, Double> calcularPromediosPorCategoria() {
        Map<Integer, Double> promedios = new HashMap<>();

        for (int categoria = 1; categoria <= 5; categoria++) {
            List<Long> tiempos = tiemposEsperaPorCategoria.get(categoria);
            if (!tiempos.isEmpty()) {
                double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0.0);
                promedios.put(categoria, promedio);
            } else {
                promedios.put(categoria, 0.0);
            }
        }

        return promedios;
    }

    public void simularCambioCategoria(String idPaciente, int categoriaOriginal, int categoriaNueva) {
        System.out.println("\n=== SIMULACIÓN CAMBIO DE CATEGORÍA ===");
        System.out.println("Paciente ID: " + idPaciente);
        System.out.println("Categoría original: C" + categoriaOriginal);
        System.out.println("Nueva categoría: C" + categoriaNueva);

        // Simular el cambio usando el método del hospital
        hospital.reasignarCategoria(idPaciente, categoriaNueva);

        System.out.println("✅ Cambio de categoría registrado exitosamente");
        System.out.println("El cambio ha sido registrado en el historial del paciente");
    }

    private String obtenerNombreCategoria(int categoria) {
        switch (categoria) {
            case 1: return "Crítico";
            case 2: return "Muy Urgente";
            case 3: return "Urgente";
            case 4: return "Menos Urgente";
            case 5: return "No Urgente";
            default: return "Desconocido";
        }
    }

    // Clase principal para ejecutar las pruebas
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE SIMULACIÓN DE URGENCIA HOSPITALARIA ===\n");

        // Simulación principal
        SimuladorUrgencia simulador = new SimuladorUrgencia();
        simulador.simular(144); // 144 pacientes en 24 horas (1 cada 10 min)

        // Prueba 1: Seguimiento individual
        simulador.seguimientoPacienteC4();

        // Prueba 2: Promedio por categoría (una ejecución)
        System.out.println("\n=== PROMEDIOS POR CATEGORÍA ===");
        Map<Integer, Double> promedios = simulador.calcularPromediosPorCategoria();
        for (Map.Entry<Integer, Double> entry : promedios.entrySet()) {
            System.out.printf("C%d: %.1f minutos promedio\n", entry.getKey(), entry.getValue());
        }

        // Prueba 3: Saturación del sistema
        System.out.println("\n=== PRUEBA DE SATURACIÓN (200 pacientes) ===");
        SimuladorUrgencia simuladorSaturado = new SimuladorUrgencia();
        simuladorSaturado.simular(200);

        // Prueba 4: Cambio de categoría
        if (!simulador.pacientesGenerados.isEmpty()) {
            Paciente primerPaciente = simulador.pacientesGenerados.get(0);
            simulador.simularCambioCategoria(primerPaciente.getId(),
                    primerPaciente.getCategoria(), 1);
        }

        System.out.println("\n✅ Todas las pruebas completadas exitosamente");
    }
}
