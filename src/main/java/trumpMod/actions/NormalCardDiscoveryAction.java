package trumpMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import trumpMod.cards.TrumpNormalCard;

public class NormalCardDiscoveryAction extends AbstractGameAction {
	private boolean retrieveCard = false;
	public static TrumpNormalCard.Suit suit;
	public static int min;
	public static int max;

	public static boolean isActive = false;

	public NormalCardDiscoveryAction() {
		this(null, 1, 10);
	}

	public NormalCardDiscoveryAction(TrumpNormalCard.Suit suit) {
		this(suit, 1, 10);
	}

	public NormalCardDiscoveryAction(int min, int max) {
		this(null, min, max);
	}

	public NormalCardDiscoveryAction(TrumpNormalCard.Suit suit, int min, int max) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		NormalCardDiscoveryAction.suit = suit;
		NormalCardDiscoveryAction.min = min;
		NormalCardDiscoveryAction.max = max;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			isActive = true;
			AbstractDungeon.cardRewardScreen.discoveryOpen();

			this.tickDuration();
		} else {
			if (!this.retrieveCard) {
				if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
					AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
					disCard.current_x = -1000.0F * Settings.scale;
					if (AbstractDungeon.player.hand.size() < 10) {
						AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
					} else {
						AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
					}

					AbstractDungeon.cardRewardScreen.discoveryCard = null;
					isActive = false;
				}

				this.retrieveCard = true;
			}
			this.tickDuration();
		}
	}
}
