package defeatedcrow.hac.core.client.base;

import net.minecraft.client.model.ModelBase;

public abstract class DCFoodModelBase extends ModelBase {

	private final boolean isBaked;

	public DCFoodModelBase(boolean baked) {
		super();
		isBaked = baked;
	}

	public abstract void render(float scale);

	public boolean isBaked() {
		return isBaked;
	}

}
