package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.placeable.IEntityItem;
import defeatedcrow.hac.api.recipe.DCEntityItemUpdateEvent;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropItemUpdateEvent {

	@SubscribeEvent
	public void onToss(DCEntityItemUpdateEvent event) {
		EntityItem entity = event.entityItem;
		if (entity != null && !entity.getEntityWorld().isRemote) {
			if (!DCUtil.isEmpty(entity.getItem())) {
				boolean flag = false;
				if (!(entity.getItem().getItem() instanceof ItemBlock)
						&& !(entity.getItem().getItem() instanceof IEntityItem)) {
					flag = true;
				} else if (CoreConfigDC.enableDropItemSmelting) {
					flag = true;
				}
				if (!entity.cannotPickup() && flag) {
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
						if (recipe != null && recipe.canProceedAsDropItem()
								&& recipe.additionalRequire(entity.getEntityWorld(), entity.getPosition())) {
							ItemStack output = recipe.getOutput().copy();
							output.setCount(entity.getItem().getCount());
							entity.setItem(output);
							entity.getEntityWorld().playSound(null, entity.getPosition(),
									SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 2.0F);
							event.setResult(Result.ALLOW);
						} else if (!DCUtil.isEmpty(burnt) && clm.getHeat().getTier() > DCHeatTier.KILN.getTier()
								&& clm.getHumidity() != DCHumidity.UNDERWATER && clm.getAirflow().getID() < 2) {
							ItemStack output = burnt.copy();
							output.setCount(entity.getItem().getCount());
							entity.setItem(output);
							entity.getEntityWorld().playSound(null, entity.getPosition(),
									SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 2.0F);
							event.setResult(Result.ALLOW);
						}
						c = 200;
					}
					entity.getEntityData().setShort("dcs.counter", c);
				}
			}
		}

	}

}
