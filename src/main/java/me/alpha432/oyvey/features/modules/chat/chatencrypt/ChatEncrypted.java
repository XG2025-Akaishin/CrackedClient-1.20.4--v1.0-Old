package me.alpha432.oyvey.features.modules.chat.chatencrypt;

import java.util.ArrayList;
import java.util.Objects;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.chat.chatencrypt.utils.AES128;
import me.alpha432.oyvey.features.modules.chat.chatencrypt.utils.Base_64;
import me.alpha432.oyvey.features.modules.chat.chatencrypt.utils.EncryptGoPro;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatEncrypted extends Module {
    public static String encryptMessage = "";
    public static PacketByteBuf buf;
    private String string;
    public static Base_64 Base64;
    private static AES128 AES = new AES128( "dTrtjACjVaoqHbVVhfTQOrRadnl4guUk2NKhVycotpG4uyJG4f6vIczoCpyuPTrr2iWrm2l9KKMBLf8mVb2yERXvMdbi3OSQxR9X" );
    private final Setting<Boolean> chatencrypted = this.register(new Setting<Boolean>("ChatEncrypted", false));
    private final Setting<ModeEncrypt> encryptMode = this.register(new Setting<ModeEncrypt>("ModeEncrypt", ModeEncrypt.AES128));
    private final Setting<Boolean> encryptchat = this.register(new Setting<Boolean>("EncryptChat", false));
    private final Setting<Boolean> decryptchat = this.register(new Setting<Boolean>("DecryptChat", false));

    public ChatEncrypted() {
    super("ChatEncrypted", "Modifies your chat ChatEncrypted", Module.Category.CHAT, true, false, false);
    }

    @Subscribe//Encrypt Message
	public void onPacketSend(PacketEvent.Send event) {
		if (event.getPacket() instanceof ChatMessageC2SPacket packet && encryptchat.getValue() && chatencrypted.getValue()) {
            if (Objects.equals(packet.chatMessage(), string)) {
                return;
            }
			if (packet.chatMessage().length() > 256) {
                sendMessage("Encrypted message length was too long, couldn't send!");
				return;
			}
            if (encryptMode.getValue() == ModeEncrypt.AES128) {
            string = "[" + AES.encrypt((String)packet.chatMessage()).toString() + "]";
            mc.player.networkHandler.sendChatMessage("[" + AES.encrypt((String)packet.chatMessage()).toString() + "]");
            sendMessage("" + Formatting.BOLD + Formatting.RED + "Encrypted Message: " + Formatting.GOLD +"[" + AES.encrypt((String)packet.chatMessage()).toString() + "]");
            } else 
            if (encryptMode.getValue() == ModeEncrypt.BASE64) {
            try{
            string = "[" + Base64.encrypt((String)packet.chatMessage()).toString() + "]";
            mc.player.networkHandler.sendChatMessage("[" + Base64.encrypt((String)packet.chatMessage()).toString() + "]");
               } catch ( Exception e ) {
                    System.out.println("Error Encrypt Base64");
                }
            } else if (encryptMode.getValue() == ModeEncrypt.GO) {
                string = "[" + EncryptGoPro.ConvertString(EncryptGoPro.encrypt((String)packet.chatMessage(), 782367836)).toString() + "]";
                mc.player.networkHandler.sendChatMessage("[" + EncryptGoPro.ConvertString(EncryptGoPro.encrypt((String)packet.chatMessage(), 782367836)).toString() + "]");
                sendMessage("" + Formatting.BOLD + Formatting.RED + "Encrypted Message: " + Formatting.GOLD +"[" + EncryptGoPro.ConvertString(EncryptGoPro.encrypt((String)packet.chatMessage(), 782367836)).toString() + "]");
            }
            event.setCancelled(true);
			//((ChatMessageC2SPacket) packet).message = s;
		}
		return;
	}

    @Subscribe //Decrypt Messsage
    public void onPacketReceivex(PacketEvent.Receive event) {
        if (event.getPacket() instanceof GameMessageS2CPacket pac && decryptchat.getValue() && chatencrypted.getValue()) {
        Text text = pac.content();
        String s = text.getString();
         
        //Mode AES128
            if (this.decryptchat.getValue() && this.encryptMode.getValue() == ModeEncrypt.AES128) {
                String name = getSender(s);
                String chatText = s;//"[Hello] world, this is a test code";
                int startIndex, endIndex = 0;
                while ((startIndex = chatText.indexOf('[', endIndex)) != -1) {
                    endIndex = chatText.indexOf(']', startIndex + 1);
                    if (endIndex != -1 && endIndex > startIndex) {
                        String subString = chatText.substring(startIndex + 1, endIndex);
                        String decryptMessage = AES.decrypt(subString);
                        sendMessage("" + Formatting.BOLD + Formatting.DARK_GREEN + "Decrypted message: "+ Formatting.DARK_AQUA + name + Formatting.BLUE  + ": "+ Formatting.GREEN + decryptMessage);
                    } else {
                        System.out.println("Invalid substring");
                    }
                }
            } else
        //Mode Base64
        if (this.decryptchat.getValue() && this.encryptMode.getValue() == ModeEncrypt.GO) {
            String name = getSender(s);
            String chatText = s;//"[Hello] world, this is a test code";
            int startIndex, endIndex = 0;
            while ((startIndex = chatText.indexOf('[', endIndex)) != -1) {
                endIndex = chatText.indexOf(']', startIndex + 1);
                if (endIndex != -1 && endIndex > startIndex) {
                    String subString = chatText.substring(startIndex + 1, endIndex);
                    try {
                    String decryptMessage = EncryptGoPro.decrypt(EncryptGoPro.convertStringToIntArray(subString),782367836);
                    sendMessage("" + Formatting.BOLD + Formatting.DARK_GREEN + "Decrypted message: "+ Formatting.DARK_AQUA + name + Formatting.BLUE  + ": "+ Formatting.GREEN + decryptMessage);
                    } catch ( Exception e ) {
                        System.out.println("Error Encrypt Base64");
                    }
                } else {
                    System.out.println("Invalid substring");
                }
            }
        } else if (this.decryptchat.getValue() && this.encryptMode.getValue() == ModeEncrypt.BASE64) {
            String name = getSender(s);
            String chatText = s;//"[Hello] world, this is a test code";
            int startIndex, endIndex = 0;
            while ((startIndex = chatText.indexOf('[', endIndex)) != -1) {
                endIndex = chatText.indexOf(']', startIndex + 1);
                if (endIndex != -1 && endIndex > startIndex) {
                    String subString = chatText.substring(startIndex + 1, endIndex);
                    try {
                    String decryptMessage = Base64.decrypt(subString);
                    sendMessage("" + Formatting.BOLD + Formatting.DARK_GREEN + "Decrypted message: "+ Formatting.DARK_AQUA + name + Formatting.BLUE  + ": "+ Formatting.GREEN + decryptMessage);
                    } catch ( Exception e ) {
                        System.out.println("Error Encrypt Base64");
                    }
                } else {
                    System.out.println("Invalid substring");
                }
            }
        }

        }
    }

    @Override
    public String getDisplayInfo() {
        return encryptMode.getValue().name();
    }

    private String getSender(String message) {
        ArrayList<PlayerListEntry> entry = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
        for (PlayerListEntry player : entry) {
            if (message.contains(player.getProfile().getName())) return player.getProfile().getName();
        }

        return "null";
    }

    public enum ModeEncrypt {
        AES128,
        BASE64,
        GO
    }
}
