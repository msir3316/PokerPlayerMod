package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.SwiftPotion;

public class BanPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "initializeCardPools")
	public static class ColorlessCardPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon __instance) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && PokerPlayerMod.banContents) {
				AbstractDungeon.colorlessCardPool.group.removeIf(i -> PokerPlayerMod.bannedCards.contains(i.cardID));
			}
		}
	}

	@SpirePatch(clz = PotionHelper.class, method = "initialize")
	public static class PotionPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer.PlayerClass chosenClass) {
			if (chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && PokerPlayerMod.banContents) {
				PotionHelper.potions.remove(SwiftPotion.POTION_ID);
			}
		}
	}
}
