package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.PokerCardChangeAction;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.Gdx;
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
					if (PokerCardChangeAction.ref.rankChange > 0) {
						__instance.upgradePreviewCard = c.makeStatEquivalentCopy();
						((PokerCard) __instance.upgradePreviewCard).rankChange(PokerCardChangeAction.ref.rankChange, false);
					} else {
						PokerPlayerMod.transformAnimTimer -= Gdx.graphics.getDeltaTime();
						if (PokerPlayerMod.transformAnimTimer < 0.0F || cardChanged) {
							__instance.upgradePreviewCard = c.makeStatEquivalentCopy();
							((PokerCard) __instance.upgradePreviewCard).rankChange(0, false);
							__instance.upgradePreviewCard.update();
							PokerPlayerMod.transformAnimTimer = 0.1F;
						}
					}
				}
			}
		}
	}
}
