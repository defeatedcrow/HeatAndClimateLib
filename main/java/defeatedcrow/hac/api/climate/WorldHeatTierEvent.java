package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * BiomeのHeatTier参照時のイベント。<br>
 * Result.ALLOWの場合、結果を変更できる
 */
@HasResult
public class WorldHeatTierEvent extends Event {

	private final World world;
	private final BlockPos pos;
	private final DCHeatTier prevTier;
	private DCHeatTier newTier;
	private final EventType type;

	public WorldHeatTierEvent(World worldIn, BlockPos posIn, DCHeatTier prev, boolean isHeat) {
		this.pos = posIn;
		this.world = worldIn;
		this.prevTier = prev;
		this.newTier = prev;
		type = isHeat ? EventType.HOT : EventType.COLD;
	}

	public DCHeatTier result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return newTier;
		}

		return prevTier;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public DCHeatTier currentClimate() {
		return prevTier;
	}

	public EventType getType() {
		return type;
	}

	public void setNewClimate(DCHeatTier tier) {
		if (tier != null)
			newTier = tier;
	}

	public static enum EventType {
		HOT,
		COLD;
	}
}
