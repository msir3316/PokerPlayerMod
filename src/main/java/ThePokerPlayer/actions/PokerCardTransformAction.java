package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class PokerCardTransformAction extends AbstractGameAction {
	AbstractCard cardFrom;
	AbstractCard cardTo;

	public PokerCardTransformAction(AbstractCard cardFrom, AbstractCard cardTo) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		this.cardFrom = cardFrom;
		this.cardTo = cardTo;
	}

	@Override
	public void update() {
		for (AbstractCard c : PokerPlayerMod.shapeshiftReturns.values()) {
			if (c.uuid == cardFrom.uuid) {
				this.isDone = true;
				return;
			}
		}

		cardTo.current_x = -1000.0F * Settings.scale;
		if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
			AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(cardTo, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
		} else {
			AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(cardTo, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
		}

		if (cardFrom != null) {
			PokerPlayerMod.shapeshiftReturns.put(cardTo, cardFrom);
		}
		if (cardTo instanceof PokerCard) {
			((PokerCard) cardTo).initCard(cardTo.isEthereal);
		}
		isDone = true;
	}
}
