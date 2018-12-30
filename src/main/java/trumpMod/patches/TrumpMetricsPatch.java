package trumpMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.ClassClassPath;
import javassist.ClassPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TrumpMetricsPatch {
//	@SpirePatch(clz = Metrics.class, method = "sendPost", paramtypez = {String.class, String.class})
	public static class SendPostPatch {
//		@SpirePrefixPatch
		public static void Prefix(Metrics __instance, @ByRef String[] url, String fileName) {
			ClassPool.getDefault().insertClassPath(new ClassClassPath(SendPostPatch.class));
			if (AbstractDungeon.player.chosenClass == TheTrumpEnum.THE_TRUMP) {
				url[0] = "http://35.185.169.207:13507/upload";
			}
		}
	}

//	@SpirePatch(clz = DeathScreen.class, method = "shouldUploadMetricData")
	public static class shouldUploadMetricData {
//		@SpirePostfixPatch
		public static boolean Postfix(boolean __retVal) {
			if (AbstractDungeon.player.chosenClass == TheTrumpEnum.THE_TRUMP) {
				__retVal = Settings.UPLOAD_DATA;
			}
			return __retVal;
		}
	}

//	@SpirePatch(clz = Metrics.class, method = "run")
	public static class RunPatch {
//		@SpirePostfixPatch
		public static void Postfix(Metrics __instance) {
			if (__instance.type == Metrics.MetricRequestType.UPLOAD_METRICS && AbstractDungeon.player.chosenClass == TheTrumpEnum.THE_TRUMP) {
				try {
					Method m = Metrics.class.getDeclaredMethod("gatherAllDataAndSend", boolean.class, boolean.class, MonsterGroup.class);
					m.setAccessible(true);
					m.invoke(__instance, __instance.death, __instance.trueVictory, __instance.monsters);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
