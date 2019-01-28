package TheCardPlayer.patches;

import TheCardPlayer.actions.PlayingCardDiscoveryAction;
import TheCardPlayer.cards.PlayingCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DiscoveryPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "returnTrulyRandomCardInCombat", paramtypez = {})
	public static class FixRandomCard {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result) {
			if (PlayingCardDiscoveryAction.isActive) {
				return new PlayingCard(
						PlayingCardDiscoveryAction.suit == null ?
								PlayingCard.Suit.values()[AbstractDungeon.cardRandomRng.random(3)] :
								PlayingCardDiscoveryAction.suit,
						AbstractDungeon.cardRandomRng.random(PlayingCardDiscoveryAction.min, PlayingCardDiscoveryAction.max));
			} else {
				return __result;
			}
		}
	}
}
