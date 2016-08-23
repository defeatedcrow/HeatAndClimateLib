package defeatedcrow.hac.api.cultivate;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;

/**
 * Climate利用作物<br>
 * Blockクラスに継承する。Entityに対応できるかは不透明。
 */
public interface IClimateCrop {

	// 対応する種や苗
	ItemStack getSeedItem(IBlockState thisState);

	// 収穫物
	List<ItemStack> getCropItems(IBlockState thisState, int fortune);

	// 成長に使用。育つ環境かどうか
	boolean isSuitableClimate(IClimate climate, IBlockState thisState);

	// 植え付けに使用。土壌のチェック
	boolean isSuitablePlace(World world, BlockPos pos, IBlockState underState);

	// bonemealや収穫判定はenumでまとめた
	GrowingStage getCurrentStage(IBlockState thisState);

	// 成長させる
	boolean grow(World world, BlockPos pos, IBlockState thisState);

	// 収穫
	boolean harvest(World world, BlockPos pos, IBlockState thisState, EntityPlayer player);

	// IClimateをチェックする範囲。{heat, hum, air}になるよう返すこと。
	int[] checkingRange();

	List<DCHeatTier> getSuitableTemp(IBlockState thisState);

	List<DCHumidity> getSuitableHum(IBlockState thisState);

	List<DCAirflow> getSuitableAir(IBlockState thisState);

}
