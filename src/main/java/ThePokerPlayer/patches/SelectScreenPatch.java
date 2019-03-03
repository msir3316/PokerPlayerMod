package ThePokerPlayer.patches;

import ThePokerPlayer.relics.ProtectiveDeckHolder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import javassist.CtBehavior;

public class SelectScreenPatch {
	@SpirePatch(clz = CharacterOption.class, method = "renderRelics")
	public static class CharSelectRelicPatch {
		@SpireInsertPatch(locator = Locator.class, localvars = {"charInfo", "relicString"})
		public static void Insert(CharacterOption __instance, SpriteBatch sb, CharSelectInfo charInfo, @ByRef String[] relicString) {
			if (charInfo.relics.get(0).equals(ProtectiveDeckHolder.ID)) {
				relicString[0] = RelicLibrary.getRelic(charInfo.relics.get(0)).DESCRIPTIONS[1];
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "tipBodyFont");
			return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
