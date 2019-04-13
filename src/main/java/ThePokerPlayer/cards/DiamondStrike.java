package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DiamondStrike extends CustomCard {
	private static final String RAW_ID = "DiamondStrike";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.ATTACK;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.COMMON;
	private static final AbstractCard.CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 8;
	private static final int UPGRADE_BONUS = 2;
	private static final int MAGIC = 4;
	private static final int UPGRADE_MAGIC = 2;

	public DiamondStrike() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseDamage = POWER;
		this.baseMagicNumber = MAGIC;
		this.magicNumber = this.baseMagicNumber;

		this.tags.add(CardTags.STRIKE);
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
				new DamageInfo(p, this.damage, this.damageTypeForTurn),
				AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
		AbstractCard c = new PokerCard(PokerCard.Suit.Diamond, this.magicNumber, true);
		AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c));
	}

	public AbstractCard makeCopy() {
		return new DiamondStrike();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
			upgradeMagicNumber(UPGRADE_MAGIC);
		}
	}
}
