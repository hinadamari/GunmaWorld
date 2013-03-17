package hinadamari.gunmaworld;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * GunmaWorld 天候スレッド
 * @author hinadamari
 * 天候を雷雨にします
 */
public class StormThread extends BukkitRunnable {

	private World world;
	public StormThread(World w){
		world = w;
	}

	@Override
	public void run() {
		// 天候を一時雷雨に
		world.setStorm(true);
    	world.setThundering(true);
        world.setWeatherDuration(25);
	}

}
