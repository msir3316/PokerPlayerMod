package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.PokerCardChangeAction;
import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

public class HandCardSelectScreenPatch {
	@SpirePatch(clz = HandCardSelectScreen.class, method = "update")
	public static class ChangeUpgradeCard {
		@SpirePostfixPatch
		public static void Postfix(HandCardSelectScreen __instance) {
			if (PokerCardChangeAction.ref != null) {
				boolean cardChanged = false;
				AbstractCard c = null;
				if (__instance.selectedCards.size() > 0) {
					c = __instance.selectedCards.getTopCard();
				}
				if (c != PokerPlayerMod.cardSelectScreenCard) {
					cardChanged = true;
					PokerPlayerMod.cardSelectScreenCard = c;
				}
				if (c != null) {
					__instance.upgradePreviewCard = c.makeStatEquivalentCopy();
					((PokerCard) __instance.upgradePreviewCard).rankChange(PokerCardChangeAction.ref.rankChange);
				}
			}
		}
	}
}
