package me.alpha432.oyvey.manager;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.Stage;
import me.alpha432.oyvey.event.impl.*;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.util.models.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Formatting;

public class EventManager extends Feature {
    private final Timer logoutTimer = new Timer();

    public void init() {
        EVENT_BUS.register(this);
    }

    public void onUnload() {
        EVENT_BUS.unregister(this);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.getWindow().setTitle(" [ Cracked | v1.0 ] -> " + MinecraftClient.getInstance().getSession().getUsername() + "");
        if (!fullNullCheck()) {
//            OyVey.inventoryManager.update();
            CrackedClient.moduleManager.onUpdate();
            CrackedClient.moduleManager.sortModules(true);
            onTick();
//            if ((HUD.getInstance()).renderingMode.getValue() == HUD.RenderingMode.Length) {
//                OyVey.moduleManager.sortModules(true);
//            } else {
//                OyVey.moduleManager.sortModulesABC();
//            }
        }
    }

    public void onTick() {
        if (fullNullCheck())
            return;
        CrackedClient.moduleManager.onTick();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player.getHealth() > 0.0F)
                continue;
            EVENT_BUS.post(new DeathEvent(player));
//            PopCounter.getInstance().onDeath(player);
        }
    }

    @Subscribe
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (fullNullCheck())
            return;
        if (event.getStage() == Stage.PRE) {
            CrackedClient.speedManager.updateValues();
            CrackedClient.rotationManager.updateRotations();
            CrackedClient.positionManager.updatePosition();
        }
        if (event.getStage() == Stage.POST) {
            CrackedClient.rotationManager.restoreRotations();
            CrackedClient.positionManager.restorePosition();
        }
    }

    @Subscribe//Test Hud Mnager tps
    public void onPacketReceive(PacketEvent.Receive event) {
        CrackedClient.hudManager.onPacketReceived();
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
        CrackedClient.hudManager.update();
    }

    @Subscribe
    public void onWorldRender(Render3DEvent event) {
        CrackedClient.moduleManager.onRender3D(event);
    }

    @Subscribe 
    public void onRenderGameOverlayEvent(Render2DEvent event) {
        CrackedClient.moduleManager.onRender2D(event);
    }

    @Subscribe 
    public void onKeyInput(KeyEvent event) {
        if (event.getKey() == 1) {
        CrackedClient.moduleManager.onKeyPressed(event.getKey());
        } else if (event.getKey() == 0) {
            CrackedClient.moduleManager.onKeyPressed(event.getKey());
        }
    }

    @Subscribe public void onChatSent(ChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.cancel();
            try {
                if (event.getMessage().length() > 1) {
                    CrackedClient.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage(Formatting.RED + "An error occurred while running this command. Check the log!");
            }
        }
    }
}