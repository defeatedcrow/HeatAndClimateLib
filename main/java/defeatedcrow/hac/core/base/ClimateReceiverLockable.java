package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.ClimateCalculateEvent;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateTileEntity;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.IClimateUpdateTile;
import defeatedcrow.hac.core.packet.MessageClimateUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class ClimateReceiverLockable extends DCLockableTE implements IClimateUpdateTile {

	private final List<BlockPos> effectiveTiles = new ArrayList<BlockPos>();

	protected int lastClimate;
	protected IClimate current = null;
	protected DCHeatTier highTemp = DCHeatTier.NORMAL;
	protected DCHeatTier lowTemp = DCHeatTier.NORMAL;
	private int count = 1;

	@Override
	public void updateTile() {
		if (!world.isRemote) {
			DCHeatTier heat = ClimateAPI.calculator.getHeat(world, pos, 2, false);
			DCHeatTier cold = ClimateAPI.calculator.getCold(world, pos, 2, false);
			DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, 1, false);
			DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, 1, false);

			List<BlockPos> remove = new ArrayList<BlockPos>();
			for (BlockPos p : effectiveTiles) {
				TileEntity tile = world.getTileEntity(p);
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
			IClimate clm = ClimateAPI.register.getClimateFromInt(code);

			ClimateCalculateEvent event = new ClimateCalculateEvent(world, pos, clm);
			clm = event.result();

			current = clm;

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

	@Override
	public IClimate getClimate() {
		return current;
	}

	@Override
	public void setClimate(int i) {
		IClimate ret = ClimateAPI.register.getClimateFromInt(i);
		if (ret != null) {
			current = ret;
		}
	}

}
