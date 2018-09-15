package defeatedcrow.hac.core.energy;

import javax.annotation.Nullable;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.blockstate.EnumSide;
import defeatedcrow.hac.api.energy.ITorqueDC;
import defeatedcrow.hac.api.energy.capability.TorqueCapabilityHandler;
import defeatedcrow.hac.core.base.DCTileEntity;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageTorqueTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileTorqueBase extends DCTileEntity implements ITorqueDC {

	public byte base = 0;
	private byte prevface = 0;
	public byte facing = 0;

	public float prevTorque = 0;
	public float currentTorque = 0;

	public float prevAccel = 0;
	public float currentAccel = 0;

	public float prevEffective = 0;
	public float effectiveAccel = 0;

	public float prevSpeed = 0;
	public float currentSpeed = 0;
	public float maxSpeed = 0;

	public float prevRotation;
	public float currentRotation = 0;
	public int age = 0;

	public TileTorqueBase() {
		super();
	}

	public float maxTorque() {
		return 32.0F;
	}

	public float maxAccel() {
		return maxTorque() / getGearTier();
	}

	public float maxSpeed() {
		return 360.0F;
	}

	@Override
	public void updateTile() {
		// torqueの処理
		// 生成直後は10Tickのインターバルがある
		if (age > 20) {

			if (!world.isRemote) {
				prevTorque = currentTorque;
				currentTorque = 0.0F;
				HaCPacket.INSTANCE.sendToAll(new MessageTorqueTile(pos, prevTorque, 0F, 0F));
			}

			currentAccel = prevTorque / getGearTier();

			if (currentAccel > maxAccel())
				currentAccel = maxAccel();

			// Acceleration
			float acc = (currentAccel - prevAccel) / 2.0F;
			if (Math.abs(acc) < 0.005F) {
				acc = 0.0F;
			}

			prevEffective = effectiveAccel;
			effectiveAccel = acc;
			prevAccel += effectiveAccel;
		}
	}

	@Override
	public void onTickUpdate() {
		age++;
		if (age > 72000) {
			// 1h程度でリセット
			age = 40;
		}

		prevSpeed = currentSpeed;

		// Speed
		float frict = 0.95F;
		if (prevAccel != 0.0F || age < 40) {
			// 動いていれば摩擦が小さくなる
			frict = 1.0F;
		}

		currentSpeed = (prevSpeed * frict) + effectiveAccel * 0.2F;

		if (currentSpeed > maxSpeed())
			currentSpeed = maxSpeed();
		if (currentSpeed < 0.005F)
			currentSpeed = 0;

		//

		prevRotation = currentRotation;
		currentRotation += currentSpeed;
		if (currentRotation > 720.0F) {
			prevRotation -= 720.0F;
			currentRotation -= 720.0F;
		}
		if (currentRotation < -720.0F) {
			prevRotation += 720.0F;
			currentRotation += 720.0F;
		}

	}

	@Override
	protected void onServerUpdate() {

	}

	@SideOnly(Side.CLIENT)
	public defeatedcrow.hac.core.client.base.DCTileModelBase getModel() {
		return null;
	}

	/* ITorqueDC */

	@Override
	public EnumFacing getBaseSide() {
		IBlockState state = world.getBlockState(getPos());
		if (DCState.hasProperty(state, DCState.SIDE)) {
			EnumSide face = state.getValue(DCState.SIDE);
			return face.getFacing();
		}
		return EnumFacing.getFront(base);
	}

	@Override
	public EnumFacing getFaceSide() {
		return this.faceFromID();
	}

	@Override
	public boolean isInputSide(EnumFacing side) {
		return side == getBaseSide();
	}

	@Override
	public boolean isOutputSide(EnumFacing side) {
		return side == getBaseSide().getOpposite();
	}

	@Override
	public void setBaseSide(EnumFacing side) {
		base = (byte) side.getIndex();
	}

	@Override
	public void setFaceSide(EnumFacing side) {
		this.facing = this.faceID(side);
	}

	@Override
	public void rotateFace() {
		switch (facing) {
		case 0:
			facing = 1;
			break;
		case 1:
			facing = 2;
			break;
		case 2:
			facing = 3;
			break;
		case 3:
			facing = 0;
			break;
		default:
			facing = 0;
			break;
		}
	}

	public byte faceID(EnumFacing face) {
		switch (face) {
		case NORTH:
			return 0;
		case EAST:
			return 1;
		case SOUTH:
			return 2;
		case WEST:
			return 3;
		default:
			return 0;
		}
	}

	public EnumFacing faceFromID() {
		switch (facing) {
		case 0:
			return EnumFacing.NORTH;
		case 1:
			return EnumFacing.EAST;
		case 2:
			return EnumFacing.SOUTH;
		case 3:
			return EnumFacing.WEST;
		default:
			return EnumFacing.NORTH;
		}
	}

	@Override
	public float getEffectiveAcceleration() {
		return effectiveAccel;
	}

	@Override
	public float getCurrentAcceleration() {
		return prevAccel;
	}

	@Override
	public float getFrictionalForce() {
		return 0.995F;
	}

	@Override
	public float getGearTier() {
		return 4.0F;
	}

	@Override
	public float getCurrentTorque() {
		return prevTorque;
	}

	@Override
	public float getRotationalSpeed() {
		return currentSpeed;
	}

	/* NBT */

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		getNBT(tag);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		tag.setByte("dcs.base", base);
		tag.setByte("dcs.face", facing);

		tag.setFloat("dcs.pretoq", prevTorque);
		tag.setFloat("dcs.curtoq", currentTorque);
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);
		this.base = tag.getByte("dcs.base");
		this.facing = tag.getByte("dcs.face");

		this.prevTorque = tag.getFloat("dcs.pretoq");
		this.currentTorque = tag.getFloat("dcs.curtoq");
	}

	/* Packet */

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		return new SPacketUpdateTileEntity(pos, 50, this.writeToNBT(nbtTagCompound));
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	/*
	 * Facing
	 * EnumFacingではカバーしきれなかったので
	 */

	public static EnumFacing rotateAround(int id, EnumFacing rot) {
		switch (rot) {
		case DOWN:
			return rotateY(id);
		case UP:
			return rotateY(2 - id);
		case NORTH:
			return rotateZ(id);
		case SOUTH:
			return rotateZ(-id);
		case WEST:
			return rotateX(id);
		case EAST:
			return rotateX(-id);
		default:
			return rotateY(id);
		}
	}

	/**
	 * NORTH => DOWN => SOUTH => UP => NORTH
	 */
	public static EnumFacing rotateX(int id) {
		switch (id) {
		case 0:
			return EnumFacing.UP;
		case 1:
		case -3:
			return EnumFacing.SOUTH;
		case 2:
		case -2:
			return EnumFacing.DOWN;
		case 3:
		case -1:
			return EnumFacing.NORTH;
		default:
			return EnumFacing.UP;
		}
	}

	/**
	 * EAST => DOWN => WEST => UP => EAST
	 */
	public static EnumFacing rotateZ(int id) {
		switch (id) {
		case 0:
			return EnumFacing.UP;
		case 1:
		case -3:
			return EnumFacing.WEST;
		case 2:
		case -2:
			return EnumFacing.DOWN;
		case 3:
		case -1:
			return EnumFacing.EAST;
		default:
			return EnumFacing.UP;
		}
	}

	/**
	 * NORTH => EAST => SOUTH => WEST => NORTH
	 */
	public static EnumFacing rotateY(int id) {
		switch (id) {
		case 0:
			return EnumFacing.SOUTH;
		case 1:
		case -3:
			return EnumFacing.EAST;
		case 2:
		case -2:
			return EnumFacing.NORTH;
		case 3:
		case -1:
			return EnumFacing.WEST;
		default:
			return EnumFacing.SOUTH;
		}
	}

	TorqueHandlerWrapper torqueWrapper = new TorqueHandlerWrapper(this);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == TorqueCapabilityHandler.TORQUE_HANDLER_CAPABILITY ? true
				: super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == TorqueCapabilityHandler.TORQUE_HANDLER_CAPABILITY ? (T) torqueWrapper
				: super.getCapability(capability, facing);
	}
}
