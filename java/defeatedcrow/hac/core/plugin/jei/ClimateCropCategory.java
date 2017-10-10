package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.ClimateCore;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ClimateCropCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateCropCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_crops_gui_jei.png");
		background = guiHelper.createDrawable(location, 8, 6, 160, 105, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.crop";
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
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof ClimateCropWrapper))
			return;
		ClimateCropWrapper wrapper = ((ClimateCropWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List inputs = ingredients.getInputs(ItemStack.class);
		List outputs = ingredients.getOutputs(ItemStack.class);

		if (inputs.size() < 2 || outputs.isEmpty()) {
			return;
		}

		recipeLayout.getItemStacks().init(0, true, 39, 33);
		recipeLayout.getItemStacks().set(0, (ItemStack) inputs.get(0));

		recipeLayout.getItemStacks().init(1, false, 80, 51);
		recipeLayout.getItemStacks().set(1, (ItemStack) inputs.get(1));

		for (int i = 0; i < outputs.size(); i++) {
			recipeLayout.getItemStacks().init(2 + i, false, 80 + 18 * i, 23);
			recipeLayout.getItemStacks().set(2 + i, (ItemStack) outputs.get(i));
		}

		List<DCHeatTier> temps = wrapper.getTemps();
		int i = 0;
		for (DCHeatTier temp : temps) {
			recipeLayout.getIngredientsGroup(DCHeatTier.class).init(i, true, new HeatTierRenderer(),
					44 + temp.getID() * 6, 74, 6, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHeatTier.class).set(i, temp);
			i++;
		}

		List<DCHumidity> hums = wrapper.getHums();
		int j = 0;
		for (DCHumidity hum : hums) {
			recipeLayout.getIngredientsGroup(DCHumidity.class).init(j, true, new HumidityRenderer(),
					44 + hum.getID() * 18, 84, 18, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHumidity.class).set(j, hum);
			j++;
		}

		List<DCAirflow> airs = wrapper.getAirs();
		int k = 0;
		for (DCAirflow air : airs) {
			recipeLayout.getIngredientsGroup(DCAirflow.class).init(k, true, new AirflowRenderer(),
					44 + air.getID() * 18, 94, 18, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCAirflow.class).set(k, air);
			k++;
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
