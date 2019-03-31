package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.interfaces.IShowdownEffect;
import ThePokerPlayer.relics.PendantOfEscape;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class PokerCardEndOfTurnAction extends AbstractGameAction {
	public static ArrayList<PokerCard> cards = new ArrayList<>();

	public PokerCardEndOfTurnAction(PokerCard card) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.SPECIAL;
		cards.add(card);
		PendantOfEscape.disabled = true;
	}

	@Override
	public void update() {
		this.isDone = true;
		if (!cards.isEmpty()) {
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (!cards.contains(c)) {
					ShowdownAction.otherCards.add(c);
				}
			}
			AbstractDungeon.player.hand.group.removeAll(ShowdownAction.otherCards);
			if (AbstractDungeon.player.hand.group.size() > 5) {
				AbstractDungeon.actionManager.addToBottom(new DiscardAction(
						AbstractDungeon.player, AbstractDungeon.player, AbstractDungeon.player.hand.group.size() - 5, false, true));
			}
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
			AbstractDungeon.actionManager.addToBottom(new ShowdownAction(true));
			cards.clear();
		}
	}
}
