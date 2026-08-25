package dev.brodino.epicknightslancefix.network;

import com.magistuarmory.item.LanceItem;
import dev.brodino.epicknightslancefix.EpicKnightsLanceFix;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class LanceCollisionHandler {

    private static final Identifier ID = new Identifier(EpicKnightsLanceFix.MOD_ID, "packet_lance_collision");
    private static final int CLICKED_TICKS_COOLDOWN = 5;

    public static void initialize() {
        EpicKnightsLanceFix.LOGGER.info("Registering server receiver for lance collision packet");
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, sender) -> {
            EpicKnightsLanceFix.LOGGER.info("Received packet from player: {}", player.getName().getString());

            int victimId = buf.readInt();
            float speed = buf.readFloat();
            double ticks = buf.readDouble();

            server.execute(() -> apply(victimId, speed, ticks, player));
        });
    }

    @Environment(EnvType.CLIENT)
    private static PacketByteBuf encode(int entityId, float speed, double ticks) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId)
            .writeFloat(speed)
            .writeDouble(ticks);
        return buf;
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(int entityId, float speed, double ticks) {
        EpicKnightsLanceFix.LOGGER.info("Sending packet to server, entityId: {}, speed: {}, ticks: {}", entityId, speed, ticks);
        ClientPlayNetworking.send(ID, encode(entityId, speed, ticks));
    }

    @Environment(EnvType.SERVER)
    private static void apply(int entityId, float speed, double ticks, ServerPlayerEntity player) {
        Entity victim = player.getWorld().getEntityById(entityId);
        if (victim == null) {
            EpicKnightsLanceFix.LOGGER.info("Victim is null");
            return;
        }
        execute(victim, speed, player, ticks);
    }

    @Environment(EnvType.SERVER)
    private static void execute(Entity victim, float speed, ServerPlayerEntity player, double ticks) {
        ItemStack stack = player.getMainHandStack();
        Item item = stack.getItem();
        if (!(item instanceof LanceItem lance)) {
            EpicKnightsLanceFix.LOGGER.info("Weapon is not a lance");
            return;
        }
        boolean dismount = victim.getWorld().getRandom().nextDouble() > (double) 1.0F - (double) clamp((float) ticks / (float) CLICKED_TICKS_COOLDOWN);
        EpicKnightsLanceFix.LOGGER.info("dismount: {}, speed: {}, ticks: {}", dismount, speed, ticks);
        lance.setRideSpeed(stack, speed);
        lance.setDismount(stack, dismount);
        player.attack(victim);
        player.resetLastAttackedTicks();
    }

    @Environment(EnvType.SERVER)
    private static float clamp(float value) {
        if (value < (float) 0.0) {
            return (float) 0.0;
        } else {
            return Math.min(value, (float) 1.0);
        }
    }
}
