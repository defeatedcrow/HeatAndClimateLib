package defeatedcrow.hac.api.cultivate;

/** 作物の成長段階を表す */
public enum GrowingStage {
	YOUNG(true, false), FLOWER(true, false), GROWN(false, true), DEAD(false, false);

	private final boolean canBonemeal;
	private final boolean canHarvest;

	private GrowingStage(boolean a, boolean b) {
		canBonemeal = a;
		canHarvest = b;
	}

	public boolean canUseBonemeal() {
		return canBonemeal;
	}

	public boolean canHarvestCrop() {
		return canHarvest;
	}

	public GrowingStage getNextStage() {
		if (this == YOUNG) {
			return FLOWER;
		} else if (this == FLOWER) {
			return GROWN;
		} else {
			return DEAD;
		}
	}

}
