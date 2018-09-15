package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DCTileModelBase extends ModelBase {
	// fields
	ModelRenderer base;

	public DCTileModelBase() {
		textureWidth = 128;
		textureHeight = 64;

		base = new ModelRenderer(this, 0, 0);
		base.addBox(-8F, 7F, -8F, 16, 1, 16);
		base.setRotationPoint(0F, 16F, 0F);
		base.setTextureSize(128, 64);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
	}

	public void render(float f) {
		setRotationAngles(f);
	}

	public void render(float f, float speed, float tick) {
		setRotationAngles(f, speed, tick);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f) {
	}

	public void setRotationAngles(float f, float speed, float tick) {
	}

}
