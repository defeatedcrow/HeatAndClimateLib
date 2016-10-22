package defeatedcrow.hac.core.plugin;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ClimateSmeltingCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateSmeltingCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_smelting_gui_jei.png");
		background = guiHelper.createDrawable(location, 8, 5, 160, 70, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.smelting";
	}

	@Override
	public String getTitle() {
		return I18n.translateToLocal(getUid());
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft mc) {
		mc.fontRendererObj.drawString("TEMP", 23, 39, 0xFF0000, true);
		mc.fontRendererObj.drawString("HUM", 121, 49, 0x0000FF, true);
		mc.fontRendererObj.drawString("AIR", 27, 59, 0x00FF00, true);
		mc.fontRendererObj.drawString("", 0, 0, 0xFFFFFF, false);
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if (!(recipeWrapper instanceof ClimateSmeltingWrapper))
			return;
		ClimateSmeltingWrapper wrapper = ((ClimateSmeltingWrapper) recipeWrapper);

		List inputs = wrapper.getInputs();
		List outputs = wrapper.getOutputs();

		recipeLayout.getItemStacks().init(0, true, 45, 13);
		recipeLayout.getItemStacks().set(0, inputs);

		recipeLayout.getItemStacks().init(1, false, 97, 13);
		recipeLayout.getItemStacks().set(1, (ItemStack) outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(2, false, 118, 13);
			recipeLayout.getItemStacks().set(2, (ItemStack) outputs.get(1));
		}
	}

}
