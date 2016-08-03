package defeatedcrow.hac.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import defeatedcrow.hac.core.recipe.ConvertTargetList;

public class OreRegister {
	public static void load() {
		loadOres();
		addConversion();
	}

	static void loadOres() {

		// vanilla
		OreDictionary.registerOre("stickWood", new ItemStack(Items.STICK));
		OreDictionary.registerOre("stickBlaze", new ItemStack(Items.BLAZE_ROD));

		OreDictionary.registerOre("dustBlaze", new ItemStack(Items.BLAZE_POWDER));
		OreDictionary.registerOre("dustGunpowder", new ItemStack(Items.GUNPOWDER));
		OreDictionary.registerOre("dustSugar", new ItemStack(Items.SUGAR));

		OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL));
		OreDictionary.registerOre("gemFlint", new ItemStack(Items.FLINT));

		OreDictionary.registerOre("itemLeather", new ItemStack(Items.LEATHER));
		OreDictionary.registerOre("itemFeather", new ItemStack(Items.FEATHER));
		OreDictionary.registerOre("itemString", new ItemStack(Items.STRING));

		OreDictionary.registerOre("foodFish", new ItemStack(Items.FISH));

		OreDictionary.registerOre("cropApple", new ItemStack(Items.APPLE));
		OreDictionary.registerOre("cropCocoa", new ItemStack(Items.DYE, 1, 3));
		OreDictionary.registerOre("cropPumpkin", new ItemStack(Blocks.PUMPKIN));
		OreDictionary.registerOre("cropMelon", new ItemStack(Items.MELON));
		OreDictionary.registerOre("cropReeds", new ItemStack(Items.REEDS));

		OreDictionary.registerOre("bucketWater", new ItemStack(Items.WATER_BUCKET));
		OreDictionary.registerOre("bucketLava", new ItemStack(Items.LAVA_BUCKET));
		OreDictionary.registerOre("bucketMilk", new ItemStack(Items.MILK_BUCKET));
		OreDictionary.registerOre("bucketEmpty", new ItemStack(Items.BUCKET));
	}

	static void addConversion() {
		ConvertTargetList.addExclusing(new ItemStack(Blocks.HAY_BLOCK));
		ConvertTargetList.addExclusing(new ItemStack(Blocks.WOOL, 1, 32767));

		ConvertTargetList.addReplaceTarget(new ItemStack(Items.MILK_BUCKET), "bucketMilk");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.WATER_BUCKET), "bucketWater");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.LAVA_BUCKET), "bucketLava");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.SUGAR), "dustSugar");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.WHEAT), "dustFlour");
		ConvertTargetList.addReplaceTarget(new ItemStack(Blocks.WOOL, 1, 32767), "itemCloth");
	}

}
