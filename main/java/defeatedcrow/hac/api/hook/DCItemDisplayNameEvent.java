package defeatedcrow.hac.api.hook;

import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * asm挿入用の追加イベント
 */
@Cancelable
public class DCItemDisplayNameEvent extends Event {

	public final ItemStack stack;
	public final String defaultName;
	public String newName;

	public DCItemDisplayNameEvent(ItemStack in) {
		this.stack = in;
		if (DCUtil.isEmpty(in)) {
			defaultName = "EMPTY";
			newName = "EMPTY";
		} else {
			String s = in.getUnlocalizedName() + ".name";
			defaultName = I18n.format(s);
			newName = defaultName;
		}
	}

	public String post() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return newName;
		}

		return defaultName;
	}
}
