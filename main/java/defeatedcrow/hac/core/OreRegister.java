package defeatedcrow.hac.core;

import defeatedcrow.hac.core.recipe.ConvertTargetList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

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
		OreDictionary.registerOre("charcoal", new ItemStack(Items.COAL, 1, 1));

		OreDictionary.registerOre("leather", new ItemStack(Items.LEATHER));
		OreDictionary.registerOre("feather", new ItemStack(Items.FEATHER));
		OreDictionary.registerOre("string", new ItemStack(Items.STRING));
		OreDictionary.registerOre("rabbithide", new ItemStack(Items.RABBIT_HIDE));

		OreDictionary.registerOre("listAllfishraw", new ItemStack(Items.FISH));
		OreDictionary.registerOre("foodCodraw", new ItemStack(Items.FISH));
		OreDictionary.registerOre("listAllfishraw", new ItemStack(Items.FISH, 1, 1));
		OreDictionary.registerOre("foodSalmonraw", new ItemStack(Items.FISH, 1, 1));

		OreDictionary.registerOre("listAllmeatraw", new ItemStack(Items.BEEF));
		OreDictionary.registerOre("listAllmeatraw", new ItemStack(Items.PORKCHOP));
		OreDictionary.registerOre("listAllmeatraw", new ItemStack(Items.CHICKEN));
		OreDictionary.registerOre("listAllmeatraw", new ItemStack(Items.MUTTON));
		OreDictionary.registerOre("listAllmeatraw", new ItemStack(Items.RABBIT));
		OreDictionary.registerOre("listAllbeefraw", new ItemStack(Items.BEEF));
		OreDictionary.registerOre("listAllporkraw", new ItemStack(Items.PORKCHOP));
		OreDictionary.registerOre("listAllchickenraw", new ItemStack(Items.CHICKEN));
		OreDictionary.registerOre("listAllmuttonraw", new ItemStack(Items.MUTTON));
		OreDictionary.registerOre("listAllrabbitraw", new ItemStack(Items.RABBIT));
		OreDictionary.registerOre("listAllsugar", new ItemStack(Items.SUGAR));

		OreDictionary.registerOre("cropApple", new ItemStack(Items.APPLE));
		OreDictionary.registerOre("cropCocoa", new ItemStack(Items.DYE, 1, 3));
		OreDictionary.registerOre("cropPumpkin", new ItemStack(Blocks.PUMPKIN));
		OreDictionary.registerOre("cropMelon", new ItemStack(Items.MELON));
		OreDictionary.registerOre("cropReeds", new ItemStack(Items.REEDS));
		OreDictionary.registerOre("cropBeetroot", new ItemStack(Items.BEETROOT));
		OreDictionary.registerOre("flowerRed", new ItemStack(Blocks.RED_FLOWER));
		OreDictionary.registerOre("flowerYellow", new ItemStack(Blocks.YELLOW_FLOWER));
		OreDictionary.registerOre("listAllmushroom", new ItemStack(Blocks.RED_MUSHROOM));
		OreDictionary.registerOre("listAllmushroom", new ItemStack(Blocks.BROWN_MUSHROOM));
		OreDictionary.registerOre("cropMushroomRed", new ItemStack(Blocks.RED_MUSHROOM));
		OreDictionary.registerOre("cropMushroomBrowm", new ItemStack(Blocks.BROWN_MUSHROOM));

		OreDictionary.registerOre("bucketWater", new ItemStack(Items.WATER_BUCKET));
		OreDictionary.registerOre("bucketLava", new ItemStack(Items.LAVA_BUCKET));
		OreDictionary.registerOre("bucketMilk", new ItemStack(Items.MILK_BUCKET));
		OreDictionary.registerOre("bucketEmpty", new ItemStack(Items.BUCKET));

		OreDictionary.registerOre("blockWool", new ItemStack(Blocks.WOOL, 1, 32767));

		OreDictionary.registerOre("blockTallgrass", new ItemStack(Blocks.TALLGRASS, 1, 0));
		OreDictionary.registerOre("blockTallgrass", new ItemStack(Blocks.TALLGRASS, 1, 1));
		OreDictionary.registerOre("blockTallgrass", new ItemStack(Blocks.TALLGRASS, 1, 2));
		OreDictionary.registerOre("blockTallgrass", new ItemStack(Blocks.DOUBLE_PLANT, 1, 2));
		OreDictionary.registerOre("blockTallgrass", new ItemStack(Blocks.DOUBLE_PLANT, 1, 3));

		OreDictionary.registerOre("listAllseeds", new ItemStack(Items.WHEAT_SEEDS, 1, 0));
		OreDictionary.registerOre("listAllseeds", new ItemStack(Items.MELON_SEEDS, 1, 0));
		OreDictionary.registerOre("listAllseeds", new ItemStack(Items.PUMPKIN_SEEDS, 1, 0));
		OreDictionary.registerOre("listAllseeds", new ItemStack(Items.BEETROOT_SEEDS, 1, 0));

		OreDictionary.registerOre("cobblestone", new ItemStack(Blocks.STONE, 1, 1));
		OreDictionary.registerOre("cobblestone", new ItemStack(Blocks.STONE, 1, 3));
		OreDictionary.registerOre("cobblestone", new ItemStack(Blocks.STONE, 1, 5));
		OreDictionary.registerOre("stoneGranite", new ItemStack(Blocks.STONE, 1, 1));
		OreDictionary.registerOre("stoneDiorite", new ItemStack(Blocks.STONE, 1, 3));
		OreDictionary.registerOre("stoneAndesite", new ItemStack(Blocks.STONE, 1, 5));

		OreDictionary.registerOre("egg", new ItemStack(Items.EGG));

		OreDictionary.registerOre("listAllveggie", new ItemStack(Items.CARROT, 1, 0));
		OreDictionary.registerOre("listAllveggie", new ItemStack(Items.POTATO, 1, 0));
		OreDictionary.registerOre("listAllveggie", new ItemStack(Items.BEETROOT, 1, 0));
		OreDictionary.registerOre("listAllveggie", new ItemStack(Blocks.PUMPKIN, 1, 0));
		OreDictionary.registerOre("bread", new ItemStack(Items.BREAD, 1, 0));

		OreDictionary.registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_BEEF, 1, 0));
		OreDictionary.registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_CHICKEN, 1, 0));
		OreDictionary.registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_MUTTON, 1, 0));
		OreDictionary.registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_PORKCHOP, 1, 0));
		OreDictionary.registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_RABBIT, 1, 0));
		OreDictionary.registerOre("listAllbeefcooked", new ItemStack(Items.COOKED_BEEF, 1, 0));
		OreDictionary.registerOre("listAllchickencooked", new ItemStack(Items.COOKED_CHICKEN, 1, 0));
		OreDictionary.registerOre("listAllmuttoncooked", new ItemStack(Items.COOKED_MUTTON, 1, 0));
		OreDictionary.registerOre("listAllporkcooked", new ItemStack(Items.COOKED_PORKCHOP, 1, 0));
		OreDictionary.registerOre("listAllrabbitcooked", new ItemStack(Items.COOKED_RABBIT, 1, 0));

		OreDictionary.registerOre("vineLeaves", new ItemStack(Blocks.VINE, 1, 0));

		Item cod = Item.REGISTRY.getObject(new ResourceLocation("minecraft:cod"));
		if (cod != null) {
			OreDictionary.registerOre("foodCodraw", new ItemStack(cod, 1, 0));
		}
	}

	static void addConversion() {
		ConvertTargetList.addExclusing(new ItemStack(Blocks.HAY_BLOCK));
		ConvertTargetList.addExclusing(new ItemStack(Blocks.WOOL, 1, 32767));
		ConvertTargetList.addExclusing(new ItemStack(Items.LEATHER));

		ConvertTargetList.addReplaceTarget(new ItemStack(Items.MILK_BUCKET), "bucketMilk");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.WATER_BUCKET), "bucketWater");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.LAVA_BUCKET), "bucketLava");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.WHEAT), "dustFlour");
		ConvertTargetList.addReplaceTarget(new ItemStack(Blocks.WOOL, 1, 32767), "itemCloth");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.RABBIT_HIDE), "rabbithide");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.LEATHER), "leather");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.FISH), "foodCodraw");
		ConvertTargetList.addReplaceTarget(new ItemStack(Items.FISH, 1, 1), "foodSalmonraw");

	}

}
