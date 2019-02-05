package ThePokerPlayer.patches;

import ThePokerPlayer.actions.PokerCardDiscoveryAction;
import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DiscoveryPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "returnTrulyRandomCardInCombat", paramtypez = {})
	public static class FixRandomCard {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result) {
			if (PokerCardDiscoveryAction.isActive) {
				return new PokerCard(
						PokerCardDiscoveryAction.suit == null ?
								PokerCard.Suit.values()[AbstractDungeon.cardRandomRng.random(3)] :
								PokerCardDiscoveryAction.suit,
						AbstractDungeon.cardRandomRng.random(PokerCardDiscoveryAction.min, PokerCardDiscoveryAction.max));
			} else {
				return __result;
			}
		}
	}
}
