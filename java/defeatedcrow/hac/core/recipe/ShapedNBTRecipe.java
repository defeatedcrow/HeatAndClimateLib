package defeatedcrow.hac.core.recipe;

import javax.annotation.Nonnull;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * OreRecipeに加えて、アイテムのNBTタグや液体情報の一致を要求する。
 */
public class ShapedNBTRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {

	public static final int MAX_CRAFT_GRID_WIDTH = 3;
	public static final int MAX_CRAFT_GRID_HEIGHT = 3;

	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = NonNullList.create();
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;
	protected boolean sensitive = false;
	protected ResourceLocation group;

	public ShapedNBTRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
		this(group, result, CraftingHelper.parseShaped(recipe));
	}

	public ShapedNBTRecipe(ResourceLocation group, @Nonnull ItemStack result, ShapedPrimer primer) {
		this.group = group;
		output = result.copy();
		this.width = primer.width;
		this.height = primer.height;
		this.input = primer.input;
		this.mirrored = primer.mirrored;
	}

	public ShapedNBTRecipe(ResourceLocation group, boolean amountSensitive, ItemStack result, Object... recipe) {
		this(group, result, recipe);
		sensitive = amountSensitive;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		if (inv.getWidth() < width || inv.getHeight() < height) {
			return false;
		}

		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (mirrored && checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;
				Ingredient target = Ingredient.EMPTY;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target.apply(slot)) {
					if (target != Ingredient.EMPTY && target.getMatchingStacks().length == 1) {
						ItemStack stack = target.getMatchingStacks()[0];
						if (!DCUtil.isEmpty(stack) && !DCUtil.isEmpty(slot)) {
							if (stack.getItem() == ForgeModContainer.getInstance().universalBucket
									&& slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
								IFluidHandler handler = slot
										.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
								FluidStack f = handler.drain(Fluid.BUCKET_VOLUME, false);
								IFluidHandler handler2 = stack
										.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
								FluidStack f2 = handler2.drain(Fluid.BUCKET_VOLUME, false);
								if (f != null && f2 != null && f.getFluid() == f2.getFluid()) {
									if (sensitive && f.amount != f2.amount) {
										return false;
									}
								}
							}
							if (stack.hasTagCompound()) {
								if (!slot.hasTagCompound()) {
									return false;
								}
								NBTTagCompound tag = stack.getTagCompound();
								NBTTagCompound slotTag = slot.getTagCompound();
								if (!tag.equals(slotTag)) {
									return false;
								}
							}
						}
					}
				} else {
					return false;
				}
			}
		}

		return true;
	}

	public ShapedNBTRecipe setMirrored(boolean mirror) {
		mirrored = mirror;
		return this;
	}

	public NonNullList<Ingredient> getInput() {
		return this.input;
	}

	@Override
	@Nonnull
	public NonNullList<Ingredient> getIngredients() {
		return this.input;
	}

	@Override
	@Nonnull
	public String getGroup() {
		return this.group == null ? "" : this.group.toString();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) // getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	@Override
	public boolean canFit(int w, int h) {
		return w >= width && h >= height;
	}

	@Override
	public int getRecipeWidth() {
		return this.width;
	}

	@Override
	public int getRecipeHeight() {
		return this.height;
	}

}
