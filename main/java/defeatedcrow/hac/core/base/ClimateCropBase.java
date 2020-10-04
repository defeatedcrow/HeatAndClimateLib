package defeatedcrow.hac.core.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Climate利用作物のベース。
 * IGrowableによる骨粉イベント対応、右クリック収穫機能を持つ。
 * 4段階版
 */
public abstract class ClimateCropBase extends BlockDC implements ISidedTexture, INameSuffix, IClimateCrop,
		IRapidCollectables, IGrowable, IPlantable {

	protected static final AxisAlignedBB CROP_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D);

	protected Random cropRand = new Random();
	public static final String CL_TEX = "dcs_climate:blocks/clear";

	public ClimateCropBase(Material material, String s, int max) {
		super(material, s);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.STAGE4, 0));
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int tickRate(World world) {
		return CoreConfigDC.cropUpdateInterval;
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
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = super.getSubItemList();
		list.add(new ItemStack(this, 1, getGrownMetadata()));
		return list;
	}

	public int getGrownMetadata() {
		return 3;
	}

	/* Block動作 */

	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
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
		if (!world.isRemote && state != null && state.getBlock() instanceof ClimateCropBase) {
			IClimate clm = this.getClimate(world, pos, state);
			DCHumidity underHum = ClimateAPI.calculator.getHumidity(world, pos.down());
			GrowingStage stage = this.getCurrentStage(state);
			int chance = this.isSuitableClimate(clm, state) ? 6 : 30;
			if ((clm.getHeat() == DCHeatTier.WARM || clm.getHeat() == DCHeatTier.HOT) && underHum == DCHumidity.WET) {
				chance /= 2;
			}
			if (stage != GrowingStage.GROWN && rand.nextInt(chance) == 0) {
				this.grow(world, pos, state);
			} else {
				this.checkAndDropBlock(world, pos, state);
			}
		}
	}

	protected IClimate getClimate(World world, BlockPos pos, IBlockState state) {
		DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(world, pos, checkingRange()[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos.down(), checkingRange()[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, checkingRange()[2], false);
		IClimate c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return c;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		GrowingStage stage = this.getCurrentStage(state);
		if (cropRand.nextFloat() <= this.getSeedDropChance()) {
			ret.add(this.getSeedItem(state));
		}
		if (stage == GrowingStage.GROWN) {
			ret.addAll(this.getCropItems(state, fortune));
		}
		return ret;
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return this.isSuitablePlace(worldIn, pos, worldIn.getBlockState(pos.down()));
	}

	@Override
	public void onNeighborChange(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
		if (!this.canBlockStay(world, pos, state)) {
			world.destroyBlock(pos, true);
		}
	}

	protected float getSeedDropChance() {
		return 1.0F;
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
	public abstract List<ItemStack> getCropItems(IBlockState thisState, int fortune);

	@Override
	public IBlockState getGrownState() {
		return this.getDefaultState().withProperty(DCState.STAGE4, 3);
	}

	@Override
	public IBlockState setGroundState(IBlockState state) {
		return state.withProperty(DCState.STAGE4, 0);
	}

	@Override
	public boolean isSuitableClimate(IClimate climate, IBlockState thisState) {
		if (climate == null || thisState == null || !(thisState.getBlock() instanceof ClimateCropBase))
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
		boolean farm = targetState.getBlock() instanceof BlockFarmland;
		return farm;
	}

	@Override
	public GrowingStage getCurrentStage(IBlockState thisState) {
		if (thisState == null)
			return GrowingStage.DEAD;
		else {
			int i = DCState.getInt(thisState, DCState.STAGE4);
			if (i == 3) {
				return GrowingStage.GROWN;
			} else if (i == 2) {
				return GrowingStage.FLOWER;
			} else if (i == 1) {
				return GrowingStage.YOUNG;
			} else {
				return GrowingStage.GROUND;
			}
		}
	}

	@Override
	public boolean grow(World world, BlockPos pos, IBlockState thisState) {
		if (thisState != null && thisState.getBlock() instanceof ClimateCropBase) {
			GrowingStage stage = this.getCurrentStage(thisState);
			if (stage == GrowingStage.DEAD) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				return false;
			} else if (stage == GrowingStage.GROWN) {
				return false;
			} else {
				int age = DCState.getInt(thisState, DCState.STAGE4);
				if (age >= 0 && age < 3) {
					age++;
					IBlockState next = thisState.withProperty(DCState.STAGE4, age);
					return world.setBlockState(pos, next, 3);
				}
			}
		}
		return false;
	}

	@Override
	public boolean harvest(World world, BlockPos pos, IBlockState thisState, EntityPlayer player) {
		if (thisState != null && thisState.getBlock() instanceof ClimateCropBase) {
			GrowingStage stage = this.getCurrentStage(thisState);
			if (stage == GrowingStage.GROWN) {
				int f = 0;
				if (player != null && !DCUtil.isEmpty(player.getActiveItemStack())) {
					f = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack());
				}
				List<ItemStack> crops = this.getCropItems(thisState, f);
				boolean ret = false;
				for (ItemStack item : crops) {
					EntityItem drop = new EntityItem(world);
					drop.setItem(item);
					if (player != null) {
						drop.setPosition(player.posX, player.posY + 0.15D, player.posZ);
					} else {
						drop.setPosition(pos.getX(), pos.getY() + 0.5D, pos.getZ());
					}
					if (!world.isRemote)
						world.spawnEntity(drop);
					ret = true;
				}
				if (ret) {
					if (canStayOnHarvest()) {
						IBlockState next = thisState.withProperty(DCState.STAGE4, 0);
						world.setBlockState(pos, next, 2);
					} else {
						world.setBlockToAir(pos);
					}
				}
				return ret;
			}
		}
		return false;
	}

	public boolean canStayOnHarvest() {
		return false;
	}

	@Override
	public int[] checkingRange() {
		return CoreConfigDC.ranges;
	}

	@Override
	public List<DCHeatTier> getSuitableTemp(IBlockState thisState) {
		List<DCHeatTier> ret = new ArrayList<DCHeatTier>();
		ret.add(DCHeatTier.COOL);
		ret.add(DCHeatTier.NORMAL);
		ret.add(DCHeatTier.WARM);
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
	public String getCollectableTool() {
		return "shears";
	}

	@Override
	public boolean isCollectable(ItemStack item) {
		if (DCUtil.isEmpty(item))
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
		IBlockState state = this.getDefaultState().withProperty(DCState.STAGE4, i);
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		i = state.getValue(DCState.STAGE4);
		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.STAGE4
		});
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
		if (this.getSeedItem(state) != null) {
			return this.getSeedItem(state).getItem();
		} else {
			return null;
		}
	}

	/* IGrowable */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		if (state != null && state.getBlock() instanceof ClimateCropBase) {
			GrowingStage stage = this.getCurrentStage(state);
			IClimate clm = this.getClimate(world, pos, state);
			return this.isSuitableClimate(clm, state) && stage.canUseBonemeal();
		}
		return false;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		if (state != null && state.getBlock() instanceof ClimateCropBase) {
			GrowingStage stage = this.getCurrentStage(state);
			return stage.canUseBonemeal();
		}
		return false;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		this.grow(world, pos, state);
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	/* IPlantable */

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this)
			return getDefaultState();
		return state;
	}

}
