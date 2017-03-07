package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateTileEntity;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageClimateUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClimateReceiveTile extends DCTileEntity {

	protected final List<BlockPos> effectiveTiles = new ArrayList<BlockPos>();

	protected int lastClimate = 0;
	protected IClimate current = null;
	protected DCHeatTier highTemp = DCHeatTier.NORMAL;
	protected DCHeatTier lowTemp = DCHeatTier.NORMAL;
	private int count = 1;

	@Override
	public void updateTile() {
		if (!worldObj.isRemote) {
			DCHeatTier heat = ClimateAPI.calculator.getHeat(worldObj, pos, CoreConfigDC.heatRange, false);
			DCHeatTier cold = ClimateAPI.calculator.getCold(worldObj, pos, CoreConfigDC.heatRange, false);
			DCHumidity hum = ClimateAPI.calculator.getHumidity(worldObj, pos, CoreConfigDC.humRange, false);
			DCAirflow air = ClimateAPI.calculator.getAirflow(worldObj, pos, CoreConfigDC.airRange, false);

			List<BlockPos> remove = new ArrayList<BlockPos>();
			for (BlockPos p : effectiveTiles) {
				TileEntity tile = worldObj.getTileEntity(p);
				if (tile != null && tile instanceof IClimateTileEntity) {
					IClimateTileEntity effect = (IClimateTileEntity) tile;
					if (effect.isActive()) {
						DCHeatTier heat2 = effect.getHeatTier(pos);
						DCHumidity hum2 = effect.getHumidity(pos);
						DCAirflow air2 = effect.getAirflow(pos);

						if (heat2.getTier() > heat.getTier()) {
							heat = heat2;
						}
						if (heat2.getTier() < cold.getTier()) {
							cold = heat2;
						}
						if (hum2.getID() > hum.getID()) {
							hum = hum2;
						}
						if (air2.getID() > air.getID()) {
							air = air2;
						}
					}
				} else {
					remove.add(p);
				}
			}

			highTemp = heat;
			lowTemp = cold;
			if (heat.getTier() > cold.getTier() && cold.getTier() < 0) {
				if (heat.getTier() < 0) {
					heat = cold;
				} else {
					heat = heat.addTier(cold.getTier());
				}
			}
			int code = (air.getID() << 6) + (hum.getID() << 4) + heat.getID();
			current = ClimateAPI.register.getClimateFromInt(code);

			if (!remove.isEmpty()) {
				effectiveTiles.removeAll(remove);
				remove.clear();
			}

			if (count < 0) {
				if (current != null && lastClimate != current.getClimateInt()) {
					lastClimate = current.getClimateInt();
					HaCPacket.INSTANCE.sendToAll(new MessageClimateUpdate(pos, lastClimate));
				}
			} else {
				count--;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList list1 = tag.getTagList("dcs.heats", 10);
		for (int i = 0; i < list1.tagCount(); ++i) {
			NBTTagCompound nbt1 = list1.getCompoundTagAt(i);
			int x = nbt1.getInteger("X");
			int y = nbt1.getInteger("Y");
			int z = nbt1.getInteger("Z");
			BlockPos pos = new BlockPos(x, y, z);
			effectiveTiles.add(pos);
		}

		if (tag.hasKey("dcs.climateInt")) {
			int ic = tag.getInteger("dcs.climateInt");
			IClimate clm = ClimateAPI.register.getClimateFromInt(ic);
			if (clm != null)
				current = clm;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagList list1 = new NBTTagList();
		for (int i = 0; i < this.effectiveTiles.size(); ++i) {
			BlockPos pos = effectiveTiles.get(i);
			if (pos != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("X", pos.getX());
				nbt.setInteger("Y", pos.getY());
				nbt.setInteger("Z", pos.getZ());
				list1.appendTag(nbt);
			}
		}

		tag.setTag("dcs.heats", list1);

		if (current != null) {
			tag.setInteger("dcs.climateInt", current.getClimateInt());
		}
		return tag;
	}

	public List<BlockPos> getHeatTilePos() {
		return this.effectiveTiles;
	}

	public void addHeatTilePos(BlockPos pos) {
		if (pos != null && !effectiveTiles.contains(pos)) {
			effectiveTiles.add(pos);
		}
	}

	public IClimate getClimate() {
		return current;
	}

	public void setClimate(int i) {
		IClimate ret = ClimateAPI.register.getClimateFromInt(i);
		if (ret != null) {
			current = ret;
		}
	}

}
