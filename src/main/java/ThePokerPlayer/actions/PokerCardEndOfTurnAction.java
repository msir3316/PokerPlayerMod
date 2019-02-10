package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.interfaces.IShowdownEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class PokerCardEndOfTurnAction extends AbstractGameAction {
	public PokerCardEndOfTurnAction(PokerCard card) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.SPECIAL;
		ShowdownAction.cards.add(card);
	}

	@Override
	public void update() {
		this.isDone = true;
		if (!ShowdownAction.cards.isEmpty()) {
			for (AbstractPower pow : AbstractDungeon.player.powers) {
				if (pow instanceof IShowdownEffect) {
					((IShowdownEffect) pow).onShowdownStart();
				}
			}
			for (AbstractRelic r : AbstractDungeon.player.relics) {
				if (r instanceof IShowdownEffect) {
					((IShowdownEffect) r).onShowdownStart();
				}
			}
			AbstractDungeon.actionManager.addToBottom(new ShowdownAction());
		}
	}
}
