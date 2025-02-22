package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class FairLicense extends CustomRelic {

	private static final String RAW_ID = "FairLicense";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	private boolean notPlayed;
	public static boolean pokerHandScored;

	public FairLicense() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.FLAT);
		this.pulse = false;
	}

	public void atPreBattle() {
		this.notPlayed = false;
		if (!this.pulse) {
			this.beginPulse();
			this.pulse = true;
		}
	}

	public void atTurnStart() {
		this.beginPulse();
		this.pulse = true;
		if (notPlayed && pokerHandScored) {
			this.flash();
			AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
			AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(2));
		}

		this.notPlayed = true;
		pokerHandScored = false;
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card instanceof PokerCard) {
			this.notPlayed = false;
			this.pulse = false;
		}
	}

	public void onVictory() {
		this.pulse = false;
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new FairLicense();
	}
}
