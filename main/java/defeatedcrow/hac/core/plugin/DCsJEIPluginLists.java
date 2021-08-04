package defeatedcrow.hac.core.plugin;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.core.plugin.jei.ClimateEffectiveTile;
import net.minecraft.item.ItemStack;

public class DCsJEIPluginLists {

	public static final DCsJEIPluginLists INSTANCE = new DCsJEIPluginLists();

	private DCsJEIPluginLists() {}

	public static final List<ItemStack> millstones = new ArrayList<>();
	public static final List<ItemStack> fluidcrafters = new ArrayList<>();

	public static final List<ItemStack> crops = new ArrayList<>();
	public static final List<ItemStack> spinning = new ArrayList<>();
	public static final List<ItemStack> crusher = new ArrayList<>();
	public static final List<ItemStack> crusher_sus = new ArrayList<>();
	public static final List<ItemStack> crusher_ti = new ArrayList<>();
	public static final List<ItemStack> crusher_sc = new ArrayList<>();

	public static final List<ItemStack> excluder = new ArrayList<>();

	public static final List<ClimateEffectiveTile> climate = new ArrayList<>();
	public static final List<ItemStack> climateIcons = new ArrayList<>();

	public static final List<ItemStack> fluidcrafters_steel = new ArrayList<>();
	public static final List<ItemStack> fluidcrafters_pot = new ArrayList<>();
	public static final List<ItemStack> fluidcrafters_drink = new ArrayList<>();
	public static final List<ItemStack> fluidcrafters_skillet = new ArrayList<>();

	public static final List<ItemStack> reactors = new ArrayList<>();
	public static final List<ItemStack> reactors_simple = new ArrayList<>();

}
