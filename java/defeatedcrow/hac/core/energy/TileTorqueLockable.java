package defeatedcrow.hac.core.energy;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IClimateTileEntity;
import defeatedcrow.hac.core.base.ITagGetter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class TileTorqueLockable extends TileTorqueBase
		implements IInteractionObject, ILockableContainer, ITagGetter {

	protected final List<BlockPos> effectiveTiles = new ArrayList<BlockPos>();
	protected IClimate current = null;
	private LockCode code = LockCode.EMPTY_CODE;

	@Override
	public void updateTile() {
		super.updateTile();
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

			if (heat.getTier() > cold.getTier() && cold.getTier() < 0) {
				if (heat.getTier() < 0) {
					heat = cold;
				} else {
					heat = heat.addTier(cold.getTier());
				}
			}
			int code = (air.getID() << 6) + (hum.getID() << 4) + heat.getID();
			current = ClimateAPI.register.getClimateFromInt(code);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.code = LockCode.fromNBT(tag);

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
		if (this.code != null) {
			this.code.toNBT(tag);
		}

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

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
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

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);
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
	public boolean isLocked() {
		return this.code != null && !this.code.isEmpty();
	}

	@Override
	public LockCode getLockCode() {
		return this.code;
	}

	@Override
	public void setLockCode(LockCode code) {
		this.code = code;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	private IItemHandler itemHandler;

	protected IItemHandler createUnSidedHandler() {
		return new InvWrapper(this);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) (itemHandler == null ? (itemHandler = createUnSidedHandler()) : itemHandler);
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	public List<BlockPos> getHeatTilePos() {
		return this.effectiveTiles;
	}

	public void addHeatTilePos(BlockPos pos) {
		if (pos != null && !effectiveTiles.contains(pos)) {
			effectiveTiles.add(pos);
		}
	}

}
