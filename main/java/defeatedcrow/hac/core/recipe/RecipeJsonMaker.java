package defeatedcrow.hac.core.recipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeJsonMaker {

	public static final RecipeJsonMaker INSTANCE = new RecipeJsonMaker();

	public static boolean canUse = false;
	public static boolean canDeprecate = false;

	public static Path dir = null;

	public static void buildShapedRecipe(String domain, int num, ItemStack result, Object... keys) {
		int count = 0;
		for (Object o : keys) {
			if (o instanceof String) {
				count++;
			} else {
				break;
			}
		}
		String[] pat = new String[count];
		Pairs[] obj = new Pairs[(keys.length - count) / 2];
		for (int i = 0; i < count; i++) {
			pat[i] = (String) keys[i];
			// DCLogger.infoLog("**** pattern " + pat[i]);
		}
		for (int i = 0; i < keys.length - count; i += 2) {
			int i1 = i / 2;
			Character c = (Character) keys[i + count];
			Object o = keys[i + 1 + count];
			Pairs pair = INSTANCE.new Pairs(c, o);
			// DCLogger.infoLog("**** pair " + pair.ch + " " + pair.obj.toString());
			obj[i1] = pair;
		}
		buildShapedRecipe(domain, num, result, pat, obj);
	}

	public static void buildShapedRecipe(String domain, int num, ItemStack result, String[] pattern, Pairs... keys) {
		if (dir != null && canUse && !DCUtil.isEmpty(result)) {
			Map<String, Map<String, Object>> key = keys(keys);
			Map<String, Object> res = result(result);
			Shaped ret = INSTANCE.new Shaped(pattern, key, res);

			String filename = result.getItem().getRegistryName().getResourcePath() + "_" + result.getItemDamage();
			if (result.getItem().getRegistryName().getResourceDomain().equalsIgnoreCase("minecraft")) {
				filename = "dcs_" + filename;
			}

			dir.normalize();
			File f = new File(dir + "/" + domain + "/" + filename + ".json");
			if (num > 0) {
				f = new File(dir + "/" + domain + "/" + filename + "_" + num + ".json");
			}
			if (!canDeprecate && f.exists())
				return;

			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}

			try {
				f.createNewFile();
				// DCLogger.infoLog("shaped file created: " + f.getName());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				if (f.canWrite()) {
					FileOutputStream fos = new FileOutputStream(f.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(ret, ret.getClass(), jsw);

					osw.close();
					fos.close();
					jsw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void buildShapelessRecipe(String domain, int num, ItemStack result, Object... keys) {
		if (dir != null && canUse && !DCUtil.isEmpty(result)) {
			// DCLogger.infoLog("**** start 2 ****");
			List<Map<String, Object>> key = ing(keys);
			Map<String, Object> res = result(result);
			Shapeless ret = INSTANCE.new Shapeless(key, res);

			String filename = result.getItem().getRegistryName().getResourcePath() + "_" + result.getItemDamage();
			if (result.getItem().getRegistryName().getResourceDomain().equalsIgnoreCase("minecraft")) {
				filename = "dcs_" + filename;
			}

			dir.normalize();
			File f = new File(dir + "/" + domain + "/" + filename + ".json");
			if (num > 0) {
				f = new File(dir + "/" + domain + "/" + filename + "_" + num + ".json");
			}
			if (!canDeprecate && f.exists())
				return;

			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}

			try {
				f.createNewFile();
				// DCLogger.infoLog("shapeless file created: " + f.getName());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				if (f.canWrite()) {
					FileOutputStream fos = new FileOutputStream(f.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(ret, ret.getClass(), jsw);

					osw.close();
					fos.close();
					jsw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Shaped {
		final String type = "forge:ore_shaped";
		final String[] pattern;
		final Map<String, Map<String, Object>> key;
		final Map<String, Object> result;

		private Shaped(String[] s, Map<String, Map<String, Object>> k, final Map<String, Object> res) {
			pattern = s;
			key = k;
			result = res;
		}
	}

	private class Shapeless {
		final String type = "forge:ore_shapeless";
		final List<Map<String, Object>> ingredients;
		final Map<String, Object> result;

		private Shapeless(List<Map<String, Object>> i, final Map<String, Object> res) {
			ingredients = i;
			result = res;
		}
	}

	private class Pairs {
		final Character ch;
		final Object obj;

		Pairs(Character c, Object o) {
			ch = c;
			obj = o;
		}
	}

	private static List<Map<String, Object>> ing(Object... obj) {
		List<Map<String, Object>> ingredients = Lists.newArrayList();
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof ItemStack) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				ItemStack item = (ItemStack) obj[i];
				ResourceLocation rn = item.getItem().getRegistryName();
				String name = rn.getResourceDomain() + ":" + rn.getResourcePath();
				map.put("item", name);
				map.put("data", item.getItemDamage());
				ingredients.add(map);
			} else {
				Map<String, Object> map = Maps.newLinkedHashMap();
				String name = (String) obj[i];
				map.put("type", "forge:ore_dict");
				map.put("ore", name);
				ingredients.add(map);
			}
		}
		return ingredients;
	}

	private static Map<String, Map<String, Object>> keys(Pairs... pairs) {
		Map<String, Map<String, Object>> keys = Maps.newLinkedHashMap();
		for (int i = 0; i < pairs.length; i++) {
			Pairs p = pairs[i];
			if (p.obj instanceof ItemStack) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				ItemStack item = (ItemStack) p.obj;
				ResourceLocation rn = item.getItem().getRegistryName();
				String name = rn.getResourceDomain() + ":" + rn.getResourcePath();
				map.put("item", name);
				map.put("data", item.getItemDamage());
				keys.put(p.ch.toString(), map);
			} else {
				Map<String, Object> map = Maps.newLinkedHashMap();
				String name = (String) p.obj;
				map.put("type", "forge:ore_dict");
				map.put("ore", name);
				keys.put(p.ch.toString(), map);
			}
		}
		return keys;
	}

	private static Map<String, Object> result(ItemStack item) {
		Map<String, Object> map = Maps.newLinkedHashMap();
		ResourceLocation rn = item.getItem().getRegistryName();
		String name = rn.getResourceDomain() + ":" + rn.getResourcePath();
		map.put("item", name);
		map.put("count", item.getCount());
		map.put("data", item.getItemDamage());
		return map;
	}

}
