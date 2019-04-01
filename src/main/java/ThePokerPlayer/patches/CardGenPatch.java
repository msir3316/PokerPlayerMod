package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class CardGenPatch {
	@SpirePatch(
			clz = ShowCardAndAddToDiscardEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class,
					float.class,
					float.class
			}
	)
	public static class AddToDiscardConstruct1 {
		@SpirePostfixPatch
		public static void Postfix(ShowCardAndAddToDiscardEffect __instance, AbstractCard srcCard, float x, float y) {
			if (srcCard.type != AbstractCard.CardType.STATUS && srcCard.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}

	@SpirePatch(
			clz = ShowCardAndAddToDiscardEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class
			}
	)
	public static class AddToDiscardConstruct2 {
		@SpirePostfixPatch
		public static void Postfix(ShowCardAndAddToDiscardEffect __instance, AbstractCard card) {
			if (card.type != AbstractCard.CardType.STATUS && card.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}

	@SpirePatch(
			clz = ShowCardAndAddToDrawPileEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class,
					float.class,
					float.class,
					boolean.class,
					boolean.class,
					boolean.class
			}
	)
	public static class AddToDrawPileConstruct1 {
		@SpirePostfixPatch
		public static void Postfix(ShowCardAndAddToDrawPileEffect __instance, AbstractCard srcCard, float x, float y, boolean randomSpot, boolean cardOffset, boolean toBottom) {
			if (srcCard.type != AbstractCard.CardType.STATUS && srcCard.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}

	@SpirePatch(
			clz = ShowCardAndAddToDrawPileEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class,
					boolean.class,
					boolean.class,
			}
	)
	public static class AddToDrawPileConstruct2 {
		@SpirePostfixPatch
		public static void Postfix(ShowCardAndAddToDrawPileEffect __instance, AbstractCard srcCard, boolean randomSpot, boolean toBottom) {
			if (srcCard.type != AbstractCard.CardType.STATUS && srcCard.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}

	@SpirePatch(
			clz = ShowCardAndAddToHandEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class,
					float.class,
					float.class
			}
	)
	public static class AddToHandConstructor1 {
		@SpirePostfixPatch()
		public static void Postfix(ShowCardAndAddToHandEffect __instance, AbstractCard card, float offsetX, float offsetY) {
			if (card.type != AbstractCard.CardType.STATUS && card.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}

	@SpirePatch(
			clz = ShowCardAndAddToHandEffect.class,
			method = SpirePatch.CONSTRUCTOR,
			paramtypez = {
					AbstractCard.class
			}
	)
	public static class AddToHandConstructor2 {
		@SpirePostfixPatch()
		public static void Postfix(ShowCardAndAddToHandEffect __instance, AbstractCard card) {
			if (card.type != AbstractCard.CardType.STATUS && card.type != AbstractCard.CardType.CURSE) {
				PokerPlayerMod.genCards++;
			}
		}
	}
}
