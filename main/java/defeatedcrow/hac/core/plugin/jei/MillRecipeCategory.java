package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.core.ClimateCore;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MillRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public MillRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_mill_gui_jei.png");
		background = guiHelper.createDrawable(location, 10, 20, 150, 40);
	}

	@Override
	public String getUid() {
		return "dcs_climate.mill";
	}

	@Override
	public String getTitle() {
		return I18n.format(getUid());
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft mc) {}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof MillRecipeWrapper))
			return;
		MillRecipeWrapper wrapper = ((MillRecipeWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<ItemStack> inputs = wrapper.getInputs();
		List<ItemStack> outputs = wrapper.getOutputs();

		recipeLayout.getItemStacks().init(0, true, 43, 11);
		recipeLayout.getItemStacks().set(0, inputs);

		recipeLayout.getItemStacks().init(1, false, 95, 11);
		recipeLayout.getItemStacks().set(1, outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(2, false, 115, 11);
			recipeLayout.getItemStacks().set(2, outputs.get(1));
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
