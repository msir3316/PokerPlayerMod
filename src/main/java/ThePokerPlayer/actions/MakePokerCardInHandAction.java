package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.relics.BrokenClock;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class MakePokerCardInHandAction extends AbstractGameAction {
	PokerCard.Suit suit;
	int rank;
	boolean ethereal;

	public MakePokerCardInHandAction(PokerCard.Suit suit, int rank, boolean ethereal) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.SPECIAL;
		this.suit = suit;
		this.rank = rank;
		this.ethereal = ethereal;
	}

	@Override
	public void update() {
		this.isDone = true;
		if (AbstractDungeon.player.hasRelic(BrokenClock.ID)) {
			AbstractDungeon.actionManager.addToTop(new PokerCardDiscoveryAction(1, suit, rank, rank, ethereal));
		} else {
			AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new PokerCard(suit, rank, ethereal), 1));
		}
	}
}
