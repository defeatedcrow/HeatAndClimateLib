package defeatedcrow.hac.core.climate;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateHelper;
import net.minecraft.nbt.NBTTagCompound;

public class ClimateHelper implements IClimateHelper {

	public static final String NBT_KEY = "dcs.climate";

	@Override
	public IClimate getClimateFromInt(int code) {
		return ClimateAPI.register.getClimateFromInt(code);
	}

	@Override
	public IClimate getClimateFromParam(DCHeatTier heat, DCHumidity hum, DCAirflow air) {
		return ClimateAPI.register.getClimateFromParam(heat, hum, air);
	}

	@Override
	public IClimate getClimateFromNBT(NBTTagCompound nbt) {
		if (nbt != null && nbt.hasKey(NBT_KEY)) {
			int i = nbt.getInteger(NBT_KEY);
			IClimate climate = getClimateFromInt(i);
			return climate;
		}
		return null;
	}

	@Override
	public void setClimateToNBT(NBTTagCompound nbt, IClimate climate) {
		if (nbt != null && climate != null) {
			int i = climate.getClimateInt();
			nbt.setInteger(NBT_KEY, i);
		}
	}

	@Override
	public String getNBTKey() {
		return NBT_KEY;
	}

}
