package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * Climateの算出直前に発火するイベント<br>
 * Result.ALLOWの場合、結果を変更できる
 */
@HasResult
public class ClimateCalculateEvent extends Event {

	private final World world;
	private final BlockPos pos;
	private final IClimate prevClimate;
	private IClimate newClimate;

	public ClimateCalculateEvent(World worldIn, BlockPos posIn, IClimate prev) {
		this.pos = posIn;
		this.world = worldIn;
		this.prevClimate = prev;
		this.newClimate = prev;
	}

	public IClimate result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return newClimate;
		}

		return prevClimate;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public IClimate currentClimate() {
		return prevClimate;
	}

	public void setNewClimate(IClimate climate) {
		if (climate != null)
			newClimate = climate;
	}
}
