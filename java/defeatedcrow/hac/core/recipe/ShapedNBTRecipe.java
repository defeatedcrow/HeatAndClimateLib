package defeatedcrow.hac.core.recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
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
public class ShapedNBTRecipe implements IRecipe {

	public static final int MAX_CRAFT_GRID_WIDTH = 3;
	public static final int MAX_CRAFT_GRID_HEIGHT = 3;

	protected ItemStack output = null;
	protected Object[] input = null;
	protected Object[] originalInput = null;
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;
	protected boolean sensitive = false;

	public ShapedNBTRecipe(ItemStack result, Object... recipe) {
		output = result.copy();

		String shape = "";
		int idx = 0;

		if (recipe[idx] instanceof Boolean) {
			mirrored = (Boolean) recipe[idx];
			if (recipe[idx + 1] instanceof Object[]) {
				recipe = (Object[]) recipe[idx + 1];
			} else {
				idx = 1;
			}
		}

		if (recipe[idx] instanceof String[]) {
			String[] parts = ((String[]) recipe[idx++]);

			for (String s : parts) {
				width = s.length();
				shape += s;
			}

			height = parts.length;
		} else {
			while (recipe[idx] instanceof String) {
				String s = (String) recipe[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length()) {
			String ret = "Invalid shaped ore recipe: ";
			for (Object tmp : recipe) {
				ret += tmp + ", ";
			}
			ret += output;
			throw new RuntimeException(ret);
		}

		HashMap<Character, Object> itemMap = new HashMap<Character, Object>();
		HashMap<Character, Object> originalMap = new HashMap<Character, Object>();

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];

			if (in instanceof ItemStack) {
				itemMap.put(chr, ((ItemStack) in).copy());
				originalMap.put(chr, ((ItemStack) in).copy());
			} else if (in instanceof Item) {
				itemMap.put(chr, new ItemStack((Item) in));
				originalMap.put(chr, new ItemStack((Item) in));
			} else if (in instanceof Block) {
				itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
				originalMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
			} else if (in instanceof String) {
				itemMap.put(chr, OreDictionary.getOres((String) in));
				originalMap.put(chr, in);
			} else {
				String ret = "Invalid shaped ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}

		input = new Object[width * height];
		originalInput = new Object[width * height];
		int x = 0;
		for (char chr : shape.toCharArray()) {
			input[x] = itemMap.get(chr);
			originalInput[x] = originalMap.get(chr);
			x++;
		}
	}

	public ShapedNBTRecipe(boolean amountSensitive, ItemStack result, Object... recipe) {
		this(result, recipe);
		sensitive = amountSensitive;
	}

	public ShapedNBTRecipe(ShapedRecipes recipe, Map<ItemStack, String> replacements) {
		output = recipe.getRecipeOutput();
		width = recipe.recipeWidth;
		height = recipe.recipeHeight;

		input = new Object[recipe.recipeItems.length];
		originalInput = new Object[recipe.recipeItems.length];

		for (int i = 0; i < input.length; i++) {
			ItemStack ingredient = recipe.recipeItems[i];

			if (ingredient == null)
				continue;

			input[i] = recipe.recipeItems[i];

			for (Entry<ItemStack, String> replace : replacements.entrySet()) {
				if (OreDictionary.itemMatches(replace.getKey(), ingredient, true)) {
					input[i] = OreDictionary.getOres(replace.getValue());
					break;
				}
			}
		}
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		return output.copy();
	}

	@Override
	public int getRecipeSize() {
		return input.length;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return output;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
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

	@SuppressWarnings("unchecked")
	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Object target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input[width - subX - 1 + subY * width];
					} else {
						target = input[subX + subY * width];
					}
				}

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target instanceof ItemStack) {
					ItemStack stack = (ItemStack) target;
					boolean check = false;
					if (stack != null && slot != null
							&& stack.getItem() == ForgeModContainer.getInstance().universalBucket) {
						if (slot.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
							IFluidHandler handler = slot.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
									null);
							FluidStack f = handler.drain(Fluid.BUCKET_VOLUME, false);
							IFluidHandler handler2 = stack
									.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
							FluidStack f2 = handler2.drain(Fluid.BUCKET_VOLUME, false);
							if (f != null && f2 != null && f.getFluid() == f2.getFluid()) {
								check = !sensitive || f.amount == Fluid.BUCKET_VOLUME;
							}
						}
						if (!check) {
							return false;
						}
					} else if (OreDictionary.itemMatches((ItemStack) target, slot, false)) {
						if (stack.hasTagCompound()) {
							if (!slot.hasTagCompound()) {
								return false;
							}
							NBTTagCompound tag = stack.getTagCompound();
							NBTTagCompound slotTag = slot.getTagCompound();
							if (tag.equals(slotTag)) {
								return false;
							}
						}
					} else {
						return false;
					}
				} else if (target instanceof List) {
					boolean matched = false;

					Iterator<ItemStack> itr = ((List<ItemStack>) target).iterator();
					while (itr.hasNext() && !matched) {
						matched = OreDictionary.itemMatches(itr.next(), slot, false);
					}

					if (!matched) {
						return false;
					}
				} else if (target == null && slot != null) {
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

	public Object[] getInput() {
		return this.input;
	}

	public Object[] getOriginalInput() {
		return this.originalInput;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) // getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

}
