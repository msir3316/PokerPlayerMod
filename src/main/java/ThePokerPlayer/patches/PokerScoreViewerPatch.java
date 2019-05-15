package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class PokerScoreViewerPatch {
	@SpirePatch(clz = AbstractPlayer.class, method = "combatUpdate")
	public static class PokerHandViewerUpdatePatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer __instance) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && !ShowdownAction.onAction) {
				PokerPlayerMod.pokerScoreViewer.update();
			}
		}
	}

	@SpirePatch(clz = EnergyPanel.class, method = "renderOrb")
	public static class PokerHandViewerRenderPatch {
		@SpirePostfixPatch
		public static void Postfix(EnergyPanel __instance, SpriteBatch sb) {
			if (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				PokerPlayerMod.pokerScoreViewer.render(sb);
			}
		}
	}
}
