package ThePokerPlayer.cards;

import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ThePokerPlayer.PokerPlayerMod;

public class PowerPower extends CustomCard {
	public static final String ID = PokerPlayerMod.makeID("PowerPower");
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String IMG = PokerPlayerMod.makePath(PokerPlayerMod.DEFAULT_UNCOMMON_POWER);

	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;

	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;
	private static final CardType TYPE = CardType.POWER;
	public static final CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;

	private static final int COST = 1;
	private static final int MAGIC = 1;

	public PowerPower() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.magicNumber = this.baseMagicNumber = MAGIC;
	}

	// Actions the card should do.
	@Override
	public void use(final AbstractPlayer p, final AbstractMonster m) {
	}

	// Which card to return when making a copy of this card.
	@Override
	public AbstractCard makeCopy() {
		return new PowerPower();
	}

	//Upgraded stats.
	@Override
	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
		}
	}
}