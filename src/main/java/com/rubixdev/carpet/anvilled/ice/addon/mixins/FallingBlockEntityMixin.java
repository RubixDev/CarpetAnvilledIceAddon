package com.rubixdev.carpet.anvilled.ice.addon.mixins;

import com.rubixdev.carpet.anvilled.ice.addon.CarpetAnvilledIceAddonSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private int frostedIceCount;
    private int iceCount;
    private int packedIceCount;

    @Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/entity/FallingBlockEntity;remove()V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1)),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void onTick(CallbackInfo ci, Block block, BlockPos pos) {
        if (block.isIn(BlockTags.ANVIL)) {
            BlockPos posBelow = new BlockPos(this.getX(), this.getY() - 0.06, this.getZ());
            Block blockBelow = this.world.getBlockState(posBelow).getBlock();
            if (blockBelow == Blocks.FROSTED_ICE && CarpetAnvilledIceAddonSettings.anvilledIce > 0) {
                if (++frostedIceCount < CarpetAnvilledIceAddonSettings.anvilledIce) {
                    world.breakBlock(posBelow, false);
                    onGround = false;
                    ci.cancel();
                } else {
                    world.breakBlock(posBelow, false);
                    world.setBlockState(posBelow, Blocks.ICE.getDefaultState(), 3);
                }
            } else if (blockBelow == Blocks.ICE && CarpetAnvilledIceAddonSettings.anvilledPackedIce > 0) {
                if (++iceCount < CarpetAnvilledIceAddonSettings.anvilledPackedIce) {
                    world.breakBlock(posBelow, false);
                    onGround = false;
                    ci.cancel();
                } else {
                    world.breakBlock(posBelow, false);
                    world.setBlockState(posBelow, Blocks.PACKED_ICE.getDefaultState(), 3);
                }
            } else if (blockBelow == Blocks.PACKED_ICE && CarpetAnvilledIceAddonSettings.anvilledBlueIce > 0) {
                if (++packedIceCount < CarpetAnvilledIceAddonSettings.anvilledBlueIce) {
                    world.breakBlock(posBelow, false);
                    onGround = false;
                    ci.cancel();
                } else {
                    world.breakBlock(posBelow, false);
                    world.setBlockState(posBelow, Blocks.BLUE_ICE.getDefaultState(), 3);
                }
            }
        }
    }
}
