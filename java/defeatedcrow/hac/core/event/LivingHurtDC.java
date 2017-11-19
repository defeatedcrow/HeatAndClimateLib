package defeatedcrow.hac.core.event;

import java.util.Map;
import java.util.Map.Entry;

import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelAmulet;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
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
		if (living != null) {

			// DIFFENCE優先
			// チャームの防御判定から
			if (living instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(player, CharmType.DEFFENCE);

				for (Entry<Integer, ItemStack> entry : charms.entrySet()) {
					DCLogger.debugLog("hurt charm");
					IJewelCharm charm = (IJewelCharm) entry.getValue().getItem();
					prev += charm.reduceDamage(source, entry.getValue());
					if (charm.onDiffence(source, player, newDam, entry.getValue())) {
						if (DCUtil.isEmpty(charm.consumeCharmItem(entry.getValue()))) {
							player.inventory.markDirty();
							player.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
						}
					}
				}
				charms.clear();
			}

			Map<Integer, ItemStack> amulets = DCUtil.getAmulets(living);
			for (Entry<Integer, ItemStack> entry : amulets.entrySet()) {
				IJewelAmulet amu = (IJewelAmulet) entry.getValue().getItem();
				prev += amu.reduceDamage(source, entry.getValue());
				if (amu.onDiffence(source, living, newDam, entry.getValue())) {
					if (DCUtil.isEmpty(amu.consumeCharmItem(entry.getValue()))) {
						living.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
					}
				}
			}
			amulets.clear();

			// ATTACK側のチャーム判定
			if (source instanceof EntityDamageSource && source.getTrueSource() != null) {
				if (source.getTrueSource() instanceof EntityPlayer) {
					EntityPlayer attacker = (EntityPlayer) source.getTrueSource();

					Map<Integer, ItemStack> charms2 = DCUtil.getPlayerCharm(attacker, CharmType.ATTACK);
					for (Entry<Integer, ItemStack> entry : charms2.entrySet()) {
						DCLogger.debugLog("attack charm");
						IJewelCharm charm = (IJewelCharm) entry.getValue().getItem();
						add *= charm.increaceDamage(living, entry.getValue());
						if (charm.onAttacking(attacker, living, source, newDam - prev, entry.getValue())) {
							if (DCUtil.isEmpty(charm.consumeCharmItem(entry.getValue()))) {
								attacker.inventory.markDirty();
								attacker.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
							}
						}
					}
					charms2.clear();
				}
				if (source.getTrueSource() instanceof EntityLivingBase) {
					EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
					Map<Integer, ItemStack> amulets2 = DCUtil.getAmulets(attacker);
					for (Entry<Integer, ItemStack> entry : amulets2.entrySet()) {
						IJewelAmulet amu = (IJewelAmulet) entry.getValue().getItem();
						add *= amu.increaceDamage(living, entry.getValue());
						if (amu.onAttacking(attacker, living, source, newDam - prev, entry.getValue())) {
							if (DCUtil.isEmpty(amu.consumeCharmItem(entry.getValue()))) {
								attacker.playSound(Blocks.GLASS.getSoundType().getBreakSound(), 1.0F, 0.75F);
							}
						}
					}
					amulets2.clear();
				}
			}

			// 最終的なダメージ
			newDam *= add;
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
