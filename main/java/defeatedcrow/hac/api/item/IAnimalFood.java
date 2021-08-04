package defeatedcrow.hac.api.item;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;

public interface IAnimalFood {

	boolean isTargetAnimal(EntityAnimal entity, ItemStack item);

}
