package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.ArrayList;

public class ShopPatch {
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
}
