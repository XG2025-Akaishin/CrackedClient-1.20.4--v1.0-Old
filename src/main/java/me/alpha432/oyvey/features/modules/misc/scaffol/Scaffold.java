package me.alpha432.oyvey.features.modules.misc.scaffol;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.AnimateUtil;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.BlockUtil;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.EntityUtil;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.InventoryUtil;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.MovementUtil;
import me.alpha432.oyvey.features.modules.misc.scaffol.utils.Timer;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.rendernew.Render3DUtil;

import java.awt.*;

public class Scaffold extends Module {
    private static Scaffold INSTANCE = new Scaffold();
    private final Setting<Boolean> tower  = this.register(new Setting<Boolean>("Tower", true));
    private final Setting<Boolean> rotate  = this.register(new Setting<Boolean>("Rotate", false));

    private final Setting<Integer> rotateTime  = this.register(new Setting<>("Rotate", 1000, 0, 3000));
    private final Setting<Boolean> render  = this.register(new Setting<Boolean>("Render", false));

    private final Setting<Integer> colorRed = this.register(new Setting<>("ColorRed", 120, 0, 255, v -> render.getValue()));
    private final Setting<Integer> colorGreen = this.register(new Setting<>("ColorGreen", 120, 0, 255, v -> render.getValue()));
    private final Setting<Integer> colorBlue = this.register(new Setting<>("ColorBlue", 120, 0, 255, v -> render.getValue()));
    private final Setting<Integer> colorAlpha = this.register(new Setting<>("ColorAlpha", 120, 0, 255, v -> render.getValue()));

    private final Setting<Boolean> esp  = this.register(new Setting<Boolean>("ESP", true, v -> render.getValue()));
    private final Setting<Boolean> box  = this.register(new Setting<Boolean>("Box", true, v -> render.getValue()));
    private final Setting<Boolean> outline  = this.register(new Setting<Boolean>("Outline", true, v -> render.getValue()));
    private final Setting<Double> sliderSpeed = this.register(new Setting<Double>("SliderSpeed", 0.2, 0.01, 1.0, v -> render.getValue()));

    public final Setting<Boolean> invSwapBypass = this.register(new Setting<Boolean>("InvSwapBypass", true)); //add(new BooleanSetting("InvSwapBypass", true));
    public final Setting<Boolean> lowVersion = this.register(new Setting<Boolean>("1.12", false)); //add(new BooleanSetting("1.12", false));
    public final Setting<Boolean> rotateSync = this.register(new Setting<Boolean>("RotateSync", true)); // add(new BooleanSetting("RotateSync", true));
    public final Setting<Boolean> packetPlace = this.register(new Setting<Boolean>("PacketPlace", true)); // add(new BooleanSetting("PacketPlace", true));
    public final Setting<Boolean> randomPitch = this.register(new Setting<Boolean>("RandomPitch", false)); // add(new BooleanSetting("RandomPitch", false));
    public final Setting<Boolean> rotations = this.register(new Setting<Boolean>("ShowRotations", true)); // add(new BooleanSetting("ShowRotations", true));
    public final Setting<Boolean> attackRotate = this.register(new Setting<Boolean>("AttackRotate", false)); //add(new BooleanSetting("AttackRotate", false));
    public final Setting<Placement> placement = this.register(new Setting<>("Placement",Placement.Vanilla)); //add(new Enum<>("Placement", Placement.Vanilla));
    public final Setting<Double> rotateTimeS = this.register(new Setting<Double>("RotateTime", 0.5, 0.0, 1.0)); //add(new SliderSetting("RotateTime", 0.5, 0, 1, 0.01));
    public final Setting<Double> attackDelay = this.register(new Setting<Double>("AttackDelay", 0.2, 0.0, 1.0)); //add(new SliderSetting("AttackDelay", 0.2, 0, 1, 0.01));
    //public final Setting<Double> tp = 50D; //add(new SliderSetting("TP", 50, 0, 300, 0.01));

    public final Setting<Boolean> test = this.register(new Setting<Boolean>("Test", true)); //add(new BooleanSetting("Test", true));

    public final Setting<Double> boxSize = this.register(new Setting<Double>("BoxSize", 0.6, 0.0, 1.0)); //add(new SliderSetting("BoxSize", 0.6, 0, 1, 0.01));
    public final Setting<Boolean> inventorySync = this.register(new Setting<Boolean>("InventorySync", false)); //add(new BooleanSetting("InventorySync", false));
    public final Setting<SwingSide> swingMode = this.register(new Setting<>("SwingMode",  SwingSide.Server)); //add(new EnumSetting<>("SwingMode", SwingSide.Server));
    public final Setting<Boolean> obsMode = this.register(new Setting<Boolean>("OBSServer", false)); //add(new BooleanSetting("OBSServer", false));
    public final Setting<OFFMain> modeMain = this.register(new Setting<>("ModeHand",  OFFMain.MAIN_HAND));
    public final Setting<Boolean> customhand = this.register(new Setting<Boolean>("CustomHand", true));
    public Scaffold() {
        super("Scaffold", "Scaffold WHAT code", Module.Category.MISC, true, false ,false);
        this.setInstance();
    }

    public static Scaffold getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Scaffold();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private final Timer timer = new Timer();

    private float[] angle = null;

    //@EventHandler(priority =  EventPriority.HIGH)
    /*public void onRotation(RotateEvent event) {
        if (rotate.getValue() && !timer.passedMs(rotateTime.getValue()) && angle != null) {
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }*/

    @Override
    public void onEnable() {
        lastVec3d = null;
        pos = null;
    }

    private BlockPos pos;
    private static Vec3d lastVec3d;
    //@Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
        Color color = new Color(colorRed.getValue(), colorGreen.getValue(), colorBlue.getValue(), colorAlpha.getValue());
        if (render.getValue()) {
            /*if (esp.getValue()) {
                GL11.glEnable(GL11.GL_BLEND);
                double temp = 0.01;
                for (double i = 0; i < 0.8; i += temp) {
                    HoleSnap.doCircle(matrixStack, injectAlpha(color, (int) Math.min(color.getAlpha() * 2 / (0.8 / temp), 255)), i, new Vec3d(interpolate(mc.player.lastRenderX, mc.player.getX(), partialTicks), interpolate(mc.player.lastRenderY, mc.player.getY(), partialTicks), interpolate(mc.player.lastRenderZ, mc.player.getZ(), partialTicks)), 5);
                }
                RenderSystem.setShaderColor(1, 1, 1, 1);
                GL11.glDisable(GL11.GL_BLEND);
            }*/
            if (pos != null) {
                Vec3d cur = pos.toCenterPos();
                if (lastVec3d == null) {
                    lastVec3d = cur;
                } else {
                    lastVec3d = new Vec3d(AnimateUtil.animate(lastVec3d.getX(), cur.x, sliderSpeed.getValue()),
                            AnimateUtil.animate(lastVec3d.getY(), cur.y, sliderSpeed.getValue()),
                            AnimateUtil.animate(lastVec3d.getZ(), cur.z, sliderSpeed.getValue()));
                }
                Render3DUtil.draw3DBox(matrixStack, new Box(lastVec3d.add(0.5, 0.5, 0.5), lastVec3d.add(-0.5, -0.5, -0.5)), injectAlpha(color, color.getAlpha()), outline.getValue(), box.getValue());
            }
        }
    }
    
    private final Timer towerTimer = new Timer();
    @Override
    public void onUpdate() {
        int block = InventoryUtil.findBlock();
        if (block == -1) return;
        BlockPos placePos = EntityUtil.getPlayerPos().down();
        if (BlockUtil.clientCanPlace(placePos, false)) {
            int old = mc.player.getInventory().selectedSlot;
            if (BlockUtil.getPlaceSide(placePos) == null) {
                double distance = 1000;
                BlockPos bestPos = null;
                for (Direction i : Direction.values()) {
                    if (i == Direction.UP) continue;
                    if (BlockUtil.canPlace(placePos.offset(i))) {
                        if (bestPos == null || mc.player.squaredDistanceTo(placePos.offset(i).toCenterPos()) < distance) {
                            bestPos = placePos.offset(i);
                            distance = mc.player.squaredDistanceTo(placePos.offset(i).toCenterPos());
                        }
                    }
                }
                if (bestPos != null) {
                    placePos = bestPos;
                } else {
                    return;
                }
            }
            if (rotate.getValue()) {
                Direction side = BlockUtil.getPlaceSide(placePos);
                angle = EntityUtil.getLegitRotations(placePos.offset(side).toCenterPos().add(side.getOpposite().getVector().getX() * 0.5, side.getOpposite().getVector().getY() * 0.5, side.getOpposite().getVector().getZ() * 0.5));
                timer.reset();
            }
            InventoryUtil.switchToSlot(block);
            BlockUtil.placeBlock(placePos, rotate.getValue(), false);
            InventoryUtil.switchToSlot(old);
            pos = placePos;
            if (tower.getValue() && mc.options.jumpKey.isPressed() && !MovementUtil.isMoving()) {
                MovementUtil.setMotionY(0.42);
                MovementUtil.setMotionX(0);
                MovementUtil.setMotionZ(0);
                if (this.towerTimer.passedMs(1500L)) {
                    MovementUtil.setMotionY(-0.28);
                    this.towerTimer.reset();
                }
            } else {
                this.towerTimer.reset();
            }
        }
    }

    public static double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public enum Placement {
        Vanilla,
        Strict,
        Legit,
        AirPlace
    }

    public enum SwingSide {
        All,
        Client,
        Server,
        None
    }

    public enum OFFMain {
        OFF_HAND,
        MAIN_HAND,
        None
    }

    /*public static int injectAlpha(int color, int alpha) {
        return toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), alpha);
    }*/
}
