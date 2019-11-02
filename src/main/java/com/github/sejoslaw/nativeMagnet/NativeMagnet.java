package com.github.sejoslaw.nativeMagnet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(NativeMagnet.MODID)
public class NativeMagnet {
    public static final String MODID = "nativemagnet";

    public NativeMagnet() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void useMagnet(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;

        if (player.isSpectator()) {
            return;
        }

        ItemStack offHandStack = player.getHeldItemOffhand();

        if (offHandStack.getItem() != Items.NETHER_STAR) {
            return;
        }

        int range = this.getRange(offHandStack);
        float itemMotion = 0.45F;

        double x = player.posX;
        double y = player.posY + 0.75;
        double z = player.posZ;

        List<ItemEntity> items = player.world.getEntitiesWithinAABB(ItemEntity.class,
                new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range));
        Vec3d playerVec = new Vec3d(x, y, z);

        for (ItemEntity itemEntity : items) {
            Vec3d itemEntityVec = new Vec3d(itemEntity.posX, itemEntity.posY, itemEntity.posZ);
            Vec3d finalVec = playerVec.subtract(itemEntityVec);

            if (finalVec.length() > 1) {
                finalVec = finalVec.normalize();
            }

            itemEntity.setMotion(new Vec3d(finalVec.getX() * itemMotion, finalVec.getY() * itemMotion, finalVec.getZ() * itemMotion));
        }
    }

    private int getRange(ItemStack stack) {
        int range = 100;

        try {
            range = Integer.parseInt(stack.getDisplayName().getFormattedText());
        } finally {
            return range;
        }
    }
}
