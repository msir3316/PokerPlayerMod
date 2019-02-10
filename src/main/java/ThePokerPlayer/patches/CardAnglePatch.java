package ThePokerPlayer.patches;

import ThePokerPlayer.actions.ShowdownAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CardAnglePatch {
	@SpirePatch(clz = AbstractCard.class, method = "update")
	public static class SetAngle {
		@SpirePostfixPatch
		public static void Postfix(AbstractCard __instance) {
			if (ShowdownAction.onAction) {
				__instance.unhover();
				__instance.drawScale = __instance.targetDrawScale = 0.75f;
				__instance.targetAngle = __instance.angle = 0;
			}
		}
	}
}
