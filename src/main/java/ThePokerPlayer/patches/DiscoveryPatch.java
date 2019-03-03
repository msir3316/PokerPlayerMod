package ThePokerPlayer.patches;

import ThePokerPlayer.actions.PokerCardDiscoveryAction;
import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

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

	@SpirePatch(clz = CardRewardScreen.class, method = "discoveryOpen", paramtypez = {})
	public static class DiscoveryOptionCountPatch {
		@SpireInsertPatch(locator = OptionCountLocator.class, localvars = {"derp"})
		public static void Insert(CardRewardScreen __instance, ArrayList derp) {
			if (PokerCardDiscoveryAction.isActive) {
				while (derp.size() < PokerCardDiscoveryAction.choices) {
					boolean dupe = false;
					AbstractCard tmp = AbstractDungeon.returnTrulyRandomCardInCombat();

					for (Object obj : derp) {
						AbstractCard c = (AbstractCard) obj;
						if (c.cardID.equals(tmp.cardID)) {
							dupe = true;
							break;
						}
					}

					if (!dupe) {
						derp.add(tmp.makeCopy());
					}
				}
			}
		}
	}

	private static class OptionCountLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardRewardScreen.class, "rewardGroup");
			final int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0]};
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "placeCards")
	public static class DiscoveryPositionPatch {
		@SpirePrefixPatch
		public static void Prefix(CardRewardScreen __instance, float x, float y) {
			if (PokerCardDiscoveryAction.isActive && __instance.rewardGroup.size() == 5) {
				final float PAD_X = 30.0F * Settings.scale;
				for (int i = 0; i < 5; i++) {
					__instance.rewardGroup.get(i).target_x = Settings.WIDTH / 2.0F + (AbstractCard.IMG_WIDTH + PAD_X) * (i - 2);
					__instance.rewardGroup.get(i).target_y = y;
				}
			}
		}
	}
}
