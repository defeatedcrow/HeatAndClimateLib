package defeatedcrow.hac.api.hook;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCBlockFreezeEvent extends Event {

	public final World world;
	public final BlockPos pos;

	public DCBlockFreezeEvent(World worldIn, BlockPos posIn) {
		this.pos = posIn;
		this.world = worldIn;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}
}
