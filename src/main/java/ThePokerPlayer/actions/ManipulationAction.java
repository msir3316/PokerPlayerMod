package ThePokerPlayer.actions;

import ThePokerPlayer.powers.EnergyManipulation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ManipulationAction extends AbstractGameAction {
	private boolean freeToPlayOnce;
	private AbstractPlayer p;
	private int energyOnUse;

	public ManipulationAction(AbstractPlayer p, boolean freeToPlayOnce, int energyOnUse) {
		this.p = p;
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.energyOnUse = energyOnUse;
	}

	public void update() {
		int effect = EnergyPanel.totalCount;
		if (this.energyOnUse != -1) {
			effect = this.energyOnUse;
		}

		if (this.p.hasRelic("Chemical X")) {
			effect += 2;
			this.p.getRelic("Chemical X").flash();
		}

		if (effect > 0) {
			AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new EnergyManipulation(p, effect), effect));
			AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(this.p, this.p, PokerCardChangeAction.Mode.RANK_CHANGE_ANY, 1, effect));

			if (!this.freeToPlayOnce) {
				this.p.energy.use(EnergyPanel.totalCount);
			}
		}

		this.isDone = true;
	}
}

