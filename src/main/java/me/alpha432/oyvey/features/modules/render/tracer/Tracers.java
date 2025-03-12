package me.alpha432.oyvey.features.modules.render.tracer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.tracer.utiltrace.RenderTracers;
import me.alpha432.oyvey.features.settings.Setting;

import java.awt.*;

public class Tracers extends Module {

    private final Setting<Float> height = this.register(new Setting<>("Height", 0f, 0f, 2f));
    //Player
    public Setting<Boolean> players = this.register(new Setting<>("Players", true));
    public Setting<Integer> playerRed = this.register(new Setting<>("Red", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> playerGreen = this.register(new Setting<>("Green", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> playerBlue = this.register(new Setting<>("Blue", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> playerAlpha = this.register(new Setting<>("Alpha", 240, 0, 255, v -> players.getValue()));
    //Player Friend
    public Setting<Integer> friendRed = this.register(new Setting<>("FriendRed", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> friendGreen = this.register(new Setting<>("FriendGreen", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> friendBlue = this.register(new Setting<>("FriendBlue", 240, 0, 255, v -> players.getValue()));
    public Setting<Integer> friendAlpha = this.register(new Setting<>("FriendAlpha", 240, 0, 255, v -> players.getValue()));
    //Animals
    public Setting<Boolean> animals = this.register(new Setting<>("Animals", true));
    public Setting<Integer> animalsRed = this.register(new Setting<>("Red", 240, 0, 255, v -> animals.getValue()));
    public Setting<Integer> animalsGreen = this.register(new Setting<>("Green", 240, 0, 255, v -> animals.getValue()));
    public Setting<Integer> animalsBlue = this.register(new Setting<>("Blue", 240, 0, 255, v -> animals.getValue()));
    public Setting<Integer> animalsAlpha = this.register(new Setting<>("Alpha", 240, 0, 255, v -> animals.getValue()));
    //MobHostil
    public Setting<Boolean> mobhostil = this.register(new Setting<>("MobHostil", true));
    public Setting<Integer> mobhostilRed = this.register(new Setting<>("Red", 240, 0, 255, v -> mobhostil.getValue()));
    public Setting<Integer> mobhostilGreen = this.register(new Setting<>("Green", 240, 0, 255, v -> mobhostil.getValue()));
    public Setting<Integer> mobhostilBlue = this.register(new Setting<>("Blue", 240, 0, 255, v -> mobhostil.getValue()));
    public Setting<Integer> mobhostilAlpha = this.register(new Setting<>("Alpha", 240, 0, 255, v -> mobhostil.getValue()));
    //Villagers
    public Setting<Boolean> villagers = this.register(new Setting<>("Villagers", true));
    public Setting<Integer> villagersRed = this.register(new Setting<>("Red", 240, 0, 255, v -> villagers.getValue()));
    public Setting<Integer> villagersGreen = this.register(new Setting<>("Green", 240, 0, 255, v -> villagers.getValue()));
    public Setting<Integer> villagersBlue = this.register(new Setting<>("Blue", 240, 0, 255, v -> villagers.getValue()));
    public Setting<Integer> villagersAlpha = this.register(new Setting<>("Alpha", 240, 0, 255, v -> villagers.getValue()));
    //Mobs
    public Setting<Boolean> mobs = this.register(new Setting<>("Mobs", true));
    public Setting<Integer> mobsRed = this.register(new Setting<>("Red", 240, 0, 255, v -> mobs.getValue()));
    public Setting<Integer> mobsGreen = this.register(new Setting<>("Green", 240, 0, 255, v -> mobs.getValue()));
    public Setting<Integer> mobsBlue = this.register(new Setting<>("Blue", 240, 0, 255, v -> mobs.getValue()));
    public Setting<Integer> mobsAlpha = this.register(new Setting<>("Alpha", 240, 0, 255, v -> mobs.getValue()));

    public Tracers() {
        super("Tracers", "Tracers", Module.Category.RENDER, true, false, false);
    }

    public void onPreRender3D(MatrixStack stack) {
        if (mc.player == null) return;
        boolean prevBob = mc.options.getBobView().getValue();
        mc.options.getBobView().setValue(false);

        //Players
    if (players.getValue()){
        for (PlayerEntity player : mc.world.getPlayers()) {//new
            if (player == mc.player)
            //Skip player(myplayer)
               continue;

            Color color1 = new Color(playerRed.getValue(),playerGreen.getValue(),playerBlue.getValue(),playerAlpha.getValue());

            if (CrackedClient.friendManager.isFriend(player))
                color1 = new Color(friendRed.getValue(),friendGreen.getValue(),friendBlue.getValue(),friendAlpha.getValue());

            Vec3d vec2 = new Vec3d(0, 0, 75)
                    .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                    .add(mc.cameraEntity.getEyePos());

            double x = player.prevX + (player.getX() - player.prevX) * mc.getTickDelta();
            double y = player.prevY + (player.getY() - player.prevY) * mc.getTickDelta();
            double z = player.prevZ + (player.getZ() - player.prevZ) * mc.getTickDelta();

            RenderTracers.drawLineDebug(vec2.x, vec2.y, vec2.z, x, y + height.getValue(), z, color1, 1f);
        }
    }
        //Animals
    if (animals.getValue()){
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof AnimalEntity)) {
                //Skip Others entities
               continue;
            }

            Color color1 = new Color(animalsRed.getValue(),animalsGreen.getValue(),animalsBlue.getValue(),animalsAlpha.getValue());
            //remove friend Color
            Vec3d vec2 = new Vec3d(0, 0, 75)
                    .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                    .add(mc.cameraEntity.getEyePos());

            double x = entity.prevX + (entity.getX() - entity.prevX) * mc.getTickDelta();
            double y = entity.prevY + (entity.getY() - entity.prevY) * mc.getTickDelta();
            double z = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getTickDelta();

            RenderTracers.drawLineDebug(vec2.x, vec2.y, vec2.z, x, y + height.getValue(), z, color1, 1f);
        }
    }
    if (mobhostil.getValue()) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof Monster ) {
                if (!(entity instanceof MobEntity)) {
                    //Skip Others entities
                   continue;
                }

                Color color1 = new Color(mobhostilRed.getValue(),mobhostilGreen.getValue(),mobhostilBlue.getValue(),mobhostilAlpha.getValue());
                //remove Friend Color
                Vec3d vec2 = new Vec3d(0, 0, 75)
                        .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                        .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                        .add(mc.cameraEntity.getEyePos());
    
                double x = entity.prevX + (entity.getX() - entity.prevX) * mc.getTickDelta();
                double y = entity.prevY + (entity.getY() - entity.prevY) * mc.getTickDelta();
                double z = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getTickDelta();
    
                RenderTracers.drawLineDebug(vec2.x, vec2.y, vec2.z, x, y + height.getValue(), z, color1, 1f);
            }
        }
    }
    //VillagerEntity
    if (villagers.getValue()) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof VillagerEntity ) {
                if (!(entity instanceof VillagerEntity)) {
                    //Skip Others entities
                   continue;
                }

                Color color1 = new Color(villagersRed.getValue(),villagersGreen.getValue(),villagersBlue.getValue(),villagersAlpha.getValue());
                //remove Friend Color
                Vec3d vec2 = new Vec3d(0, 0, 75)
                        .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                        .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                        .add(mc.cameraEntity.getEyePos());
    
                double x = entity.prevX + (entity.getX() - entity.prevX) * mc.getTickDelta();
                double y = entity.prevY + (entity.getY() - entity.prevY) * mc.getTickDelta();
                double z = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getTickDelta();
    
                RenderTracers.drawLineDebug(vec2.x, vec2.y, vec2.z, x, y + height.getValue(), z, color1, 1f);
            }
        }
    }
        //Mobs
    if (mobs.getValue()) {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof MobEntity)) {
                //Skip Others entities
               continue;
            }

            Color color1 = new Color(mobsRed.getValue(),mobsGreen.getValue(),mobsBlue.getValue(),mobsAlpha.getValue());
            //remove Friend Color
            Vec3d vec2 = new Vec3d(0, 0, 75)
                    .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                    .add(mc.cameraEntity.getEyePos());

            double x = entity.prevX + (entity.getX() - entity.prevX) * mc.getTickDelta();
            double y = entity.prevY + (entity.getY() - entity.prevY) * mc.getTickDelta();
            double z = entity.prevZ + (entity.getZ() - entity.prevZ) * mc.getTickDelta();

            RenderTracers.drawLineDebug(vec2.x, vec2.y, vec2.z, x, y + height.getValue(), z, color1, 1f);
        }
    }

        mc.options.getBobView().setValue(prevBob);
    }
}
