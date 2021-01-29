package defeatedcrow.hac.core.client;

import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientClimateData {

	public static final ClientClimateData INSTANCE = new ClientClimateData();

	// 表示用データ
	private static IClimate climate = null;
	private static int tempTier = 0;
	private static int iconTier = 2;
	private static float heatPrev = 0;
	private static float coldPrev = 0;

	public void updatePlayerClimate(World world, EntityPlayer player) {
		/* 10Fごとに使用データを更新 */
		int px = MathHelper.floor(player.posX);
		int py = MathHelper.floor(player.posY) + 1;
		int pz = MathHelper.floor(player.posZ);
		BlockPos pos = new BlockPos(px, py, pz);
		if (pos != null && world.isAreaLoaded(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
			climate = ClimateAPI.calculator.getClimate(world, pos);

			if (CoreConfigDC.heldItem && climate != null) {
				List<ItemStack> hands = Lists.newArrayList();
				if (!DCUtil.isEmpty(player.getHeldItemMainhand())) {
					hands.add(player.getHeldItemMainhand());
				}
				if (!DCUtil.isEmpty(player.getHeldItemOffhand())) {
					hands.add(player.getHeldItemOffhand());
				}
				for (ItemStack item : hands) {
					if (DCUtil.isEmpty(item))
						continue;

					Block target = null;
					int meta = 0;

					if (item.getItem() instanceof ItemBlock) {
						target = ((ItemBlock) item.getItem()).getBlock();
						meta = item.getItemDamage();
					} else if (item
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) instanceof FluidBucketWrapper) {
						FluidBucketWrapper bucket = (FluidBucketWrapper) item
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
						if (bucket.getFluid() != null && bucket.getFluid().getFluid().getBlock() != null) {
							target = bucket.getFluid().getFluid().getBlock();
						}
					}

					if (target == null)
						continue;

					DCHeatTier nT = climate.getHeat();
					DCHumidity nH = climate.getHumidity();
					DCAirflow nA = climate.getAirflow();

					DCHeatTier cT = DCUtil.getBlockTemp(new BlockSet(target, meta), player.world, pos);
					if (nT != cT) {
						nT = nT.getAverageTemp(cT);
					}

					DCHumidity cH = DCUtil.getBlockHum(new BlockSet(target, meta), player.world, pos);
					if (nH != cH) {
						nH = nH.getAverageHumidity(cH);
					}

					DCAirflow cA = DCUtil.getBlockAir(new BlockSet(target, meta), player.world, pos);
					if (nA != cA) {
						nA = nA.getAverageAirflow(cA);
					}

					climate = ClimateAPI.register.getClimateFromParam(nT, nH, nA);

				}
			}

			if (climate != null) {
				tempTier = climate.getHeat().getTier();
			}

		}

		float conf_prev = 3F - CoreConfigDC.damageDifficulty;
		float damage = 0;
		boolean isCold = tempTier < 0;
		heatPrev = 0;
		coldPrev = 0;

		if (player.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
			heatPrev += 4.0F;
		}
		if (player.isPotionActive(DCInit.prevFreeze)) {
			coldPrev += 4.0F;
		}

		// 防具の計算
		Iterable<ItemStack> items = player.getArmorInventoryList();
		if (items != null) {
			for (ItemStack item : items) {
				if (DCUtil.isEmpty(item))
					continue;

				heatPrev += DCUtil.getItemResistantData(item, false);
				coldPrev += DCUtil.getItemResistantData(item, true);
			}
		}

		// charm
		NonNullList<ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.DEFFENCE);
		DamageSource source = tempTier > 0 ? DamageSourceClimate.climateHeatDamage :
				DamageSourceClimate.climateColdDamage;
		for (ItemStack check : charms) {
			IJewelCharm charm = (IJewelCharm) check.getItem();
			if (isCold)
				coldPrev += charm.reduceDamage(source, check);
			else
				heatPrev += charm.reduceDamage(source, check);
		}

		items = null;
		charms = null;

		if (player.world.getDifficulty() != EnumDifficulty.PEACEFUL || CoreConfigDC.peacefulDam) {
			if (isCold) {
				damage = (tempTier + conf_prev) * 2;
				damage += coldPrev;
				if (damage > 0F) {
					damage = 0F;
				}
			} else {
				damage = tempTier - conf_prev;
				damage -= heatPrev;
				if (damage < 0F) {
					damage = 0F;
				}
			}
		}

		iconTier = 2;
		if (damage > 0F) {
			if (damage >= 2F) {
				iconTier = 4;
			} else if (damage >= 1F) {
				iconTier = 3;
			}
		} else {
			if (damage <= -2F) {
				iconTier = 0;
			} else if (damage <= -1F) {
				iconTier = 1;
			}
		}
	}

	public int getTempTier() {
		return tempTier;
	}

	public int getIconTier() {
		return iconTier;
	}

	public float getArmorHeatPrev() {
		return heatPrev;
	}

	public float getArmorColdPrev() {
		return coldPrev;
	}

	public IClimate getClimate() {
		return climate;
	}
}
