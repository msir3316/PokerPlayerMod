package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

public class ShopPatch {
	public static final int[] PRICE_RANK = new int[]{0, 10, 20, 30, 40, 50, 70, 90, 110, 130, 150};
	public static final int[] PRICE_SUIT = new int[]{30, 10, 20, 0};

	@SpirePatch(clz = ShopScreen.class, method = "init")
	public static class FixRandomCard {
		@SpirePrefixPatch
		public static void Prefix(ShopScreen __instance, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				coloredCards.set(2, coloredCards.get(1));
				int[] n = new int[]{
						AbstractDungeon.cardRng.random(39),
						AbstractDungeon.cardRng.random(38)
				};
				if (n[1] >= n[0]) {
					n[1]++;
				}
				for (int i = 0; i < 2; i++) {
					PokerCard.Suit suit = PokerCard.Suit.values()[n[i] / 10];
					int num = n[i] % 10 + 1;
					coloredCards.set(i, new PokerCard(suit, num));
				}
			}
		}
	}

	@SpirePatch(clz = ShopScreen.class, method = "initCards")
	public static class PricePatch {
		@SpireInsertPatch(locator = ShopPatch.PriceLocator.class, localvars = {"c"})
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

	@SpirePatch(clz = ShopScreen.class, method = "setPrice")
	public static class CourierPricePatch {
		@SpireInsertPatch(locator = ShopPatch.CourierPriceLocator.class, localvars = {"tmpPrice"})
		public static void Insert(ShopScreen __instance, AbstractCard card, @ByRef float[] tmpPrice) {
			if (card instanceof PokerCard) {
				PokerCard pc = (PokerCard) card;
				tmpPrice[0] = (int) ((PRICE_RANK[pc.rank] + PRICE_SUIT[pc.suit.value]) * AbstractDungeon.merchantRng.random(0.9F, 1.1F));
			}
		}
	}

	private static class CourierPriceLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "color");
			return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "getCardFromPool")
	public static class GetPokerCardFromPool {
		@SpirePrefixPatch
		public static SpireReturn<AbstractCard> Prefix(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean useRng) {
			if (type == CardTypeEnum.POKER) {
				int n = AbstractDungeon.cardRng.random(39);
				return SpireReturn.Return(new PokerCard(PokerCard.Suit.values()[n / 10], n % 10 + 1));
			} else {
				return SpireReturn.Continue();
			}
		}
	}
}
