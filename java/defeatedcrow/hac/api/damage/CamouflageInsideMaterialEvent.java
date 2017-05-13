package defeatedcrow.hac.api.damage;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * asm挿入用の追加イベント
 */
@HasResult
public class CamouflageInsideMaterialEvent extends EntityEvent {

	public final Material material;

	public CamouflageInsideMaterialEvent(Entity entityIn, Material materialIn) {
		super(entityIn);
		this.material = materialIn;
	}

	public boolean result() {
		MinecraftForge.EVENT_BUS.post(this);
		if (hasResult() && getResult() == Result.ALLOW) {
			return true;
		}

		return false;
	}

	public boolean test() {
		return getEntity().isInsideOfMaterial(material);
	}
}
