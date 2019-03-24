package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class StuffedPocket extends CustomRelic {

	private static final String RAW_ID = "StuffedPocket";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);
	private static final int PER = 10;

	public StuffedPocket() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.HEAVY);
	}


	public void atPreBattle() {
		if (AbstractDungeon.player.masterDeck.size() >= PER)
			this.flash();
		AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, AbstractDungeon.player.masterDeck.size() / PER));
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + PER + DESCRIPTIONS[1];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new StuffedPocket();
	}
}
