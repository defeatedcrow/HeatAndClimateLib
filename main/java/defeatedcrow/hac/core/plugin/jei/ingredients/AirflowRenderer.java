package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;

public class AirflowRenderer implements IIngredientRenderer<DCAirflow> {
	private final int width;
	private final int height;
	private final boolean show;

	public AirflowRenderer() {
		this(false, 16, 16);
	}

	public AirflowRenderer(int w, int h) {
		this(true, w, h);
	}

	public AirflowRenderer(boolean s, int w, int h) {
		show = s;
		width = w;
		height = h;
	}

	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, DCAirflow ingredient) {
		return;
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, DCAirflow ingredient, ITooltipFlag tooltipFlag) {
		List<String> tooltip = new ArrayList<String>();
		if (ingredient != null) {
			tooltip.add(ingredient.name());
		}
		return tooltip;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, DCAirflow ingredient) {
		return minecraft.fontRenderer;
	}

}
