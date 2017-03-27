package defeatedcrow.hac.core.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;

public class ConvertTargetList {

	private ConvertTargetList() {}

	private static ArrayList<ItemStack> EXCLUSIONS = new ArrayList<ItemStack>();

	private static Map<ItemStack, String> REPLACES = new HashMap<ItemStack, String>();

	public static ArrayList<ItemStack> getExclusionList() {
		return EXCLUSIONS;
	}

	public static Map<ItemStack, String> getReplaceTable() {
		return REPLACES;
	}

	public static void addExclusing(ItemStack item) {
		if (DCUtil.isEmpty(item)) {
			return;
		}
		boolean f = true;
		for (ItemStack tar : EXCLUSIONS) {
			if (DCUtil.isSameItem(item, tar, false)) {
				f = false;
			}
		}
		if (f) {
			EXCLUSIONS.add(item);
			// DCLogger.debugLog("add ex: " + item.toString());
		}
	}

	public static void addReplaceTarget(ItemStack item, String name) {
		if (DCUtil.isEmpty(item) || name == null) {
			return;
		}
		boolean f = true;
		for (ItemStack tar : REPLACES.keySet()) {
			if (DCUtil.isSameItem(item, tar, false)) {
				f = false;
			}
		}
		if (f) {
			REPLACES.put(item, name);
			// DCLogger.debugLog("add rep: " + item.toString());
		}
	}

}
