package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.powers.CardPrinterPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CardPrinter extends CustomCard {
	private static final String RAW_ID = "CardPrinter";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 3;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.POWER;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.UNCOMMON;
	private static final AbstractCard.CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 2;

	public CardPrinter() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseMagicNumber = POWER;
		this.magicNumber = this.baseMagicNumber;
		updateDescription();
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new CardPrinterPower(p, p, this.magicNumber), this.magicNumber));
	}

	public AbstractCard makeCopy() {
		return new CardPrinter();
	}

	private void updateDescription() {
		this.name = NAME + " <" + this.magicNumber + ">";
		this.rawDescription = DESCRIPTION;
		if (timesUpgraded == 0) {
			this.rawDescription += EXTENDED_DESCRIPTION[0];
		} else if (timesUpgraded < 7) {
			this.rawDescription += EXTENDED_DESCRIPTION[1] + (8 - timesUpgraded) + EXTENDED_DESCRIPTION[2];
		} else if (timesUpgraded == 7) {
			this.rawDescription += EXTENDED_DESCRIPTION[3];
		}
		this.initializeDescription();
	}

	@Override
	public boolean canUpgrade() {
		return timesUpgraded < 9;
	}

	public void upgrade() {
		if (timesUpgraded < 8) {
			timesUpgraded++;
			this.upgradeMagicNumber(1);
			updateDescription();
		}
	}
}
