package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ThinkingTime extends CustomCard {
	private static final String RAW_ID = "ThinkingTime";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final AbstractCard.CardType TYPE = AbstractCard.CardType.SKILL;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.SPECIAL;
	private static final AbstractCard.CardTarget TARGET = AbstractCard.CardTarget.SELF;

	private static final int DRAW = 1;
	private static final int UPGRADE_BONUS = 1;

	int num = 0;

	public ThinkingTime() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseBlock = 0;
		this.baseMagicNumber = DRAW;
		this.magicNumber = this.baseMagicNumber;
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
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
			if (num != count) {
				this.baseBlock += (count - num);
				num = count;
				this.initializeDescription();
			}
		}
		super.update();
	}

	public AbstractCard makeCopy() {
		return new ThinkingTime();
	}

	@Override
	public AbstractCard makeStatEquivalentCopy() {
		ThinkingTime c = (ThinkingTime)(super.makeStatEquivalentCopy());
		c.num = this.num;
		return c;
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
			rawDescription = UPGRADE_DESCRIPTION;
			initializeDescription();
		}
	}
}
