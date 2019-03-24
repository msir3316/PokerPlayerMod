package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class EnergeticBat extends CustomRelic {

	private static final String RAW_ID = "EnergeticBat";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	public EnergeticBat() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.HEAVY);
	}

	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card.hasTag(AbstractCard.CardTags.STRIKE)) {
			AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
			AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new EnergeticBat();
	}
}
