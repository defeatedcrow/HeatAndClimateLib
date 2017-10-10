package defeatedcrow.hac.core.client.event;

import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.client.DCTextures;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTempHUDEvent {

	public static final RenderTempHUDEvent INSTANCE = new RenderTempHUDEvent();

	private int tier = 0;
	private float conf_prev = 0.0F;
	private float prev2 = 0.0F;
	private float damage = 0.0F;
	private int count = 20;

	public static boolean enable = CoreConfigDC.showDamageIcon;

	@SubscribeEvent
	public void doRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() != null && event.getType() == ElementType.ALL) {
			EntityPlayer player = ClimateCore.proxy.getPlayer();
			World world = ClimateCore.proxy.getClientWorld();
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if (player != null && world != null && gui == null && !player.isSpectator()) {

				if (count == 0) {
					count = 10;

					/* 10Fごとに使用データを更新 */
					int px = MathHelper.floor(player.posX);
					int py = MathHelper.floor(player.posY) + 1;
					int pz = MathHelper.floor(player.posZ);
					BlockPos pos = new BlockPos(px, py, pz);
					if (pos != null && world.isAreaLoaded(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
						DCHeatTier temp = ClimateAPI.calculator.getAverageTemp(world, pos);
						if (temp != null) {
							tier = temp.getTier();
						}
					}

					conf_prev = 3F - CoreConfigDC.damageDifficulty;
					damage = 0;
					prev2 = 0F;

					// 防具の計算
					Iterable<ItemStack> items = player.getArmorInventoryList();
					if (items != null) {
						for (ItemStack item : items) {
							if (DCUtil.isEmpty(item))
								continue;

							float p = 0F;
							if (tier < 0) {
								p += DamageAPI.itemRegister.getColdPreventAmount(item);
							} else {
								p += DamageAPI.itemRegister.getHeatPreventAmount(item);
							}
							if (p == 0F && item.getItem() instanceof ItemArmor) {
								ArmorMaterial mat = ((ItemArmor) item.getItem()).getArmorMaterial();
								if (tier < 0) {
									p += DamageAPI.armorRegister.getColdPreventAmount(mat);
								} else {
									p += DamageAPI.armorRegister.getHeatPreventAmount(mat);
								}
								if (tier > 0 && EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION,
										item) > 0) {
									p += EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, item)
											* 1.0F;
								}
							}
							prev2 += p;
						}
					}

					// charm
					Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.DEFFENCE);
					DamageSource source = tier > 0 ? DamageSourceClimate.climateHeatDamage
							: DamageSourceClimate.climateColdDamage;
					for (Entry<Integer, ItemStack> entry : charms.entrySet()) {
						IJewelCharm charm = (IJewelCharm) entry.getValue().getItem();
						prev2 += charm.reduceDamage(source, entry.getValue());
					}

					items = null;
					charms = null;

				} else {
					count--;
				}

				if (tier < 0) {
					damage = (tier + conf_prev) * 2;
					damage += prev2;
					if (damage > 0F) {
						damage = 0F;
					}
				} else if (tier > 0) {
					damage = tier - conf_prev;
					damage -= prev2;
					if (damage < 0F) {
						damage = 0F;
					}
				}

				int tX = 2;
				if (damage > 0F) {
					if (damage >= 2F) {
						tX = 4;
					} else if (damage >= 1F) {
						tX = 3;
					}
				} else {
					if (damage <= -2F) {
						tX = 0;
					} else if (damage <= -1F) {
						tX = 1;
					}
				}

				Minecraft.getMinecraft().getTextureManager().bindTexture(DCTextures.HUD.getRocation());
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableBlend();

				if (enable && !player.capabilities.isCreativeMode) {
					int factX = -106;
					int factY = -4;
					int offsetX = CoreConfigDC.iconX;
					int offsetY = CoreConfigDC.iconY;
					int x = (event.getResolution().getScaledWidth() / 2) + factX + offsetX;
					int y = event.getResolution().getScaledHeight() - 39 + factY + offsetY;
					drawTexturedModalRect(x, y, tX * 16, 0, 16, 16);
				}

				int sizeX = event.getResolution().getScaledWidth();
				int sizeY = event.getResolution().getScaledHeight();

				if (CoreConfigDC.hudEffect) {
					if (tX > 2) {
						GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
								GlStateManager.DestFactor.ZERO);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
						Minecraft.getMinecraft().getTextureManager().bindTexture(DCTextures.HOT_DISP.getRocation());
						drawDispTexture(0, 0, sizeX, sizeY);
					} else if (tX < 2) {
						GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
								GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
								GlStateManager.DestFactor.ZERO);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
						Minecraft.getMinecraft().getTextureManager().bindTexture(DCTextures.COLD_DISP.getRocation());
						drawDispTexture(0, 0, sizeX, sizeY);
					}
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	public void drawTexturedModalRect(int x, int y, int tX, int tY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, -90.0F).tex((tX + 0) * f, (tY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + height, -90.0F).tex((tX + width) * f, (tY + height) * f1).endVertex();
		vertexbuffer.pos(x + width, y + 0, -90.0F).tex((tX + width) * f, (tY + 0) * f1).endVertex();
		vertexbuffer.pos(x + 0, y + 0, -90.0F).tex((tX + 0) * f, (tY + 0) * f1).endVertex();
		tessellator.draw();
	}

	public void drawDispTexture(int x, int y, int width, int height) {
		float f = 0.00390625F * 256;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(x + 0, y + height, -90.0F).tex(0, f).endVertex();
		vertexbuffer.pos(x + width, y + height, -90.0F).tex(f, f).endVertex();
		vertexbuffer.pos(x + width, y + 0, -90.0F).tex(f, 0).endVertex();
		vertexbuffer.pos(x + 0, y + 0, -90.0F).tex(0, 0).endVertex();
		tessellator.draw();
	}

	private void drawOutlinedBoundingBox(AxisAlignedBB aabb) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(aabb.minX, aabb.maxY, aabb.minZ).tex(1, 0).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).tex(0, 0).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).tex(0, 1).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).tex(1, 1).endVertex();

		vertexbuffer.pos(aabb.maxX, aabb.minY, aabb.minZ).tex(1, 0).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.minY, aabb.minZ).tex(0, 0).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.minY, aabb.maxZ).tex(0, 1).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).tex(1, 1).endVertex();

		vertexbuffer.pos(aabb.minX, aabb.minY, aabb.minZ).tex(1, 0).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.minY, aabb.minZ).tex(0, 0).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).tex(0, 1).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.maxY, aabb.minZ).tex(1, 1).endVertex();

		vertexbuffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).tex(1, 0).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).tex(0, 0).endVertex();
		vertexbuffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).tex(0, 1).endVertex();
		vertexbuffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).tex(1, 1).endVertex();
		tessellator.draw();
	}
}
