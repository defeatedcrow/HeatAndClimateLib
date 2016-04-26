package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// AMT式Potion追加効果
public class LivingPotionEvent {

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		Entity entity = event.entity;

		if ((entity instanceof EntityLivingBase)) {
			EntityLivingBase living = (EntityLivingBase) event.entity;

			ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();

			if (living != null && !living.worldObj.isRemote) {

				boolean f = true;
				if (living instanceof EntityLiving && ((EntityLiving) living).hasCustomName()) {

				} else {
					if (living instanceof IMob) {
						f = false;
					} else if (living.riddenByEntity != null && living.riddenByEntity instanceof IMob) {
						f = false;
					} else if (living.ridingEntity != null && living.ridingEntity instanceof IMob) {
						f = false;
					}
				}

				if (f) {
					// PotionEffectのリスト
					Iterator iterator = living.getActivePotionEffects().iterator();

					while (iterator.hasNext()) {
						PotionEffect effect = (PotionEffect) iterator.next();

						int id = effect.getPotionID();
						Potion potion = Potion.potionTypes[id];

						if (potion != null && potion.id == potion.jump.id) {
							living.fallDistance = 0.0F;
						}

						// 騎乗関係のMobにポーション効果を分け与える
						if (living.ridingEntity != null && living.ridingEntity instanceof EntityLivingBase) {
							EntityLivingBase riding = (EntityLivingBase) event.entity.ridingEntity;
							if (potion != null) {
								riding.addPotionEffect(effect);
							}
						}

					}
				}
			}
		}
	}
}
