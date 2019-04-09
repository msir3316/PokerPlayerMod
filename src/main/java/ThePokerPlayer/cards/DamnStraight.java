package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.powers.DamnStraightPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DamnStraight extends CustomCard {
	private static final String RAW_ID = "DamnStraight";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.POWER;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.UNCOMMON;
	private static final AbstractCard.CardTarget TARGET = CardTarget.SELF;

	public DamnStraight() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		updateDescription();
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		if (upgraded) {
			AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
		}
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new DamnStraightPower(p, p, 1), 1));
	}

	public AbstractCard makeCopy() {
		return new DamnStraight();
	}

	@Override
	public void applyPowers() {
		updateDescription();
	}

	private void updateDescription() {
		this.rawDescription = (upgraded ? UPGRADE_DESCRIPTION : DESCRIPTION) +
				EXTENDED_DESCRIPTION[0] + ShowdownAction.modifierByHand(ShowdownAction.STRAIGHT) +
				EXTENDED_DESCRIPTION[1] + ShowdownAction.modifierByHand(ShowdownAction.STRAIGHT) * 2 +
				EXTENDED_DESCRIPTION[2];
		this.initializeDescription();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			updateDescription();
		}
	}
}
