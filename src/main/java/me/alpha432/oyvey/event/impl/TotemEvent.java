package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;
import me.alpha432.oyvey.event.Stage;
import net.minecraft.entity.player.PlayerEntity;

public class TotemEvent extends Event {
    private final PlayerEntity player;
    
    public TotemEvent(PlayerEntity player) {
        //this.stage = stage;
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}