package me.alpha432.oyvey.util.soundutil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class SoundUtill {
        public static void playSound(final Identifier rl) {
            MinecraftClient.getInstance().execute(() -> {
                try {
                    Clip clip = AudioSystem.getClip();
                    ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
                    Resource resource = resourceManager.getResource(rl).orElseThrow();
                    InputStream inputStream = resource.getInputStream();
                    byte[] buffer = inputStream.readNBytes(inputStream.available());
                    InputStream bufferedInputStream = new ByteArrayInputStream(buffer);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
}