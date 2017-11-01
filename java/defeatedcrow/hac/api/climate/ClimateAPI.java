package defeatedcrow.hac.api.climate;

/**
 * 気候の登録や参照はここから行って下さい
 */
public class ClimateAPI {

	private ClimateAPI() {}

	public static boolean isLoaded;

	public static IBiomeClimateRegister register;
	public static IHeatBlockRegister registerBlock;
	public static IClimateCalculator calculator;
	public static IClimateHelper helper;

}
