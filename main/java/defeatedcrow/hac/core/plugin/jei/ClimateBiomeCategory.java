package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.ClimateTypes;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ClimateBiomeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateBiomeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_biome_gui.png");
		background = guiHelper.createDrawable(location, 8, 9, 160, 110);
	}

	@Override
	public String getUid() {
		return "dcs_climate.biome";
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
		if (!(recipeWrapper instanceof ClimateBiomeWrapper))
			return;
		ClimateBiomeWrapper wrapper = ((ClimateBiomeWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<DCHeatTier> temps = wrapper.getTemps();
		for (int i = 0; i < 4; i++) {
			recipeLayout.getIngredientsGroup(ClimateTypes.TEMP).init(i, true, new HeatTierRenderer(), 17 + 21 * i, 45,
					20, 5, 0, 0);
			recipeLayout.getIngredientsGroup(ClimateTypes.TEMP).set(i, temps.get(i));
		}

		List<DCHumidity> hums = wrapper.getHums();
		recipeLayout.getIngredientsGroup(ClimateTypes.HUM).init(0, true, new HumidityRenderer(), 17, 66, 40, 5, 0, 0);
		recipeLayout.getIngredientsGroup(ClimateTypes.HUM).set(0, hums.get(0));

		List<DCAirflow> airs = wrapper.getAirs();
		recipeLayout.getIngredientsGroup(ClimateTypes.AIR).init(0, true, new AirflowRenderer(), 17, 87, 40, 5, 0, 0);
		recipeLayout.getIngredientsGroup(ClimateTypes.AIR).set(0, airs.get(0));

	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
