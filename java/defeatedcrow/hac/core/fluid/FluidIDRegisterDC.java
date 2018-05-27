package defeatedcrow.hac.core.fluid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.core.DCLogger;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

// 数値IDの保管用
public class FluidIDRegisterDC {

	private FluidIDRegisterDC() {}

	public static final FluidIDRegisterDC INSTANCE = new FluidIDRegisterDC();

	static int maxID = 0;

	private static BiMap<String, Object> jsonMap = HashBiMap.create();
	private static TreeMap<Integer, String> fluidMap = Maps.newTreeMap();

	private static File dir = null;

	public static boolean isRegistered(Fluid fluid) {
		if (fluid != null) {
			String name = fluid.getName();
			if (fluidMap.containsValue(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRegistered(String s) {
		if (s != null) {
			if (fluidMap.containsValue(s)) {
				return true;
			}
		}
		return false;
	}

	public static int getID(Fluid fluid) {
		if (fluid != null) {
			String name = fluid.getName();
			for (Entry<Integer, String> ent : fluidMap.entrySet()) {
				if (ent.getValue().equals(name)) {
					return ent.getKey();
				}
			}
		}
		return -1;
	}

	public static Fluid getFluid(int id) {
		if (fluidMap.containsKey(id)) {
			String name = fluidMap.get(id);
			Fluid ret = FluidRegistry.getFluid(name);
			return ret;
		}
		return null;
	}

	public static boolean registerFluid(Fluid fluid) {
		if (fluid != null) {
			String name = fluid.getName();
			if (!isRegistered(name)) {
				maxID++;
				fluidMap.put(maxID, name);
				return true;
			}
		}
		return false;
	}

	public static boolean registerFluid(String name) {
		if (name != null) {
			if (!isRegistered(name)) {
				maxID++;
				fluidMap.put(maxID, name);
				return true;
			}
		}
		return false;
	}

	public static void startMap() {
		if (!jsonMap.isEmpty()) {
			for (Entry<String, Object> ent : jsonMap.entrySet()) {
				if (ent != null) {
					String s = ent.getKey();
					int i = Integer.parseInt(s);
					String name = ent.getValue().toString();
					DCLogger.debugTrace("get fluid from json: " + i + ", " + name);
					fluidMap.put(i, name);
					maxID++;
				}
			}
		}
	}

	public static void loadFluidMap() {
		Map<String, Fluid> get = FluidRegistry.getRegisteredFluids();
		Set<String> names = get.keySet();
		for (String s : names) {
			if (registerFluid(s)) {
				DCLogger.debugLog("register fluid: " + s);
			}
		}
	}

	public static void pre() {
		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				}

				if (dir.canRead()) {
					FileInputStream fis = new FileInputStream(dir.getPath());
					InputStreamReader isr = new InputStreamReader(fis);
					JsonReader jsr = new JsonReader(isr);
					Gson gson = new Gson();
					Map get = gson.fromJson(jsr, Map.class);

					isr.close();
					fis.close();
					jsr.close();

					if (get != null && !get.isEmpty()) {
						jsonMap.putAll(get);

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		startMap();
	}

	public static void post() {
		loadFluidMap();

		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				}

				if (dir.canWrite()) {
					FileOutputStream fos = new FileOutputStream(dir.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(fluidMap, HashMap.class, jsw);

					osw.close();
					fos.close();
					jsw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setDir(File file) {
		dir = new File(file, "defeatedcrow/climate/fluids.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}

}
