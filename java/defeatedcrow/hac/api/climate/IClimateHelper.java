package defeatedcrow.hac.api.climate;

import net.minecraft.nbt.NBTTagCompound;

public interface IClimateHelper {

	/**
	 * climateを0bAABBCCCのintとして表現したものと互換性を持たせる。
	 * NBT用。
	 */
	IClimate getClimateFromInt(int i);

	IClimate getClimateFromParam(DCHeatTier heat, DCHumidity hum, DCAirflow air);

	/**
	 * NBTからIClimateに。
	 */
	IClimate getClimateFromNBT(NBTTagCompound nbt);

	/**
	 * NBTにIClimateをセット。
	 */
	void setClimateToNBT(NBTTagCompound nbt, IClimate climate);

	String getNBTKey();

}
