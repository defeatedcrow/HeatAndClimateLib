package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityRenderer;
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

public class ClimateBiomeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateBiomeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_biome_gui.png");
		background = guiHelper.createDrawable(location, 8, 8, 160, 100);
	}

	@Override
	public String getUid() {
		return "dcs_climate.biome";
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
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		setRecipe(recipeLayout, recipeWrapper, null);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof ClimateBiomeWrapper))
			return;
		ClimateBiomeWrapper wrapper = ((ClimateBiomeWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<DCHeatTier> temps = wrapper.getTemps();
		recipeLayout.getIngredientsGroup(DCHeatTier.class).init(0, true, new HeatTierRenderer(), 17, 37, 40, 5, 0, 0);
		recipeLayout.getIngredientsGroup(DCHeatTier.class).set(0, temps.get(0));

		List<DCHumidity> hums = wrapper.getHums();
		recipeLayout.getIngredientsGroup(DCHumidity.class).init(0, true, new HumidityRenderer(), 17, 59, 40, 5, 0, 0);
		recipeLayout.getIngredientsGroup(DCHumidity.class).set(0, hums.get(0));

		List<DCAirflow> airs = wrapper.getAirs();
		recipeLayout.getIngredientsGroup(DCAirflow.class).init(0, true, new AirflowRenderer(), 17, 81, 40, 5, 0, 0);
		recipeLayout.getIngredientsGroup(DCAirflow.class).set(0, airs.get(0));

	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

}
