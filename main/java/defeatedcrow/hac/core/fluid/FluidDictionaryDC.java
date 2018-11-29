package defeatedcrow.hac.core.fluid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidDictionaryDC {

	public static final List<FluidDic> DICS = Lists.newArrayList();

	public static final FluidDictionaryDC INSTANCE = new FluidDictionaryDC();

	public static void registerFluidDic(Fluid fluid, String name) {
		if (fluid == null || name == null)
			return;

		FluidDic dic = getDic(name);

		for (FluidDic d2 : DICS) {
			String n2 = d2.dicName;
			if (n2 != null && n2.equalsIgnoreCase(name)) {
				dic = d2;
				dic.fluids.add(fluid);
				break;
			}
		}
	}

	public static FluidDic getDic(Fluid fluid) {
		if (fluid == null)
			return null;

		if (!DICS.isEmpty()) {
			for (FluidDic d2 : DICS) {
				if (d2.match(fluid)) {
					return d2;
				}
			}
		}
		return null;
	}

	public static FluidDic getDic(FluidStack fluidstack) {
		if (fluidstack == null)
			return null;

		return getDic(fluidstack.getFluid());
	}

	public static FluidDic getDic(String name) {
		if (name == null) {
			return null;
		}
		if (DICS.isEmpty()) {
			FluidDic dic = new FluidDic(name);
			DICS.add(dic);
			return dic;
		} else {
			FluidDic dic = null;
			for (FluidDic d2 : DICS) {
				String n2 = d2.dicName;
				if (n2 != null && n2.equalsIgnoreCase(name)) {
					dic = d2;
					return dic;
				}
			}
			FluidDic d3 = new FluidDic(name);
			DICS.add(d3);
			dic = d3;
			return dic;

		}
	}

	public static boolean matchFluid(Fluid target, Fluid ref) {
		if (target == null || ref == null)
			return false;

		if (target == ref) {
			return true;
		} else {
			FluidDic dic = getDic(ref);
			return dic != null && dic.match(target);
		}
	}

	public static boolean matchFluidName(Fluid target, String name) {
		if (target == null || name == null)
			return false;

		FluidDic dic = getDic(name);
		return dic != null && dic.match(target);
	}

	private static File dir = null;
	public static final Map<String, List<String>> fluidMap = new HashMap<String, List<String>>();

	public static void post() {

		if (dir != null) {
			try {
				if (!dir.exists() && !dir.createNewFile()) {
					return;
				}

				if (dir.canWrite() && !DICS.isEmpty()) {
					fluidMap.clear();
					for (FluidDic d2 : DICS) {
						if (d2 != null) {
							List<String> list = Lists.newArrayList();
							for (Fluid f : d2.fluids) {
								if (f != null)
									list.add(f.getName());
							}
							fluidMap.put(d2.dicName, list);
						}
					}

					FileOutputStream fos = new FileOutputStream(dir.getPath());
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					JsonWriter jsw = new JsonWriter(osw);
					jsw.setIndent(" ");
					Gson gson = new Gson();
					gson.toJson(fluidMap, Map.class, jsw);

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
		dir = new File(file, "fluid_dic.json");
		if (dir.getParentFile() != null) {
			dir.getParentFile().mkdirs();
		}
	}

}
