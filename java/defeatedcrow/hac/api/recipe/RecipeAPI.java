package defeatedcrow.hac.api.recipe;

/**
 * レシピの登録や参照はここから行って下さい
 */
public class RecipeAPI {

	private RecipeAPI() {}

	public static boolean isLoaded;

	public static IClimateRecipeRegister registerRecipes;
	public static IClimateSmeltingRegister registerSmelting;

	public static IMillRecipeRegister registerMills;
	public static IFluidRecipeRegister registerFluidRecipes;
	public static IReactorRecipeRegister registerReactorRecipes;
	public static ISpinningRecipeRegister registerSpinningRecipes;
}
