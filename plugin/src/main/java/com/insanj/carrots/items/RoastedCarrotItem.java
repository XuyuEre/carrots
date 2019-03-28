package com.insanj.carrots.items;

import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.text.StringTextComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.FoodItemSetting;
import net.minecraft.item.FoodItemSettings;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.client.network.ClientPlayerEntity;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.insanj.carrots.CarrotsMod;

public class RoastedCarrotItem extends Item {
	public static final String ITEM_ID = "roasted_carrot";
	private static final FoodItemSetting FOOD_SETTING = (new FoodItemSetting.Builder()).hunger(4).saturationModifier(0.6F).eatenFast().build();

	public RoastedCarrotItem() {
		super(new Item.Settings().food(FOOD_SETTING).itemGroup(ItemGroup.FOOD));
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltip, TooltipContext options) {
		CompoundTag tags = stack.getTag();

		TranslatableTextComponent desc = new TranslatableTextComponent("item.insanj_carrots.roasted_carrot.desc");//, new SimpleDateFormat("MM/dd HH:mm").format(new Date()));
		desc.setStyle(new Style().setColor(TextFormat.RED));
		tooltip.add(desc);
	}

	@Override
	public ItemStack onItemFinishedUsing(ItemStack stack, World world, LivingEntity entity) {
		try {
			if (!world.isClient) {
				// 1 water breathing effect
				StatusEffectInstance waterBreathing = new StatusEffectInstance(StatusEffect.byRawId(13), 80, 50, true, false);
				entity.addPotionEffect(waterBreathing);

				// 2 night vision effect
				StatusEffectInstance nightVision = new StatusEffectInstance(StatusEffect.byRawId(16), 80, 50, true, false);
				entity.addPotionEffect(nightVision); // Potion.byId("water_breathing")
			} else {
				// 3 four heart particles rendered where player is facing
				Vec3d pos = entity.getPos();
				double x = pos.getX();
				double y = pos.getY() + entity.getEyeHeight(entity.getPose());
				double z = pos.getZ();

				WorldRenderer renderer = MinecraftClient.getInstance().worldRenderer;
				renderer.addParticle(ParticleTypes.HEART, true, true, x + 1, y, z, 0, 2, 0);
				renderer.addParticle(ParticleTypes.HEART, true, true, x, y, z + 1, 0, 2, 0);
				renderer.addParticle(ParticleTypes.HEART, true, true, x - 1, y, z, 0, 2, 0);
				renderer.addParticle(ParticleTypes.HEART, true, true, x, y, z - 1, 0, 2, 0);
			}
		} catch (Exception e) {
			System.out.println(String.format("[%s]: onItemFinishedUsing exception: %s", CarrotsMod.MOD_ID, ExceptionUtils.getStackTrace(e)));
		}

		return super.onItemFinishedUsing(stack, world, entity);
	}
}
