package ThePokerPlayer.patches;

import ThePokerPlayer.actions.PokerCardDiscoveryAction;
import ThePokerPlayer.cards.ChoiceCard.BrokenClockChoice;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.relics.BrokenClock;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
								PokerCard.Suit.values()[AbstractDungeon.cardRandomRng.random(2) + 1] :
								PokerCardDiscoveryAction.suit,
						AbstractDungeon.cardRandomRng.random(PokerCardDiscoveryAction.min, PokerCardDiscoveryAction.max),
						PokerCardDiscoveryAction.ethereal);
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
				for (int i = derp.size() - 1; i >= PokerCardDiscoveryAction.choices; i--) {
					derp.remove(i);
				}
				if (AbstractDungeon.player.hasRelic(BrokenClock.ID)) {
					derp.add(new BrokenClockChoice());
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
			if (PokerCardDiscoveryAction.isActive) {
				int len = __instance.rewardGroup.size();
				if (len >= 5) {
					final float PAD_X = (80.0F - len * 10.0f) * Settings.scale;
					for (int i = 0; i < __instance.rewardGroup.size(); i++) {
						__instance.rewardGroup.get(i).target_x = Settings.WIDTH / 2.0F + (AbstractCard.IMG_WIDTH + PAD_X) * (i - (len / 2.0F - 0.5F));
						__instance.rewardGroup.get(i).target_y = y;
					}
				}
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "update")
	public static class DiscoveryHandUpdatePatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance) {
			if (PokerCardDiscoveryAction.isActive) {
				AbstractDungeon.player.hand.update();
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "render")
	public static class DiscoveryHandVisiblePatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance, SpriteBatch sb) {
			if (PokerCardDiscoveryAction.isActive) {
				AbstractDungeon.player.hand.render(sb);
			}
		}
	}
}
