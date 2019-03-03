package ThePokerPlayer.powers;

import ThePokerPlayer.PokerPlayerMod;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DamnStraightPower extends AbstractPower {
	public AbstractCreature source;

	private static final String RAW_ID = "DamnStraightPower";
	public static final String POWER_ID = PokerPlayerMod.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 128)), 0, 0, 90, 88);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 48)), 0, 0, 33, 32);

	public DamnStraightPower(AbstractCreature owner, AbstractCreature source, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.type = PowerType.BUFF;
		this.isTurnBased = false;
		this.region128 = IMG128;
		this.region48 = IMG48;
		this.source = source;
	}

	@Override
	public void updateDescription() {
		if (this.amount == 1) {
			this.description = DESCRIPTIONS[0] + DESCRIPTIONS[1];
		} else if (this.amount < 24) {
			this.description = DESCRIPTIONS[0] + DESCRIPTIONS[2] + (1 << this.amount) + DESCRIPTIONS[3];
		} else {
			this.description = DESCRIPTIONS[0] + DESCRIPTIONS[4];
		}
	}
}
