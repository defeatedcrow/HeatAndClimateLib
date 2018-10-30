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

	public static String getFluidDicName(Fluid fluid) {
		if (fluid == null)
			return null;

		if (fluidMap.containsKey(fluid)) {
			Integer fid = fluidMap.get(fluid);
			if (fid != null) {
				return idToName.get(fid);
			}

		}
		return null;
	}

	public static int getNameID(String name) {
		if (idToName.isEmpty()) {
			idToName.add(name);
			return 0;
		} else {
			int id = -1;
			int size = idToName.size();
			for (int i = 0; i < idToName.size(); i++) {
				String n2 = idToName.get(i);
				if (n2 != null && n2.equalsIgnoreCase(name)) {
					id = i;
				}
				if (id != -1) {
					break;
				}
			}

			if (id == -1) {
				idToName.add(name);
				return size;
			} else {
				return id;
			}
		}
	}

	public static boolean matchFluid(Fluid target, Fluid ref) {
		if (target == null || ref == null)
			return false;

		if (target == ref) {
			return true;
		}

		if (target == ref) {
			return true;
		} else {
			String dic = getFluidDicName(ref);
			return matchFluidName(target, dic);
		}
	}

	public static boolean matchFluidName(Fluid target, String name) {
		if (target == null || name == null)
			return false;

		String dic = getFluidDicName(target);

		if (dic != null && dic.equals(name)) {
			return true;
		}

		String targetName = target.getName();
		if (targetName != null && targetName.contains(name)) {
			return true;
		}

		return false;
	}

}
