package defeatedcrow.hac.core.plugin.jei;

import java.util.ArrayList;
import java.util.List;

import defeatedcrow.hac.api.cultivate.CropAPI;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import mezz.jei.api.IModRegistry;

public final class ClimateCropMaker {

	private ClimateCropMaker() {}

	public static void register(IModRegistry registry) {
		List<IClimateCrop> list = new ArrayList<IClimateCrop>();
		list.addAll(CropAPI.register.getList().values());
		registry.addRecipes(list);
	}

}
