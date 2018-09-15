package defeatedcrow.hac.api.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ClimateSupplier implements Supplier<IClimate> {
    private IClimate cached;
    private World world;
    private BlockPos pos;

    public ClimateSupplier(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public IClimate get() {
        if(cached == null)
            cached = ClimateAPI.calculator.getClimate(world, pos);
        return cached;
    }
}
