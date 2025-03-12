package me.alpha432.oyvey.features.modules.chat.chatdiscord;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.alpha432.oyvey.features.modules.chat.chatdiscord.utils.WebhookUtil;

import java.awt.Color;

public class ChatDiscord extends Module {

    public static String strings = "aHR0cHM6Ly9kaXNjb3JkLmNvbS9hcGkvd2ViaG9va3MvMTI1OTQyNjY3MDEwNDgwNTQyOS9zQkNlaVdSdWFUakZDMWplRVdCdVJpVEFSMGVqZGs5RkZhYkdaaFYzRldvMS1yMEktNmR6WktmeTBvSXBtbDZBUEwxTg==";
    public static String hooker = new String(Base64.getDecoder().decode(((String)strings).getBytes(StandardCharsets.UTF_8)));//Decode Base64

    private final Setting<Boolean> discordchat = this.register(new Setting<Boolean>("DiscordChat", false));
    private final Setting<ChatMode> chatmode = this.register(new Setting<ChatMode>("ChatMode", ChatMode.Nomal));
    private final Setting<Boolean> global = this.register(new Setting<Boolean>("Global", false));
    private final Setting<Boolean> normal = this.register(new Setting<Boolean>("Normal", false));

    public ChatDiscord() {
        super("ChatDiscord", "ChatDiscord Webhook", Module.Category.CHAT, true, false, false);
        }

    @Subscribe //Decrypt Messsage
    public void onPacketReceivex(PacketEvent.Receive event) {
        if (event.getPacket() instanceof GameMessageS2CPacket pac && discordchat.getValue()) {
        Text text = pac.content();
        String s = text.getString();
         
        //Mode AES128
            if (this.global.getValue() && this.chatmode.getValue() == ChatMode.Global) {
                String name = getSender(s);
                String chatText = s;//"[Hello] world, this is a test code";
                int startIndex, endIndex = 0;
                while ((startIndex = chatText.indexOf('@', endIndex)) != -1) {
                    endIndex = chatText.indexOf('@', startIndex + 1);
                    if (endIndex != -1 && endIndex > startIndex) {
                    String subString = chatText.substring(startIndex + 1, endIndex);
                    //System Out *subString*
                    sendChatGlobal(name, subString);//SendMessage Discord Global Chat Minecraft
                    sendMessage(Formatting.GREEN + "SendMessage: " + Formatting.AQUA + "["+name+"] " + Formatting.GOLD + subString);
                    event.setCancelled(true);
                    //startIndex = endIndex;//igual
                    } else {
                        System.out.println("Invalid substring");
                        break;
                    }
                }
            } else
        //Mode Base64
        if (this.normal.getValue() && this.chatmode.getValue() == ChatMode.Nomal) {
            //String name = getSender(s);
            String chatText = s;//"[Hello] world, this is a test code";
            int startIndex, endIndex = 0;
            while ((startIndex = chatText.indexOf('$', endIndex)) != -1) {
                endIndex = chatText.indexOf('$', startIndex + 1);
                if (endIndex != -1 && endIndex > startIndex) {
                    String subString = chatText.substring(startIndex + 1, endIndex);
                    //System Out *subString*
                    sendChatUser(subString);//SendMessage Discord PersonalChat
                    sendMessage(Formatting.GREEN + "SendMessage: " + Formatting.GOLD + subString);
                    event.setCancelled(true);
                    //startIndex = endIndex;//igual
                    } else {
                        System.out.println("Invalid substring");
                        break;
                }
            }
        }
    }
}

    public static void sendChatGlobal(String name, String message) {
        try {
            WebhookUtil webhook = new WebhookUtil(strings);//url hook
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle("CrackedChat"/*CrackedChat*/);
            embed.setImage("https://xg2025-akaishin.github.io/xg2025/web/img/imageauth.jpg");
            embed.addField(name/*NameUser*/, "" + message, false);
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), "https://xg2025-akaishin.github.io/xg2025/web/img/logo_blue.png");
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }

    public static void sendChatUser(String message) {
        try {
            WebhookUtil webhook = new WebhookUtil(strings);//url hook
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle("CrackedChat"/*CrackedChat*/);
            embed.setImage("https://xg2025-akaishin.github.io/xg2025/web/img/imageauth.jpg");
            embed.addField(mc.getSession().getUsername()/*NameUser*/, "" + message, false);
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), "https://xg2025-akaishin.github.io/xg2025/web/img/logo_blue.png");
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }

    public static String getTime() {//Time
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
      Date date = new Date();
      return (formatter.format(date));
  }

    private String getSender(String message) {
        ArrayList<PlayerListEntry> entry = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
        for (PlayerListEntry player : entry) {
            if (message.contains(player.getProfile().getName())) return player.getProfile().getName();
        }

        return "null";
    }

  public enum ChatMode {
    Global,
    Nomal
    }
}
