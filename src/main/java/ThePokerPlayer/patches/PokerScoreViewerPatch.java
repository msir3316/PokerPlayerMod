package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class PokerScoreViewerPatch {
	@SpirePatch(clz = EnergyPanel.class, method = "update")
	public static class PokerHandViewerUpdatePatch {
		@SpirePrefixPatch
		public static void Prefix(EnergyPanel __instance) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && !ShowdownAction.onAction) {
				PokerPlayerMod.pokerScoreViewer.update();
			}
		}
	}

	@SpirePatch(clz = EnergyPanel.class, method = "renderOrb")
	public static class PokerHandViewerRenderPatch {
		@SpirePrefixPatch
		public static void Prefix(EnergyPanel __instance, SpriteBatch sb) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				PokerPlayerMod.pokerScoreViewer.render(sb);
			}
		}
	}
}
