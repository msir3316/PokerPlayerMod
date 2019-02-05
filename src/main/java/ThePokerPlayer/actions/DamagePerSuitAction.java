package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DamagePerSuitAction extends AbstractGameAction {
	private DamageInfo info;
	private float startingDuration;
	private PokerCard.Suit suit;

	public DamagePerSuitAction(AbstractCreature target, PokerCard.Suit suit, DamageInfo info) {
		this.info = info;
		this.setValues(target, info);
		this.actionType = AbstractGameAction.ActionType.WAIT;
		this.attackEffect = AbstractGameAction.AttackEffect.SLASH_DIAGONAL;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.suit = suit;
	}

	public void update() {
		if (this.duration == this.startingDuration) {
			for (AbstractCard c : AbstractDungeon.player.hand.group) {
				if (c instanceof PokerCard && ((PokerCard) c).suit == this.suit) {
					AbstractDungeon.actionManager.addToBottom(new DamageAction(target, info, AbstractGameAction.AttackEffect.FIRE));
				}
			}
		}

		this.tickDuration();
	}
}
