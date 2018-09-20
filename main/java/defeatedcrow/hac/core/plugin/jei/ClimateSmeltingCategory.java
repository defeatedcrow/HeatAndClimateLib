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
		mc.fontRenderer.drawString("TEMP", 18, 39, 0xFF0000, false);
		mc.fontRenderer.drawString("HUM", 126, 49, 0x0000FF, false);
		mc.fontRenderer.drawString("AIR", 22, 59, 0x00FF00, false);
		mc.fontRenderer.drawString("", 0, 0, 0xFFFFFF, false);
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof ClimateSmeltingWrapper))
			return;
		ClimateSmeltingWrapper wrapper = ((ClimateSmeltingWrapper) recipeWrapper);
		// wrapper.getIngredients(ingredients);

		List<ItemStack> inputs = wrapper.getInputs();
		List<ItemStack> outputs = wrapper.getOutputs();

		recipeLayout.getItemStacks().init(0, true, 45, 13);
		recipeLayout.getItemStacks().set(0, inputs);

		recipeLayout.getItemStacks().init(1, false, 97, 13);
		recipeLayout.getItemStacks().set(1, outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(2, false, 118, 13);
			recipeLayout.getItemStacks().set(2, outputs.get(1));
		}

		List<DCHeatTier> temps = wrapper.getTemps();
		int i = 0;
		for (DCHeatTier temp : temps) {
			recipeLayout.getIngredientsGroup(DCHeatTier.class).init(i, true, new HeatTierRenderer(),
					38 + temp.getID() * 6, 42, 6, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHeatTier.class).set(i, temp);
			i++;
		}

		List<DCHumidity> hums = wrapper.getHums();
		int j = 0;
		for (DCHumidity hum : hums) {
			recipeLayout.getIngredientsGroup(DCHumidity.class).init(j, true, new HumidityRenderer(),
					38 + hum.getID() * 21, 52, 21, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCHumidity.class).set(j, hum);
			j++;
		}

		List<DCAirflow> airs = wrapper.getAirs();
		int k = 0;
		for (DCAirflow air : airs) {
			recipeLayout.getIngredientsGroup(DCAirflow.class).init(k, true, new AirflowRenderer(),
					38 + air.getID() * 21, 62, 21, 5, 0, 0);
			recipeLayout.getIngredientsGroup(DCAirflow.class).set(k, air);
			k++;
		}
	}

	@Override
	public String getModName() {
		return ClimateCore.MOD_NAME;
	}

}
