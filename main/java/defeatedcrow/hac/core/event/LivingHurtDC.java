package defeatedcrow.hac.core.event;

import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHurtDC {

	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		DamageSource source = event.getSource();
		float newDam = event.getAmount();
		float prev = 0.0F;
		float add = 1.0F;
		float add2 = 0.0F;
		if (living != null) {

			// DIFFENCE優先
			// チャームの防御判定から
			if (living instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				NonNullList<ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.DEFFENCE);
				for (ItemStack check : charms) {
					// DCLogger.debugLog("hurt charm");
					IJewelCharm charm = (IJewelCharm) check.getItem();
					prev += charm.reduceDamage(source, check);
					charm.onDiffence(source, player, newDam, check);
				}
			} else {
				NonNullList<ItemStack> charms = DCUtil.getMobCharm(living);
				for (ItemStack check : charms) {
					IJewelCharm amu = (IJewelCharm) check.getItem();
					prev += amu.reduceDamage(source, check);
					amu.onDiffence(source, living, newDam, check);
				}
			}

			// ATTACK側のチャーム判定
			if (source instanceof EntityDamageSource && source.getTrueSource() != null) {
				if (source.getTrueSource() instanceof EntityPlayer) {
					EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
					NonNullList<ItemStack> charms2 = DCUtil.getPlayerCharm(attacker, CharmType.ATTACK);
					for (ItemStack check : charms2) {
						// DCLogger.debugLog("attack charm");
						IJewelCharm charm = (IJewelCharm) check.getItem();
						add *= charm.increaceDamage(living, check);
						charm.onPlayerAttacking(attacker, living, source, newDam - prev, check);
					}
				} else if (source.getTrueSource() instanceof EntityLivingBase) {
					EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
					NonNullList<ItemStack> charms2 = DCUtil.getMobCharm(attacker);
					for (ItemStack check : charms2) {
						IJewelCharm amu = (IJewelCharm) check.getItem();
						add2 += amu.increaceDamage(living, check);
						amu.onAttacking(attacker, living, source, newDam - prev, check);
					}
				}
			}

			// 最終的なダメージ
			newDam *= add;
			newDam += add2;
			newDam -= prev;

			// 最終的に
			if (newDam < 0.5F) {
				event.setAmount(0F);
				event.setCanceled(true);
			} else {
				event.setAmount(newDam);
			}
			// DCLogger.debugLog("last climate dam:" + newDam);
		}
	}

}
