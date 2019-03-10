package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

public class PokerCardPricePatch {
	public static final int[] PRICE_RANK = new int[]{0, 15, 20, 25, 30, 40, 60, 80, 100, 125, 150};
	public static final int[] PRICE_SUIT = new int[]{25, 10, 20, 0};

	@SpirePatch(clz = ShopScreen.class, method = "initCards")
	public static class PricePatch {
		@SpireInsertPatch(locator = PokerCardPricePatch.PriceLocator.class, localvars = {"c"})
		public static void Insert(ShopScreen __instance, AbstractCard c) {
			if (c instanceof PokerCard) {
				PokerCard pc = (PokerCard) c;
				c.price = (int) ((PRICE_RANK[pc.rank] + PRICE_SUIT[pc.suit.value]) * AbstractDungeon.merchantRng.random(0.9F, 1.1F));
			}
		}
	}

	private static class PriceLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "current_x");
			return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
