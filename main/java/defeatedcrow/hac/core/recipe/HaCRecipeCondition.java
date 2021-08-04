package defeatedcrow.hac.core.recipe;

import java.util.function.BooleanSupplier;

import com.google.gson.JsonObject;

import defeatedcrow.hac.api.module.HaCModule;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class HaCRecipeCondition implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		if (JsonUtils.hasField(json, "module")) {
			String moduleID = JsonUtils.getString(json, "module");
			HaCModule module = HaCModule.getModule(moduleID);
			if (module == HaCModule.PLUGIN && JsonUtils.hasField(json, "modid")) {
				String modID = JsonUtils.getString(json, "modid");
				return () -> modID != null && Loader.isModLoaded(modID);
			}
			return () -> module == HaCModule.CORE || module.enabled;
		}

		return () -> true;
	}

}
