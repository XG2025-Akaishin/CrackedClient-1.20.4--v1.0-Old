package me.alpha432.oyvey.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteorExecutor {
    public static ExecutorService executor;

    public static void init() {
        AtomicInteger threadNumber = new AtomicInteger(1);

        executor = Executors.newCachedThreadPool((task) -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setName("Cracked-Executor-" + threadNumber.getAndIncrement());
            return thread;
        });
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static class MyTask implements Runnable {
        private int id;
    
        public MyTask(int id) {
            this.id = id;
        }
    
        @Override
        public void run() {
            System.out.println("Ejecutando tarea " + id + "...");
            try {
                Thread.sleep(2000); // Simula una tarea que tarda 2 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Tarea " + id + " finalizada.");
        }
    }

    public static void main(String[] args) {
        MeteorExecutor.init(); // Inicializa el ejecutor de tareas

        // Crea 5 tareas concurrentes
        for (int i = 0; i < 5; i++) {
            MyTask task = new MyTask(i);
            MeteorExecutor.execute(task);
        }

        // Espera a que las tareas finalicen
        try {
            Thread.sleep(10000); // Espera 10 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        MeteorExecutor.shutdown(); // Detiene el ejecutor de tareas
    }
}
