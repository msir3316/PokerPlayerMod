package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class RiskyBetAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("RiskyBetAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	private AbstractPlayer p;

	public RiskyBetAction(int amount) {
		this.p = AbstractDungeon.player;
		this.setValues(target, source, amount);
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.WAIT;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			if (this.p.hand.size() == 0) {
				this.isDone = true;
				return;
			}

			if (this.p.hand.group.size() <= 1) {
				for (AbstractCard c : this.p.hand.group) {
					doAction(c);
				}
				this.isDone = true;
				return;
			} else {
				AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
				this.tickDuration();
				return;
			}
		}
		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
				doAction(c);
			}
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
			this.isDone = true;
		}

		tickDuration();
	}

	private void doAction(AbstractCard c) {
		this.p.hand.moveToDiscardPile(c);
		c.triggerOnManualDiscard();
		GameActionManager.incrementDiscard(false);
		AbstractCard card = AbstractDungeon.player.drawPile.getTopCard();
		if (card instanceof PokerCard && c instanceof PokerCard && ((PokerCard) card).suit == ((PokerCard) c).suit) {
			AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, amount));
			AbstractDungeon.actionManager.addToTop(new TalkAction(true, TEXT[1], 0.6f, 2.0f));
		} else {
			AbstractDungeon.actionManager.addToTop(new TalkAction(true, TEXT[2], 0.6f, 2.0f));
		}
		AbstractDungeon.actionManager.addToTop(new WaitAction(0.2f));
		AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, 1));
	}
}
