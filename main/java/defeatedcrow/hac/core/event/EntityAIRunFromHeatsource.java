package defeatedcrow.hac.core.event;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIRunFromHeatsource extends EntityAIPanic {

	public Vec3d avoidPos;

	public EntityAIRunFromHeatsource(EntityCreature creature, Vec3d pos) {
		super(creature, 1.0F);
		avoidPos = pos;
	}

	@Override
	public boolean shouldExecute() {
		if (this.avoidPos == null) {
			return false;
		} else {
			// 4blockくらい離れたら終わり
			double d = avoidPos.distanceTo(this.creature.getPositionVector());
			if (d > 6D) {
				return false;
			}

			return this.findRandomPosition();
		}
	}

	@Override
	protected boolean findRandomPosition() {
		Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 5, 4, avoidPos);

		if (vec3d == null) {
			return false;
		} else {
			this.randPosX = vec3d.x;
			this.randPosY = vec3d.y;
			this.randPosZ = vec3d.z;
			return true;
		}
	}

}
