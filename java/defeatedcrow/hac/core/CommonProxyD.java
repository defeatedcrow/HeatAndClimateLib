package defeatedcrow.hac.core;

import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.recipe.IReactorRecipe;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.client.base.ModelThinBiped;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.climate.recipe.ReactorRecipe;
import defeatedcrow.hac.core.event.BlockUpdateDC;
import defeatedcrow.hac.core.event.CaveGenLavaDC;
import defeatedcrow.hac.core.event.ClickEventDC;
import defeatedcrow.hac.core.event.LivingEventDC;
import defeatedcrow.hac.core.event.LivingHurtDC;
import defeatedcrow.hac.core.event.SuffocationEventDC;
import defeatedcrow.hac.core.event.TickEventDC;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.util.DCPotion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CommonProxyD {

	public void loadMaterial() {
		DCPotion.init();
		MaterialRegister.load();
	}

	public void loadTE() {

	}

	public void loadWorldGen() {}

	public void loadEntity() {}

	public void loadInit() {
		OreRegister.load();
		if (CoreConfigDC.enableVanilla) {
			VanillaRecipeRegister.load();
		}
		MinecraftForge.EVENT_BUS.register(new LivingEventDC());
		MinecraftForge.EVENT_BUS.register(new BlockUpdateDC());
		MinecraftForge.EVENT_BUS.register(new LivingHurtDC());
		MinecraftForge.EVENT_BUS.register(new ClickEventDC());
		MinecraftForge.TERRAIN_GEN_BUS.register(new CaveGenLavaDC());
		MinecraftForge.EVENT_BUS.register(new TickEventDC());
		if (CoreConfigDC.enableSuffocation) {
			MinecraftForge.EVENT_BUS.register(new SuffocationEventDC());
		}

		HaCPacket.init();

		ItemStack ret = new ItemStack(Items.IRON_PICKAXE);
		Map<Enchantment, Integer> ench = new HashMap<>();
		ench.put(Enchantments.UNBREAKING, 1);
		EnchantmentHelper.setEnchantments(ench, ret);
		IReactorRecipe recipe = new ReactorRecipe(ret, null, new FluidStack(FluidRegistry.WATER, 1000), null,
				DCHeatTier.WARM, 0F, new ItemStack(Items.IRON_INGOT), new FluidStack(FluidRegistry.WATER, 1000), null,
				new Object[] {
						new ItemStack(Items.IRON_PICKAXE)
				});
		RecipeAPI.registerReactorRecipes.addRecipe(recipe, DCHeatTier.WARM);
	}

	public ModelThinBiped getArmorModel(int slot) {
		return null;
	}

	public boolean isJumpKeyDown() {
		return false;
	}

	public boolean isSneakKeyDown() {
		return false;
	}

	public boolean isShiftKeyDown() {
		return false;
	}

	public boolean isWarpKeyDown() {
		return false;
	}

	public EntityPlayer getPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public int getWeatherHeatOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getTempOffset(dim, isHell);
		}
		return 0;
	}

	public int getWeatherHumOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getHumOffset(dim, isHell);
		}
		return 0;
	}

	public int getWeatherAirOffset(World world) {
		if (world != null) {
			int dim = world.provider.getDimension();
			boolean isHell = world.provider.doesWaterVaporize();
			return WeatherChecker.INSTANCE.getWindOffset(dim, isHell);
		}
		return 0;
	}

}
