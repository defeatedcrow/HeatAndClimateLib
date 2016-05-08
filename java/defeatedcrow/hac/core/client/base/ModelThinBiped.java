package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// バニラモデルを細くしたもの
@SideOnly(Side.CLIENT)
public class ModelThinBiped extends ModelBiped {
	public ModelRenderer head;
	public ModelRenderer bipedHeadwear;
	public ModelRenderer body;
	public ModelRenderer rightArm;
	public ModelRenderer leftArm;
	public ModelRenderer rightLeg;
	public ModelRenderer leftLeg;

	public int heldItemLeft = 0;
	public int heldItemRight = 0;
	public boolean isSneak = false;
	public boolean isBlocking = false;
	public boolean aimedBow = false;
	public int slot;

	public ModelThinBiped(int b) {
		this(0.35F, b);
	}

	public ModelThinBiped(float f, int b) {
		this(f, 0.0F, 64, 32, b);
	}

	public ModelThinBiped(float f1, float f2, int i3, int i4, int s) {
		slot = s;
		this.textureWidth = i3;
		this.textureHeight = i4;
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f1);
		head.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		body = new ModelRenderer(this, 16, 16);
		body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f1);
		body.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		rightArm = new ModelRenderer(this, 40, 16);
		rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f1);
		rightArm.setRotationPoint(-5.0F, 2.0F + f2, 0.0F);
		leftArm = new ModelRenderer(this, 40, 16);
		leftArm.mirror = true;
		leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f1);
		leftArm.setRotationPoint(5.0F, 2.0F + f2, 0.0F);
		rightLeg = new ModelRenderer(this, 0, 16);
		rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f1);
		rightLeg.setRotationPoint(-1.9F, 12.0F + f2, 0.0F);
		leftLeg = new ModelRenderer(this, 0, 16);
		leftLeg.mirror = true;
		leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f1);
		leftLeg.setRotationPoint(1.9F, 12.0F + f2, 0.0F);

	}

	@Override
	public void render(Entity ent, float f2, float f3, float f4, float f5, float f6, float f7) {

		this.heldItemRight = 0;
		this.isBlocking = false;
		this.isChild = false;
		this.isSneak = false;
		this.isRiding = false;
		this.swingProgress = 0F;

		if (ent != null) {
			if (ent.isRiding())
				this.isRiding = true;
			if (ent.isSneaking())
				this.isSneak = true;
			if (ent instanceof EntityLivingBase) {
				EntityLivingBase liv = (EntityLivingBase) ent;
				// this.swingProgress = liv.swingProgress;
				if (liv instanceof EntityPlayer) {
					if (((EntityPlayer) liv).inventory.getCurrentItem() != null) {
						ItemStack held = ((EntityPlayer) liv).inventory.getCurrentItem();
						this.heldItemRight = 1;
						if (((EntityPlayer) liv).isUsingItem())
							if (held.getItem().getItemUseAction(held) == EnumAction.BOW)
								this.isBlocking = true;
							else if (held.getItem().getItemUseAction(held) == EnumAction.BLOCK)
								this.heldItemRight = 3;
					}
				} else if (liv instanceof EntityLiving) {
					if (((EntityLiving) liv).getEquipmentInSlot(0) != null)
						this.heldItemRight = 1;
					if (((EntityLiving) liv).isChild()) {
						this.isChild = true;
					}
				}
			}
		}

		this.setRotationAngles(f2, f3, f4, f5, f6, f7, ent);
		GlStateManager.pushMatrix();

		// showModelをここでいじる
		head.showModel = true;
		body.showModel = true;
		rightArm.showModel = true;
		leftArm.showModel = true;
		rightLeg.showModel = true;
		leftLeg.showModel = true;

		if (this.isChild) {
			float f = 2.0F;
			GlStateManager.scale(1.5F / f, 1.5F / f, 1.5F / f);
			GlStateManager.translate(0.0F, 16.0F * f7, 0.0F);
			this.head.render(f7);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * f7, 0.0F);
			this.body.render(f7);
			this.rightArm.render(f7);
			this.leftArm.render(f7);
			this.rightLeg.render(f7);
			this.leftLeg.render(f7);
		} else {
			if (ent.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			this.head.render(f7);
			this.body.render(f7);
			this.rightArm.render(f7);
			this.leftArm.render(f7);
			this.rightLeg.render(f7);
			this.leftLeg.render(f7);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
			float scaleFactor, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

		this.head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		this.head.rotateAngleX = headPitch / (180F / (float) Math.PI);
		this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		this.rightArm.rotateAngleZ = 0.0F;
		this.leftArm.rotateAngleZ = 0.0F;
		this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.rightLeg.rotateAngleY = 0.0F;
		this.leftLeg.rotateAngleY = 0.0F;

		if (this.isRiding) {
			this.rightArm.rotateAngleX += -((float) Math.PI / 5F);
			this.leftArm.rotateAngleX += -((float) Math.PI / 5F);
			this.rightLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
			this.leftLeg.rotateAngleX = -((float) Math.PI * 2F / 5F);
			this.rightLeg.rotateAngleY = ((float) Math.PI / 10F);
			this.leftLeg.rotateAngleY = -((float) Math.PI / 10F);
		}

		if (this.heldItemLeft != 0) {
			this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F) * this.heldItemLeft;
		}

		this.rightArm.rotateAngleY = 0.0F;
		this.rightArm.rotateAngleZ = 0.0F;

		if (this.heldItemRight == 1) {
			this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F) * 1;
		} else if (this.heldItemRight == 3) {
			this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F) * 3;
			this.rightArm.rotateAngleY = -0.5235988F;
		}

		this.leftArm.rotateAngleY = 0.0F;
		float f6;
		float f7;

		if (this.swingProgress > -9990.0F) {
			f6 = this.swingProgress;
			this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float) Math.PI * 2.0F) * 0.2F;
			this.rightArm.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F;
			this.rightArm.rotationPointX = -MathHelper.cos(this.body.rotateAngleY) * 5.0F;
			this.leftArm.rotationPointZ = -MathHelper.sin(this.body.rotateAngleY) * 5.0F;
			this.leftArm.rotationPointX = MathHelper.cos(this.body.rotateAngleY) * 5.0F;
			this.rightArm.rotateAngleY += this.body.rotateAngleY;
			this.leftArm.rotateAngleY += this.body.rotateAngleY;
			this.leftArm.rotateAngleX += this.body.rotateAngleY;
			f6 = 1.0F - this.swingProgress;
			f6 *= f6;
			f6 *= f6;
			f6 = 1.0F - f6;
			f7 = MathHelper.sin(f6 * (float) Math.PI);
			float f8 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
			this.rightArm.rotateAngleX = (float) (this.rightArm.rotateAngleX - (f7 * 1.2D + f8));
			this.rightArm.rotateAngleY += this.body.rotateAngleY * 2.0F;
			this.rightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
		}

		if (this.isSneak) {
			this.body.rotateAngleX = 0.5F;
			this.rightArm.rotateAngleX += 0.4F;
			this.leftArm.rotateAngleX += 0.4F;
			this.rightLeg.rotationPointZ = 4.0F;
			this.leftLeg.rotationPointZ = 4.0F;
			this.rightLeg.rotationPointY = 9.0F;
			this.leftLeg.rotationPointY = 9.0F;
			this.head.rotationPointY = 1.0F;
		} else {
			this.body.rotateAngleX = 0.0F;
			this.rightLeg.rotationPointZ = 0.1F;
			this.leftLeg.rotationPointZ = 0.1F;
			this.rightLeg.rotationPointY = 12.0F;
			this.leftLeg.rotationPointY = 12.0F;
			this.head.rotationPointY = 0.0F;
		}

		this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

		if (this.isBlocking) {
			f6 = 0.0F;
			f7 = 0.0F;
			this.rightArm.rotateAngleZ = 0.0F;
			this.leftArm.rotateAngleZ = 0.0F;
			this.rightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + this.head.rotateAngleY;
			this.leftArm.rotateAngleY = 0.1F - f6 * 0.6F + this.head.rotateAngleY + 0.4F;
			this.rightArm.rotateAngleX = -((float) Math.PI / 2F) + this.head.rotateAngleX;
			this.leftArm.rotateAngleX = -((float) Math.PI / 2F) + this.head.rotateAngleX;
			this.rightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
			this.leftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
			this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
			this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
			this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		}
	}

	@Override
	public void setModelAttributes(ModelBase model) {
		super.setModelAttributes(model);

		if (model instanceof ModelBiped) {
			ModelBiped modelbiped = (ModelBiped) model;
			this.heldItemLeft = modelbiped.heldItemLeft;
			this.heldItemRight = modelbiped.heldItemRight;
			this.isSneak = modelbiped.isSneak;
			this.aimedBow = modelbiped.aimedBow;
			this.isRiding = model.isRiding;
			this.isChild = model.isChild;
		}
	}

	@Override
	public void setInvisible(boolean invisible) {
		this.head.showModel = invisible;
		this.body.showModel = invisible;
		this.rightArm.showModel = invisible;
		this.leftArm.showModel = invisible;
		this.rightLeg.showModel = invisible;
		this.leftLeg.showModel = invisible;
	}

	@Override
	public void postRenderArm(float scale) {
		this.rightArm.postRender(scale);
	}
}
