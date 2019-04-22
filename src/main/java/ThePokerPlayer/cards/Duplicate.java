package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ChooseAction;
import ThePokerPlayer.actions.PokerCardChangeAction;
import ThePokerPlayer.cards.ChoiceCard.BrokenClockChoice;
import ThePokerPlayer.cards.ChoiceCard.ChooseOption;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.relics.BrokenClock;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Duplicate extends CustomCard {
	private static final String RAW_ID = "Duplicate";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.SKILL;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.RARE;
	private static final AbstractCard.CardTarget TARGET = CardTarget.SELF;

	private static final int NEW_COST = 1;

	public Duplicate() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		if (AbstractDungeon.player.hasRelic(BrokenClock.ID)) {
			AbstractDungeon.player.getRelic(BrokenClock.ID).flash();
			BrokenClockChoice bcc = new BrokenClockChoice();
			AbstractDungeon.actionManager.addToBottom(new ChooseAction(
					PoorCopy.EXTENDED_DESCRIPTION[0],
					new ChooseOption(
							bcc,
							bcc.name,
							bcc.rawDescription,
							() -> {
								AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
							}),
					new ChooseOption(
							this,
							this.name,
							this.rawDescription,
							() -> {
								AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(p, p, PokerCardChangeAction.Mode.COPY, 1, 0));
							})
			));
		} else {
			AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(p, p, PokerCardChangeAction.Mode.COPY, 1, 0));
		}
	}

	public AbstractCard makeCopy() {
		return new Duplicate();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			this.upgradeBaseCost(NEW_COST);
		}
	}
}
