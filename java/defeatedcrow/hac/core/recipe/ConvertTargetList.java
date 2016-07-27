package defeatedcrow.hac.core.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import defeatedcrow.hac.core.util.DCUtil;

public class ConvertTargetList {

	private ConvertTargetList() {
	}

	private static ArrayList<ItemStack> EXCLUSIONS = new ArrayList<ItemStack>();

	private static Map<ItemStack, String> REPLACES = new HashMap<ItemStack, String>();

	public static ArrayList<ItemStack> getExclusionList() {
		return EXCLUSIONS;
	}

	public static Map<ItemStack, String> getReplaceTable() {
		return REPLACES;
	}

	public static void addExclusing(ItemStack item) {
		if (item == null || item.getItem() == null) {
			return;
		}
		boolean f = true;
		for (ItemStack tar : EXCLUSIONS) {
			if (DCUtil.isSameItem(item, tar)) {
				f = false;
			}
		}
		if (f) {
			EXCLUSIONS.add(item);
			// DCLogger.debugLog("add ex: " + item.toString());
		}
	}

	public static void addReplaceTarget(ItemStack item, String name) {
		if (item == null || item.getItem() == null || name == null) {
			return;
		}
		boolean f = true;
		for (ItemStack tar : REPLACES.keySet()) {
			if (DCUtil.isSameItem(item, tar)) {
				f = false;
			}
		}
		if (f) {
			REPLACES.put(item, name);
			// DCLogger.debugLog("add rep: " + item.toString());
		}
	}

}
