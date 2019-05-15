package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.relics.StuffedPocket;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ItsShowtimeAction extends AbstractGameAction {
	public ItsShowtimeAction() {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.SPECIAL;
	}

	@Override
	public void update() {
		this.isDone = true;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (!(c instanceof PokerCard)) {
				ShowdownAction.otherCards.add(c);
			}
		}
		if (AbstractDungeon.player.hand.group.size() <= ShowdownAction.otherCards.size()) {
			this.isDone = true;
			return;
		}
		AbstractDungeon.player.hand.group.removeAll(ShowdownAction.otherCards);

		if (AbstractDungeon.player.hand.group.size() > 5) {
			if (AbstractDungeon.player.hasRelic(StuffedPocket.ID)) {
				AbstractDungeon.player.getRelic(StuffedPocket.ID).flash();
			} else {
				AbstractDungeon.actionManager.addToBottom(new DiscardAction(
						AbstractDungeon.player, AbstractDungeon.player, AbstractDungeon.player.hand.group.size() - 5, false, true));
			}
		}
		AbstractDungeon.actionManager.addToBottom(new ShowdownAction(false));
	}
}
