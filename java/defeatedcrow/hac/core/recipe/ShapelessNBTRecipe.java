package defeatedcrow.hac.core.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * OreRecipeに加えて、アイテムのNBTタグや液体情報の一致を要求する。
 */
public class ShapelessNBTRecipe implements IRecipe {

	@Nonnull
	protected ItemStack output = null;
	protected List<List<ItemStack>> input = Lists.newArrayList();
	protected boolean sensitive = false;
	protected ResourceLocation group;

	public ShapelessNBTRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
		this.group = group;
		output = result.copy();
		for (Object in : recipe) {
			List<ItemStack> ing = getIngredient(in);
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

	private List<ItemStack> getIngredient(Object obj) {
		List<ItemStack> ret = Lists.newArrayList();
		if (obj instanceof List)
			return (List<ItemStack>) obj;
		else if (obj instanceof ItemStack) {
			ret.add((ItemStack) obj);
			return ret;
		} else if (obj instanceof Item) {
			ret.add(new ItemStack((Item) obj));
			return ret;
		} else if (obj instanceof Block) {
			ret.add(new ItemStack((Item) obj, 1, OreDictionary.WILDCARD_VALUE));
			return ret;
		} else if (obj instanceof String) {
			ret.addAll(OreDictionary.getOres((String) obj));
			return ret;
		}

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
		ArrayList<List<ItemStack>> required = new ArrayList<List<ItemStack>>(input);

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack slot = inv.getStackInSlot(x);

			if (slot != null && !required.isEmpty()) {
				boolean inRecipe = false;
				if (!required.isEmpty()) {
					Iterator<List<ItemStack>> req = required.iterator();

					while (req.hasNext()) {
						boolean match = false;

						List<ItemStack> next = req.next();
						if (next != null && !next.isEmpty())
							for (ItemStack item : next) {
								if (OreDictionary.itemMatches(item, slot, false) && checkMatch(item, slot)) {
									match = true;
								}
							}

						if (match) {
							inRecipe = true;
							required.remove(next);
							break;
						}
					}

					if (!inRecipe) {
						return false;
					}
				}
			}
		}

		return required.isEmpty();
	}

	@SuppressWarnings("unchecked")
	protected boolean checkMatch(ItemStack slot, ItemStack stack) {
		if (!DCUtil.isEmpty(stack) && !DCUtil.isEmpty(slot)) {
			if (stack.getItem() == ForgeModContainer.getInstance().universalBucket
					&& slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
				IFluidHandler handler = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				FluidStack f = handler.drain(Fluid.BUCKET_VOLUME, false);
				IFluidHandler handler2 = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				FluidStack f2 = handler2.drain(Fluid.BUCKET_VOLUME, false);
				if (f != null && f2 != null && f.getFluid() == f2.getFluid()) {
					return !sensitive || f.amount == f2.amount;
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
		} else {
			return false;
		}

		return true;
	}

	public List<List<ItemStack>> getInput() {
		return this.input;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) // getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	@Override
	public int getRecipeSize() {
		return input.size();
	}

}
