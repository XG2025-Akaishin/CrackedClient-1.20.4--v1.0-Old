package me.alpha432.oyvey.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import me.alpha432.oyvey.CrackedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static me.alpha432.oyvey.util.traits.Util.mc;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow
    @Final
    private Window window;

    @Shadow
    private static MinecraftClient instance;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setIcon(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/client/util/Icons;)V"))
    private void onChangeIcon(Window instance, ResourcePack resourcePack, Icons icons) throws IOException {
        RenderSystem.assertInInitPhase();

        if (GLFW.glfwGetPlatform() == 393218) {
            MacWindowUtil.setApplicationIconImage(icons.getMacIcon(resourcePack));
            return;
        }
        setWindowIcon(CrackedClient.class.getResourceAsStream("/logo.png"), CrackedClient.class.getResourceAsStream("/logo.png"));
    }

    public void setWindowIcon(InputStream img16x16, InputStream img32x32) {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            GLFWImage.Buffer buffer = GLFWImage.malloc(2, memorystack);
            List<InputStream> imgList = List.of(img16x16, img32x32);
            List<ByteBuffer> buffers = new ArrayList<>();

            for (int i = 0; i < imgList.size(); i++) {
                NativeImage nativeImage = NativeImage.read(imgList.get(i));
                ByteBuffer bytebuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);

                bytebuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());
                buffer.position(i);
                buffer.width(nativeImage.getWidth());
                buffer.height(nativeImage.getHeight());
                buffer.pixels(bytebuffer);

                buffers.add(bytebuffer);
            }

            GLFW.glfwSetWindowIcon(mc.getWindow().getHandle(), buffer);
            buffers.forEach(MemoryUtil::memFree);
        } catch (IOException ignored) {
        }
    }

	@Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
	private void onClose(CallbackInfo ci) {
		try {
			CrackedClient.endClient();
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
}
