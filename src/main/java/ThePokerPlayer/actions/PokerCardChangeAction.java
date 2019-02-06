package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

public class PokerCardChangeAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerCardChangeAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	private AbstractPlayer p;
	private Mode mode;
	private ArrayList<AbstractCard> nonPokerCards = new ArrayList<>();

	public static PokerCardChangeAction ref = null;

	public enum Mode {
		RANK_CHANGE_SINGLE,
		RANK_CHANGE_ALL,
		EXTRACT
	}


	/// amount = how much card rank will be changed. amount of 0 means it's randomized.
	public PokerCardChangeAction(AbstractCreature target, AbstractCreature source, Mode mode, int amount) {
		this.p = (AbstractPlayer) target;
		this.mode = mode;
		this.setValues(target, source, amount);
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.EXHAUST;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			if (this.p.hand.size() == 0) {
				this.isDone = true;
				return;
			}

			switch (mode) {
				case RANK_CHANGE_SINGLE:
				case EXTRACT:
					for (AbstractCard c : this.p.hand.group) {
						if (!(c instanceof PokerCard)) {
							this.nonPokerCards.add(c);
						}
					}
					if (this.nonPokerCards.size() == this.p.hand.group.size()) {
						this.isDone = true;
						return;
					}

					if (mode == Mode.RANK_CHANGE_SINGLE) {
						if (this.p.hand.group.size() - this.nonPokerCards.size() >= 1) {
							this.p.hand.group.removeAll(this.nonPokerCards);
							ref = this;
							PokerPlayerMod.transformAnimTimer = 0;
							AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, true, true, false, true);
							this.tickDuration();
							return;
						}
					} else if (mode == Mode.EXTRACT) {
						if (this.p.hand.group.size() - this.nonPokerCards.size() <= amount) {
							for (AbstractCard c : this.p.hand.group) {
								if (c instanceof PokerCard) {
									doExtract((PokerCard) c);
								}
							}
							this.isDone = true;
							return;
						} else {
							this.p.hand.group.removeAll(this.nonPokerCards);
							AbstractDungeon.handCardSelectScreen.open(TEXT[1], amount, false, false, false, false);
							this.tickDuration();
							return;
						}
					}
					break;
				case RANK_CHANGE_ALL:
					for (AbstractCard c : AbstractDungeon.player.hand.group)
						if (c instanceof PokerCard) {
							((PokerCard) c).rankChange(amount, true);
							c.superFlash();
						}

					this.isDone = true;
					return;
			}
		}

		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
				switch (mode) {
					case RANK_CHANGE_SINGLE:
						((PokerCard) c).rankChange(amount, true);
						c.superFlash();
						this.p.hand.addToTop(c);
						break;
					case EXTRACT:
						doExtract((PokerCard) c);
				}
			}

			this.returnCards();
			AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
			AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
			this.isDone = true;
			ref = null;
		}

		tickDuration();
	}

	private void returnCards() {
		for (AbstractCard c : this.nonPokerCards) {
			this.p.hand.addToTop(c);
		}

		this.p.hand.refreshHandLayout();
	}

	private void doExtract(PokerCard c) {
		AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, p.hand, true));
		AbstractDungeon.actionManager.addToTop(new GainEnergyAction(c.rank));
	}
}
