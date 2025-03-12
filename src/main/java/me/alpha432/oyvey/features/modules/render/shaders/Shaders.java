package me.alpha432.oyvey.features.modules.render.shaders;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.AspectRatio;
import me.alpha432.oyvey.features.settings.Setting;

import me.alpha432.oyvey.manager.shader.ShaderManager;

public class Shaders extends Module {
    private static Shaders INSTANCE = new Shaders();
    private final Setting<Boolean> select = this.register(new Setting<>("Select", false));
    private final Setting<Boolean> hands = this.register(new Setting<>("Hands", true)).withParent(select);
    private final Setting<Boolean> players = this.register(new Setting<>("Players", true)).withParent(select);
    private final Setting<Boolean> friends = this.register(new Setting<>("Friends", true)).withParent(select);
    private final Setting<Boolean> crystals = this.register(new Setting<>("Crystals", true)).withParent(select);
    private final Setting<Boolean> creatures = this.register(new Setting<>("Creatures", false)).withParent(select);
    private final Setting<Boolean> monsters = this.register(new Setting<>("Monsters", false)).withParent(select);
    private final Setting<Boolean> ambients = this.register(new Setting<>("Ambients", false)).withParent(select);
    private final Setting<Boolean> others = this.register(new Setting<>("Others", false)).withParent(select);

    public Setting<ShaderManager.Shader> mode = this.register(new Setting<>("Mode", ShaderManager.Shader.Default));
    public Setting<ShaderManager.Shader> handsMode = this.register(new Setting<>("HandsMode", ShaderManager.Shader.Default));

    public final Setting<Integer> maxRange = this.register(new Setting<>("MaxRange", 64, 16, 256));

    public final Setting<Float> factor = this.register(new Setting<>("GradientFactor", 2f, 0f, 20f));
    public final Setting<Float> gradient = this.register(new Setting<>("Gradient", 2f, 0f, 20f));
    public final Setting<Integer> alpha2 = this.register(new Setting<>("GradientAlpha", 170, 0, 255));
    public final Setting<Integer> lineWidth = this.register(new Setting<>("LineWidth", 2, 0, 20));
    public final Setting<Integer> quality = this.register(new Setting<>("Quality", 3, 0, 20));
    public final Setting<Integer> octaves = this.register(new Setting<>("SmokeOctaves", 10, 5, 30));
    public final Setting<Integer> fillAlpha = this.register(new Setting<>("FillAlpha", 170, 0, 255));
    public final Setting<Boolean> glow = this.register(new Setting<>("SmokeGlow", true));

    public final Setting<Boolean> colors = this.register(new Setting<>("Colors", false));


    public final Setting<Integer> outlineColorRed = this.register(new Setting<>("OutlineRed", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColorGreen = this.register(new Setting<>("OutlineGreen", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColorBlue = this.register(new Setting<>("OutlineBlue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColorAlpha = this.register(new Setting<>("OutlineAlpha", 170, 0, 255)).withParent(colors);

    public final Setting<Integer> outlineColor1Red = this.register(new Setting<>("SmokeOutlineRed", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor1Green = this.register(new Setting<>("SmokeOutlineGreen", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor1Blue = this.register(new Setting<>("SmokeOutlineBlue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor1Alpha = this.register(new Setting<>("SmokeOutlineAlpha", 170, 0, 255)).withParent(colors);

    public final Setting<Integer> outlineColor2Red = this.register(new Setting<>("SmokeOutline2Red", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor2Green = this.register(new Setting<>("SmokeOutline2Green", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor2Blue = this.register(new Setting<>("SmokeOutline2Blue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> outlineColor2Alpha = this.register(new Setting<>("SmokeOutline2Alpha", 170, 0, 255)).withParent(colors);

    public final Setting<Integer> fillColor1Red = this.register(new Setting<>("FillRed", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor1Green = this.register(new Setting<>("FillGreen", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor1Blue = this.register(new Setting<>("FillBlue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor1Alpha = this.register(new Setting<>("FillAlpha", 170, 0, 255)).withParent(colors);

    public final Setting<Integer> fillColor2Red = this.register(new Setting<>("SmokeFillRed", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor2Green = this.register(new Setting<>("SmokeFillGreen", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor2Blue = this.register(new Setting<>("SmokeFillBlue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor2Alpha = this.register(new Setting<>("SmokeFillAlpha", 170, 0, 255)).withParent(colors);

    public final Setting<Integer> fillColor3Red = this.register(new Setting<>("SmokeFill2Red", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor3Green = this.register(new Setting<>("SmokeFill2Green", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor3Blue = this.register(new Setting<>("SmokeFill2Blue", 170, 0, 255)).withParent(colors);
    public final Setting<Integer> fillColor3Alpha = this.register(new Setting<>("SmokeFill2Alpha", 170, 0, 255)).withParent(colors);

    public Shaders() {
        super("Shaders", "Shaders", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static Shaders getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Shaders();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    public boolean shouldRender(Entity entity) {
        if (entity == null)
            return false;

        if (mc.player == null)
            return false;

        if (mc.player.squaredDistanceTo(entity.getPos()) > maxRange.getPow2Value())
            return false;

        if (entity instanceof PlayerEntity) {
            if (entity == mc.player)
                return false;
            if (CrackedClient.friendManager.isFriend((PlayerEntity) entity))
                return friends.getValue();
            return players.getValue();
        }

        if (entity instanceof EndCrystalEntity)
            return crystals.getValue();

        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE, WATER_CREATURE -> creatures.getValue();
            case MONSTER -> monsters.getValue();
            case AMBIENT, WATER_AMBIENT -> ambients.getValue();
            default -> others.getValue();
        };
    }

    public void onPreRender3D(MatrixStack matrices) {
        if (hands.getValue())
        CrackedClient.shaderManager.renderShader(()-> mc.gameRenderer.renderHand(matrices, mc.gameRenderer.getCamera(), mc.getTickDelta()), handsMode.getValue());
    }

    @Override
    public void onDisable() {
        CrackedClient.shaderManager.reloadShaders();
    }
}
