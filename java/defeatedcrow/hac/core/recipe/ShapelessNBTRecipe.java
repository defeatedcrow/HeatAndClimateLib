package defeatedcrow.hac.core.recipe;

import javax.annotation.Nonnull;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * OreRecipeに加えて、アイテムのNBTタグや液体情報の一致を要求する。
 */
public class ShapelessNBTRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Nonnull
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> input = NonNullList.create();
	protected boolean sensitive = false;
	protected ResourceLocation group;

	public ShapelessNBTRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
		this.group = group;
		output = result.copy();
		for (Object in : recipe) {
			Ingredient ing = CraftingHelper.getIngredient(in);
			if (ing != null) {
				input.add(ing);
			} else {
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
	}

	public ShapelessNBTRecipe(ResourceLocation group, boolean amountSensitive, ItemStack result, Object... recipe) {
		this(group, result, recipe);
		sensitive = amountSensitive;
	}

	private Ingredient getIngredient(Object obj) {
		if (obj instanceof Ingredient)
			return (Ingredient) obj;
		else if (obj instanceof ItemStack)
			return Ingredient.fromStacks(((ItemStack) obj).copy());
		else if (obj instanceof Item)
			return Ingredient.fromItem((Item) obj);
		else if (obj instanceof Block)
			return Ingredient.fromStacks(new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE));
		else if (obj instanceof String)
			return new OreIngredient((String) obj);

		return null;
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
		int count = 0;

		for (Ingredient ing : this.input) {
			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack item = inv.getStackInSlot(i);
				if (checkMatch(item, ing)) {
					count++;
					break;
				}
			}
		}

		if (count != this.input.size())
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	protected boolean checkMatch(ItemStack slot, Ingredient target) {
		if (target.apply(slot)) {
			if (target != Ingredient.EMPTY && target.getMatchingStacks().length == 1) {
				ItemStack stack = target.getMatchingStacks()[0];
				if (!DCUtil.isEmpty(stack) && !DCUtil.isEmpty(slot)) {
					if (stack.getItem() == ForgeModContainer.getInstance().universalBucket
							&& slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
						IFluidHandler handler = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
								null);
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

		return true;
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

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) // getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	@Override
	public boolean canFit(int w, int h) {
		return w * h >= this.input.size();
	}

}
