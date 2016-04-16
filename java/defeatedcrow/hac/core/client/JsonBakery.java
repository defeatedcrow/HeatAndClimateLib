package defeatedcrow.hac.core.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import defeatedcrow.hac.api.placeable.ISidedTexture;

/**
 * Original code was made by A.K.<br>
 * <br>
 * EventのターゲットはISidedTextureを継承しているBlockのみ。
 */
@SideOnly(Side.CLIENT)
public class JsonBakery {

	public static JsonBakery instance = new JsonBakery();

	/*
	 * Dummy Model
	 * 横向きの丸太状のSided、縦向きの丸太状のTBの2種がある
	 */
	private static ModelResourceLocation normalSided = new ModelResourceLocation("dcs_climate:dcs_cube_sided", "normal");
	private static ModelResourceLocation inventorySided = new ModelResourceLocation("dcs_climate:dcs_cube_sided", "inventory");

	private static ModelResourceLocation normalTB = new ModelResourceLocation("dcs_climate:dcs_cube_tb", "normal");
	private static ModelResourceLocation inventoryTB = new ModelResourceLocation("dcs_climate:dcs_cube_tb", "inventory");

	private static final List<String> TEX = new ArrayList<String>();

	public void regDummySidedModel(Block block) {
		/* Item用 */
		for (int i = 0; i < 16; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, inventorySided);
		}
		/* Block用 */
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return normalSided;
			}
		});
	}

	public void regDummyTBModel(Block block) {
		/* Item用 */
		for (int i = 0; i < 16; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, inventoryTB);
		}
		/* Block用 */
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return normalTB;
			}
		});
	}

	/* preInitに呼ぶ */
	public void addTex(List<String> list) {
		if (list != null && !list.isEmpty()) {
			TEX.addAll(list);
		}
	}

	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre event) {
		TextureMap textureMap = event.map;
		// TEX.addAll(MainInit.logCont.getTexList());
		for (String s : TEX) {
			ResourceLocation ret = new ResourceLocation(s);
			textureMap.registerSprite(ret);
		}
	}

	@SubscribeEvent
	public void onBakingModelEvent(ModelBakeEvent event) {
		ResourceLocation rawSided = new ResourceLocation("dcs_climate:block/dcs_cube_sided");
		try {
			IModel modelS = event.modelLoader.getModel(rawSided);
			if (modelS instanceof IRetexturableModel) {
				IBakedModel bakedSided = new BakedSidedBaguette((IRetexturableModel) modelS);
				event.modelRegistry.putObject(normalSided, bakedSided);
				event.modelRegistry.putObject(inventorySided, bakedSided);
			}
		} catch (IOException e) {
			/* モデル指定がミスるとここに飛ぶ */
			e.printStackTrace();
		}
		ResourceLocation rawTB = new ResourceLocation("dcs_climate:block/dcs_cube_tb");
		try {
			IModel modelT = event.modelLoader.getModel(rawTB);
			if (modelT instanceof IRetexturableModel) {
				IBakedModel bakedTB = new BakedTBBaguette((IRetexturableModel) modelT);
				event.modelRegistry.putObject(normalTB, bakedTB);
				event.modelRegistry.putObject(inventoryTB, bakedTB);
			}
		} catch (IOException e) {
			/* モデル指定がミスるとここに飛ぶ */
			e.printStackTrace();
		}
	}

	private static final String clear = "dcs_climate:blocks/clear";

	private static class BakedSidedBaguette implements ISmartBlockModel, ISmartItemModel {
		private final IRetexturableModel retexturableModel;
		private Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
			@Override
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		};

		public BakedSidedBaguette(IRetexturableModel model) {
			retexturableModel = model;
		}

		@Override
		public IBakedModel handleItemState(ItemStack stack) {
			/* 6面それぞれの貼り替え */
			if (stack != null && stack.getItem() != null) {
				if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).block instanceof ISidedTexture) {
					ISidedTexture sided = (ISidedTexture) ((ItemBlock) stack.getItem()).block;
					int meta = stack.getItemDamage();
					String top = sided.getTexture(meta, 1, false);
					String down = sided.getTexture(meta, 1, false);
					String ns = sided.getTexture(meta, 3, false);
					String we = sided.getTexture(meta, 5, false);

					ImmutableMap<String, String> textures = new ImmutableMap.Builder<String, String>().put("down1", down).put("up1", top)
							.put("ns1", ns).put("we1", we).put("down2", clear).put("up2", clear).build();
					return retexturableModel.retexture(textures).bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT,
							textureGetter);
				}
			}
			return retexturableModel.bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		}

		@Override
		public IBakedModel handleBlockState(IBlockState state) {
			/* 6面それぞれの貼り替え */
			if (state.getBlock() instanceof ISidedTexture) {
				ISidedTexture sided = (ISidedTexture) state.getBlock();
				int meta = state.getBlock().getMetaFromState(state);
				boolean face = (state.getBlock().getMetaFromState(state) & 8) == 0;
				String top = sided.getTexture(meta, 1, face);
				String down = sided.getTexture(meta, 1, face);
				String ns = sided.getTexture(meta, 3, face);
				String we = sided.getTexture(meta, 5, face);

				if (face) {
					ImmutableMap<String, String> textures = new ImmutableMap.Builder<String, String>().put("down1", clear)
							.put("up1", clear).put("ns1", ns).put("we1", we).put("down2", down).put("up2", down).build();
					return retexturableModel.retexture(textures).bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT,
							textureGetter);
				} else {
					ImmutableMap<String, String> textures = new ImmutableMap.Builder<String, String>().put("down1", down).put("up1", top)
							.put("ns1", ns).put("we1", we).put("down2", clear).put("up2", clear).build();
					return retexturableModel.retexture(textures).bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT,
							textureGetter);
				}
			}
			return retexturableModel.bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		}

		/* 以下、IBakedModelのメソッドだけど、handle～メソッドで違うモデルを返しているので、以下の状態で問題ない。 */
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing facing) {
			return null;
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return null;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return null;
		}
	}

	private static class BakedTBBaguette implements ISmartBlockModel, ISmartItemModel {
		private final IRetexturableModel retexturableModel;
		private Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
			@Override
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		};

		public BakedTBBaguette(IRetexturableModel model) {
			retexturableModel = model;
		}

		@Override
		public IBakedModel handleItemState(ItemStack stack) {
			/* 6面それぞれの貼り替え */
			if (stack != null && stack.getItem() != null) {
				if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).block instanceof ISidedTexture) {
					ISidedTexture sided = (ISidedTexture) ((ItemBlock) stack.getItem()).block;
					int meta = stack.getItemDamage();
					String top = sided.getTexture(meta, 1, false);
					String down = sided.getTexture(meta, 1, false);
					String side = sided.getTexture(meta, 3, false);
					return retexturableModel.retexture(ImmutableMap.of("down", down, "up", top, "side", side)).bake(
							retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
				}
			}
			return retexturableModel.bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		}

		@Override
		public IBakedModel handleBlockState(IBlockState state) {
			/* 6面それぞれの貼り替え */
			if (state.getBlock() instanceof ISidedTexture) {
				ISidedTexture sided = (ISidedTexture) state.getBlock();
				int meta = state.getBlock().getMetaFromState(state);
				String top = sided.getTexture(meta, 1, false);
				String down = sided.getTexture(meta, 1, false);
				String side = sided.getTexture(meta, 3, false);
				return retexturableModel.retexture(ImmutableMap.of("down", down, "up", top, "side", side)).bake(
						retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
			}
			return retexturableModel.bake(retexturableModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
		}

		/* 以下、IBakedModelのメソッドだけど、handle～メソッドで違うモデルを返しているので、以下の状態で問題ない。 */
		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing facing) {
			return null;
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return null;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return null;
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return null;
		}
	}

}
