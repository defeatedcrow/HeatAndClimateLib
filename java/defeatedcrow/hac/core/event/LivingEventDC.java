package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.damage.ClimateDamageEvent;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageCharmKey;
import defeatedcrow.hac.core.util.DCPotion;
import defeatedcrow.hac.core.util.DCTimeHelper;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
					if (!living.worldObj.isRemote) {
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

			if (!living.worldObj.isRemote) {

				/* Potion */
				boolean f = true;
				if (living instanceof EntityLiving && ((EntityLiving) living).hasCustomName()) {

				} else {
					if (living instanceof IMob) {
						f = false;
					} else if (living.getLowestRidingEntity() != null
							&& living.getLowestRidingEntity() instanceof IMob) {
						f = false;
					} else if (living.getRidingEntity() != null && living.getRidingEntity() instanceof IMob) {
						f = false;
					}
				}

				if (f) {
					// PotionEffectのリスト
					Iterator iterator = living.getActivePotionEffects().iterator();

					while (iterator.hasNext()) {
						PotionEffect effect = (PotionEffect) iterator.next();

						Potion potion = effect.getPotion();

						if (potion != null && potion == DCPotion.jump) {
							living.fallDistance = 0.0F;
						}

						// 騎乗関係のMobにポーション効果を分け与える
						if (living.getRidingEntity() != null && living.getRidingEntity() instanceof EntityLivingBase) {
							EntityLivingBase riding = (EntityLivingBase) event.getEntity().getRidingEntity();
							if (potion != null) {
								riding.addPotionEffect(effect);
							}
						}
					}
					iterator = null;
				}

				/* climate damage */

				if (!living.worldObj.isRemote && DCTimeHelper.getCount(living.worldObj) == 0
						&& CoreConfigDC.climateDam) {
					int px = MathHelper.floor_double(living.posX);
					int py = MathHelper.floor_double(living.posY) + 1;
					int pz = MathHelper.floor_double(living.posZ);
					DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(living.worldObj, new BlockPos(px, py, pz));

					float prev = 2.0F; // normal
					if (living instanceof EntityPlayer) {
						prev = 1.0F * (3 - CoreConfigDC.damageDifficulty); // 1.0F ~ 3.0F
					}
					float dam = Math.abs(heat.getTier()) * 1.0F; // hot 0F ~ 7.0F / cold 0F ~ 4.0F
					boolean isCold = heat.getTier() < 0;

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
					if (living.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && !CoreConfigDC.peacefulDam) {
						dam = 0.0F;
					}

					/* damage判定 */
					// mobごとの特性
					if (isCold) {
						float adj = DamageAPI.resistantData.getColdResistant(living);
						if (adj != 0F) {
							prev += adj;
						}
						if (living.isImmuneToFire()) {
							prev -= 2.0F;
						}
						if (living.isEntityUndead()) {
							prev += 2.0F;
						}
					} else {
						float adj = DamageAPI.resistantData.getHeatResistant(living);
						if (adj != 0F) {
							prev += adj;
						}
						if (living.isPotionActive(DCPotion.fire_reg)) {
							prev += 2.0F;
						}
						if (living.isImmuneToFire()) {
							prev += CoreConfigDC.infernalInferno ? 6.0F : 2.0F;
						} else if (heat.getTier() > DCHeatTier.OVEN.getTier() && living.isEntityUndead()) {
							prev -= 2.0F;
						}
					}

					// 防具の計算
					Iterable<ItemStack> items = living.getArmorInventoryList();
					if (items != null) {
						for (ItemStack item : items) {
							if (!DCUtil.isEmpty(item) && item.getItem() instanceof ItemArmor) {
								ArmorMaterial mat = ((ItemArmor) item.getItem()).getArmorMaterial();
								prev += DamageAPI.armorRegister.getPreventAmount(mat);
								if (!isCold && EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION,
										item) > 0) {
									prev += EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, item)
											* 1.0F;
								}
							}
						}
					}
					items = null;

					dam -= prev;

					DamageSourceClimate source = isCold ? DamageSourceClimate.climateColdDamage
							: DamageSourceClimate.climateHeatDamage;
					ClimateDamageEvent fireEvent = new ClimateDamageEvent(living, source, heat, dam);
					float result = fireEvent.result();
					dam = result;

					// 2.0F未満の場合はとどめを刺さない
					if (dam < 2.0F && living.getHealth() < 1.0F) {
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
			// 装備
			ItemStack[] equip = player.inventory.armorInventory;
			ItemStack[] inside = new ItemStack[9];
			for (int i = 0; i < 9; i++) {
				inside[i] = player.inventory.getStackInSlot(i + 9);
			}

			// charm
			if (!player.worldObj.isRemote) {
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

		if (CoreConfigDC.enableVanilla && (entity instanceof EntityPlayer)) {
			if (count2 > 0) {
				count2--;
				return;
			}
			EntityPlayer player = (EntityPlayer) event.getEntity();
			World world = player.worldObj;
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
							int y = world.provider.getHasNoSky() ? MathHelper.floor_double(player.posY) + 5
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

	/* spawn制御 */
	@SubscribeEvent
	public void spawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if (CoreConfigDC.customizedSpawn && event.getEntityLiving() != null
				&& event.getEntityLiving() instanceof IMob) {
			float i1 = 64F - event.getY();
			int abs = (int) Math.abs(i1);
			if (abs < 20 || event.getWorld().rand.nextInt(64) >= abs) {
				event.setResult(Result.DENY);
			}
		}
	}

	/* dropが消えなくなる */
	@SubscribeEvent
	public void livingDropItemEvent(ItemExpireEvent event) {
		EntityItem item = event.getEntityItem();
		int life = event.getExtraLife();
		if (CoreConfigDC.enableFreezeDrop && item != null && !item.worldObj.isRemote) {
			BlockPos pos = item.getPosition();
			DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(item.worldObj, pos);
			if (heat.getTier() < DCHeatTier.COLD.getTier()) {
				// frostbite以下
				life += 6000;
				event.setExtraLife(life);
				event.setCanceled(true);
			}
		}
	}
}
