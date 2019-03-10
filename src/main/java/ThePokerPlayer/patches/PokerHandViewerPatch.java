package ThePokerPlayer.patches;

import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static ThePokerPlayer.cards.PokerCard.SUIT_HEIGHT;
import static ThePokerPlayer.cards.PokerCard.SUIT_WIDTH;

public class PokerHandViewerPatch {
	@SpirePatch(clz = EnergyPanel.class, method = "update")
	public static class PokerHandViewerUpdatePatch {
		@SpirePrefixPatch
		public static void Prefix(EnergyPanel __instance) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && !ShowdownAction.onAction) {
				ShowdownAction.calculateShowdown();
			}
		}
	}

	private static final float DIST = 10.0f;

	@SpirePatch(clz = EnergyPanel.class, method = "renderOrb")
	public static class PokerHandViewerRenderPatch {
		@SpirePrefixPatch
		public static void Prefix(EnergyPanel __instance, SpriteBatch sb) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				for (int i = 0; i < PokerCard.Suit.values().length; i++) {
					float dx = Settings.scale * (50.0f * (i - 1.5f));
					sb.draw(
							PokerCard.Suit.values()[i].getImage(),
							Settings.WIDTH / 2.0f + dx - DIST,
							Settings.HEIGHT * 0.8f,
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
							String.valueOf(ShowdownAction.pow[i]),
							Settings.WIDTH / 2.0f + dx + DIST,
							Settings.HEIGHT * 0.8f,
							Settings.BLUE_TEXT_COLOR);
				}
			}
		}
	}
}
