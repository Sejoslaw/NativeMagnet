package com.github.sejoslaw.nativeMagnet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.stream.Collectors;

public class NativeMagnet implements ModInitializer, ServerTickCallback {
    public static final String MODID = "nativemagnet";

    @Override
    public void onInitialize() {
        System.out.println("Registering Native Magnet...");
        ServerTickCallback.EVENT.register(this);
    }

    @Override
    public void tick(MinecraftServer minecraftServer) {
        for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
            ItemStack offHandStack = player.getOffHandStack();

            if (offHandStack.getItem() != Items.NETHER_STAR) {
                return;
            }

            int range = this.getRange(offHandStack);
            float itemMotion = 0.45F;

            double x = player.getX();
            double y = player.getY() + 0.75;
            double z = player.getZ();

            List<Entity> items = player.world
                    .getEntities(player, new Box(x - range, y - range, z - range, x + range, y + range, z + range))
                    .stream()
                    .filter(entity -> entity instanceof ItemEntity)
                    .collect(Collectors.toList());

            Vec3d playerVec = new Vec3d(x, y, z);

            for (Entity entity : items) {
                Vec3d finalVec = playerVec.subtract(entity.getPos());

                if (finalVec.length() > 1) {
                    finalVec = finalVec.normalize();
                }

                entity.setVelocity(new Vec3d(finalVec.getX() * itemMotion, finalVec.getY() * itemMotion, finalVec.getZ() * itemMotion));
            }
        }
    }

    private int getRange(ItemStack stack) {
        int range = 100;

        try {
            range = Integer.parseInt(stack.getName().asFormattedString());

            if (range < 1) {
                range = 100;
            }
        } finally {
            return range;
        }
    }
}
