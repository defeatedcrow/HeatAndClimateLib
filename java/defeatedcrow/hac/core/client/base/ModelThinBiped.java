package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
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
		this.leftArmPose = ModelBiped.ArmPose.EMPTY;
		this.rightArmPose = ModelBiped.ArmPose.EMPTY;
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
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);

		setAngle(head, this.bipedHead);
		setAngle(body, this.bipedBody);
		setAngle(rightArm, this.bipedRightArm);
		setAngle(leftArm, this.bipedLeftArm);
		setAngle(rightLeg, this.bipedRightLeg);
		setAngle(leftLeg, this.bipedLeftLeg);
	}

	protected void setAngle(ModelRenderer m1, ModelRenderer m2) {
		m1.rotationPointX = m2.rotationPointX;
		m1.rotationPointY = m2.rotationPointY;
		m1.rotationPointZ = m2.rotationPointZ;

		m1.rotateAngleX = m2.rotateAngleX;
		m1.rotateAngleY = m2.rotateAngleY;
		m1.rotateAngleZ = m2.rotateAngleZ;
	}

	@Override
	public void setModelAttributes(ModelBase model) {
		super.setModelAttributes(model);

		if (model instanceof ModelBiped) {
			ModelBiped modelbiped = (ModelBiped) model;
			this.leftArmPose = modelbiped.leftArmPose;
			this.rightArmPose = modelbiped.rightArmPose;
			this.isSneak = modelbiped.isSneak;
			this.isChild = modelbiped.isChild;
			this.isRiding = modelbiped.isRiding;
			this.swingProgress = modelbiped.swingProgress;
		}
	}

	@Override
	public void setInvisible(boolean visible) {
		this.head.showModel = visible;
		this.body.showModel = visible;
		this.rightArm.showModel = visible;
		this.leftArm.showModel = visible;
		this.rightLeg.showModel = visible;
		this.leftLeg.showModel = visible;
	}

	@Override
	public void postRenderArm(float scale, EnumHandSide side) {
		this.getArmForSide(side).postRender(scale);
	}

	@Override
	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	protected EnumHandSide getMainHand(Entity entityIn) {
		return entityIn instanceof EntityLivingBase ? ((EntityLivingBase) entityIn).getPrimaryHand()
				: EnumHandSide.RIGHT;
	}
}
