package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.damage.ClimateDamageEvent;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.IJewelAmulet;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.DCInit;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageCharmKey;
import defeatedcrow.hac.core.util.DCTimeHelper;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

// 常時監視系
public class LivingEventDC {

	// ログイン直後は動かない
	private static int count = 5;

	@SubscribeEvent
	public void onEvent(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living != null) {

			if (count == 0) {
				if (living instanceof EntityPlayer) {
					this.onPlayerUpdate(event);
					if (!living.world.isRemote) {
						this.playerChunkUpdate(event);
					} else {
						this.onPlayerKeyUpdate(event);
					}
				}
				this.onLivingUpdate(event);
			} else {
				count--;
			}
		}
	}

	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();

		/* Potion */
		ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();

		if (living != null) {

			if (!living.world.isRemote) {

				/* Potion */
				boolean f = false;
				if (living.isRiding()) {
					f = true;
					if (living instanceof EntityLiving && ((EntityLiving) living).hasCustomName()) {

					} else {
						if (living instanceof IMob) {
							f = false;
						} else if (living.getRidingEntity() != null && living.getRidingEntity() instanceof IMob) {
							f = false;
						}
					}
				}

				if (f) {
					// PotionEffectのリスト
					if (!living.getActivePotionEffects().isEmpty()) {
						Iterator iterator = living.getActivePotionEffects().iterator();

						while (iterator.hasNext()) {
							PotionEffect effect = (PotionEffect) iterator.next();

							Potion potion = effect.getPotion();

							if (potion != null && potion == MobEffects.JUMP_BOOST) {
								living.fallDistance = 0.0F;
							}

							// 騎乗関係のMobにポーション効果を分け与える
							if (living.getRidingEntity() != null
									&& living.getRidingEntity() instanceof EntityLivingBase) {
								EntityLivingBase riding = (EntityLivingBase) event.getEntity().getRidingEntity();
								if (potion != null) {
									riding.addPotionEffect(effect);
								}
							}
						}
						iterator = null;
					}
				}

				/* Amulet */

				Map<Integer, ItemStack> charms = DCUtil.getAmulets(living);
				for (ItemStack item2 : charms.values()) {
					int m = item2.getMetadata();
					IJewelAmulet amu = (IJewelAmulet) item2.getItem();
					amu.constantEffect(living, item2);
				}
				charms.clear();

				/* climate damage */

				if (!living.world.isRemote && DCTimeHelper.getCount(living.world) == 0 && CoreConfigDC.climateDam) {
					int px = MathHelper.floor(living.posX);
					int py = MathHelper.floor(living.posY) + 1;
					int pz = MathHelper.floor(living.posZ);
					DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(living.world, new BlockPos(px, py, pz));

					float prev = 2.0F; // normal
					if (living instanceof EntityPlayer) {
						prev = 1.0F * (3 - CoreConfigDC.damageDifficulty); // 1.0F ~ 3.0F
					}
					float dam = Math.abs(heat.getTier()) * 1.0F; // hot 0F ~ 8.0F / cold 0F ~ 10.0F
					boolean isCold = heat.getTier() < 0;
					DamageSourceClimate source = isCold ? DamageSourceClimate.climateColdDamage
							: DamageSourceClimate.climateHeatDamage;

					// 基礎ダメージ
					if (isCold) {
						dam -= prev;
						dam *= 2.0F;
					} else {
						dam -= prev;
					}

					// 次に装備と耐性計算
					prev = 0.0F;

					// ピースフルではダメージがない
					if (living.world.getDifficulty() == EnumDifficulty.PEACEFUL && !CoreConfigDC.peacefulDam) {
						dam = 0.0F;
					}

					/* damage判定 */
					// mobごとの特性
					if (isCold) {
						float adj = DamageAPI.resistantData.getColdResistant(living);
						prev += adj;
						if (living.isPotionActive(DCInit.prevFreeze)) {
							prev += 4.0F;
						}
						if (living.isImmuneToFire()) {
							prev -= 2.0F;
						}
						if (living.isEntityUndead()) {
							prev += 2.0F;
						}
					} else {
						float adj = DamageAPI.resistantData.getHeatResistant(living);
						prev += adj;
						if (living.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
							prev += 4.0F;
						}
						if (living.isImmuneToFire()) {
							prev += CoreConfigDC.infernalInferno ? 8.0F : 4.0F;
						} else if (heat.getTier() > DCHeatTier.OVEN.getTier() && living.isEntityUndead()) {
							prev /= 2.0F;
						}
					}

					// 防具の計算
					if (living.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
						IItemHandler handler = living.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
								EnumFacing.NORTH);
						if (handler != null) {
							for (int s = 0; s < handler.getSlots(); s++) {
								ItemStack item = handler.getStackInSlot(s);
								if (DCUtil.isEmpty(item))
									continue;

								float p = DCUtil.getItemResistantData(item, isCold);
								prev += p;
							}
						}
					}

					dam -= prev;

					ClimateDamageEvent fireEvent = new ClimateDamageEvent(living, source, heat, dam);
					float result = fireEvent.result();
					dam = result;

					// 2.0F未満の場合はとどめを刺さない
					if (dam < 2.0F && living.getHealth() < 2.0F) {
						dam = 0.0F;
					}

					if (dam >= 1.0F) {
						living.attackEntityFrom(source, dam);
					}
				}
			}
		}
	}

	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// charm
			if (!player.world.isRemote) {
				Map<Integer, ItemStack> charms = DCUtil.getPlayerCharm(player, null);
				for (ItemStack item2 : charms.values()) {
					int m = item2.getMetadata();
					IJewelCharm jew = (IJewelCharm) item2.getItem();
					jew.constantEffect(player, item2);
				}
				charms.clear();
			}
		}
	}

	private boolean x_key = false;

	@SideOnly(Side.CLIENT)
	public void onPlayerKeyUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (ClimateCore.proxy.isWarpKeyDown()) {
				if (!x_key) {
					x_key = true;
					HaCPacket.INSTANCE.sendToServer(new MessageCharmKey((byte) 1));
				}
			} else {
				x_key = false;
			}
		}
	}

	private int localCount = 0;
	private int count2 = 20;

	// Block Update をプレイヤーに肩代わりさせる
	public void playerChunkUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if (CoreConfigDC.enableVanilla) {
			if (count2 > 0) {
				count2--;
				return;
			}
			EntityPlayer player = (EntityPlayer) event.getEntity();
			World world = player.world;
			int count = DCTimeHelper.getCount2(world);

			int tick = count & 255;

			if (tick != localCount) {
				localCount = tick;
				// DCLogger.debugLog("update tick" + tick);
				// 3x3回やる
				int i = 0;
				while (i < 3) {
					int cx = player.chunkCoordX - 4 + world.rand.nextInt(9);
					int cz = player.chunkCoordZ - 4 + world.rand.nextInt(9);
					if (world.getChunkFromChunkCoords(cx, cz).isLoaded()) {
						int j = 0;
						while (j < 3) {
							int x = (cx << 4) + world.rand.nextInt(16);
							int z = (cz << 4) + world.rand.nextInt(16);
							int y = world.provider.hasSkyLight() ? MathHelper.floor(player.posY) + 5
									: world.provider.getActualHeight();
							for (int y1 = y; y1 > 1; y1--) {
								BlockPos pos = new BlockPos(x, y1, z);
								if (world.isAirBlock(pos) || world.getBlockState(pos).getMaterial().isLiquid()) {
									continue;
								}
								// 表面のみ
								IBlockState state = world.getBlockState(pos);
								Block block = state.getBlock();
								world.scheduleUpdate(pos, block, 200);
								break;
							}
							j++;
						}
					}
					i++;
				}

			}
		}
	}

	/* dropが消えなくなる */
	@SubscribeEvent
	public void livingDropItemEvent(ItemExpireEvent event) {
		EntityItem item = event.getEntityItem();
		int life = event.getExtraLife();
		if (item != null && !item.world.isRemote) {
			BlockPos pos = item.getPosition();
			IClimate clm = ClimateAPI.calculator.getClimate(item.world, pos);
			if (CoreConfigDC.enableFreezeDrop && clm.getHeat().getTier() < DCHeatTier.COLD.getTier()) {
				// frostbite以下
				life += 6000;
				event.setExtraLife(life);
				event.setCanceled(true);
			}
		}
	}
}
