package defeatedcrow.hac.core.block.smelting;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.core.base.DCSimpleBlock;

public class BlockOres extends DCSimpleBlock {

	public BlockOres(Material m, String s, int max) {
		super(m, s, max);
		this.setTickRandomly(true);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
	}

	@Override
	public void onUpdateClimate(World world, BlockPos pos) {
	}

	@Override
	public void setHarvestLevel(String toolClass, int level) {
		for (int i = 0; i < 16; i++) {
			switch (i) {
			case 0:
			case 1:
			case 4:
			case 6:
			case 8:
			case 9:
				super.setHarvestLevel("pickaxe", 1, this.getStateFromMeta(i));
				break;
			default:
				super.setHarvestLevel("pickaxe", 2, this.getStateFromMeta(i));
			}
		}
	}
	/*
	 * 0: 石膏
	 * 1: 赤鉄鉱
	 * 2: 青カルセドニー
	 * 3: サファイア
	 * 4: 黄鉄鋼
	 * 5: 磁鉄鉱
	 * 6: 黄銅鉱
	 * 7: 紅砒ニッケル鋼
	 * 8: 閃亜鉛鉱
	 * 9: 白カルセドニー
	 * 10: 水晶
	 * 11: 金
	 * 12: 銀
	 * 13: ダイヤ
	 * 14: エメラルド
	 * 15: 水晶中の磁鉄鉱
	 */
}
