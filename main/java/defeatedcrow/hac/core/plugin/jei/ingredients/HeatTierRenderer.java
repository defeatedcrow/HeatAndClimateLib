package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCHeatTier;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;

public class HeatTierRenderer implements IIngredientRenderer<DCHeatTier> {
	private final int width;
	private final int height;
	private final boolean show;

	public HeatTierRenderer() {
		this(false, 16, 16);
	}

	public HeatTierRenderer(int w, int h) {
		this(true, w, h);
	}

	public HeatTierRenderer(boolean s, int w, int h) {
		show = s;
		width = w;
		height = h;
	}

	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, DCHeatTier ingredient) {
		return;
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DCHeatTier ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<String>();
		if (ingredient != null) {
			tooltip.add(ingredient.localize() + " " + ingredient.getTemp());
		}
		return tooltip;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, DCHeatTier ingredient) {
		return minecraft.fontRenderer;
	}

}
