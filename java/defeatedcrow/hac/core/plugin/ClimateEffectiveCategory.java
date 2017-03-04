package defeatedcrow.hac.core.plugin;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ClimateEffectiveCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateEffectiveCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_effective_gui.png");
		background = guiHelper.createDrawable(location, 8, 8, 160, 56);
	}

	@Override
	public String getUid() {
		return "dcs_climate.effective";
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
	public void drawExtras(Minecraft mc) {}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		setRecipe(recipeLayout, recipeWrapper, null);
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof ClimateEffectiveWrapper))
			return;
		ClimateEffectiveWrapper wrapper = ((ClimateEffectiveWrapper) recipeWrapper);
		wrapper.getIngredients(ingredients);

		List inputs = wrapper.getInputs();

		recipeLayout.getItemStacks().init(0, true, 44, 24);
		recipeLayout.getItemStacks().set(0, inputs);
	}

}
