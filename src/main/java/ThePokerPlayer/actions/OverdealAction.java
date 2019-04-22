package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class OverdealAction extends AbstractGameAction {
	private boolean freeToPlayOnce;
	private boolean upgraded;
	private AbstractPlayer p;
	private int energyOnUse;

	public OverdealAction(AbstractPlayer p, boolean upgraded, boolean freeToPlayOnce, int energyOnUse) {
		this.p = p;
		this.upgraded = upgraded;
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

		if (this.p.hasRelic(ChemicalX.ID)) {
			effect += 2;
			this.p.getRelic(ChemicalX.ID).flash();
		}


		if (this.upgraded) {
			++effect;
		}

		for (int i = 0; i < effect; ++i) {
			AbstractDungeon.actionManager.addToBottom(new MakePokerCardInHandAction(
					PokerCard.Suit.values()[AbstractDungeon.cardRandomRng.random(3)],
					AbstractDungeon.cardRandomRng.random(1, 10),
					false));
		}

		if (effect > 0 && !this.freeToPlayOnce) {
			this.p.energy.use(EnergyPanel.totalCount);
		}

		this.isDone = true;
	}
}
