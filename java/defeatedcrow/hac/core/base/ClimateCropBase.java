package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.cultivate.GrowingStage;
import defeatedcrow.hac.api.cultivate.IClimateCrop;
import defeatedcrow.hac.api.placeable.IRapidCollectables;
import defeatedcrow.hac.api.placeable.ISidedTexture;

/**
 * Climate利用作物のベース。
 * IGrowableによる骨粉イベント対応、右クリック収穫機能を持つ。
 */
public abstract class ClimateCropBase extends Block implements ISidedTexture, INameSuffix, IClimateCrop,
		IRapidCollectables, IGrowable {

	protected Random rand = new Random();
	public static final String CL_TEX = "dcs_climate:blocks/clear";

	public ClimateCropBase(Material material, String s, int max) {
		super(material);
		this.setUnlocalizedName(s);
		this.setTickRandomly(true);
	}

	@Override
	public int tickRate(World world) {
		return 40;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(this, 1, 15));
	}

	/* Block動作 */

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			@Nullable ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null) {
			IBlockState crop = world.getBlockState(pos);
			GrowingStage stage = this.getCurrentStage(crop);
			if (stage == GrowingStage.GROWN) {
				player.playSound(SoundEvents.BLOCK_GRASS_PLACE, 1.0F, 1.0F);
				return this.harvest(world, pos, crop, player);
			}
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		super.onBlockClicked(world, pos, player);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
		if (!world.isRemote && state != null && state.getBlock() == this) {
			IClimate clm = this.getClimate(world, pos, state);
			GrowingStage stage = this.getCurrentStage(state);
			if (this.isSuitableClimate(clm, state) && stage != GrowingStage.GROWN) {
				this.grow(world, pos, state);
			} else if (rand.nextInt(10) == 0) {
				this.grow(world, pos, state);
			} else {
				this.checkAndDropBlock(world, pos, state);
			}
		}
	}

	protected IClimate getClimate(World world, BlockPos pos, IBlockState state) {
		DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(world, pos, checkingRange()[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, checkingRange()[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, checkingRange()[2], false);
		IClimate c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return c;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		GrowingStage stage = this.getCurrentStage(state);
		ret.add(this.getSeedItem(state));
		if (stage == GrowingStage.GROWN) {
			ret.addAll(this.getCropItems(state));
		}
		return ret;
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return this.isSuitablePlace(worldIn, pos, worldIn.getBlockState(pos.down()));
	}

	/* Json登録用 */

	@Override
	public abstract String[] getNameSuffix();

	@Override
	public String getTexture(int meta, int side, boolean face) {
		return CL_TEX;
	}

	/* IClimateCrop */

	@Override
	public abstract ItemStack getSeedItem(IBlockState thisState);

	@Override
	public abstract List<ItemStack> getCropItems(IBlockState thisState);

	@Override
	public boolean isSuitableClimate(IClimate climate, IBlockState thisState) {
		if (climate == null || thisState == null || thisState.getBlock() != this)
			return false;
		boolean temp = this.getSuitableTemp(thisState).contains(climate.getHeat());
		boolean hum = this.getSuitableHum(thisState).contains(climate.getHumidity());
		boolean air = this.getSuitableAir(thisState).contains(climate.getAirflow());
		return temp && hum && air;
	}

	@Override
	public boolean isSuitablePlace(World world, BlockPos pos, IBlockState targetState) {
		if (targetState == null)
			return false;
		boolean grass = targetState.getMaterial() == Material.GRASS;
		boolean dirt = targetState.getMaterial() == Material.GROUND;
		return grass || dirt;
	}

	@Override
	public GrowingStage getCurrentStage(IBlockState thisState) {
		if (thisState == null)
			return GrowingStage.DEAD;
		if (thisState.getValue(DCState.GROWN)) {
			return GrowingStage.GROWN;
		}
		if (thisState.getValue(DCState.FLOWER)) {
			return GrowingStage.FLOWER;
		}
		return GrowingStage.YOUNG;
	}

	@Override
	public IBlockState getStateFromStage(GrowingStage stage) {
		IBlockState ret = this.getDefaultState();
		switch (stage) {
		case DEAD:
			break;
		case GROWN:
			ret.withProperty(DCState.GROWN, true);
		case FLOWER:
			ret.withProperty(DCState.FLOWER, true);
		case YOUNG:
			ret.withProperty(DCState.STAGE, 3);
		default:
			break;
		}
		return ret;
	}

	@Override
	public boolean grow(World world, BlockPos pos, IBlockState thisState) {
		if (thisState != null && thisState.getBlock() == this) {
			GrowingStage stage = this.getCurrentStage(thisState);
			if (stage == GrowingStage.DEAD) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				return false;
			} else if (stage == GrowingStage.GROWN) {
				return false;
			} else if (stage == GrowingStage.FLOWER) {
				IBlockState ret = this.getStateFromStage(stage.getNextStage());
				return world.setBlockState(pos, ret, 3);
			} else {
				int age = thisState.getValue(DCState.STAGE);
				if (age < 3) {
					age++;
					return world.setBlockState(pos, thisState.withProperty(DCState.STAGE, age), 3);
				} else {
					IBlockState ret = this.getStateFromStage(stage.getNextStage());
					return world.setBlockState(pos, ret, 3);
				}
			}
		}
		return false;
	}

	@Override
	public boolean harvest(World world, BlockPos pos, IBlockState thisState, EntityPlayer player) {
		if (thisState != null && thisState.getBlock() == this) {
			GrowingStage stage = this.getCurrentStage(thisState);
			if (stage == GrowingStage.GROWN) {
				List<ItemStack> crops = this.getCropItems(thisState);
				boolean ret = false;
				for (ItemStack item : crops) {
					EntityItem drop = new EntityItem(world);
					drop.setEntityItemStack(item);
					if (player != null) {
						drop.setPosition(player.posX, player.posY + 0.15D, player.posZ);
					} else {
						drop.setPosition(pos.getX(), pos.getY() + 0.5D, pos.getZ());
					}
					if (!world.isRemote)
						world.spawnEntityInWorld(drop);
					ret = true;
				}
				if (ret) {
					IBlockState next = this.getStateFromStage(GrowingStage.YOUNG);
					world.setBlockState(pos, next, 3);
				}
				return ret;
			}
		}
		return false;
	}

	@Override
	public int[] checkingRange() {
		return new int[] {
				2,
				1,
				1 };
	}

	@Override
	public List<DCHeatTier> getSuitableTemp(IBlockState thisState) {
		List<DCHeatTier> ret = new ArrayList<DCHeatTier>();
		ret.add(DCHeatTier.COLD);
		ret.add(DCHeatTier.NORMAL);
		ret.add(DCHeatTier.HOT);
		return ret;
	}

	@Override
	public List<DCHumidity> getSuitableHum(IBlockState thisState) {
		List<DCHumidity> ret = new ArrayList<DCHumidity>();
		ret.add(DCHumidity.NORMAL);
		ret.add(DCHumidity.WET);
		return ret;
	}

	@Override
	public List<DCAirflow> getSuitableAir(IBlockState thisState) {
		List<DCAirflow> ret = new ArrayList<DCAirflow>();
		ret.add(DCAirflow.NORMAL);
		ret.add(DCAirflow.FLOW);
		ret.add(DCAirflow.WIND);
		return ret;
	}

	/* IRapidCollectables */

	@Override
	public boolean isCollectable(ItemStack item) {
		if (item == null || item.getItem() == null)
			return false;
		// デフォルトではハサミ
		return item.getItem() instanceof ItemShears;
	}

	@Override
	public int getCollectArea(ItemStack item) {
		return 3;
	}

	@Override
	public boolean doCollect(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack tool) {
		return this.harvest(world, pos, state, player);
	}

	/* state関連 */

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int i = meta & 3;
		boolean flower = (meta & 4) > 0;
		boolean grown = (meta & 8) > 0;
		IBlockState state = this.getDefaultState().withProperty(DCState.STAGE, i).withProperty(DCState.FLOWER, flower)
				.withProperty(DCState.GROWN, grown);
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		i = state.getValue(DCState.STAGE);
		if (state.getValue(DCState.FLOWER)) {
			i |= 4;
		}
		if (state.getValue(DCState.GROWN)) {
			i |= 8;
		}
		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.STAGE,
				DCState.FLOWER,
				DCState.GROWN });
	}

	// drop
	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(state.getBlock());
	}

	/* IGrowable */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		if (state != null && state.getBlock() == this) {
			GrowingStage stage = this.getCurrentStage(state);
			IClimate clm = this.getClimate(world, pos, state);
			return this.isSuitableClimate(clm, state) && stage.canUseBonemeal();
		}
		return false;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		if (state != null && state.getBlock() == this) {
			GrowingStage stage = this.getCurrentStage(state);
			return stage.canUseBonemeal();
		}
		return false;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		this.grow(world, pos, state);
	}

}
