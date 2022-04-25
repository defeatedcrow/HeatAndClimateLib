package defeatedcrow.hac.core.base;

import net.minecraft.block.properties.IProperty;

public interface IPropertyIgnore {

	IProperty[] ignoreTarget();

	EnumStateType getType();

}
