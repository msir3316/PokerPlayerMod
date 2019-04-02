package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.NlothsGift;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

public class PokerCardRewardPatch {
	public static final int[] PRICE_RANK = new int[]{0, 15, 20, 25, 30, 40, 60, 80, 100, 125, 150};
	public static final int[] PRICE_SUIT = new int[]{25, 10, 20, 0};

	public static final int[] RATIO_RANK_NORMAL =
			new int[]{0, 15, 15, 15, 15, 10, 10, 8, 6, 4, 2};
	public static final int[] RATIO_RANK_NORMAL_NLOTH =
			new int[]{0, 13, 13, 13, 13, 10, 10, 10, 8, 6, 4};
	public static final int[] RATIO_RANK_ELITE =
			new int[]{0, 13, 13, 13, 13, 10, 10, 10, 8, 6, 4};
	public static final int[] RATIO_RANK_ELITE_NLOTH =
			new int[]{0, 11, 11, 11, 11, 10, 10, 10, 10, 8, 8};
	public static final int[] RATIO_RANK_BOSS =
			new int[]{0, 0, 0, 0, 0, 0, 0, 25, 25, 25, 25};
	public static final int[] RATIO_RANK_BOSS_NLOTH =
			new int[]{0, 0, 0, 0, 0, 0, 0, 10, 10, 40, 40};
	public static final int[] RATIO_SUIT = new int[]{1, 3, 2, 4};

	@SpirePatch(clz = ShopScreen.class, method = "initCards")
	public static class PricePatch {
		@SpireInsertPatch(locator = PokerCardRewardPatch.PriceLocator.class, localvars = {"c"})
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

	@SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
	public static class RewardPatch {
		@SpirePostfixPatch
		public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				for (int pos = 0; pos < (__result.size() + 1) / 2; pos++) {
					int value = AbstractDungeon.cardRng.random(1999);
					if (pos * 2 + 1 == __result.size() && value < 1000) {
						break;
					}
					int num = value % 1000;
					int suitNum = num % 10;
					int rankNum = num / 10;
					PokerCard.Suit suit = PokerCard.Suit.Diamond;
					int rank;
					for (int i = 0; i < 4; i++) {
						suit = PokerCard.Suit.values()[i];
						if (suitNum < RATIO_SUIT[i]) {
							break;
						}
						suitNum -= RATIO_SUIT[i];
					}

					int[] ratio = AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite ?
							(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_ELITE_NLOTH : RATIO_RANK_ELITE) :
							AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss ?
									(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_BOSS_NLOTH : RATIO_RANK_BOSS) :
									(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_NORMAL_NLOTH : RATIO_RANK_NORMAL);
					for (rank = 1; rank <= 10; rank++) {
						if (rankNum < ratio[rank]) {
							break;
						}
						rankNum -= ratio[rank];
					}
					boolean dup = false;
					for (int i = 0; i < pos; i++) {
						AbstractCard c = __result.get(i);
						if (c instanceof PokerCard) {
							PokerCard pc = (PokerCard) c;
							if (pc.suit == suit && pc.rank == rank) {
								dup = true;
								break;
							}
						}
					}
					if (dup) {
						pos--;
					} else {
						__result.set(pos, new PokerCard(suit, rank));
					}
				}
			}
			return __result;
		}
	}
}
