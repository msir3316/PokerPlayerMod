package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.ChoiceCard.BrokenClockChoice;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.relics.BrokenClock;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class WildCardAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("WildCardAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	public static CardGroup group = null;
	public static CardGroup groupWithClock = null;


	private AbstractPlayer p;

	public WildCardAction() {
		this.p = AbstractDungeon.player;
		this.setValues(p, p, 1);
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FASTER;
	}

	public void update() {
		if (AbstractDungeon.getCurrRoom().isBattleEnding()) {
			this.isDone = true;
		} else {
			if (this.duration == Settings.ACTION_DUR_FASTER) {
				if (group == null) {
					group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
					for (PokerCard.Suit suit : PokerCard.Suit.values()) {
						for (int i = 1; i <= 10; i++) {
							group.addToTop(new PokerCard(suit, i));
						}
					}
				}
				if (groupWithClock == null) {
					groupWithClock = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
					for (int i = 0; i < 5; i++) {
						groupWithClock.addToTop(new BrokenClockChoice());
					}
					for (PokerCard.Suit suit : PokerCard.Suit.values()) {
						for (int i = 1; i <= 10; i++) {
							groupWithClock.addToTop(new PokerCard(suit, i));
						}
					}
				}
				if (AbstractDungeon.player.hasRelic(BrokenClock.ID)) {
					AbstractDungeon.player.getRelic(BrokenClock.ID).flash();
					AbstractDungeon.gridSelectScreen.open(groupWithClock, 1, TEXT[0], false);
				} else {
					AbstractDungeon.gridSelectScreen.open(group, 1, TEXT[0], false);
				}

			} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
				for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
					if (c instanceof BrokenClockChoice) {
						AbstractDungeon.actionManager.addToTop(new DrawCardAction(p, 1));
					} else {
						AbstractCard cc = c.makeCopy();
						if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
							AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(cc, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
						} else {
							AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(cc, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
						}
						cc.lighten(false);
						cc.unhover();
					}
				}

				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				this.p.hand.refreshHandLayout();
			}
		}
		this.tickDuration();
	}
}
