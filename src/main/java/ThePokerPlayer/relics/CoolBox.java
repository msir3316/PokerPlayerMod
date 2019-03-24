package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class CoolBox extends CustomRelic {

	private static final String RAW_ID = "CoolBox";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	private boolean reduced = false;

	public CoolBox() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.HEAVY);
	}

	@Override
	public void atBattleStartPreDraw() {
		setReduce(true);
		AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new PokerCard(PokerCard.Suit.Spade, 10, true)));
		AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
	}

	@Override
	public void onPlayerEndTurn() {
		setReduce(false);
	}

	public void onVictory() {
		setReduce(false);
	}

	void setReduce(boolean to) {
		if (reduced != to) {
			reduced = to;
			if (to) {
				AbstractDungeon.player.gameHandSize--;
			} else {
				AbstractDungeon.player.gameHandSize++;
			}
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new CoolBox();
	}
}
