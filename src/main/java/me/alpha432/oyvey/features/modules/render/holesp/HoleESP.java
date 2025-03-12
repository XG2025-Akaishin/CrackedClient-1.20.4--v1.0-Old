package me.alpha432.oyvey.features.modules.render.holesp;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Timer;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.MathUtility;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.PlayerUtility;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Render2DEngine;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Render3DEngine;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.HoleUtility;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HoleESP extends Module {
    private static HoleESP INSTANCE = new HoleESP();
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.CubeOutline));
    private final Setting<Integer> rangeXZ = this.register(new Setting<>("Range XY", 10, 1, 128));
    private final Setting<Integer> rangeY = this.register(new Setting<>("Range Y", 5, 1, 128));

    private final Setting<Integer> indestrictibleColorr = this.register(new Setting<>("IndestructibleR", 55, 0, 255));
    private final Setting<Integer> indestrictibleColorg = this.register(new Setting<>("IndestructibleG", 55, 0, 255));
    private final Setting<Integer> indestrictibleColorb = this.register(new Setting<>("IndestructibleB", 55, 0, 255));
    private final Setting<Integer> indestrictibleColora = this.register(new Setting<>("IndestructibleA", 55, 0, 255));


    private final Setting<Integer> bedrockColorr = this.register(new Setting<>("BedrockR",55, 0, 255));
    private final Setting<Integer> bedrockColorg = this.register(new Setting<>("BedrockG",55, 0, 255));
    private final Setting<Integer> bedrockColorb = this.register(new Setting<>("BedrockB",55, 0, 255));
    private final Setting<Integer> bedrockColora = this.register(new Setting<>("BedrockA",55, 0, 255));

    private final Setting<Float> height = this.register(new Setting<>("Height", 1f, 0.01f, 5f));
    private final Setting<Float> lineWith = this.register(new Setting<>("Line Width", 0.5f, 0.01f, 5f));
    public final Setting<Boolean> culling = this.register(new Setting<>("Culling", true, v-> mode.getValue() == Mode.Fade || mode.getValue() == Mode.Fade2));

    private enum Mode {
        Fade,
        Fade2,
        CubeOutline,
        CubeFill,
        CubeBoth
    }

    private final Timer logicTimer = new Timer();
    private final List<BoxWithColor> positions = new CopyOnWriteArrayList<>();

    public HoleESP() {
        super("HoleESP", "HoleESP", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        positions.clear();
    }

    //@Override
    public void onPreRender3D(MatrixStack stack) {//onRender3D
        if (positions.isEmpty()) return;

        for (BoxWithColor pwc : positions) {
            switch (mode.getValue()) {
                case Fade -> renderFade(pwc);
                case Fade2 -> renderFade2(pwc);
                case CubeFill -> renderFill(pwc);
                case CubeOutline -> renderOutline(pwc);
                case CubeBoth -> {
                    renderOutline(pwc);
                    renderFill(pwc);
                }
            }
        }
    }

    public void renderFade(@NotNull HoleESP.BoxWithColor posWithColor) {
        Render3DEngine.FADE_QUEUE.add(
                new Render3DEngine.FadeAction(posWithColor.box, getColor(posWithColor.box, posWithColor.color(), 60), getColor(posWithColor.box, posWithColor.color(), 0))
        );
        Render3DEngine.OUTLINE_SIDE_QUEUE.add(
                new Render3DEngine.OutlineSideAction(posWithColor.box, getColor(posWithColor.box, posWithColor.color(), posWithColor.color.getAlpha()), lineWith.getValue(), Direction.DOWN)
        );
    }


    public void renderFade2(@NotNull HoleESP.BoxWithColor boxWithColor) {
        Render3DEngine.FADE_QUEUE.add(
                new Render3DEngine.FadeAction(boxWithColor.box, getColor(boxWithColor.box, boxWithColor.color(), 60), getColor(boxWithColor.box, boxWithColor.color(), 0)
                ));

        Render3DEngine.drawHoleOutline(
                boxWithColor.box, getColor(boxWithColor.box, boxWithColor.color(), boxWithColor.color.getAlpha()), lineWith.getValue()
        );

        Render3DEngine.FILLED_QUEUE.add(
                new Render3DEngine.FillAction(new Box(boxWithColor.box.minX, boxWithColor.box.minY, boxWithColor.box.minZ,
                        boxWithColor.box.maxX, boxWithColor.box.minY + 0.01f, boxWithColor.box.maxZ), getColor(boxWithColor.box, boxWithColor.color(), boxWithColor.color.getAlpha())
                )
        );
    }

    private Color getColor(Box box, Color color, int alpha) {
        float dist = PlayerUtility.squaredDistance2d(box.getCenter().getX(), box.getCenter().getZ());
        float factor = dist / (rangeXZ.getPow2Value());

        factor = 1f - easeOutExpo(factor);

        factor = MathUtility.clamp(factor, 0f, 1f);

        return Render2DEngine.injectAlpha(color, (int) (factor * alpha));
    }

    private float easeOutExpo(float x){
        return x == 1f ? 1f : (float) (1f - Math.pow(2f, -10f * x));
    }
    public void renderOutline(@NotNull HoleESP.BoxWithColor boxWithColor) {
        Render3DEngine.OUTLINE_QUEUE.add(
                new Render3DEngine.OutlineAction(boxWithColor.box, getColor(boxWithColor.box, boxWithColor.color(), boxWithColor.color.getAlpha()), lineWith.getValue())
        );
    }

    public void renderFill(@NotNull HoleESP.BoxWithColor boxWithColor) {
        Render3DEngine.FILLED_QUEUE.add(
                new Render3DEngine.FillAction(boxWithColor.box(), getColor(boxWithColor.box, boxWithColor.color(), boxWithColor.color.getAlpha()))
        );
    }

    @Override
    public void onThread() {
        if (fullNullCheck() /*|| !logicTimer.passedMs(100)*/)
            return;
        findHoles();
        //logicTimer.reset();
    }

    private void findHoles() {
        ArrayList<BoxWithColor> blocks = new ArrayList<>();
        if (mc.world == null || mc.player == null) {
            positions.clear();
            return;
        }
        BlockPos centerPos = BlockPos.ofFloored(mc.player.getPos());
        List<Box> boxes = new ArrayList<>();

        for (int i = centerPos.getX() - rangeXZ.getValue(); i < centerPos.getX() + rangeXZ.getValue(); i++) {
            for (int j = centerPos.getY() - rangeY.getValue(); j < centerPos.getY() + rangeY.getValue(); j++) {
                for (int k = centerPos.getZ() - rangeXZ.getValue(); k < centerPos.getZ() + rangeXZ.getValue(); k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + height.getValue(), pos.getZ() + 1);
                    Color color = new Color(indestrictibleColorr.getValue(),indestrictibleColorg.getValue(),indestrictibleColorb.getValue(),indestrictibleColora.getValue());//indestrictibleColor.getValue().getColorObject();
                    if (HoleUtility.validIndestructible(pos)) {
                    } else if (HoleUtility.validBedrock(pos)) {
                        color = new Color(bedrockColorr.getValue(),bedrockColorg.getValue(),bedrockColorb.getValue(),bedrockColora.getValue()); //bedrockColor.getValue().getColorObject();
                    } else if (HoleUtility.validTwoBlockBedrock(pos)) {
                        boolean east = mc.world.isAir(pos.offset(Direction.EAST));
                        boolean south = mc.world.isAir(pos.offset(Direction.SOUTH));
                        box = new Box(box.minX, box.minY, box.minZ, box.maxX + (east ? 1 : 0), box.maxY, box.maxZ + (south ? 1 : 0));
                        color = new Color(bedrockColorr.getValue(),bedrockColorg.getValue(),bedrockColorb.getValue(),bedrockColora.getValue()); //bedrockColor.getValue().getColorObject();
                    } else if (HoleUtility.validTwoBlockIndestructible(pos)) {
                        boolean east = mc.world.isAir(pos.offset(Direction.EAST));
                        boolean south = mc.world.isAir(pos.offset(Direction.SOUTH));
                        box = new Box(box.minX, box.minY, box.minZ, box.maxX + (east ? 1 : 0), box.maxY, box.maxZ + (south ? 1 : 0));
                    } else if (HoleUtility.validQuadBedrock(pos)) {
                        box = new Box(box.minX, box.minY, box.minZ, box.maxX + 1, box.maxY, box.maxZ + 1);
                        color = new Color(bedrockColorr.getValue(),bedrockColorg.getValue(),bedrockColorb.getValue(),bedrockColora.getValue());//bedrockColor.getValue().getColorObject();
                    } else if (HoleUtility.validQuadIndestructible(pos)) {
                        box = new Box(box.minX, box.minY, box.minZ, box.maxX + 1, box.maxY, box.maxZ + 1);
                    } else {
                        continue;
                    }

                    boolean skip = false;
                    for (Box boxOffset : boxes) {
                        if (boxOffset.intersects(box))
                            skip = true;
                    }

                    if (skip)
                        continue;

                    blocks.add(new BoxWithColor(box, color));
                    boxes.add(box);
                }
            }
        }
        positions.clear();
        positions.addAll(blocks);
    }

    public record BoxWithColor(Box box, Color color) {
    }
}
