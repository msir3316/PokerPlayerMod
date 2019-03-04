package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.unique.ExpertiseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class ProtectiveDeckHolder extends CustomRelic implements ClickableRelic {

	private static final String RAW_ID = "ProtectiveDeckHolder";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);
	public static boolean disabled = false;
	public boolean alreadyUsed;

	public ProtectiveDeckHolder() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.STARTER, LandingSound.MAGICAL);
	}

	@Override
	public void onRightClick() {
		if (!isObtained || alreadyUsed) {
			return;
		}

		if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
				AbstractDungeon.getCurrRoom().monsters != null && !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead() &&
				!AbstractDungeon.actionManager.turnHasEnded && !disabled
		) {
			alreadyUsed = true;
			flash();
			stopPulse();
			CardGroup[] groups = new CardGroup[]{
					AbstractDungeon.player.hand,
					AbstractDungeon.player.drawPile,
					AbstractDungeon.player.discardPile,
					AbstractDungeon.player.exhaustPile
			};
			AbstractDungeon.actionManager.addToTop(new ExpertiseAction(AbstractDungeon.player, 5));
			for (CardGroup cg : groups) {
				for (AbstractCard c : cg.group) {
					if (c.type == AbstractCard.CardType.STATUS) {
						AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, cg));
					}
				}
			}
		}
	}

	@Override
	public void atPreBattle() {
		alreadyUsed = false;
		beginLongPulse();
	}

	@Override
	public void onVictory() {
		stopPulse();
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public void atTurnStartPostDraw() {
		disabled = false;
	}

	@Override
	public AbstractRelic makeCopy() {
		return new ProtectiveDeckHolder();
	}
}
