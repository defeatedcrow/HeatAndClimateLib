package defeatedcrow.hac.api.hook;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class DCBiomeTempEvent extends Event {

	public final Biome biome;
	public final BlockPos pos;
	public final float defaultTemp;
	public float newTemp;

	public DCBiomeTempEvent(Biome biomeIn, BlockPos posIn) {
		this.pos = posIn;
		this.biome = biomeIn;
		this.defaultTemp = biomeIn.getDefaultTemperature();
		this.newTemp = biomeIn.getDefaultTemperature();
	}

	public float result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return newTemp;
		}

		return defaultTemp;
	}
}
