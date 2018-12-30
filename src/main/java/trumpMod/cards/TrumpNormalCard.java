package trumpMod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import trumpMod.TrumpTheSpire;
import trumpMod.patches.CardColorEnum;

public class TrumpNormalCard extends CustomCard {
	private static final String RAW_ID = "TrumpNormalCard";
	public static final String ID = TrumpTheSpire.makeID(RAW_ID);

	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;

	public static final String BLANK_IMG = TrumpTheSpire.GetCardPath(RAW_ID);

	public static final String[] SUIT_TO_RAW_ID = new String[]{"Spade", "Diamond", "Heart", "Clover"};
	public static final String[] SUIT_TO_IMG = new String[]{
			TrumpTheSpire.GetCardPath(SUIT_TO_RAW_ID[Suit.Spade.value]),
			TrumpTheSpire.GetCardPath(SUIT_TO_RAW_ID[Suit.Diamond.value]),
			TrumpTheSpire.GetCardPath(SUIT_TO_RAW_ID[Suit.Heart.value]),
			TrumpTheSpire.GetCardPath(SUIT_TO_RAW_ID[Suit.Clover.value]),
	};
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.STATUS;
	private static final CardColor COLOR = CardColorEnum.TRUMP_GRAY;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int NEW_COST = 1;

	public enum Suit {
		Spade(0),
		Diamond(1),
		Heart(2),
		Clover(3);

		public int value;

		Suit(int value) {
			this.value = value;
		}

		public String getImage() {
			return SUIT_TO_IMG[value];
		}

		public String toString() {
			return EXTENDED_DESCRIPTION[value];
		}
	}

	public Suit suit;
	public int num;

	public static String getID(Suit suit, int num) {
		return TrumpTheSpire.makeID(SUIT_TO_RAW_ID[suit.value] + num);
	}

	public TrumpNormalCard(Suit suit, int num) {
		super(getID(suit, num), NAME, BLANK_IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

		this.suit = suit;
		this.num = num;
		this.baseMagicNumber = num;
		this.magicNumber = this.baseMagicNumber;
		this.baseBlock = num;
		this.baseDamage = num;

		this.name = suit.toString() + " " + num;
		this.rawDescription = this.name;
		this.initializeTitle();
		this.initializeDescription();
	}

	@Override
	public void triggerOnEndOfTurnForPlayingCard() {
		doEffect(AbstractDungeon.player, this);
	}

	public static void doEffect(AbstractPlayer p, TrumpNormalCard card) {
		switch (card.suit) {
			case Spade:
				AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, card.block));
				break;
			case Diamond:
				AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(card.damage), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_LIGHT));
				break;
			case Heart:
				AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, card.magicNumber));
				break;
			case Clover:
				AbstractDungeon.actionManager.addToBottom(new DamageRandomEnemyAction(new DamageInfo(p, card.damage, card.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
				break;
		}
	}

	public AbstractCard makeCopy() {
		TrumpNormalCard card = new TrumpNormalCard(this.suit, this.num);
		return card;
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
	}

	public void upgrade() {
		this.upgradeBaseCost(NEW_COST);
	}
}
