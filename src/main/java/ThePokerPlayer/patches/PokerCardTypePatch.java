package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.BaseMod;
import basemod.helpers.SuperclassFinder;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderFixSwitches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PokerCardTypePatch {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerCardType"));
	public static final String[] TEXT = uiStrings.TEXT;

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderFrame")
	public static class CardFramePatch {
		@SpireInsertPatch(locator = CardFrameLocator.class, localvars = {"card", "tmpImg", "tOffset", "tWidth"})
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard card, @ByRef Texture[] tmpImg, @ByRef float[] tOffset, @ByRef float[] tWidth) {
			if (card.type == CardTypeEnum.POKER) {
				tWidth[0] = PokerCard.typeWidthPoker;
				tOffset[0] = PokerCard.typeOffsetPoker;
				switch (card.rarity) {
					case COMMON:
						tmpImg[0] = ImageMaster.CARD_FRAME_SKILL_COMMON_L;
						break;
					case UNCOMMON:
						tmpImg[0] = ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L;
						break;
					case RARE:
						tmpImg[0] = ImageMaster.CARD_FRAME_SKILL_RARE_L;
						break;
					default:
						tmpImg[0] = ImageMaster.CARD_FRAME_SKILL_COMMON_L;
						break;
				}
			}
		}
	}

	private static class CardFrameLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "draw");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardTypeText")
	public static class CardTypeTextPatch {
		@SpireInsertPatch(locator = CardTypeTextLocator.class, localvars = {"card", "label"})
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard card, @ByRef String[] label) {
			if (card.type == CardTypeEnum.POKER) {
				label[0] = TEXT[0];
			}
		}
	}

	private static class CardTypeTextLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderFontCentered");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(clz = AbstractCard.class, method = "renderType")
	public static class RenderTypePatch {
		@SpireInsertPatch(locator = RenderTextLocator.class, localvars = {"text"})
		public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] text) {
			if (__instance.type == CardTypeEnum.POKER) {
				text[0] = TEXT[0];
			}
		}
	}

	private static class RenderTextLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderRotatedText");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	// Hey basemod people, I'm patching your patch!!!
	@SpirePatch(clz = RenderFixSwitches.RenderBgSwitch.class, method = "Prefix")
	public static class FuckRenderFixSwitchesPatch {
		@SpirePrefixPatch
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float xPos, float yPos) {
			if (__instance.type == CardTypeEnum.POKER) {
				AbstractCard.CardColor color = __instance.color;
				if (BaseMod.getSkillBgTexture(color) == null) {
					BaseMod.saveSkillBgTexture(color, ImageMaster.loadImage(BaseMod.getSkillBg(color)));
				}
				Texture texture = BaseMod.getSkillBgTexture(color);
				renderHelper(__instance, sb, Color.WHITE.cpy(), texture, xPos, yPos);
				return SpireReturn.Return(SpireReturn.Return(null));
			} else {
				return SpireReturn.Continue();
			}
		}
	}

	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, Texture texture, float xPos, float yPos) {
		try {
			Method renderHelperMethod;
			Field renderColorField;

			renderHelperMethod = SuperclassFinder.getSuperClassMethod(card.getClass(), "renderHelper", SpriteBatch.class, Color.class, Texture.class, float.class, float.class);
			renderHelperMethod.setAccessible(true);
			renderColorField = SuperclassFinder.getSuperclassField(card.getClass(), "renderColor");
			renderColorField.setAccessible(true);

			Color renderColor = (Color) renderColorField.get(card);
			renderHelperMethod.invoke(card, sb, renderColor, texture, xPos, yPos);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "sortByRarity")
	public static class SortByRarityPatch {
		@SpirePostfixPatch()
		public static void Postfix(CardGroup __instance, boolean ascending) {
			ArrayList<AbstractCard> pokers = new ArrayList<>();
			for (AbstractCard c : __instance.group) {
				if (c instanceof PokerCard) {
					pokers.add(c);
				}
			}
			__instance.group.removeAll(pokers);
			if (ascending) {
				__instance.group.addAll(0, pokers);
			} else {
				__instance.group.addAll(pokers);
			}
		}
	}

	@SpirePatch(clz = CardGroup.class, method = "sortByRarityPlusStatusCardType")
	public static class SortByRarityPlusStatusCardTypePatch {
		@SpirePostfixPatch()
		public static void Postfix(CardGroup __instance, boolean ascending) {
			ArrayList<AbstractCard> pokers = new ArrayList<>();
			for (AbstractCard c : __instance.group) {
				if (c instanceof PokerCard) {
					pokers.add(c);
				}
			}
			__instance.group.removeAll(pokers);
			if (ascending) {
				__instance.group.addAll(0, pokers);
			} else {
				__instance.group.addAll(pokers);
			}
		}
	}
}
