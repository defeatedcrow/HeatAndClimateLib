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
	public void drawExtras(Minecraft mc) {
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if (!(recipeWrapper instanceof ClimateCropWrapper))
			return;
		ClimateCropWrapper wrapper = ((ClimateCropWrapper) recipeWrapper);

		List inputs = wrapper.getInputs();
		List outputs = wrapper.getOutputs();

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
	}

}
