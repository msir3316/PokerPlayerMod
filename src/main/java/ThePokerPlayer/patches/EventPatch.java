package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.events.BackToBasicsPoker;
import ThePokerPlayer.events.ExordiumClub;
import ThePokerPlayer.events.VampiresPoker;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.EventHelper;


public class EventPatch {
	@SpirePatch(clz = EventHelper.class, method = "getEvent")
	public static class GetEvent {
		public static AbstractEvent Postfix(AbstractEvent __result, String key) {
			if (key.equals(ExordiumClub.ID)) {
				return new ExordiumClub();
			} else if (key.equals(BackToBasicsPoker.ID)) {
				return new BackToBasicsPoker();
			} else if (key.equals(VampiresPoker.ID)) {
				return new VampiresPoker();
			} else {
				return __result;
			}
		}
	}

	@SpirePatch(clz = Exordium.class, method = "initializeEventList")
	public static class ExordiumEventPatch {
		@SpirePostfixPatch
		public static void Postfix(Exordium __instance) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER || PokerPlayerMod.exordiumAll) {
				AbstractDungeon.eventList.add(ExordiumClub.ID);
			}
		}
	}

	@SpirePatch(clz = TheCity.class, method = "initializeEventList")
	public static class CityEventPatch {
		@SpirePostfixPatch
		public static void Postfix(TheCity __instance) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				AbstractDungeon.eventList.remove(BackToBasics.ID);
				AbstractDungeon.eventList.remove(Vampires.ID);
				AbstractDungeon.eventList.add(BackToBasicsPoker.ID);
				AbstractDungeon.eventList.add(VampiresPoker.ID);
			}
		}
	}
}
