package me.alpha432.oyvey.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.TimeoutException;
import me.alpha432.oyvey.event.impl.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.handler.PacketEncoderException;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

@Mixin( ClientConnection.class )
public class MixinClientConnection {

    @Shadow private Channel channel;
    @Shadow @Final private NetworkSide side;


	@Inject(at = { @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", ordinal = 0) }, method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
	protected void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
		PacketEvent event = new PacketEvent.Receive(packet);
		EVENT_BUS.post(event);
		if (event.isCancelled()) {
			ci.cancel();
		}
	}
	@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
	private void onHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
		PacketEvent event = new PacketEvent.Receive(packet);
		if (event.isCancelled()) {
			ci.cancel();
		}
	}
	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"),cancellable = true)
	private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
		PacketEvent.Send event = new PacketEvent.Send(packet);
		EVENT_BUS.post(event);
		if (event.isCancelled()) {
			info.cancel();
		}
	}

    /*
    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
	private void exceptionCaught(ChannelHandlerContext context, Throwable throwable, CallbackInfo ci) {
		if (!(throwable instanceof TimeoutException) && !(throwable instanceof PacketEncoderException) && ExceptionPatcher.INSTANCE.isOn()) {
			if (ExceptionPatcher.INSTANCE.log.getValue()) CommandManager.sendChatMessage("§4[!] Caught exception: §7" + throwable.getMessage());
			ci.cancel();
		}
	}*/
    /*

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo ci) {
        if (this.channel.isOpen() && packet != null) {
            PacketEvent.Receive event = new PacketEvent.Receive(packet);
            EVENT_BUS.post(event);
            if (event.isCancelled()) ci.cancel();
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean isAsync, CallbackInfo ci) {
        if (this.side != NetworkSide.CLIENTBOUND) return;
        PacketEvent.Send event = new PacketEvent.Send(packet);
        EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }*/


}