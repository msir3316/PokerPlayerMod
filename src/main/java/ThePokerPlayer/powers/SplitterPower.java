package ThePokerPlayer.powers;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.PokerCardChangeAction;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.interfaces.IShowdownEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SplitterPower extends AbstractPower implements IShowdownEffect {
	public AbstractCreature source;

	private static final String RAW_ID = "SplitterPower";
	public static final String POWER_ID = PokerPlayerMod.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 128)), 0, 0, 84, 84);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	private boolean activated = false;

	public SplitterPower(AbstractCreature owner, AbstractCreature source, int amount) {
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
		this.priority--;
	}

	@Override
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}

	@Override
	public void onShowdownStart() {
		if (!activated) {
			boolean result = true;
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (c instanceof PokerCard && ((PokerCard) c).rank > 5) {
					result = false;
					break;
				}
			}

			if (result) {
				AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(this.owner, this.source, PokerCardChangeAction.Mode.RANK_CHANGE_ALL, 0, this.amount));
				activated = true;
			}
		}
	}

	@Override
	public void atEndOfRound() {
		activated = false;
	}
}
