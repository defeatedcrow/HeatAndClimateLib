package defeatedcrow.hac.core.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.api.climate.ItemSet;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class ConvertTargetList {

	private ConvertTargetList() {}

	private static ArrayList<ItemSet> EXCLUSIONS = new ArrayList<ItemSet>();

	private static Map<ItemSet, String> REPLACES = new HashMap<ItemSet, String>();

	public static ArrayList<ItemSet> getExclusionList() {
		return EXCLUSIONS;
	}

	public static Map<ItemSet, String> getReplaceTable() {
		return REPLACES;
	}

	public static void addExclusing(ItemStack item) {
		if (DCUtil.isEmpty(item)) {
			return;
		}
		ItemSet add = new ItemSet(item);
		if (!EXCLUSIONS.contains(add)) {
			EXCLUSIONS.add(add);
			// DCLogger.debugLog("add ex: " + item.toString());
		}
	}

	public static void addReplaceTarget(ItemStack item, String name) {
		if (DCUtil.isEmpty(item) || name == null) {
			return;
		}
		ItemSet add = new ItemSet(item);
		if (!REPLACES.keySet().contains(add)) {
			REPLACES.put(add, name);
			// DCLogger.debugLog("add rep: " + item.toString());
		}
	}

}
