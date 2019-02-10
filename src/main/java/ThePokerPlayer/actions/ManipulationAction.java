package ThePokerPlayer.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class ManipulationAction extends AbstractGameAction {
	private boolean freeToPlayOnce;
	private int damage;
	private AbstractPlayer p;
	private AbstractMonster m;
	private DamageInfo.DamageType damageTypeForTurn;
	private int energyOnUse;

	public ManipulationAction(AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageTypeForTurn, boolean freeToPlayOnce, int energyOnUse) {
		this.p = p;
		this.m = m;
		this.damage = damage;
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.damageTypeForTurn = damageTypeForTurn;
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
			for (int i = 0; i < effect; ++i) {
				AbstractDungeon.actionManager.addToBottom(new DamageAction(this.m, new DamageInfo(this.p, this.damage, this.damageTypeForTurn), AttackEffect.BLUNT_LIGHT));
			}
			AbstractDungeon.actionManager.addToBottom(new PokerCardChangeAction(this.p, this.p, PokerCardChangeAction.Mode.RANK_CHANGE_ANY, 1, effect));

			if (!this.freeToPlayOnce) {
				this.p.energy.use(EnergyPanel.totalCount);
			}
		}

		this.isDone = true;
	}
}

