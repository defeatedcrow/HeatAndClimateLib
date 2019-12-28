package defeatedcrow.hac.api.hook;

import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCRavineWaterEvent extends Event {

	public final MapGenRavine event;
	public final ChunkPrimer data;
	public final int x;
	public final int y;
	public final int z;
	public final int cx;
	public final int cz;
	public final boolean foundTop;

	public DCRavineWaterEvent(MapGenRavine eventIn, ChunkPrimer dataIn, int x1, int y1, int z1, int cx1, int cz1,
			boolean foundTopIn) {
		this.event = eventIn;
		this.data = dataIn;
		this.x = x1;
		this.y = y1;
		this.z = z1;
		this.cx = cx1;
		this.cz = cz1;
		this.foundTop = foundTopIn;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}
}
