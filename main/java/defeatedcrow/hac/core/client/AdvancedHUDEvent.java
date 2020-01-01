package defeatedcrow.hac.core.client;

import java.util.Calendar;

import org.lwjgl.opengl.GL11;

import defeatedcrow.hac.api.climate.EnumSeason;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.climate.WeatherChecker;
import defeatedcrow.hac.core.util.DCTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedHUDEvent {

	public static final AdvancedHUDEvent INSTANCE = new AdvancedHUDEvent();
	public static final ResourceLocation TEX1 = new ResourceLocation(ClimateCore.PACKAGE_ID, CoreConfigDC.tex1);
	public static final ResourceLocation TEX2 = new ResourceLocation(ClimateCore.PACKAGE_ID, CoreConfigDC.tex2);
	public static final ResourceLocation TEX3 = new ResourceLocation(ClimateCore.PACKAGE_ID, CoreConfigDC.tex3);
	public static final ResourceLocation TEX4 = new ResourceLocation(ClimateCore.PACKAGE_ID, CoreConfigDC.tex4);

	private int count = 20;
	private int biomeID = 0;
	private Biome biome = Biomes.PLAINS;

	public static boolean enable = CoreConfigDC.enableAdvHUD;

	public static boolean hasAcv = true;

	public static boolean active = false;

	@SubscribeEvent
	public void doRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() != null && event.getType() == ElementType.ALL) {
			EntityPlayer player = ClimateCore.proxy.getPlayer();
			World world = ClimateCore.proxy.getClientWorld();
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if (player != null && world != null && gui == null) {
				if (enable) {
					if (count == 0) {
						active = true;
						count = 10;

						if (!hasAcv) {
							hasAcv = true;
						}

						/* 10Fごとに使用データを更新 */

						if (hasAcv) {
							int px = MathHelper.floor(player.posX);
							int py = MathHelper.floor(player.posY);
							int pz = MathHelper.floor(player.posZ);
							BlockPos pos = player.getPosition(); // new BlockPos(px, py, pz);
							if (pos != null) {
								biome = world.provider.getBiomeForCoords(pos);
							}
						}

					} else {
						active = false;
						count--;
					}

					if (hasAcv && ClientClimateData.INSTANCE.getClimate() != null) {
						IClimate clm = ClientClimateData.INSTANCE.getClimate();
						float tempF = biome.getTemperature(player.getPosition());
						if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {
							tempF += 1.5F;
						} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
							tempF -= 1.0F;
						} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER)) {
							tempF += 0.25F;
						}
						float we = WeatherChecker.getTempOffsetFloat(world.provider.getDimension(), false);
						if (CoreConfigDC.enableWeatherEffect) {
							tempF += we;
						}
						float ni = DCTimeHelper.getTimeOffset(world, biome);
						if (CoreConfigDC.enableTimeEffect) {
							tempF += ni;
						}

						FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GlStateManager.enableBlend();

						int offsetX = CoreConfigDC.iconHX;
						int offsetY = CoreConfigDC.iconHY;
						int x = offsetX;
						int y = event.getResolution().getScaledHeight() + offsetY;

						int fontX = 0;
						int fontY = 0;

						if (CoreConfigDC.useAnalogueHUD) {
							Minecraft.getMinecraft().getTextureManager().bindTexture(TEX4);
							drawTexturedModalRect(x, y - 12, 0, 0, 16, 58);
							int w = 20;
							if (!DCTimeHelper.isDayTime(world)) {
								w += 10;
							}
							if (we < 0) {
								w += 20;
							}
							drawTexturedModalRect(x + 4, y - 8, w, 0, 8, 8);

							float temp2 = tempF * 10F;
							int bar = MathHelper.ceil(temp2) + 1;
							if (bar > 25) {
								bar = 25;
							}
							if (bar < -10) {
								bar = -10;
							}
							drawTexturedModalRect(x + 1, y + 30 - bar, 20, 10, 9, 1);
							x += 5;
						} else {

							if (we > 0) {
								Minecraft.getMinecraft().getTextureManager().bindTexture(TEX2);
							} else if (we < 0) {
								Minecraft.getMinecraft().getTextureManager().bindTexture(TEX3);
							} else {
								Minecraft.getMinecraft().getTextureManager().bindTexture(TEX1);
							}
							drawTexturedModalRect(x, y, 0, 0, 48, 48);
						}

						String s = "";
						int color = 16383998;
						EnumSeason season = EnumSeason.SPRING;
						if (CoreConfigDC.enableSeasonEffect && CoreConfigDC.showSeason) {
							season = DCTimeHelper.getSeasonEnum(world);
							s += season.getName();
							color = season.color.getColorValue();
						}
						if (CoreConfigDC.showDay) {
							if (CoreConfigDC.enableRealSeason) {
								Calendar cal = Calendar.getInstance();
								s += " " + (cal.get(cal.MONTH) + 1) + "/" + cal.get(cal.DATE);
							} else {
								int day = DCTimeHelper.getDay(world);
								int sD = (int) (DCTimeHelper
										.seasonPeriod(season)[0] * (CoreConfigDC.yearLength / 365D));
								if (season == CoreConfigDC.overYear && day < sD) {
									sD = 0;
								}
								s += " Day " + (day - sD);
								// if (ClimateCore.isDebug) {
								// s += " totalday" + day;
								// }
							}
						}
						if (s.length() > 1) {
							if (CoreConfigDC.useAnalogueHUD)
								fr.drawString(s, x + 10 + CoreConfigDC.offsetSeason[0], y + CoreConfigDC.offsetSeason[1], color, true);
							else
								fr.drawString(s, x + CoreConfigDC.offsetSeason[0], y + CoreConfigDC.offsetSeason[1], color, true);
						}

						// Biome biome = Biome.getBiomeForId(biomeID);
						if (biome != null && CoreConfigDC.showBiome) {
							if (CoreConfigDC.useAnalogueHUD)
								fr.drawString(biome
										.getBiomeName(), x - 5 + CoreConfigDC.offsetBiome[0], y + CoreConfigDC.offsetBiome[1], 0xFFFFFF, true);
							else
								fr.drawString(biome
										.getBiomeName(), x + CoreConfigDC.offsetBiome[0], y + CoreConfigDC.offsetBiome[1], 0xFFFFFF, true);
						}

						if (CoreConfigDC.showClimate) {
							String temp = clm.getHeat().localize();
							String hum = clm.getHumidity().localize();
							String air = clm.getAirflow().localize();
							if (ClimateCore.isDebug) {
								temp += String.format(" %.2f F", (tempF));
							}

							fr.drawString(temp, x + CoreConfigDC.offsetClimate[0], y + CoreConfigDC.offsetClimate[1], clm
									.getHeat().getColorInt(), true);
							fr.drawString(hum, x + CoreConfigDC.offsetClimate[0], y + CoreConfigDC.offsetClimate[1] + 10, clm
									.getHumidity().getColorInt(), true);
							fr.drawString(air, x + CoreConfigDC.offsetClimate[0], y + CoreConfigDC.offsetClimate[1] + 20, clm
									.getAirflow().getColorInt(), true);
						}

						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					}
				}
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

}
