package me.alpha432.oyvey.features.modules.render.nametags.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class TextUtil {
	public static final Matrix4f lastProjMat = new Matrix4f();
	public static final Matrix4f lastModMat = new Matrix4f();
	public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
	public static Vec3d worldSpaceToScreenSpace(Vec3d pos) {
		Camera camera = mc.getEntityRenderDispatcher().camera;
		int displayHeight = mc.getWindow().getHeight();
		int[] viewport = new int[4];
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		Vector3f target = new Vector3f();

		double deltaX = pos.x - camera.getPos().x;
		double deltaY = pos.y - camera.getPos().y;
		double deltaZ = pos.z - camera.getPos().z;

		Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(lastWorldSpaceMatrix);
		Matrix4f matrixProj = new Matrix4f(lastProjMat);
		Matrix4f matrixModel = new Matrix4f(lastModMat);
		matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);
		return new Vec3d(target.x / mc.getWindow().getScaleFactor(), (displayHeight - target.y) / mc.getWindow().getScaleFactor(), target.z);
	}
}
