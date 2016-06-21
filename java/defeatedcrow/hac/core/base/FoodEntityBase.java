package defeatedcrow.hac.core.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class FoodEntityBase extends Entity {

	private int age;
	private int cookingTime;
	private String ownerID;

	public FoodEntityBase(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.5F);
	}

	public FoodEntityBase(World worldIn, double posX, double posY, double posZ) {
		this(worldIn);
		this.setPosition(posX, posY, posZ);
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public String getOwnerID() {
		return this.ownerID;
	}

	public void setOwner(EntityPlayer player) {
		this.ownerID = player.getUniqueID().toString();
	}

	public void setRotation(float f) {
		this.rotationYaw = f;
	}

}
