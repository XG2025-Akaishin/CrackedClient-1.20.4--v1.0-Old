package me.alpha432.oyvey.features.modules.player.middleclick;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import  me.alpha432.oyvey.features.modules.player.middleclick.util.Timer;
import  me.alpha432.oyvey.features.modules.player.middleclick.util.PlayerUtility;
import  me.alpha432.oyvey.features.modules.player.middleclick.util.InventoryUtility;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.autototem.EventSync;

public class MiddleClick extends Module {
    private static MiddleClick INSTANCE = new MiddleClick();
    private final Setting<Boolean> friend = this.register(new Setting<>("Friend", false));
    private final Setting<Boolean> xp = this.register(new Setting<>("XP", true));
    private final Setting<Boolean> feetExp = this.register(new Setting<>("FeetXP", false, v -> xp.getValue()));
    private final Setting<Boolean> silent = this.register(new Setting<>("SilentXP", true, v -> xp.getValue()));
    private final Setting<Boolean> ep = this.register(new Setting<>("Pearl", false));
    private final Setting<Boolean> silentPearl = this.register(new Setting<>("SilentPearl", false, v -> ep.getValue()));
    private final Setting<Boolean> inventoryPearl = this.register(new Setting<>("InventoryPearl", false, v -> ep.getValue()));
    private final Setting<Integer> swapDelay = this.register(new Setting<>("SwapDelay", 100, 0, 1000, v -> !silentPearl.getValue()));
    private final Setting<Boolean> healingPot = this.register(new Setting<>("HealingPot", false));
    public final Setting<Boolean> antiPickUp = this.register(new Setting<>("AntiPickUp", false));

    private final Timer timer = new Timer();
    private int lastSlot = -1;

    public MiddleClick() {
        super("MiddleClick", "MiddleClick", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static MiddleClick getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MiddleClick();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo() {
        String sb = "";

        if (friend.getValue()) sb += ("Friend");
        if (xp.getValue()) sb += ("Experience");
        if (ep.getValue()) sb += ("EnderPearl");
        if (healingPot.getValue()) sb += ("PotionHeal");
        return sb;
    }

    //@EventHandler
    @Subscribe
    @SuppressWarnings("unused")
    private void onSync(EventSync event) {
        if (fullNullCheck()) return;

        if (xp.getValue() && feetExp.getValue() && mc.options.pickItemKey.isPressed())
            mc.player.setPitch(90);

        if (friend.getValue() && mc.currentScreen == null && mc.options.pickItemKey.isPressed() && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity entity && timer.passedMs(800)) {
            if (CrackedClient.friendManager.isFriend(entity.getName().getString())) {
                CrackedClient.friendManager.removeFriend(entity.getName().getString());
                sendMessage("§b" + entity.getName().getString() + "§r removed from friends!");
            } else {
                CrackedClient.friendManager.addFriend(entity.getName().getString());
                sendMessage("Added friend §b" + entity.getName().getString());
            }
            timer.reset();
            return;
        }

        if (ep.getValue() && timer.passedMs(500) && mc.currentScreen == null && mc.options.pickItemKey.isPressed()) {
            //if (ModuleManager.aura.isEnabled() && Aura.target != null)
                sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));

            if (silentPearl.getValue()) {
                if (!inventoryPearl.getValue() || (inventoryPearl.getValue() && findEPSlot() != -1)) {
                    int epSlot = findEPSlot();
                    int originalSlot = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        mc.player.getInventory().selectedSlot = epSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(epSlot));
                        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));
                        mc.player.getInventory().selectedSlot = originalSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                    }
                } else {
                    int epSlot = InventoryUtility.findItemInInventory(Items.ENDER_PEARL).slot();
                    if (epSlot != -1) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                    }
                }
            } else {
                if (!inventoryPearl.getValue() || (inventoryPearl.getValue() && findEPSlot() != -1)) {
                    int epSlot = findEPSlot();
                    int originalSlot = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        new PearlThread(mc.player, epSlot, originalSlot, swapDelay.getValue(), false).start();
                    }
                } else {
                    int epSlot = InventoryUtility.findItemInInventory(Items.ENDER_PEARL).slot();
                    int currentItem = mc.player.getInventory().selectedSlot;
                    if (epSlot != -1) {
                        sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                        new PearlThread(mc.player, epSlot, currentItem, swapDelay.getValue(), true).start();
                    }
                }
            }
            timer.reset();
        }

        if (xp.getValue()) {
            //if (noWasteXp.getValue() && !needXp()) return;
            if (mc.options.pickItemKey.isPressed()) {
                int slot = findXPSlot();
                if (slot != -1) {
                    int lastSlot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slot;
                    sendPacket(new UpdateSelectedSlotC2SPacket(slot));

                    sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));

                    if (silent.getValue()) {
                        mc.player.getInventory().selectedSlot = lastSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                    }
                } else if (lastSlot != -1) {
                    mc.player.getInventory().selectedSlot = lastSlot;
                    sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                    lastSlot = -1;
                }
            } else if (lastSlot != -1) {
                mc.player.getInventory().selectedSlot = lastSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
                lastSlot = -1;
            }
        }

        if (healingPot.getValue() && mc.currentScreen == null && mc.options.pickItemKey.isPressed() && timer.every(200)) {
            if (silentPearl.getValue()) {
                if (getHpSlot() != -1) {
                    int hpSlot = getHpSlot();
                    int originalSlot = mc.player.getInventory().selectedSlot;
                    if (hpSlot != -1) {
                        mc.player.getInventory().selectedSlot = hpSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(hpSlot));
                        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));
                        mc.player.getInventory().selectedSlot = originalSlot;
                        sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                    }
                } else {
                    int hpSlot = findHpInInventory();
                    if (hpSlot != -1) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, hpSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                        sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, hpSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                    }
                }
            }
        }
    }

    /*private boolean needXp() {
        if (mc.player == null) return false;

        for (ItemStack stack : mc.player.getArmorItems()) {
            if (PlayerUtility.calculatePercentage(stack) < durability.getValue()) {
                return true;
            }
        }

        return false;
    }*/

    private int findEPSlot() {
        int epSlot = -1;
        if (mc.player.getMainHandStack().getItem() == Items.ENDER_PEARL) {
            epSlot = mc.player.getInventory().selectedSlot;
        }
        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.ENDER_PEARL) {
                    epSlot = l;
                    break;
                }
            }
        }
        return epSlot;
    }

    private int getHpSlot() {
        for (int i = 0; i < 9; ++i)
            if (isStackPotion(mc.player.getInventory().getStack(i)))
                return i;
        return -1;
    }

    private int findHpInInventory() {
        for (int i = 36; i >= 0; i--)
            if (isStackPotion(mc.player.getInventory().getStack(i)))
                return i < 9 ? i + 36 : i;
        return -1;
    }

    private boolean isStackPotion(ItemStack stack) {
        if (stack == null)
            return false;

        if (stack.getItem() == Items.SPLASH_POTION)
            for (StatusEffectInstance effect : PotionUtil.getPotion(stack).getEffects())
                if (effect.getEffectType() == StatusEffects.INSTANT_HEALTH)
                    return true;
        return false;
    }

    private int findXPSlot() {
        int epSlot = -1;
        if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            epSlot = mc.player.getInventory().selectedSlot;
        }
        if (epSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.getInventory().getStack(l).getItem() == Items.EXPERIENCE_BOTTLE) {
                    epSlot = l;
                    break;
                }
            }
        }
        return epSlot;
    }

    public class PearlThread extends Thread {
        public ClientPlayerEntity player;
        int epSlot, originalSlot, delay;
        boolean inv;

        public PearlThread(ClientPlayerEntity entityPlayer, int epSlot, int originalSlot, int delay, boolean inventory) {
            this.player = entityPlayer;
            this.epSlot = epSlot;
            this.originalSlot = originalSlot;
            this.delay = delay;
            inv = inventory;
        }

        @Override
        public void run() {
            if (!inv) {
                mc.player.getInventory().selectedSlot = epSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(epSlot));

                try {
                    sleep(delay);
                } catch (Exception ignore) {
                }
                sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));

                try {
                    sleep(delay);
                } catch (Exception ignore) {
                }
                mc.player.getInventory().selectedSlot = originalSlot;
                sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                super.run();
            } else {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, originalSlot, SlotActionType.SWAP, mc.player);

                try {
                    sleep(delay);
                } catch (Exception ignore) {
                }
                //if (ModuleManager.aura.isEnabled() && Aura.target != null)
                    sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, PlayerUtility.getWorldActionId(mc.world)));

                try {
                    sleep(delay);
                } catch (Exception ignore) {
                }
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, epSlot, originalSlot, SlotActionType.SWAP, mc.player);

                super.run();
            }
        }
    }
}
