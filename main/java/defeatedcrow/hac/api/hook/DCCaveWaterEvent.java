package defeatedcrow.hac.api.hook;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCCaveWaterEvent extends Event {

	public final MapGenCaves event;
	public final ChunkPrimer data;
	public final int x;
	public final int y;
	public final int z;
	public final int cx;
	public final int cz;
	public final boolean foundTop;
	public final IBlockState state;
	public final IBlockState up;

	public DCCaveWaterEvent(MapGenCaves eventIn, ChunkPrimer dataIn, int x1, int y1, int z1, int cx1, int cz1,
			boolean foundTopIn, IBlockState stateIn, IBlockState upIn) {
		this.event = eventIn;
		this.data = dataIn;
		this.x = x1;
		this.y = y1;
		this.z = z1;
		this.cx = cx1;
		this.cz = cz1;
		this.foundTop = foundTopIn;
		this.state = stateIn;
		this.up = upIn;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}
}
