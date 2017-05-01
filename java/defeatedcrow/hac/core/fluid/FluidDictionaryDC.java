package defeatedcrow.hac.core.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class FluidDictionaryDC {

	private static List<String> idToName = new ArrayList<String>();
	private static Map<String, Integer> nameMap = new HashMap<String, Integer>(128);
	private static Map<Fluid, List<Integer>> fluidMap = new HashMap<Fluid, List<Integer>>(128);
	public static final ImmutableList<ItemStack> EMPTY_LIST = ImmutableList.of();

	public static void registerFluidDic(Fluid fluid, String name) {
		if (fluid == null || name == null)
			return;

		int id = getNameID(name);

		if (fluidMap.containsKey(fluid)) {
			List<Integer> list = fluidMap.get(fluid);
			if (!list.contains(id)) {
				list.add(id);
			}
		} else {
			List<Integer> list = Lists.newArrayList();
			list.add(id);
			fluidMap.put(fluid, list);
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

		List<Integer> ids = fluidMap.get(ref);
		if (ids != null && !ids.isEmpty()) {
			List<String> names = Lists.newArrayList();
			for (Integer i : ids) {
				if (i != null && idToName.size() > i) {
					String s = idToName.get(i);
					names.add(s);
				}
			}

			for (String check : names) {
				String targetName = target.getName();
				if (targetName.contains(check)) {
					return true;
				}
			}
		}

		return false;
	}

}
