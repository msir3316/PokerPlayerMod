package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ChooseAction;
import ThePokerPlayer.actions.PokerCardChangeAction;
import ThePokerPlayer.cards.ChoiceCard.ChooseOption;
import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PoorCopy extends CustomCard {
	private static final String RAW_ID = "PoorCopy";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final AbstractCard.CardType TYPE = AbstractCard.CardType.SKILL;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.UNCOMMON;
	private static final AbstractCard.CardTarget TARGET = AbstractCard.CardTarget.SELF;

	private static final int DRAW = 1;
	private static final int NEW_COST = 1;

	public PoorCopy() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseMagicNumber = DRAW;
		this.magicNumber = this.baseMagicNumber;
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new ChooseAction(
				EXTENDED_DESCRIPTION[0],
				new ChooseOption(
						this,
						EXTENDED_DESCRIPTION[1],
						EXTENDED_DESCRIPTION[3],
						() -> {
							AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
						}),
				new ChooseOption(
						this,
						EXTENDED_DESCRIPTION[2],
						EXTENDED_DESCRIPTION[4],
						() -> {
							AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(p, p, PokerCardChangeAction.Mode.COPY, 1, -1));
						})
		));
	}

	@Override
	public void update() {
		if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null) {
			int count = 0;
			for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
				if (c instanceof PokerCard) {
					count++;
				}
			}
			if (this.baseBlock != count) {
				this.baseBlock = count;
				this.initializeDescription();
			}
		}
		super.update();
	}

	public AbstractCard makeCopy() {
		return new PoorCopy();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			this.upgradeBaseCost(NEW_COST);
		}
	}
}
