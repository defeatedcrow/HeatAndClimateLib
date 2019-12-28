package defeatedcrow.hac.api.hook;

import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCCaveOceanBlock extends Event {

	public final ChunkPrimer primer;
	public final int x;
	public final int y;
	public final int z;

	public DCCaveOceanBlock(ChunkPrimer data, int x1, int y1, int z1) {
		primer = data;
		x = x1;
		y = y1;
		z = z1;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}
}
