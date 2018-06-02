package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.placeable.IEntityItem;
import defeatedcrow.hac.api.recipe.DCEntityItemUpdateEvent;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropItemUpdateEvent {

	@SubscribeEvent
	public void onToss(DCEntityItemUpdateEvent event) {
		EntityItem entity = event.entityItem;
		if (entity != null && !entity.getEntityWorld().isRemote) {
			if (!DCUtil.isEmpty(entity.getItem()) && !(entity.getItem().getItem() instanceof ItemBlock)
					&& !(entity.getItem().getItem() instanceof IEntityItem)) {
				if (!entity.cannotPickup()) {
					ItemStack item = entity.getItem();
					IClimate clm = ClimateAPI.calculator.getClimate(entity.getEntityWorld(), entity.getPosition());
					IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, item);
					if (recipe != null && recipe.canProceedAsDropItem()
							&& recipe.additionalRequire(entity.getEntityWorld(), entity.getPosition())) {
						ItemStack output = recipe.getOutput().copy();
						output.setCount(entity.getItem().getCount());
						entity.setItem(output);
						entity.getEntityWorld().playSound(null, entity.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH,
								SoundCategory.BLOCKS, 0.8F, 2.0F);
						event.setResult(Result.ALLOW);
					}
				}
			}
		}

	}

}
