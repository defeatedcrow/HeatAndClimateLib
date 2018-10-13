package defeatedcrow.hac.core.client.event;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.client.ClientClimateData;
import defeatedcrow.hac.core.client.DCTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DCGuiInfomationEvent {

	public static final DCGuiInfomationEvent INSTANCE = new DCGuiInfomationEvent();

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
						ClientClimateData.INSTANCE.getIconTier() * 16, 0, 16, 16);

				List<String> list = Lists.newArrayList();
				// list.add("x:" + x + ", y:" + y);
				if (x > 26 && x < 42 && y > 6 && y < 22) {
					list.add("Armor Heat Resistant");
					list.add(TextFormatting.GOLD.toString() + "Heat: "
							+ String.format("%.1fF", ClientClimateData.INSTANCE.getArmorHeatPrev()));
					list.add(TextFormatting.AQUA.toString() + "Cold: "
							+ String.format("%.1fF", ClientClimateData.INSTANCE.getArmorColdPrev()));
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
