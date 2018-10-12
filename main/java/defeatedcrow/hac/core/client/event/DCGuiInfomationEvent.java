package defeatedcrow.hac.core.client.event;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.client.DCTextures;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DCGuiInfomationEvent {

	public static final DCGuiInfomationEvent INSTANCE = new DCGuiInfomationEvent();

	private float heat_prev = 0.0F;
	private float cold_prev = 0.0F;
	private int count = 20;

	public static boolean enable = CoreConfigDC.showDamageIcon;

	@SubscribeEvent
	public void doRender(DrawScreenEvent.Post event) {

		if (event.getGui() != null && event.getGui() instanceof GuiInventory) {
			GuiInventory gui = (GuiInventory) event.getGui();
			int x = event.getMouseX() - gui.getGuiLeft();
			int y = event.getMouseY() - gui.getGuiTop();

			if (gui.getSlotUnderMouse() != null && gui.getSlotUnderMouse().getHasStack()) {
				return;
			}

			if (enable) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(DCTextures.HUD.getRocation());
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				gui.drawTexturedModalRect(gui.getGuiLeft() + 25, gui.getGuiTop() + 7,
						RenderTempHUDEvent.INSTANCE.iconTier * 16, 0, 16, 16);

				if (count < 0) {
					if (gui.mc.player != null) {
						heat_prev = 0.0F;
						cold_prev = 0.0F;
						for (ItemStack armor : gui.mc.player.inventory.armorInventory) {
							if (!DCUtil.isEmpty(armor)) {
								heat_prev += DCUtil.getItemResistantData(armor, false);
								cold_prev += DCUtil.getItemResistantData(armor, true);
							}
						}
					}
					count = 20;
				} else {
					count--;
				}

				List<String> list = Lists.newArrayList();
				// list.add("x:" + x + ", y:" + y);
				if (x > 26 && x < 42 && y > 6 && y < 22) {
					list.add("Armor Heat Resistant");
					list.add(TextFormatting.GOLD.toString() + "Heat: " + String.format("%.1fF", heat_prev));
					list.add(TextFormatting.AQUA.toString() + "Cold: " + String.format("%.1fF", cold_prev));
				}
				gui.drawHoveringText(list, event.getMouseX(), event.getMouseY());
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
