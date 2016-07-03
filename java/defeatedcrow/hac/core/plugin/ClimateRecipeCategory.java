package defeatedcrow.hac.core.plugin;

import java.util.Collection;
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

public class ClimateRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public ClimateRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_crafting_gui.png");
		background = guiHelper.createDrawable(location, 8, 5, 160, 66, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.recipe";
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
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if (!(recipeWrapper instanceof ClimateRecipeWrapper))
			return;
		ClimateRecipeWrapper wrapper = ((ClimateRecipeWrapper) recipeWrapper);

		List inputs = wrapper.getInputs();
		List outputs = wrapper.getOutputs();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int l = i * 3 + j;
				if (l < inputs.size()) {
					recipeLayout.getItemStacks().init(l, true, 10 + j * 18, 12 + i * 18);
					if (inputs.get(l) instanceof ItemStack) {
						recipeLayout.getItemStacks().set(l, (ItemStack) inputs.get(l));
					} else if (inputs.get(l) instanceof Collection) {
						recipeLayout.getItemStacks().set(l, (Collection<ItemStack>) inputs.get(l));
					}
				}
			}
		}

		recipeLayout.getItemStacks().init(9, false, 96, 48);
		recipeLayout.getItemStacks().set(9, (ItemStack) outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(10, false, 117, 48);
			recipeLayout.getItemStacks().set(10, (ItemStack) outputs.get(1));
		}
	}

}
