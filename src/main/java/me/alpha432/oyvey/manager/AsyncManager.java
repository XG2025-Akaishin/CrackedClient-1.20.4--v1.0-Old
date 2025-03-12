package me.alpha432.oyvey.manager;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.autototem.EventPostTick;
import me.alpha432.oyvey.event.impl.autototem.EventSync;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

import static me.alpha432.oyvey.util.traits.Util.mc;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;

public class AsyncManager {
    public static ExecutorService executor = Executors.newCachedThreadPool();
    private ClientService clientService = new ClientService();


    public void run(Runnable runnable, long delay) {
        executor.execute(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
        });
    }

    public void run(Runnable r) {
        executor.execute(r);
    }

        public AsyncManager() {
            clientService.setName("Cracked-AsyncProcessor");
            clientService.setDaemon(true);
            clientService.start();
        }
    
        //@EventHandler
        @Subscribe
        public void onSync(EventSync e) {
            if (!clientService.isAlive()) {
                clientService = new ClientService();
                clientService.setName("Cracked-AsyncProcessor");
                clientService.setDaemon(true);
                clientService.start();
            }
        }
    
        public static class ClientService extends Thread {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (!Module.fullNullCheck()) {
                            for (Module module : CrackedClient.moduleManager.modules) {
                                if (module.isEnabled()) {
                                    module.onThread();
                                }
                            }
                            Thread.sleep(0);
                        } else Thread.yield();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Command.sendMessage(exception.getMessage());
                    }
                }
            }
        }
}