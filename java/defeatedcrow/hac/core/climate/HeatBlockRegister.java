package defeatedcrow.hac.core.climate;

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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.climate.IHeatBlockRegister;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.plugin.DCsJEIPluginLists;
import defeatedcrow.hac.core.plugin.jei.ClimateEffectiveTile;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class HeatBlockRegister implements IHeatBlockRegister {

	private Map<BlockSet, DCHeatTier> heats;
	private Map<BlockSet, DCHumidity> hums;
	private Map<BlockSet, DCAirflow> airs;

	public HeatBlockRegister() {
		this.heats = new HashMap<BlockSet, DCHeatTier>();
		this.hums = new HashMap<BlockSet, DCHumidity>();
		this.airs = new HashMap<BlockSet, DCAirflow>();
	}

	@Override
	public void registerHeatBlock(Block block, int meta, DCHeatTier temp) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredHeat(block, meta)) {
				heats.put(set, temp);
				registerEffectiveHeat(block, meta, temp);
			}
		}
	}

	@Override
	public void registerHumBlock(Block block, int meta, DCHumidity hum) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredHum(block, meta)) {
				hums.put(set, hum);
				registerEffectiveHum(block, meta, hum);
			}
		}
	}

	@Override
	public void registerAirBlock(Block block, int meta, DCAirflow air) {
		if (block != null) {
			BlockSet set = new BlockSet(block, meta);
			if (!this.isRegisteredAir(block, meta)) {
				airs.put(set, air);
				registerEffectiveAir(block, meta, air);
			}
		}
	}

	@Override
	public DCHeatTier getHeatTier(Block block, int meta) {
		Set<BlockSet> s = heats.keySet();
		DCHeatTier heat = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			heat = heats.get(b);
		}
		if (heat != null) {
			return heat;
		} else {
			return DCHeatTier.NORMAL;
		}

	}

	@Override
	public DCHumidity getHumidity(Block block, int meta) {
		Set<BlockSet> s = hums.keySet();
		DCHumidity hum = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			hum = hums.get(b);
		}
		if (hum != null) {
			return hum;
		} else {
			return DCHumidity.NORMAL;
		}
	}

	@Override
	public DCAirflow getAirflow(Block block, int meta) {
		Set<BlockSet> s = airs.keySet();
		DCAirflow air = null;
		BlockSet b = this.include(s, new BlockSet(block, meta));
		if (b != null) {
			air = airs.get(b);
		}
		if (air != null) {
			return air;
		} else {
			return DCAirflow.NORMAL;
		}
	}

	@Override
	public boolean isRegisteredHeat(Block block, int meta) {
		Set<BlockSet> s = heats.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public boolean isRegisteredHum(Block block, int meta) {
		Set<BlockSet> s = hums.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public boolean isRegisteredAir(Block block, int meta) {
		Set<BlockSet> s = airs.keySet();
		return this.include(s, new BlockSet(block, meta)) != null;
	}

	@Override
	public Map<BlockSet, DCHeatTier> getHeatList() {
		return heats;
	}

	@Override
	public Map<BlockSet, DCHumidity> getHumList() {
		return hums;
	}

	@Override
	public Map<BlockSet, DCAirflow> getAirList() {
		return airs;
	}

	private BlockSet include(Set<BlockSet> list, BlockSet target) {
		BlockSet ret = null;
		for (BlockSet b : list) {
			if (b.equals(target)) {
				ret = b;
			}
		}
		return ret;
	}

	private void registerEffectiveHeat(Block block, int meta, DCHeatTier t) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, t, null, null));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setHeat(t);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, t, null, null));
			}
		}
	}

	private void registerEffectiveHum(Block block, int meta, DCHumidity h) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, h, null));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setHumidity(h);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, h, null));
			}
		}
	}

	private void registerEffectiveAir(Block block, int meta, DCAirflow a) {
		if (DCsJEIPluginLists.climate.isEmpty()) {
			DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, null, a));
		} else {
			boolean flag = false;
			for (ClimateEffectiveTile tile : DCsJEIPluginLists.climate) {
				if (tile.isSameBlock(block)
						&& (tile.getInputMeta() == OreDictionary.WILDCARD_VALUE || tile.getInputMeta() == meta)) {
					tile.setAirflow(a);
					flag = true;
					break;
				}
			}
			if (!flag) {
				DCsJEIPluginLists.climate.add(new ClimateEffectiveTile(block, meta, null, null, a));
			}
		}
	}

	/* json */
	public static void registerBlockClimate(String name, IClimate clm) {
		if (name != null) {
			String itemName = name;
			String modid = "minecraft";
			int meta = 32767;
			if (name.contains(":")) {
				String[] n2 = name.split(":");
				if (n2 != null && n2.length > 0) {
					if (n2.length == 1) {
						itemName = n2[0];
					} else {
						modid = n2[0];
						itemName = n2[1];
						if (n2.length > 2) {
							Integer m = Integer.parseInt(n2[2]);
							if (m != null && m >= 0) {
								meta = m;
							}
						}
					}

				} else {
					DCLogger.debugLog("fail to register target block from json: " + name);
					return;
				}
			}

			Block item = Block.REGISTRY.getObject(new ResourceLocation(modid, itemName));
			if (item != null && item != Blocks.AIR && clm != null) {
				DCLogger.debugLog("register target block from json: " + modid + ":" + itemName + ", " + meta);
				if (clm.getHeat() != DCHeatTier.NORMAL) {
					ClimateAPI.registerBlock.registerHeatBlock(item, meta, clm.getHeat());
				}
				if (clm.getHumidity() != DCHumidity.NORMAL) {
					ClimateAPI.registerBlock.registerHumBlock(item, meta, clm.getHumidity());
				}
				if (clm.getAirflow() != DCAirflow.TIGHT) {
					ClimateAPI.registerBlock.registerAirBlock(item, meta, clm.getAirflow());
				}
			} else {
				DCLogger.debugLog("fail to register target block from json: " + name);
				return;
			}
		}
	}

	private static Map<String, Object> jsonMap = new HashMap<String, Object>();

	private static File dir = null;

	public static void startMap() {
		if (!jsonMap.isEmpty()) {
			for (Entry<String, Object> ent : jsonMap.entrySet()) {
				if (ent != null) {
					String name = ent.getKey();
					Object value = ent.getValue();
					if (value instanceof Map) {
						String h = ((Map) value).get("temperature").toString();
						DCHeatTier heat = DCHeatTier.getFromName(h);
						String m = ((Map) value).get("humidity").toString();
						DCHumidity hum = DCHumidity.getFromName(m);
						String q = ((Map) value).get("airflow").toString();
						DCAirflow air = DCAirflow.getFromName(q);
						IClimate clm = ClimateAPI.register.getClimateFromParam(heat, hum, air);
						registerBlockClimate(name, clm);
					}
				}
			}
		} else {
			DCLogger.debugLog("no block resistant json data.");
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

	// 生成は初回のみ
	public static void post() {

		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				} else if (!jsonMap.isEmpty()) {
					DCLogger.infoLog("block resistant data json is already exists.");
					return;
				}

				if (jsonMap.isEmpty()) {
					Map<String, String> map = Maps.newHashMap();
					map.put("temperature", "HOT (If you set NORMAL, it'll ignored.)");
					map.put("humidity", "WET (If you set NORMAL, it'll ignored.)");
					map.put("airflow", "NORMAL (If you set TIGHT, it'll ignored.)");
					jsonMap.put("sampleModID:sampleBlockName:0", map);
				}

				if (dir.canWrite()) {
					FileOutputStream fos = new FileOutputStream(dir.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(jsonMap, Map.class, jsw);

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
		dir = new File(file, "defeatedcrow/climate/block_climate_parameter.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}

}
