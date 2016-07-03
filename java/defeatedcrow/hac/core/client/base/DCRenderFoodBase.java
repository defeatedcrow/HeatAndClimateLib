package defeatedcrow.hac.core.client.base;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.core.base.FoodEntityBase;

/* FoodEntityBase用のRender */
@SideOnly(Side.CLIENT)
public abstract class DCRenderFoodBase<T extends FoodEntityBase> extends Render<T> {

	protected DCRenderFoodBase(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float yaw, float partialTicks) {
		float height = entity.height * 0.5F;
		EnumFacing side = entity.getSide();
		boolean baked = !entity.getRaw();
		boolean burnt = entity.getBURNT();
		DCFoodModelBase model = this.getEntityModel(baked);
		ResourceLocation tex = this.getFoodTexture(baked);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.bindTexture(tex);
		if (burnt) {
			GlStateManager.color(0.2F, 0.2F, 0.2F);
		}

		float rotX = 0F;
		float rotY = entity.rotationYaw;
		float rotZ = 0F;

		GlStateManager.rotate(rotX, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(rotY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(rotZ, 0.0F, 0.0F, 1.0F);

		model.render(0.0625F);

		if (burnt) {
			GlStateManager.color(1F, 1F, 1F);
		}
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, yaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(FoodEntityBase entity) {
		return getFoodTexture(!entity.getRaw());
	}

	protected abstract ResourceLocation getFoodTexture(boolean baked);

	protected abstract DCFoodModelBase getEntityModel(boolean baked);

}
