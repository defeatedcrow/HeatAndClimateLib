package defeatedcrow.hac.api.hook;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCItemEatEvent extends Event {

	public final World world;
	public final EntityLivingBase living;
	public final ItemStack item;
	public final PotionEffect potion;

	public DCItemEatEvent(ItemStack stack, World worldIn, EntityLivingBase livingIn, PotionEffect potionIn) {
		world = worldIn;
		living = livingIn;
		item = stack;
		potion = potionIn;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}
}
