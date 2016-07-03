package defeatedcrow.hac.core.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.gson.Gson;

import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.base.DCFacelessTileBlock;
import defeatedcrow.hac.core.base.DCTileBlock;
import defeatedcrow.hac.core.base.ITexturePath;

/**
 * Jsonの登録と生成を行うクラス。<br>
 * 他MODから使用する場合は、現在の開発環境ディレクトリパスを用いてインスタンスを生成して下さい。
 */
@SideOnly(Side.CLIENT)
public class JsonRegisterHelper {
	private final String basePath;

	public JsonRegisterHelper(String s) {
		basePath = s;
	}

	public static final JsonRegisterHelper INSTANCE = new JsonRegisterHelper(
			"E:\\forge1.9.0\\HaC_Lib\\src\\main\\resources");

	/**
	 * 一枚絵アイコンItemのJson生成と登録をまとめて行うメソッド。
	 */
	public void regSimpleItem(Item item, String domein, String name, String dir, int max) {
		int m = 0;
		while (m < max + 1) {
			this.checkAndBuildJson(item, domein, name, dir, m);
			ModelLoader.setCustomModelResourceLocation(item, m, new ModelResourceLocation(domein + ":" + dir + "/"
					+ name + m, "inventory"));
			m++;
		}
	}

	/**
	 * 汎用BakedBlock使用メソッド
	 * Blockのモデルには一切手を付けず、一枚絵アイコンの標準的なItemJsonを生成
	 */
	public void regBakedBlock(Block block, String domein, String name, String dir, int max) {
		int m = 0;
		while (m < max + 1) {
			this.checkAndBuildJson(block, domein, name, dir, m);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), m, new ModelResourceLocation(
					domein + ":" + dir + "/" + name + m, "inventory"));
			m++;
		}
	}

	/**
	 * 汎用Tile使用メソッド
	 * 外見は仮のJsonファイルに紐付け、TESRで描画する
	 */
	public void regTEBlock(Block block, String domein, String name, String dir, int maxMeta) {
		ModelLoader.setCustomStateMapper(block,
				(new StateMap.Builder()).ignore(((DCTileBlock) block).FACING, ((DCTileBlock) block).TYPE).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(domein + ":"
				+ "basetile"));
		if (maxMeta == 0) {
			this.checkAndBuildJson(block, domein, name, dir, 0);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
					domein + ":" + dir + "/" + name, "inventory"));
		} else {
			for (int i = 0; i < maxMeta + 1; i++) {
				this.checkAndBuildJson(block, domein, name, dir, i);
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(
						domein + ":" + dir + "/" + name + i, "inventory"));
			}
		}
	}

	/**
	 * 汎用Tile使用メソッド2
	 * FACINGがないやつ
	 */
	public void regTESimpleBlock(Block block, String domein, String name, String dir, int maxMeta) {
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(((DCFacelessTileBlock) block).TYPE)
				.build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(domein + ":"
				+ "basetile"));
		if (maxMeta == 0) {
			this.checkAndBuildJson(block, domein, name, dir, 0);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
					domein + ":" + dir + "/" + name, "inventory"));
		} else {
			for (int i = 0; i < maxMeta + 1; i++) {
				this.checkAndBuildJson(block, domein, name, dir, i);
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(
						domein + ":" + dir + "/" + name + i, "inventory"));
			}
		}
	}

	/**
	 * メタ持ちブロックのJson登録とItemBlock用Json生成を行う。
	 * Block、Blockstate用Jsonはここでは生成しない。
	 */
	public void regSimpleBlock(Block block, String domein, String name, String dir, int maxMeta) {
		if (maxMeta == 0) {
			ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(domein + ":" + dir
					+ "/" + name, "type"));
			this.checkAndBuildJsonItemBlock(domein, name, dir, 0);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
					domein + ":" + dir + "/" + name, "inventory"));
		} else {
			ModelResourceLocation[] models = new ModelResourceLocation[maxMeta + 1];
			for (int i = 0; i < maxMeta + 1; i++) {
				models[i] = new ModelResourceLocation(domein + ":" + dir + "/" + name + i, "type");
			}
			ModelBakery.registerItemVariants(Item.getItemFromBlock(block), models);
			for (int i = 0; i < maxMeta + 1; i++) {
				this.checkAndBuildJsonItemBlock(domein, name, dir, i);
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(
						domein + ":" + dir + "/" + name + i, "inventory"));
			}
		}
	}

	/**
	 * Jsonあれ 1<br>
	 * デバッグモード時に限り、標準的な一枚絵アイコン用のJsonを生成する。既に生成済みの場合は生成処理を行わない。<br>
	 * テクスチャの取得にITexturePathを使用するため、登録するItemに実装する。
	 */
	public void checkAndBuildJson(Object item, String domein, String name, String dir, int meta) {
		if (!(item instanceof ITexturePath))
			return;

		String filePath = null;
		File gj = null;
		boolean find = false;
		boolean tool = false;
		if (item instanceof Item && ((Item) item).isFull3D())
			tool = true;

		try {
			Path path = Paths.get(basePath);
			path.normalize();
			filePath = path.toString() + "\\assets\\" + domein + "\\models\\item\\";
			if (dir != null) {
				filePath += dir + "\\";
			}
			// DCLogger.debugLog("dcs_climate", "current pass " + filePath.toString());
			if (filePath != null) {
				gj = new File(filePath + name + meta + ".json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (gj.exists()) {
			find = true;
			DCLogger.debugLog("File is found! " + gj.getName());
		}

		if (!find) {
			ITexturePath tex = (ITexturePath) item;

			try {
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(gj.getPath())));
				if (!tool) {
					Textures textures = new Textures(tex.getTexPath(meta, false));
					// Disp display = new Disp();
					jsonMap.put("parent", "item/generated");
					jsonMap.put("textures", textures);
					// jsonMap.put("display", display);
				} else {
					Textures textures = new Textures(tex.getTexPath(meta, false));
					// Disp2 display = new Disp2();
					jsonMap.put("parent", "item/handheld");
					jsonMap.put("textures", textures);
					// jsonMap.put("display", display);
				}

				Gson gson = new Gson();
				String output = gson.toJson(jsonMap);
				pw.println(output);
				pw.close();
				output = "";
				DCLogger.debugLog("File writed! " + gj.getPath());

			} catch (FileNotFoundException e) {
				DCLogger.debugLog("File not found! " + gj.getPath());
			} catch (IOException e) {
				DCLogger.debugLog("fail");
			}
		}
	}

	/**
	 * Jsonあれ 2<br>
	 * parentに同名のblockmodelを要求するItemBlock用Jsonを生成する。<br>
	 * blockstate用Jsonは生成しないため、ぬくもりある手作りを用いて下さい。
	 */
	public void checkAndBuildJsonItemBlock(String domein, String name, String dir, int meta) {

		String filePath = null;
		File gj = null;
		boolean find = false;
		try {
			Path path = Paths.get(basePath);
			path.normalize();
			filePath = path.toString() + "\\assets\\" + domein + "\\models\\item\\";
			if (dir != null) {
				filePath += dir + "\\";
			}
			// DCLogger.debugLog("dcs_climate", "current pass " + filePath.toString());
			if (filePath != null) {
				gj = new File(filePath + name + meta + ".json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (gj.exists()) {
			find = true;
			DCLogger.debugLog("File is found! " + gj.getName());
		}

		if (!find) {

			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(gj.getPath())));
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				// Disp3 display = new Disp3();
				jsonMap.put("parent", domein + ":block/" + dir + "/" + name + meta);
				// jsonMap.put("display", display);

				Gson gson = new Gson();
				String output = gson.toJson(jsonMap);
				pw.println(output);
				pw.close();
				output = "";
				DCLogger.debugLog("File writed! " + gj.getPath());

			} catch (FileNotFoundException e) {
				DCLogger.debugLog("File not found! " + gj.getPath());
			} catch (IOException e) {
				DCLogger.debugLog("fail");
			}
		}
	}

	/**
	 * Jsonあれ 3<br>
	 * 全面同テクスチャのCubeモデルのJsonを生成する。既に生成済みの場合は生成処理を行わない。<br>
	 * テクスチャの取得にITexturePathを使用するため、登録するblockに実装する。
	 */
	public void checkAndBuildJsonCube(ITexturePath block, String domein, String name, String dir, int meta) {
		if (block == null)
			return;

		String filePath = null;
		File gj = null;
		boolean find = false;
		try {
			Path path = Paths.get(basePath);
			path.normalize();
			filePath = path.toString() + "\\assets\\" + domein + "\\models\\block\\";
			if (dir != null) {
				filePath += dir + "\\";
			}
			// DCLogger.debugLog("dcs_climate", "current pass " + filePath.toString());
			if (filePath != null) {
				gj = new File(filePath + name + meta + ".json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (gj.exists()) {
			find = true;
			DCLogger.debugLog("File is found! " + gj.getName());
		}

		if (!find) {

			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(gj.getPath())));
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				BlockTex textures = new BlockTex(block.getTexPath(meta, false));
				jsonMap.put("parent", domein + ":block/dcs_cube_all");
				jsonMap.put("textures", textures);

				Gson gson = new Gson();
				String output = gson.toJson(jsonMap);
				pw.println(output);
				pw.close();
				output = "";
				DCLogger.debugLog("File writed! " + gj.getPath());

			} catch (FileNotFoundException e) {
				DCLogger.debugLog("File not found! " + gj.getPath());
			} catch (IOException e) {
				DCLogger.debugLog("fail");
			}
		}
	}

	private class Textures {
		String layer0;

		private Textures(String tex) {
			layer0 = tex;
		}
	}

	private class BlockTex {
		String all;

		private BlockTex(String tex) {
			all = tex;
		}
	}

	private class Disp {
		Third thirdperson = new Third(new int[] {
				-90,
				0,
				0 }, new double[] {
				0,
				1,
				-3 }, new double[] {
				0.55D,
				0.55D,
				0.55D });
		First firstperson = new First();
	}

	private class Disp2 {
		Third thirdperson = new Third(new int[] {
				0,
				90,
				-35 }, new double[] {
				0,
				1.25D,
				-3.5D }, new double[] {
				0.85D,
				0.85D,
				0.85D });
		First firstperson = new First();
	}

	private class Disp3 {
		Third thirdperson = new Third(new int[] {
				10,
				45,
				170 }, new double[] {
				0,
				1.5D,
				-2.75D }, new double[] {
				0.35D,
				0.35D,
				0.35D });
	}

	private class Third {
		private Third(int[] i, double[] j, double[] k) {
			rotation = i;
			translation = j;
			scale = k;
		}

		final int[] rotation;
		final double[] translation;
		final double[] scale;
	}

	private class First {
		int[] rotation = new int[] {
				0,
				-135,
				25 };
		int[] translation = new int[] {
				0,
				4,
				2 };
		double[] scale = new double[] {
				1.7D,
				1.7D,
				1.7D };
	}

}
