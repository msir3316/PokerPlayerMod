package ThePokerPlayer.patches;

import ThePokerPlayer.relics.BottledPoker;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BottledPokerPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
	public static class SetStatus {
		@SpirePostfixPatch
		public static void Postfix() {
			if (BottledPoker.selectingInstance != null && BottledPoker.selectingInstance.status == BottledPoker.bottleStatus.GridOpen) {
				BottledPoker.selectingInstance.status = BottledPoker.bottleStatus.GridSelected;
			}
		}
	}
}
