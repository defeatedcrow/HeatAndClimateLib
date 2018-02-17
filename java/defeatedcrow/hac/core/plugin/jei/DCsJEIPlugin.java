package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.climate.recipe.ClimateSmelting;
import defeatedcrow.hac.core.climate.recipe.CrusherRecipe;
import defeatedcrow.hac.core.climate.recipe.FluidCraftRecipe;
import defeatedcrow.hac.core.climate.recipe.MillRecipe;
import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import defeatedcrow.hac.core.climate.recipe.SpinningRecipe;
import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityRenderer;
import defeatedcrow.hac.core.recipe.ShapedNBTRecipe;
import defeatedcrow.hac.core.recipe.ShapelessNBTRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

@JEIPlugin
public class DCsJEIPlugin implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registry.addRecipeCategories(new ClimateBiomeCategory(guiHelper), new ClimateEffectiveCategory(guiHelper),
				new ClimateSmeltingCategory(guiHelper), new SpinningRecipeCategory(guiHelper),
				new MillRecipeCategory(guiHelper), new FluidRecipeCategory(guiHelper),
				new ReactorRecipeCategory(guiHelper), new ClimateCropCategory(guiHelper),
				new CrusherRecipeCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		registry.handleRecipes(Biome.class, recipe -> new ClimateBiomeWrapper(recipe), BIOME_UID);
		registry.handleRecipes(IClimateCrop.class, recipe -> new ClimateCropWrapper(recipe), CROP_UID);
		registry.handleRecipes(ClimateEffectiveTile.class, recipe -> new ClimateEffectiveWrapper(recipe), CLIMATE_UID);
		registry.handleRecipes(ClimateSmelting.class, recipe -> new ClimateSmeltingWrapper(recipe), SMELTING_UID);
		registry.handleRecipes(MillRecipe.class, recipe -> new MillRecipeWrapper(recipe), MILL_UID);
		registry.handleRecipes(FluidCraftRecipe.class, recipe -> new FluidRecipeWrapper(recipe), FLUID_UID);
		registry.handleRecipes(ReactorRecipe.class, recipe -> new ReactorRecipeWrapper(recipe), REACTOR_UID);
		registry.handleRecipes(SpinningRecipe.class, recipe -> new SpinningRecipeWrapper(recipe), SPINNING_UID);
		registry.handleRecipes(CrusherRecipe.class, recipe -> new CrusherRecipeWrapper(recipe), CRUSHER_UID);

		ClimateEffectiveMaker.register(registry);
		ClimateBiomeMaker.register(registry);
		ClimateCropMaker.register(registry);
		ClimateSmeltingMaker.register(registry);
		FluidRecipeMaker.register(registry);
		ReactorRecipeMaker.register(registry);
		MillRecipeMaker.register(registry);
		SpinningRecipeMaker.register(registry);
		CrusherRecipeMaker.register(registry);

		registry.handleRecipes(ShapedNBTRecipe.class, recipe -> new ShapedNBTWrapper(jeiHelpers, recipe),
				VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(ShapelessNBTRecipe.class, recipe -> new ShapelessNBTWrapper(jeiHelpers, recipe),
				VanillaRecipeCategoryUid.CRAFTING);

		if (!DCsJEIPluginLists.climateIcons.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.climateIcons) {
				registry.addRecipeCatalyst(item, CLIMATE_UID);
			}
		}
		registry.addRecipeCatalyst(new ItemStack(Blocks.SAPLING), BIOME_UID);
		registry.addRecipeCatalyst(new ItemStack(DCInit.climate_checker), SMELTING_UID);
		if (!DCsJEIPluginLists.fluidcrafters.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.fluidcrafters) {
				registry.addRecipeCatalyst(item, FLUID_UID);
			}
		}
		if (!DCsJEIPluginLists.reactors.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.reactors) {
				registry.addRecipeCatalyst(item, REACTOR_UID);
			}
		}
		if (!DCsJEIPluginLists.millstones.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.millstones) {
				registry.addRecipeCatalyst(item, MILL_UID);
			}
		}
		if (!DCsJEIPluginLists.spinning.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.spinning) {
				registry.addRecipeCatalyst(item, SPINNING_UID);
			}
		}
		if (!DCsJEIPluginLists.crops.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.crops) {
				registry.addRecipeCatalyst(item, CROP_UID);
			}
		}
		if (!DCsJEIPluginLists.crusher.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.crusher) {
				registry.addRecipeCatalyst(item, CRUSHER_UID);
			}
		}

		if (!DCsJEIPluginLists.excluder.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.excluder) {
				jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(item);
			}
		}

	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		registry.register(DCHeatTier.class, DCHeatTier.createList(), new HeatTierHelper(), new HeatTierRenderer());
		registry.register(DCHumidity.class, DCHumidity.createList(), new HumidityHelper(), new HumidityRenderer());
		registry.register(DCAirflow.class, DCAirflow.createList(), new AirflowHelper(), new AirflowRenderer());
	}

	public static final String BIOME_UID = "dcs_climate.biome";
	public static final String CLIMATE_UID = "dcs_climate.effective";
	public static final String SMELTING_UID = "dcs_climate.smelting";
	public static final String MILL_UID = "dcs_climate.mill";
	public static final String FLUID_UID = "dcs_climate.fluidcraft";
	public static final String REACTOR_UID = "dcs_climate.reactor";
	public static final String SPINNING_UID = "dcs_climate.spinning";
	public static final String CROP_UID = "dcs_climate.crop";
	public static final String CRUSHER_UID = "dcs_climate.crusher";
	public static final String NBT_UID = "dcs_climate.nbt";

}
