package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class PokerCardChangeAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerCardChangeAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	private AbstractPlayer p;
	private Mode mode;
	private ArrayList<AbstractCard> nonPokerCards = new ArrayList<>();
	public int rankChange;

	public static PokerCardChangeAction ref = null;

	public enum Mode {
		RANK_CHANGE_ANY,
		RANK_CHANGE_ALL,
		EXTRACT,
		COPY,
		ROYAL_STRIKE
	}


	// amount = how much pokerCards will be changed for RANK_CHANGE_ANY and EXTRACT.
	// rankChange = how much card rank will be changed.
	//   0 = randomized. -1 = randomized between 6 and 10.
	public PokerCardChangeAction(AbstractCreature target, AbstractCreature source, Mode mode, int amount, int rankChange) {
		this.p = (AbstractPlayer) source;
		this.mode = mode;
		this.setValues(target, source, amount);
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.EXHAUST;
		this.rankChange = rankChange;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			if (this.p.hand.size() == 0) {
				this.isDone = true;
				return;
			}

			switch (mode) {
				case RANK_CHANGE_ANY:
				case COPY:
				case ROYAL_STRIKE:
					for (AbstractCard c : this.p.hand.group) {
						if (!(c instanceof PokerCard)) {
							this.nonPokerCards.add(c);
						}
					}
					if (this.nonPokerCards.size() == this.p.hand.group.size()) {
						this.isDone = true;
						return;
					}

					if (mode == Mode.RANK_CHANGE_ANY) {
						this.p.hand.group.removeAll(this.nonPokerCards);
						ref = this;
						PokerPlayerMod.transformAnimTimer = 0;
						AbstractDungeon.handCardSelectScreen.open(TEXT[0], amount, true, true, false, amount == 1);
						this.tickDuration();
						return;
					} else if (mode == Mode.COPY) {
						if (this.p.hand.group.size() - this.nonPokerCards.size() <= amount) {
							for (AbstractCard c : this.p.hand.group) {
								if (c instanceof PokerCard) {
									doCopy((PokerCard) c);
								}
							}
							this.isDone = true;
							return;
						} else {
							this.p.hand.group.removeAll(this.nonPokerCards);
							AbstractDungeon.handCardSelectScreen.open(TEXT[2], amount, false, false, false, false);
							this.tickDuration();
							return;
						}
					} else if (mode == Mode.ROYAL_STRIKE) {
						if (target == null) {
							this.isDone = true;
							return;
						}
						if (this.p.hand.group.size() - this.nonPokerCards.size() <= amount) {
							for (AbstractCard c : this.p.hand.group) {
								if (c instanceof PokerCard) {
									doRoyalStrike((PokerCard) c);
								}
							}
							this.isDone = true;
							return;
						} else {
							this.p.hand.group.removeAll(this.nonPokerCards);
							AbstractDungeon.handCardSelectScreen.open(TEXT[3], amount, false, false, false, false);
							this.tickDuration();
							return;
						}
					}
				case EXTRACT:
					if (this.p.hand.group.size() <= amount) {
						for (AbstractCard c : this.p.hand.group) {
							doExtract(c);
						}
						this.isDone = true;
						return;
					} else {
						AbstractDungeon.handCardSelectScreen.open(TEXT[1], amount, false, false, false, false);
						this.tickDuration();
						return;
					}
				case RANK_CHANGE_ALL:
					for (AbstractCard c : AbstractDungeon.player.hand.group)
						if (c instanceof PokerCard) {
							((PokerCard) c).rankChange(rankChange, true);
							c.superFlash();
						}

					this.isDone = true;
					return;
			}
		}

		if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
			for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
				switch (mode) {
					case RANK_CHANGE_ANY:
						((PokerCard) c).rankChange(rankChange, true);
						c.superFlash();
						this.p.hand.addToTop(c);
						break;
					case EXTRACT:
						doExtract(c);
						this.p.hand.addToTop(c);
						break;
					case COPY:
						doCopy((PokerCard) c);
						this.p.hand.addToTop(c);
						break;
					case ROYAL_STRIKE:
						doRoyalStrike((PokerCard) c);
						break;
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

	private void doExtract(AbstractCard c) {
		AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(c, p.hand, true));
		if (c instanceof PokerCard) {
			AbstractDungeon.actionManager.addToTop(new GainEnergyAction(((PokerCard) c).rank));
		}
	}

	private void doCopy(PokerCard c) {
		AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c));
	}

	private void doRoyalStrike(PokerCard c) {
		this.p.hand.moveToDiscardPile(c);
		c.triggerOnManualDiscard();
		GameActionManager.incrementDiscard(false);
		if (target != null) {
			AttackEffect effect = c.rank > 6 ? AttackEffect.BLUNT_HEAVY :
					c.rank > 3 ? AttackEffect.BLUNT_HEAVY : AttackEffect.SLASH_DIAGONAL;
			float damage = c.rank * rankChange;
			for (AbstractPower p : source.powers) {
				damage = p.atDamageGive(damage, DamageInfo.DamageType.NORMAL);
			}
			for (AbstractPower p : target.powers) {
				damage = p.atDamageReceive(damage, DamageInfo.DamageType.NORMAL);
			}
			for (AbstractPower p : source.powers) {
				damage = p.atDamageFinalGive(damage, DamageInfo.DamageType.NORMAL);
			}
			for (AbstractPower p : target.powers) {
				damage = p.atDamageFinalReceive(damage, DamageInfo.DamageType.NORMAL);
			}
			if (damage < 0.0F) {
				damage = 0.0F;
			}

			DamageInfo info = new DamageInfo(source, MathUtils.floor(damage));

			AbstractDungeon.actionManager.addToTop(new DamageAction(target, info, effect));
		}
	}
}
