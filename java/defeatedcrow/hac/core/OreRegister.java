package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreRegister {
	public static void load() {
		loadOres();
	}

	static void loadOres() {

		// vanilla
		OreDictionary.registerOre("stickWood", new ItemStack(Items.stick));
		OreDictionary.registerOre("stickBlaze", new ItemStack(Items.blaze_rod));

		OreDictionary.registerOre("dustBlaxe", new ItemStack(Items.blaze_powder));
		OreDictionary.registerOre("dustGumpowder", new ItemStack(Items.gunpowder));
		OreDictionary.registerOre("dustSugar", new ItemStack(Items.sugar));

		OreDictionary.registerOre("itemLeather", new ItemStack(Items.leather));
		OreDictionary.registerOre("itemFeather", new ItemStack(Items.feather));
		OreDictionary.registerOre("itemString", new ItemStack(Items.string));

		OreDictionary.registerOre("foodEgg", new ItemStack(Items.egg));
		OreDictionary.registerOre("foodFish", new ItemStack(Items.fish));

		OreDictionary.registerOre("cropApple", new ItemStack(Items.apple));
		OreDictionary.registerOre("cropCocoa", new ItemStack(Items.dye, 1, 3));
		OreDictionary.registerOre("cropPumpkin", new ItemStack(Blocks.pumpkin));
		OreDictionary.registerOre("cropMelon", new ItemStack(Items.melon));

		OreDictionary.registerOre("bucketWater", new ItemStack(Items.water_bucket));
		OreDictionary.registerOre("bucketLava", new ItemStack(Items.lava_bucket));
		OreDictionary.registerOre("bucketMilk", new ItemStack(Items.milk_bucket));
	}

}
