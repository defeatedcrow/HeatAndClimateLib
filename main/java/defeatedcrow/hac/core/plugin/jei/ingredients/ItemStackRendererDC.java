package defeatedcrow.hac.core.plugin.jei.ingredients;

import java.util.List;

import mezz.jei.plugins.vanilla.ingredients.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

public class ItemStackRendererDC extends ItemStackRenderer {

	private final int chance;

	public ItemStackRendererDC() {
		super();
		chance = 100;
	}

	public ItemStackRendererDC(float c) {
		super();
		chance = (int) (c * 100);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, ItemStack ingredient, ITooltipFlag tooltipFlag) {
		List<String> list = super.getTooltip(minecraft, ingredient, tooltipFlag);
		if (chance > 0 && chance < 100) {
			list.add("chance " + chance + "%");
		}
		return list;
	}

}
