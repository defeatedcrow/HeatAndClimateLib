package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

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
		base.render(0.0625F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f) {
	}

}
