package trumpMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import trumpMod.actions.TrumpNormalCardAction;

public class CardAnglePatch {
	@SpirePatch(clz = AbstractCard.class, method = "update")
	public static class SetAngle {
		@SpirePostfixPatch
		public static void Postfix(AbstractCard __instance) {
			if (TrumpNormalCardAction.onAction) {
				__instance.targetAngle = __instance.angle = 0;
			}
		}
	}
}
