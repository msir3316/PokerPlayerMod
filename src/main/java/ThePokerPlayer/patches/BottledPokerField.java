package ThePokerPlayer.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
		clz = AbstractCard.class,
		method = SpirePatch.CLASS
)
public class BottledPokerField {
	public static SpireField<Boolean> inBottledPoker = new SpireField<>(() -> false);

	@SpirePatch(
			clz = AbstractCard.class,
			method = "makeSameInstanceOf"
	)
	public static class MakeSameInstanceOf {
		public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
			inBottledPoker.set(__result, inBottledPoker.get(__instance));
			return __result;
		}
	}

	@SpirePatch(
			clz = CardGroup.class,
			method = "initializeDeck"
	)
	public static class InitDeck {
		@SpireInsertPatch(
				locator = Locator.class,
				localvars = {"copy", "placeOnTop"}
		)
		public static void Insert(CardGroup __instance, CardGroup masterDeck, CardGroup copy, ArrayList<AbstractCard> placeOnTop) {
			for (AbstractCard c : copy.group) {
				if (inBottledPoker.get(c)) {
					__instance.removeCard(c);
					placeOnTop.add(c);
				}
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "iterator");

				return new int[]{LineFinder.findAllInOrder(ctBehavior, finalMatcher)[1]};
			}
		}
	}
}