package defeatedcrow.hac.core.climate.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import defeatedcrow.hac.api.cultivate.CropAPI;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.cultivate.IClimateCropRegister;

public class ClimateCropRegister implements IClimateCropRegister {

	public IClimateCropRegister instance() {
		return CropAPI.register;
	}

	private static Map<String, IClimateCrop> list;

	public ClimateCropRegister() {
		list = new HashMap<String, IClimateCrop>();
	}

	@Override
	public Map<String, IClimateCrop> getList() {
		return list;
	}

	@Override
	public void addCropData(IClimateCrop block) {
		if (block != null && block instanceof Block) {
			Block b = (Block) block;
			String name = b.getUnlocalizedName();
			if (list.isEmpty()) {
				list.put(name, block);
			} else if (!list.containsKey(name)) {
				list.put(name, block);
			}
		}
	}

}
