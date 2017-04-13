package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHumidity;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class HumidityRenderer implements IIngredientRenderer<DCHumidity> {
	private final int width;
	private final int height;
	private final boolean show;

	public HumidityRenderer() {
		this(false, 16, 16);
	}

	public HumidityRenderer(int w, int h) {
		this(true, w, h);
	}

	public HumidityRenderer(boolean s, int w, int h) {
		show = s;
		width = w;
		height = h;
	}

	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, DCHumidity ingredient) {
		if (ingredient == null || !show) {
			return;
		}

		int[] color = ingredient.getColor();
		GlStateManager.color(color[0], color[1], color[2], 1.0F);
		minecraft.renderEngine.bindTexture(new ResourceLocation("dcs_climate", "textures/gui/icon_base.png"));

		int x = xPosition;
		int y = yPosition + 16;

		double uMin = 0D;
		double uMax = 0D;
		double vMin = width / 16.0D;
		double vMax = height / 16.0D;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(x, y + height, 100).tex(uMin, vMax).endVertex();
		vertexBuffer.pos(x + width, y + height, 100).tex(uMax, vMax).endVertex();
		vertexBuffer.pos(x + width, y, 100).tex(uMax, vMin).endVertex();
		vertexBuffer.pos(x, y, 100).tex(uMin, vMin).endVertex();
		tessellator.draw();

		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DCHumidity ingredient) {
		List<String> tooltip = new ArrayList<String>();
		if (ingredient != null) {
			tooltip.add(ingredient.name());
		}
		return tooltip;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, DCHumidity ingredient) {
		return minecraft.fontRendererObj;
	}

}
