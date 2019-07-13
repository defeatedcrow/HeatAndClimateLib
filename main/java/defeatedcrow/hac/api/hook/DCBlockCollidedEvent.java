package defeatedcrow.hac.api.hook;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * asm挿入用の追加イベント
 */
@Cancelable
public class DCBlockCollidedEvent extends Event {

	public final World world;
	public final BlockPos pos;
	public final IBlockState state;
	public final Entity entity;

	public DCBlockCollidedEvent(World worldIn, BlockPos posIn, IBlockState stateIn, Entity entityIn) {
		this.pos = posIn;
		this.world = worldIn;
		this.state = stateIn;
		this.entity = entityIn;
	}

	public boolean post() {
		return MinecraftForge.EVENT_BUS.post(this);
	}
}
