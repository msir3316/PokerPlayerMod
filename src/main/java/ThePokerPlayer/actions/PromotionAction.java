package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PromotionAction extends AbstractGameAction {
	private AbstractPlayer p;

	public PromotionAction(int amount) {
		this.p = AbstractDungeon.player;
		this.duration = Settings.ACTION_DUR_MED;
		this.actionType = ActionType.WAIT;
		this.amount = amount;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_MED) {
			this.promoteAllCardsInGroup(p.hand);
			this.promoteAllCardsInGroup(p.drawPile);
			this.promoteAllCardsInGroup(p.discardPile);
			this.promoteAllCardsInGroup(p.exhaustPile);
			this.isDone = true;
		}
	}

	private void promoteAllCardsInGroup(CardGroup cardGroup) {
		for (AbstractCard c : cardGroup.group) {
			if (c instanceof PokerCard) {
				PokerCard pc = (PokerCard) c;
				if (pc.rank < 10) {
					pc.rankChange(amount, false);
				}
				if (cardGroup.type == CardGroup.CardGroupType.HAND) {
					c.superFlash();
				}
				c.applyPowers();
			}
		}
	}
}
