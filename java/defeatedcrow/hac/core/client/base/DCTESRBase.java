package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.core.base.DCTileEntity;
import defeatedcrow.hac.machine.client.ModelFuelStove;

@SideOnly(Side.CLIENT)
public class DCTESRBase extends TileEntitySpecialRenderer<DCTileEntity> {

	@Override
	public void renderTileEntityAt(DCTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		int type = 0;
		int face = 0;
		float f = 0.0F;

		if (te.hasWorldObj()) {
			int meta = te.getBlockMetadata();
			type = meta & 3;
			face = 5 - (meta >> 2);
			if (face == 2) {
				f = 0F;
			}
			if (face == 3) {
				f = 180F;
			}
			if (face == 4) {
				f = -90F;
			}
			if (face == 5) {
				f = 90F;
			}
		}

		this.bindTexture(new ResourceLocation(getTexPass(type)));

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);

		GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
		this.render(type, 0.0F);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	protected String getTexPass(int i) {
		return "dcs_climate:textures/tiles/stove_fuel.png";
	}

	protected ModelBase getModel(int i) {
		return new ModelFuelStove();
	}

	protected void render(int meta, float f) {
		((ModelFuelStove) getModel(meta)).render(f);
	}
}
