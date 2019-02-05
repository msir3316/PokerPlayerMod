package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class PlaceholderRelic2 extends CustomRelic {
	/*
	 * https://github.com/daviscook477/BaseMod/wiki/Custom-Relics
	 *
	 * At the start of each combat, gain 1 strenght (i.e. Varja)
	 */

	// ID, images, text.
	public static final String ID = PokerPlayerMod.makeID("PlaceholderRelic2");
	public static final String IMG = PokerPlayerMod.makePath(PokerPlayerMod.PLACEHOLDER_RELIC_2);
	public static final String OUTLINE = PokerPlayerMod.makePath(PokerPlayerMod.PLACEHOLDER_RELIC_OUTLINE_2);

	public PlaceholderRelic2() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.MAGICAL);
	}


	// Gain 1 energy on eqip.
	@Override
	public void atBattleStart() {
		this.flash();
		AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 1), 1));
		AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
	}


	// Description
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	// Which relic to return on making a copy of this relic.
	@Override
	public AbstractRelic makeCopy() {
		return new PlaceholderRelic2();
	}
}
