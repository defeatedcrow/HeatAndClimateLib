package defeatedcrow.hac.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.ItemSet;
import defeatedcrow.hac.core.DCLogger;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class JsonUtilDC {

	public static List<BlockSet> getListFromStrings(String[] names, String logname) {
		List<BlockSet> list = Lists.newArrayList();
		if (names != null && names.length > 0) {
			for (String name : names) {
				if (name != null) {
					String itemName = name;
					String modid = "minecraft";
					int meta = 32767;
					if (name.contains(":")) {
						String[] n2 = name.split(":");
						if (n2 != null && n2.length > 0) {
							if (n2.length > 2) {
								meta = parseInt(n2[2], 32767);
							}

							if (n2.length == 1) {
								itemName = n2[0];
							} else {
								modid = n2[0];
								itemName = n2[1];
							}
						}
					}

					Block block = Block.REGISTRY.getObject(new ResourceLocation(modid, itemName));
					if (block != null && block != Blocks.AIR) {
						DCLogger.debugLog(logname + " add target: " + modid + ":" + itemName + ", " + meta);
						BlockSet set = new BlockSet(block, meta);
						list.add(set);
					} else {
						DCLogger.debugLog("Failed find target: " + modid + ":" + itemName);
					}
				}
			}
		}
		return list;
	}

	public static List<BlockSet> getListFromStringsNullable(String[] names, String logname) {
		List<BlockSet> list = Lists.newArrayList();
		if (names != null && names.length > 0) {
			for (String name : names) {
				BlockSet set = getBlockSetFromString(name);
				if (set != null && !set.equals(BlockSet.AIR)) {
					DCLogger.debugLog(logname + " add target: " + set.toString());
					list.add(set);
				}
			}
		}
		return list;
	}

	public static BlockSet getBlockSetFromStringWildcard(String name) {
		if (name == null || name.equalsIgnoreCase("empty")) {
			return BlockSet.AIR;
		} else {
			String itemName = name;
			String modid = "minecraft";
			int meta = 32767;

			if (name.contains(":")) {
				String[] n2 = name.split(":");
				if (n2 != null && n2.length > 0) {
					if (n2.length > 2) {
						meta = parseInt(n2[2], 32767);
					}

					if (n2.length == 1) {
						itemName = n2[0];
					} else {
						modid = n2[0];
						itemName = n2[1];
					}
				}
			}

			Block block = Block.REGISTRY.getObject(new ResourceLocation(modid, itemName));
			if (block != null && block != Blocks.AIR) {
				// DCLogger.debugTrace("Find target: " + modid + ":" + itemName + ", " + meta);
				BlockSet set = new BlockSet(block, meta);
				return set;
			} else {
				DCLogger.debugLog("Failed find target: " + modid + ":" + itemName);
			}
		}
		return BlockSet.AIR;
	}

	public static BlockSet getBlockSetFromString(String name) {
		if (name == null || name.equalsIgnoreCase("empty")) {
			return BlockSet.AIR;
		} else {
			String itemName = name;
			String modid = "minecraft";
			int meta = 0;

			if (name.contains(":")) {
				String[] n2 = name.split(":");
				if (n2 != null && n2.length > 0) {
					if (n2.length > 2) {
						meta = parseInt(n2[2], 0);
					}

					if (n2.length == 1) {
						itemName = n2[0];
					} else {
						modid = n2[0];
						itemName = n2[1];
					}
				}
			}

			Block block = Block.REGISTRY.getObject(new ResourceLocation(modid, itemName));
			if (block != null && block != Blocks.AIR) {
				// DCLogger.debugTrace("Find target: " + modid + ":" + itemName + ", " + meta);
				BlockSet set = new BlockSet(block, meta);
				return set;
			} else {
				DCLogger.debugLog("Failed find target: " + modid + ":" + itemName);
			}
		}
		return BlockSet.AIR;
	}

	public static ItemSet getItemSetFromStringWildcard(String name) {
		if (name == null || name.equalsIgnoreCase("empty")) {
			return ItemSet.EMPTY;
		} else {
			String itemName = name;
			String modid = "minecraft";
			int meta = 32767;

			if (name.contains(":")) {
				String[] n2 = name.split(":");
				if (n2 != null && n2.length > 0) {
					if (n2.length > 2) {
						meta = parseInt(n2[2], 32767);
					}

					if (n2.length == 1) {
						itemName = n2[0];
					} else {
						modid = n2[0];
						itemName = n2[1];
					}
				}
			}

			Item item = Item.REGISTRY.getObject(new ResourceLocation(modid, itemName));
			if (item != null && item != Item.getItemFromBlock(Blocks.AIR)) {
				// DCLogger.debugTrace("Find target: " + modid + ":" + itemName + ", " + meta);
				ItemSet ret = new ItemSet(item, meta);
				return ret;
			} else {
				DCLogger.debugLog("Failed find target: " + modid + ":" + itemName);
			}
		}
		return ItemSet.EMPTY;
	}

	public static ItemSet getItemSetFromString(String name) {
		if (name == null || name.equalsIgnoreCase("empty")) {
			return ItemSet.EMPTY;
		} else {
			String itemName = name;
			String modid = "minecraft";
			int meta = 0;

			if (name.contains(":")) {
				String[] n2 = name.split(":");
				if (n2 != null && n2.length > 0) {
					if (n2.length > 2) {
						meta = parseInt(n2[2], 0);
					}

					if (n2.length == 1) {
						itemName = n2[0];
					} else {
						modid = n2[0];
						itemName = n2[1];
					}
				}
			}

			Item item = Item.REGISTRY.getObject(new ResourceLocation(modid, itemName));
			if (item != null && item != Item.getItemFromBlock(Blocks.AIR)) {
				// DCLogger.debugTrace("Find target: " + modid + ":" + itemName + ", " + meta);
				ItemSet ret = new ItemSet(item, meta);
				return ret;
			} else {
				DCLogger.debugLog("Failed find target: " + modid + ":" + itemName);
			}
		}
		return ItemSet.EMPTY;
	}

	public static IClimate getClimate(Map<String, Object> map) {
		DCHeatTier heat = DCHeatTier.NORMAL;
		DCHumidity hum = DCHumidity.NORMAL;
		DCAirflow air = DCAirflow.NORMAL;
		if (map != null && !map.isEmpty()) {
			if (map.containsKey("climate_temperature")) {
				Object o1 = map.get("temperature");
				if (o1 instanceof String) {
					String heat2 = (String) o1;
					heat = DCHeatTier.getFromName(heat2);
				}
			}
			if (map.containsKey("climate_humidity")) {
				Object o2 = map.get("humidity");
				if (o2 instanceof String) {
					String hum2 = (String) o2;
					hum = DCHumidity.getFromName(hum2);
				}
			}
			if (map.containsKey("climate_airflow")) {
				Object o3 = map.get("airflow");
				if (o3 instanceof String) {
					String air2 = (String) o3;
					air = DCAirflow.getFromName(air2);
				}
			}
		}
		return ClimateAPI.register.getClimateFromParam(heat, hum, air);
	}

	public static List<ItemStack> getIngredient(Map<String, Object> map) {
		List<ItemStack> list = Lists.newArrayList();
		if (map != null && !map.isEmpty()) {
			int c = 1;
			if (map.containsKey("count")) {
				String i = map.get("count").toString();
				c = parseInt(i, c);
			}
			if (map.containsKey("item")) {
				Object o1 = map.get("item");
				if (o1 instanceof String) {
					String name = (String) o1;
					ItemSet set = JsonUtilDC.getItemSetFromString(name);
					if (!ItemSet.isEmpty(set)) {
						ItemStack stack = set.getSingleStack();
						stack.setCount(c);
						list.add(stack);
					}
				}
			}
			if (map.containsKey("ore_dict")) {
				Object o2 = map.get("ore_dict");
				if (o2 instanceof String) {
					String name = (String) o2;
					NonNullList<ItemStack> ores = OreDictionary.getOres(name);
					if (!ores.isEmpty()) {
						for (ItemStack stack : ores) {
							if (!DCUtil.isEmpty(stack)) {
								ItemStack ret = stack.copy();
								ret.setCount(c);
								list.add(ret);
							}
						}
					}
				}
			}
		}
		return list;
	}

	public static Object[] getInputObjects(List<String> input) {
		List<Object> list = Lists.newArrayList();
		if (input != null && !input.isEmpty()) {
			for (String name : input) {
				if (name != null) {
					if (!name.contains(":")) {
						list.add(name);
						continue;
					}
					ItemSet set = JsonUtilDC.getItemSetFromString(name);
					if (!ItemSet.isEmpty(set)) {
						ItemStack stack = set.getSingleStack();
						list.add(stack);
					}
				}
			}
		}
		return list.isEmpty() ? null : list.toArray();
	}

	public static ArrayList<ItemStack> getInputLists(Object[] inputs) {
		ArrayList<ItemStack> list = Lists.newArrayList();
		if (inputs != null && inputs.length > 0) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof String) {
					List<ItemStack> ret = new ArrayList<ItemStack>();
					ret.addAll(OreDictionary.getOres((String) inputs[i]));
					list.add(ret.get(i));
				} else if (inputs[i] instanceof ItemStack) {
					if (!DCUtil.isEmpty((ItemStack) inputs[i])) {
						ItemStack ret = ((ItemStack) inputs[i]).copy();
						list.add(ret);
					}
				}
			}
		}
		return list;
	}

	public static ItemStack getOutput(Map<String, Object> map) {
		if (map != null && !map.isEmpty()) {
			if (map.containsKey("item")) {
				Object o1 = map.get("item");
				if (o1 instanceof String) {
					String name = (String) o1;
					ItemSet set = JsonUtilDC.getItemSetFromString(name);
					if (!ItemSet.isEmpty(set)) {
						int c = 1;
						if (map.containsKey("count")) {
							String i = map.get("count").toString();
							c = parseInt(i, c);
						}
						ItemStack stack = set.getSingleStack();
						stack.setCount(c);
						return stack;
					}
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static FluidStack getFluid(Map<String, Object> map) {
		if (map != null && !map.isEmpty()) {
			if (map.containsKey("fluid")) {
				Object o1 = map.get("fluid");
				if (o1 instanceof String) {
					String name = (String) o1;
					Fluid fluid = FluidRegistry.getFluid(name);
					if (fluid != null) {
						int c = 1000;
						if (map.containsKey("amount")) {
							String i = map.get("amount").toString();
							c = parseInt(i, c);
						}
						FluidStack ret = new FluidStack(fluid, c);
						return ret;
					}
				}
			}
		}
		return null;
	}

	public static List<Class<? extends Entity>> getEntityListFromStrings(String[] names, String logname) {
		List<Class<? extends Entity>> list = Lists.newArrayList();
		if (names != null && names.length > 0) {
			for (String name : names) {
				if (name != null) {
					ResourceLocation res = new ResourceLocation(name);
					if (res.getResourceDomain().equalsIgnoreCase("minecraft")) {
						String n = res.getResourcePath();
						res = new ResourceLocation(n);
					}
					if (EntityList.getClass(res) != null) {
						Class<? extends Entity> entity = EntityList.getClass(res);
						if (entity != null) {
							list.add(entity);
							DCLogger.debugLog("Registered to the update blacklist: " + name);
						}
						continue;
					}
				}
				DCLogger.debugLog("Failed find target: " + name);
			}
		}
		return list;
	}

	public static int parseInt(String s, int def) {
		int c = def;
		try {
			float f = Float.parseFloat(s);
			c = MathHelper.floor(f);
		} catch (NumberFormatException exp2) {
			DCLogger.debugLog("Tried to parse non Integer target: " + s);
		}
		return c;
	}

	public static float parseFloat(String s, float def) {
		float c = def;
		try {
			c = Float.parseFloat(s);
		} catch (NumberFormatException exp2) {
			DCLogger.debugLog("Tried to parse non Float target: " + s);
		}
		return c;
	}

}
