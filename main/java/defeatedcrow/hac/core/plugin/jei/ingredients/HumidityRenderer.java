package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHumidity;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;

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
		return;
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DCHumidity ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<String>();
		if (ingredient != null) {
			tooltip.add(ingredient.localize());
		}
		return tooltip;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, DCHumidity ingredient) {
		return minecraft.fontRenderer;
	}

}
