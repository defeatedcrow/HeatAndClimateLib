package defeatedcrow.hac.core.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class FluidDictionaryDC {

	private static List<String> idToName = new ArrayList<String>();
	private static Map<String, Integer> nameMap = new HashMap<String, Integer>(128);
	private static Map<Fluid, Integer> fluidMap = new HashMap<Fluid, Integer>(128);
	public static final ImmutableList<ItemStack> EMPTY_LIST = ImmutableList.of();

	public static void registerFluidDic(Fluid fluid, String name) {
		if (fluid == null || name == null)
			return;

		int id = getNameID(name);

		if (fluidMap.containsKey(fluid)) {
			Integer fid = fluidMap.get(fluid);
			if (fid == null) {
				fluidMap.put(fluid, id);
			}
		} else {
			fluidMap.put(fluid, id);
		}
	}

	public static int getNameID(String name) {
		Integer val = nameMap.get(name);
		if (val == null) {
			idToName.add(name);
			val = idToName.size() - 1;
			nameMap.put(name, val);
		}
		return val;
	}

	public static boolean matchFluid(Fluid target, Fluid ref) {
		if (target == null || ref == null)
			return false;

		if (target == ref) {
			return true;
		}

		Integer id = fluidMap.get(ref);
		Integer id2 = fluidMap.get(target);
		if (id != null) {
			if (id2 != null && id.intValue() == id2.intValue()) {
				return true;
			} else {
				String name = idToName.get(id2);
				if (matchFluidName(target, name)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean matchFluidName(Fluid target, String name) {
		if (target == null || name == null)
			return false;

		if (nameMap.containsKey(name)) {
			Integer id = nameMap.get(name);
			Integer id2 = nameMap.get(target);
			if (id != null && id2 != null && id.intValue() == id2.intValue()) {
				return true;
			}
		}

		String targetName = target.getName();
		if (targetName != null && targetName.contains(name)) {
			return true;
		}

		return false;
	}

}
