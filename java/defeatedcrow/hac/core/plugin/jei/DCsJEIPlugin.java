package defeatedcrow.hac.core.plugin.jei;

import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.AirflowRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HeatTierRenderer;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityHelper;
import defeatedcrow.hac.core.plugin.jei.ingredients.HumidityRenderer;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class DCsJEIPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		registry.addRecipeCategories(new ClimateBiomeCategory(jeiHelpers.getGuiHelper()),
				new ClimateEffectiveCategory(jeiHelpers.getGuiHelper()),
				new ClimateSmeltingCategory(jeiHelpers.getGuiHelper()),
				new SpinningRecipeCategory(jeiHelpers.getGuiHelper()),
				new MillRecipeCategory(jeiHelpers.getGuiHelper()), new CrusherRecipeCategory(jeiHelpers.getGuiHelper()),
				new FluidRecipeCategory(jeiHelpers.getGuiHelper()),
				new ReactorRecipeCategory(jeiHelpers.getGuiHelper()),
				new ClimateCropCategory(jeiHelpers.getGuiHelper()));
		registry.addRecipeHandlers(new ShapedNBTHandler(jeiHelpers), new ShapelessNBTHandler(jeiHelpers),
				new ClimateBiomeHandler(), new ClimateEffectiveHandler(), new ClimateSmeltingHandler(),
				new SpinningRecipeHandler(), new MillRecipeHandler(), new CrusherRecipeHandler(),
				new FluidRecipeHandler(), new ReactorRecipeHandler(), new ClimateCropHandler());

		ClimateEffectiveMaker.register(registry);
		ClimateBiomeMaker.register(registry);
		ClimateCropMaker.register(registry);
		ClimateSmeltingMaker.register(registry);
		FluidRecipeMaker.register(registry);
		ReactorRecipeMaker.register(registry);
		MillRecipeMaker.register(registry);
		SpinningRecipeMaker.register(registry);
		CrusherRecipeMaker.register(registry);

		if (!DCsJEIPluginLists.climateIcons.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.climateIcons) {
				registry.addRecipeCategoryCraftingItem(item, CLIMATE_UID);
			}
		}
		registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.SAPLING), BIOME_UID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(DCInit.climate_checker), SMELTING_UID);
		if (!DCsJEIPluginLists.fluidcrafters.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.fluidcrafters) {
				registry.addRecipeCategoryCraftingItem(item, FLUID_UID);
			}
		}
		if (!DCsJEIPluginLists.reactors.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.reactors) {
				registry.addRecipeCategoryCraftingItem(item, REACTOR_UID);
			}
		}
		if (!DCsJEIPluginLists.millstones.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.millstones) {
				registry.addRecipeCategoryCraftingItem(item, MILL_UID);
			}
		}
		if (!DCsJEIPluginLists.spinning.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.spinning) {
				registry.addRecipeCategoryCraftingItem(item, SPINNING_UID);
			}
		}
		if (!DCsJEIPluginLists.crops.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.crops) {
				registry.addRecipeCategoryCraftingItem(item, CROP_UID);
			}
		}
		if (!DCsJEIPluginLists.crusher.isEmpty()) {
			for (ItemStack item : DCsJEIPluginLists.crusher) {
				registry.addRecipeCategoryCraftingItem(item, CRUSHER_UID);
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
