package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class PokerEnergyPatch {
	@SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
	public static class RenderEnergyPrefix {
		@SpirePrefixPatch
		public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
			if (__instance instanceof PokerCard) {
				__instance.type = AbstractCard.CardType.SKILL;
			}
		}
	}

	@SpirePatch(clz = AbstractCard.class, method = "renderEnergy")
	public static class RenderEnergyPostfix {
		@SpirePostfixPatch
		public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
			if (__instance instanceof PokerCard) {
				__instance.type = CardTypeEnum.POKER;
			}
		}
	}
}
