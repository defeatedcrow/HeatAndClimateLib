package defeatedcrow.hac.core.plugin.jei;

import java.util.List;

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

public class MillRecipeCategory implements IRecipeCategory {

	private final IDrawableStatic background;

	public MillRecipeCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation("dcs_climate", "textures/gui/c_mill_gui_jei.png");
		background = guiHelper.createDrawable(location, 8, 5, 160, 66, 3, 0, 0, 0);
	}

	@Override
	public String getUid() {
		return "dcs_climate.mill";
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
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if (!(recipeWrapper instanceof MillRecipeWrapper))
			return;
		MillRecipeWrapper wrapper = ((MillRecipeWrapper) recipeWrapper);
		wrapper.getIngredients(ingredients);

		List inputs = wrapper.getInputs();
		List outputs = wrapper.getOutputs();

		recipeLayout.getItemStacks().init(0, true, 45, 29);
		recipeLayout.getItemStacks().set(0, inputs);

		recipeLayout.getItemStacks().init(1, false, 97, 29);
		recipeLayout.getItemStacks().set(1, (ItemStack) outputs.get(0));
		if (outputs.size() > 1) {
			recipeLayout.getItemStacks().init(2, false, 117, 29);
			recipeLayout.getItemStacks().set(2, (ItemStack) outputs.get(1));
		}
	}

}
