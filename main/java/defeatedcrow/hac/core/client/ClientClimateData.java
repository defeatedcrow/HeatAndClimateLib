package defeatedcrow.hac.core.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.plugin.baubles.DCPluginBaubles;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
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
		Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.DEFFENCE);
		DamageSource source = tempTier > 0 ? DamageSourceClimate.climateHeatDamage
				: DamageSourceClimate.climateColdDamage;
		for (Entry<Integer, ItemStack> entry : charms.entrySet()) {
			IJewelCharm charm = (IJewelCharm) entry.getValue().getItem();
			if (isCold)
				coldPrev += charm.reduceDamage(source, entry.getValue());
			else
				heatPrev += charm.reduceDamage(source, entry.getValue());
		}

		if (Loader.isModLoaded("baubles")) {
			List<ItemStack> charms2 = DCPluginBaubles.getBaublesCharm(player, CharmType.DEFFENCE);
			for (ItemStack charm : charms2) {
				if (!DCUtil.isEmpty(charm)) {
					if (isCold)
						coldPrev += ((IJewelCharm) charm.getItem()).reduceDamage(source, charm);
					else
						heatPrev += ((IJewelCharm) charm.getItem()).reduceDamage(source, charm);
				}
			}
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
