package ThePokerPlayer.actions;

import ThePokerPlayer.vfx.DiceThrowEffect;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class TheDieIsCastAction extends AbstractGameAction {
	private boolean init = false;
	private boolean upgraded;
	private DiceThrowEffect effect;

	public TheDieIsCastAction(boolean upgraded) {
		this.duration = Settings.ACTION_DUR_FAST;
		this.actionType = ActionType.SPECIAL;
		this.upgraded = upgraded;
	}

	@Override
	public void update() {
		if (!init) {
			effect = new DiceThrowEffect(upgraded);
			AbstractDungeon.effectList.add(effect);
			init = true;
		} else if (effect.isDone) {
			AbstractDungeon.actionManager.addToTop(new PokerCardChangeAction(
					AbstractDungeon.player, AbstractDungeon.player, PokerCardChangeAction.Mode.RANK_CHANGE_SET, BaseMod.MAX_HAND_SIZE, effect.diceResult));
			this.isDone = true;
		}
	}
}
