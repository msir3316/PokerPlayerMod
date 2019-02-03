package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class SingleCardViewPatch {
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait")
	public static class SendPostPatch {
		@SpirePostfixPatch
		public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
			AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
			if (card instanceof PokerCard) {
				PokerCard tnc = (PokerCard) card;

				for (int i = 0; i < tnc.num; i++) {
					float scale = Settings.scale * 2;
					float dx = PokerCard.OFFSETS_X[tnc.num - 1][i];
					float dy = PokerCard.OFFSETS_Y[tnc.num - 1][i];
					sb.draw(
							tnc.suit.getImage(),
							(float) Settings.WIDTH / 2.0F - PokerCard.SUIT_WIDTH,
							(float) Settings.HEIGHT / 2.0F - PokerCard.SUIT_HEIGHT + 72.0F * Settings.scale,
							PokerCard.SUIT_WIDTH - dx * 2,
							PokerCard.SUIT_HEIGHT - 72.0f - dy * 2,
							PokerCard.SUIT_WIDTH * 2,
							PokerCard.SUIT_HEIGHT * 2,
							scale,
							scale,
							0.0F,
							0,
							0,
							PokerCard.SUIT_WIDTH,
							PokerCard.SUIT_HEIGHT,
							false,
							false);
				}
			}
		}
	}
}
