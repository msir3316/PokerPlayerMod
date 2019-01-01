package trumpMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import trumpMod.actions.NormalCardDiscoveryAction;
import trumpMod.cards.TrumpNormalCard;

public class DiscoveryPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "returnTrulyRandomCardInCombat", paramtypez = {})
	public static class FixRandomCard {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result) {
			if (NormalCardDiscoveryAction.isActive) {
				return new TrumpNormalCard(
						NormalCardDiscoveryAction.suit == null ?
								TrumpNormalCard.Suit.values()[AbstractDungeon.cardRandomRng.random(3)] :
								NormalCardDiscoveryAction.suit,
						AbstractDungeon.cardRandomRng.random(NormalCardDiscoveryAction.min, NormalCardDiscoveryAction.max));
			} else {
				return __result;
			}
		}
	}
}
