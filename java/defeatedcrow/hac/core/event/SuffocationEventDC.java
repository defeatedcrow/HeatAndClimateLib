package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.damage.CamouflageInsideMaterialEvent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SuffocationEventDC {

	@SubscribeEvent
	public void CamouflageInWater(CamouflageInsideMaterialEvent event) {
		Entity target = event.getEntity();
		Material mat = event.material;
		if (target != null && target.isEntityAlive() && mat == Material.WATER) {
			double d0 = target.posY + target.getEyeHeight();
			BlockPos pos = new BlockPos(target.posX, d0, target.posZ);
			IBlockState state = target.worldObj.getBlockState(pos);
			if (ClimateAPI.calculator.getAirflow(target.worldObj, pos) == DCAirflow.TIGHT) {
				event.setResult(Result.ALLOW);
			}
		}
	}

}
