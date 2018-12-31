package trumpMod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import trumpMod.cards.TrumpNormalCard;

public class SingleCardViewPatch {
	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait")
	public static class SendPostPatch {
		@SpirePostfixPatch
		public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
			AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
			if (card instanceof TrumpNormalCard) {
				TrumpNormalCard tnc = (TrumpNormalCard) card;

				for (int i = 0; i < tnc.num; i++) {
					float scale = Settings.scale * 2;
					float dx = TrumpNormalCard.OFFSETS_X[tnc.num - 1][i];
					float dy = TrumpNormalCard.OFFSETS_Y[tnc.num - 1][i];
					sb.draw(
							tnc.suit.getImage(),
							(float) Settings.WIDTH / 2.0F - TrumpNormalCard.SUIT_WIDTH,
							(float) Settings.HEIGHT / 2.0F - TrumpNormalCard.SUIT_HEIGHT + 72.0F * Settings.scale,
							TrumpNormalCard.SUIT_WIDTH - dx * 2,
							TrumpNormalCard.SUIT_HEIGHT - 72.0f - dy * 2,
							TrumpNormalCard.SUIT_WIDTH * 2,
							TrumpNormalCard.SUIT_HEIGHT * 2,
							scale,
							scale,
							0.0F,
							0,
							0,
							TrumpNormalCard.SUIT_WIDTH,
							TrumpNormalCard.SUIT_HEIGHT,
							false,
							false);
				}
			}
		}
	}
}
