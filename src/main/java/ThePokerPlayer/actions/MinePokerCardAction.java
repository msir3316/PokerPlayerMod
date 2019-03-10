package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MinePokerCardAction extends AbstractGameAction {
	private DamageInfo info;
	private PokerCard.Suit suit;

	public MinePokerCardAction(AbstractMonster target, DamageInfo info, AttackEffect effect, PokerCard.Suit suit) {
		this.setValues(target, info);
		this.info = info;
		this.actionType = ActionType.DAMAGE;
		this.attackEffect = effect;
		this.duration = Settings.ACTION_DUR_FAST;
		this.suit = suit;
	}

	public void update() {
		if (this.shouldCancelAction() && this.info.type != DamageInfo.DamageType.THORNS) {
			this.isDone = true;
		} else if (this.duration == Settings.ACTION_DUR_FAST) {
			this.addPokerCard();
			AbstractDungeon.actionManager.addToTop(new DamageAction(target, info, attackEffect));
			this.isDone = true;
		}
		this.tickDuration();
	}

	private void addPokerCard() {
		int damaged = info.output - target.currentBlock;
		if (damaged > target.currentHealth)
			damaged = target.currentHealth;

		if (damaged > 0) {
			int num = damaged < 10 ? damaged : 10;
			AbstractCard c = new PokerCard(this.suit, num);
			AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(c, true));
		}
	}
}
