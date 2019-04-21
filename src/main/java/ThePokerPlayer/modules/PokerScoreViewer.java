package ThePokerPlayer.modules;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import static ThePokerPlayer.cards.PokerCard.SUIT_HEIGHT;
import static ThePokerPlayer.cards.PokerCard.SUIT_WIDTH;

public class PokerScoreViewer {
	private Hitbox hb;
	private static UIStrings uiStrings = null;
	public static String[] TEXT = null;

	private static final int width = 240;
	private static final int height = 100;

	// dragging
	private int moveState = 0;
	private float dx;
	private float dy;
	private float startx;
	private float starty;

	public PokerScoreViewer() {
		hb = new Hitbox(width * Settings.scale, height * Settings.scale);
		hb.move(Settings.WIDTH / 2.0f, Settings.HEIGHT * 0.79f);
		uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerScoreViewer"));
		TEXT = uiStrings.TEXT;
	}

	public void update() {
		ShowdownAction.calculateShowdown();

		hb.update();

		// dragging
		if (InputHelper.justClickedLeft) {
			if (hb.hovered) {
				dx = hb.cX - InputHelper.mX;
				dy = hb.cY - InputHelper.mY;
				moveState = 1;
				startx = InputHelper.mX;
				starty = InputHelper.mY;
			}
		}

		if (moveState > 0) {
			if (InputHelper.justReleasedClickLeft) {
				moveState = 0;
			} else {
				float x = Math.min(Math.max(InputHelper.mX + dx, 0.05f * Settings.WIDTH), 0.95f * Settings.WIDTH);
				float y = Math.min(Math.max(InputHelper.mY + dy, 0.3f * Settings.HEIGHT), 0.85f * Settings.HEIGHT);

				if ((startx - InputHelper.mX) * (startx - InputHelper.mX) + (starty - InputHelper.mY) * (starty - InputHelper.mY) > 64) {
					moveState = 2;
				}

				if (moveState == 2) {
					hb.move(x, y);
				}
			}
		}
	}

	private static final float DIST = Settings.scale * 10.0f;
	private static final float HARDEN_DIST = Settings.scale * 30.0f;

	public void render(SpriteBatch sb) {

		for (int i = 0; i < PokerCard.Suit.values().length; i++) {
			float dx = Settings.scale * (50.0f * (i - 1.5f));
			sb.draw(
					PokerCard.Suit.values()[i].getImage(),
					hb.cX + dx - DIST,
					hb.cY,
					SUIT_WIDTH / 2.0f,
					SUIT_HEIGHT / 2.0f,
					SUIT_WIDTH,
					SUIT_HEIGHT,
					Settings.scale * 2,
					Settings.scale * 2,
					0, 0, 0, SUIT_WIDTH, SUIT_HEIGHT, false, false);
			FontHelper.renderFontCentered(
					sb,
					FontHelper.topPanelAmountFont,
					String.valueOf(ShowdownAction.powView[i]),
					hb.cX + dx + DIST,
					hb.cY,
					Settings.BLUE_TEXT_COLOR);
		}

		if (ShowdownAction.hardenCount > 0) {
			float dx = Settings.scale * (50.0f * (1 - 1.5f));
			FontHelper.renderFontCentered(
					sb,
					FontHelper.powerAmountFont,
					"+" + ShowdownAction.hardenCount,
					hb.cX + dx + DIST,
					hb.cY + HARDEN_DIST,
					Settings.GREEN_TEXT_COLOR);
		}

		if (this.hb.hovered) {
			TipHelper.renderGenericTip((float) InputHelper.mX + 50.0F * Settings.scale, (float) InputHelper.mY, TEXT[0], getTipBody());
		}

		hb.render(sb);
	}

	public String getTipBody() {
		String result = TEXT[1] + " NL " + TEXT[2];

		for (int i = 1; i <= ShowdownAction.FIVE_CARD; i++) {
			if (i == ShowdownAction.hand) {
				result += " NL " + highlightedText(ShowdownAction.TEXT[i]) + " : #b+" + ShowdownAction.modifierByHand(i) + "%";
			} else {
				result += " NL " + ShowdownAction.TEXT[i] + " : #b+" + ShowdownAction.modifierByHand(i) + "%";
			}
		}

		if (ShowdownAction.flush) {
			result += " NL" + highlightedText(ShowdownAction.TEXT[ShowdownAction.FLUSH]) + " : #b+" + ShowdownAction.rawModifierBonus(ShowdownAction.FLUSH) + "%";
		} else {
			result += " NL" + ShowdownAction.TEXT[ShowdownAction.FLUSH] + " : #b+" + ShowdownAction.rawModifierBonus(ShowdownAction.FLUSH) + "%";
		}
		return result;
	}

	public String highlightedText(String text) {
		return text.replaceAll("(?<=\\s|^)(?=\\S)", "#y");
	}
}
