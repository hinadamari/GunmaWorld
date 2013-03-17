package hinadamari.gunmaworld;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GunmaWorld イベントリスナ
 * @author hinadamari
 */
public class GunmaWorldEventListener extends JavaPlugin implements Listener
{
    public GunmaWorld plugin;
    public final static Logger log = Logger.getLogger("Minecraft");
    StormThread thread = null;
    List<String> gunmmer = new ArrayList<String>();

    public GunmaWorldEventListener(GunmaWorld instance)
    {
        plugin = instance;
    }

    /**
     * StormThread開始処理
     * @param player
     */
    private void startStormThread(Player player) {
    	// 天候を雷雨に
    	if (gunmmer.contains(player.getName())) gunmmer.add(player.getName());
    	if (thread == null){
        	thread = new StormThread(player.getWorld());
        	thread.runTaskTimer(this, 0, 20);
    	}
    }

    /**
     * StormThread停止処理
     * @param player
     */
    private void stopStormThread(Player player) {
    	// 誰も居ない場合、天候をもとに戻す
		if (gunmmer.contains(player.getName())) gunmmer.remove(player.getName());
		if (gunmmer.size() == 0 && thread != null){
			thread.cancel();
			thread = null;
		}
    }

    /**
     * プレイヤーがログインした時の処理
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {

    	Player player = event.getPlayer();

    	if (GunmaWorld.getConfigLocation().getWorld().getName() != player.getWorld().getName()
    			|| player.getLocation().distance(GunmaWorld.getConfigLocation()) > GunmaWorld.getConfigRange() + 10) {
    		stopStormThread(player);
    	} else {
    		startStormThread(player);
    	}
    }

    /**
     * プレイヤーがログアウトした時の処理
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	stopStormThread(event.getPlayer());
    }

    /**
     * プレイヤーが圏内に入った時の処理
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {

    	Player player = event.getPlayer();

    	if (GunmaWorld.getConfigLocation().getWorld().getName() != event.getTo().getWorld().getName()
    			|| event.getTo().distance(GunmaWorld.getConfigLocation()) > GunmaWorld.getConfigRange() + 10) {
    		stopStormThread(player);
    	} else {
    		startStormThread(player);
    	}

    	//if (player.getGameMode() == GameMode.CREATIVE) return;

        // 一定範囲のオオカミのターゲットを指定
        /*List<Entity> mobs = player.getNearbyEntities(32, 16, 32);

        for(Entity mob : mobs) {
        	if (mob instanceof Wolf && !((Wolf) mob).isTamed() && ((Wolf) mob).isAngry()) {
        		Wolf wolf = (Wolf) mob;
        		if (wolf.getTarget() == null) {
        			wolf.damage(0, player);
        		}
        	}
        }*/
    }

    /**
     * MOBがスポーンした時の処理
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onMobSpawn(CreatureSpawnEvent event) {

    	if (event.getSpawnReason() == SpawnReason.CUSTOM) return;

    	Entity mob = event.getEntity();

    	switch(event.getEntityType()) {
    		case ZOMBIE:
    			Zombie zom = (Zombie) mob;
    			if (zom.getEquipment().getHelmet().getType() == Material.AIR) {
    				if (Math.random() >= 0.99) {
    	    			Short damage = 3;
    	    			zom.setVillager(false);
    	    			zom.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM, 1, damage));
    	    			zom.getEquipment().setHelmetDropChance(1F);
    	    		}
    			}
    			break;
    	}

    	if (GunmaWorld.getConfigLocation().getWorld().getName() != mob.getLocation().getWorld().getName()
    			|| mob.getLocation().distance(GunmaWorld.getConfigLocation()) > GunmaWorld.getConfigRange()) return;

    	switch(event.getEntityType()) {
    	    case CREEPER:
    	    	Creeper cre = (Creeper) mob;
    	    	cre.setPowered(true);
	    		break;
    	    case SPIDER:
    	    	if (Math.random() >= 0.7) {
    	    		event.setCancelled(true);
    	    		if (mob.getLocation().getY() > 60) {
    	    			/*Wolf alt = (Wolf) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.WOLF);
            	    	alt.setAngry(true);
            	    	alt.setMaxHealth(20);
            	    	alt.setHealth(20);*/
    	    			Silverfish fish = (Silverfish) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.SILVERFISH);
            	    	fish.setMaxHealth(16);
            	    	fish.setHealth(16);
            	    	Silverfish fish2 = (Silverfish) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.SILVERFISH);
            	    	fish2.setMaxHealth(16);
            	    	fish2.setHealth(16);
                	} else {
    	    			mob.getWorld().spawnEntity(mob.getLocation(), EntityType.CAVE_SPIDER);
    	    		}
    	    	} else {
    	    		Spider spi = (Spider) mob;
    	    		Entity rider;
    	    		if (Math.random() >= 0.99) {
    	    			rider = mob.getWorld().spawnEntity(mob.getLocation(), EntityType.CREEPER);
    	    		} else {
    	    			rider = mob.getWorld().spawnEntity(mob.getLocation(), EntityType.SKELETON);
    	    			rider.getLocation().zero();
    	    			((Skeleton) rider).getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
    	    			((Skeleton) rider).getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
    	    			if (mob.getLocation().getY() < 35 && Math.random() >= 0.7)
    	    				((Skeleton) rider).setSkeletonType(SkeletonType.WITHER);
    	    		}
    	    		spi.setPassenger(rider);
    	    	}
    	    	break;
    	    case ZOMBIE:
    	    	Zombie zom = (Zombie) mob;
    	    	if (zom.getEquipment().getHelmet().getType() == Material.AIR)
    	    		zom.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
    	    	zom.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD, 1));
    	    	break;
    	    case SQUID:
    	    	if (Math.random() >= 0.97) {
    	    		Location loc = mob.getLocation();
    	    		loc.setY(loc.getY() + 30);
    	    		for (Player p : mob.getWorld().getPlayers()) {
    	    			if (p.getLocation().distance(loc) < 32) return;
    	    		}
        	    	mob.getWorld().spawnEntity(loc, EntityType.GHAST);
    	    	}
    	    	break;
    	    case SKELETON:
    	    	Skeleton ske = (Skeleton) mob;
    	    	if (Math.random() >= 0.7) {
    	    		if (ske.getEquipment().getHelmet().getType() == Material.AIR)
    	    			ske.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
    	    	} else {
    	    		event.setCancelled(true);
    	    		Skeleton ske2 = (Skeleton) mob.getWorld().spawnEntity(mob.getLocation(), EntityType.SKELETON);
    	    		ske2.setSkeletonType(SkeletonType.WITHER);
    	    		if (mob.getLocation().getY() > 50 || Math.random() >= 0.3) {
    	    			ske2.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD, 1));
    	    		} else {
    	    			ske2.getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
    	    		}
        	    }
    	    	break;
    	}
    }

    /**
     * 敵死亡時処理
     * @param event
     */
    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {

    	if (GunmaWorld.getConfigLocation().getWorld().getName() != event.getEntity().getLocation().getWorld().getName()) return;

        switch (event.getEntity().getType()) {
            case SILVERFISH:
            	if (event.getEntity().getMaxHealth() > 10){
        			event.getDrops().add(new ItemStack(Material.STONE,(int) Math.ceil(Math.random() * 2)));
            	}
    			break;
        }

        EntityDamageEvent cause = event.getEntity().getLastDamageCause();

        if (cause == null) return;

        // プレイヤーに倒された時
        if (cause instanceof EntityDamageByEntityEvent) {

            Entity killer = ((EntityDamageByEntityEvent) cause).getDamager();

            Short damage;

            if (killer instanceof Wolf && ((Wolf) killer).isTamed()
            		|| killer instanceof Projectile && ((Projectile) killer).getShooter() instanceof Player
            		|| killer instanceof Player){

            	switch (event.getEntity().getType()) {
            		case CREEPER:
            			if (!((Creeper) event.getEntity()).isPowered()) return;
            			if (Math.random() >= 0.9 ||
            					event.getEntity().getLocation().distance(GunmaWorld.getConfigLocation()) > GunmaWorld.getConfigRange() + 10) {
            				damage = 4;
                            event.getDrops().add(new ItemStack(Material.SKULL_ITEM, 1, damage));
            			}
                        break;
            		case SILVERFISH:
            			if (event.getEntity().getMaxHealth() > 10){
                			if (Math.random() >= 0.9) {
                				damage = ((Double) Math.floor(Math.random() * 3)).shortValue();
                    			event.getDrops().add(new ItemStack(Material.MONSTER_EGGS, 1, damage));
                			}
            			}
                        break;
            		/*case WOLF:
            			if (event.getEntity().getMaxHealth() < 15) return;
            			ItemStack drop2 = new ItemStack(Material.RAW_BEEF,(int) Math.ceil(Math.random() * 3));
                        event.getDrops().add(drop2);
                        break;*/
            	}

            	if (event.getEntity().getLocation().distance(GunmaWorld.getConfigLocation()) < GunmaWorld.getConfigRange() + 10) {
            		event.setDroppedExp(event.getDroppedExp() * 10);
            		switch (event.getEntity().getType()) {
        		    	case ZOMBIE:
        		    		if (Math.random() >= 0.97) {
        		    			damage = 2;
        		    			event.getDrops().add(new ItemStack(Material.SKULL_ITEM, 1, damage));
        		    		}
        		    		break;
        		    	case SKELETON:
        		    		if (Math.random() >= 0.97) {
        		    			damage = 0;
        		    			event.getDrops().add(new ItemStack(Material.SKULL_ITEM, 1, damage));
        		    		}
        		    		break;
            		}
            	}
            }
        }
    }

    /**
     * 雑草を剣で刈れないようにします
     * (おまけでスポーンブロックを破壊不可能に)
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {

    	Block block = event.getBlock();
    	Player player = event.getPlayer();

    	if (GunmaWorld.getConfigLocation().getWorld().getName() != block.getWorld().getName()
    			|| block.getLocation().distance(GunmaWorld.getConfigLocation()) > GunmaWorld.getConfigRange()) return;

    	if (block.getType() == Material.RED_ROSE || block.getType() == Material.LONG_GRASS) {
    		if (player.getItemInHand().getType() == Material.WOOD_SWORD ||
    				player.getItemInHand().getType() == Material.STONE_SWORD ||
    				player.getItemInHand().getType() == Material.IRON_SWORD ||
    				player.getItemInHand().getType() == Material.GOLD_SWORD ||
    				player.getItemInHand().getType() == Material.DIAMOND_SWORD) {
    			event.setCancelled(true);
    		}
    	} else if (block.getType() == Material.MOB_SPAWNER) {
    		event.setCancelled(true);
    	}
    }

}