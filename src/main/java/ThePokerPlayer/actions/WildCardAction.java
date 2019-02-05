package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class WildCardAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("WildCardAction"));
	public static final String[] TEXT = uiStrings.TEXT;
	public static CardGroup group = null;


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
				AbstractDungeon.gridSelectScreen.open(group, 1, TEXT[0], false);

			} else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
				for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
					AbstractCard cc = c.makeCopy();
					if (this.p.hand.size() < 10) {
						this.p.hand.addToHand(cc);
						this.p.discardPile.removeCard(cc);
					}
					cc.lighten(false);
					cc.unhover();
				}

				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				this.p.hand.refreshHandLayout();
			}
		}
		this.tickDuration();
	}
}
