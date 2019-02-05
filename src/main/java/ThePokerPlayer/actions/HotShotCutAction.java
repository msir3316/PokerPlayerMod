package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class HotShotCutAction extends AbstractGameAction {
	private DamageInfo info;
	private float startingDuration;
	private boolean upgraded;

	public HotShotCutAction(AbstractCreature target, DamageInfo info, boolean upgraded) {
		this.info = info;
		this.setValues(target, info);
		this.actionType = ActionType.WAIT;
		this.attackEffect = AttackEffect.SLASH_DIAGONAL;
		this.startingDuration = Settings.ACTION_DUR_FAST;
		this.duration = this.startingDuration;
		this.upgraded = upgraded;
	}

	public void update() {
		if (this.duration == this.startingDuration) {
			int count = AbstractDungeon.player.hand.size();
			int atkCount;
			if (!upgraded) {
				atkCount = 0;
				for (AbstractCard c : AbstractDungeon.player.hand.group) {
					if (c instanceof PokerCard) {
						atkCount++;
					}
				}
			} else {
				atkCount = count;
			}

			AbstractDungeon.actionManager.addToTop(new SwordBoomerangAction(AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng), this.info, atkCount));

			for (int i = 0; i < count; ++i) {
				AbstractDungeon.actionManager.addToTop(new ExhaustAction(AbstractDungeon.player, AbstractDungeon.player, 1, true, true));
			}
		}

		this.tickDuration();
	}
}
