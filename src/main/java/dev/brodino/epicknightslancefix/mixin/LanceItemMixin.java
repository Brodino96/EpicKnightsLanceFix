package dev.brodino.epicknightslancefix.mixin;

import com.magistuarmory.item.LanceItem;
import dev.brodino.epicknightslancefix.network.LanceCollisionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LanceItem.class)
public class LanceItemMixin {

    @Shadow()
    private int clickedticks;

    @Redirect(
            method = "collide(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/magistuarmory/network/PacketLanceCollision;sendToServer(IF)V"
            )
    )
    public void epicknightslancefix$collide(int entityid, float damage) {
        LanceCollisionHandler.sendToServer(entityid, damage, this.clickedticks);
    }

}
