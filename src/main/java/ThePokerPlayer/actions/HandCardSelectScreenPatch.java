package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;

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
					if (PokerCardChangeAction.ref.amount != 0) {
						((PokerCard) __instance.upgradePreviewCard).rankChange(PokerCardChangeAction.ref.amount, false);
					} else {
						PokerPlayerMod.transformAnimTimer -= Gdx.graphics.getDeltaTime();
						if (PokerPlayerMod.transformAnimTimer < 0.0F || cardChanged) {
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
