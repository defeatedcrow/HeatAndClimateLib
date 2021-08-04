package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.hook.DCEntityItemUpdateEvent;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropItemUpdateEvent {

	@SubscribeEvent
	public void onToss(DCEntityItemUpdateEvent event) {
		EntityItem entity = event.entityItem;
		if (entity != null && !entity.getEntityWorld().isRemote) {
			if (!DCUtil.isEmpty(entity.getItem())) {
				if (!entity.cannotPickup()) {
					NBTTagCompound tag = entity.getEntityData();
					short c = 0;
					if (tag.hasKey("dcs.counter")) {
						c = tag.getShort("dcs.counter");
					}
					if (c > 0) {
						c--;
					} else {
						ItemStack item = entity.getItem();
						IClimate clm = ClimateAPI.calculator.getClimate(entity.getEntityWorld(), entity.getPosition());
						IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, item);
						ItemStack burnt = FurnaceRecipes.instance().getSmeltingResult(item);
						if (recipe != null && recipe.canProceedAsDropItem() && recipe.additionalRequire(entity
								.getEntityWorld(), entity.getPosition())) {
							ItemStack output = recipe.getOutput().copy();
							output.setCount(entity.getItem().getCount());
							entity.setItem(output);
							entity.getEntityWorld().playSound(null, entity
									.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 2.0F);
							event.setResult(Result.ALLOW);
						} else if (!DCUtil.isEmpty(burnt) && clm.getHeat().getTier() > DCHeatTier.KILN.getTier() && clm
								.getHumidity() != DCHumidity.UNDERWATER && clm.getAirflow().getID() < 2) {
							ItemStack output = burnt.copy();
							output.setCount(entity.getItem().getCount());
							entity.setItem(output);
							entity.getEntityWorld().playSound(null, entity
									.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 2.0F);
							event.setResult(Result.ALLOW);
						}
						c = 200;
					}
					entity.getEntityData().setShort("dcs.counter", c);
				}
			}
		}

	}

	/* dropが消えなくなる */
	@SubscribeEvent
	public void livingDropItemEvent(ItemExpireEvent event) {
		EntityItem item = event.getEntityItem();
		int life = event.getExtraLife();
		if (item != null && !item.world.isRemote) {
			BlockPos pos = item.getPosition();
			IClimate clm = ClimateAPI.calculator.getClimate(item.world, pos);
			if (CoreConfigDC.enableFreezeDrop && clm.getHeat().getTier() < DCHeatTier.COLD.getTier()) {
				// frostbite以下
				life += 6000;
				event.setExtraLife(life);
				event.setCanceled(true);
			}
		}
	}

}
