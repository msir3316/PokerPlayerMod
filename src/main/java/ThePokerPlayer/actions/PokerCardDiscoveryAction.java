package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

public class PokerCardDiscoveryAction extends AbstractGameAction {
	private boolean retrieveCard = false;
	private AbstractCard shapeshiftCard;
	public static int choices;
	public static PokerCard.Suit suit;
	public static int min;
	public static int max;

	public static boolean isActive = false;

	public PokerCardDiscoveryAction(AbstractCard shapeshiftCard, int choices) {
		this(shapeshiftCard, choices, null, 1, 10);
	}

	public PokerCardDiscoveryAction(AbstractCard shapeshiftCard, int choices, PokerCard.Suit suit) {
		this(shapeshiftCard, choices, suit, 1, 10);
	}

	public PokerCardDiscoveryAction(AbstractCard shapeshiftCard, int choices, int min, int max) {
		this(shapeshiftCard, choices, null, min, max);
	}

	public PokerCardDiscoveryAction(AbstractCard shapeshiftCard, int choices, PokerCard.Suit suit, int min, int max) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		this.shapeshiftCard = shapeshiftCard;
		PokerCardDiscoveryAction.choices = choices;
		PokerCardDiscoveryAction.suit = suit;
		PokerCardDiscoveryAction.min = min;
		PokerCardDiscoveryAction.max = max;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			for (AbstractCard c : PokerPlayerMod.shapeshiftReturns.values()) {
				if (c.uuid == shapeshiftCard.uuid) {
					this.isDone = true;
					return;
				}
			}
			isActive = true;
			AbstractDungeon.cardRewardScreen.discoveryOpen();

			this.tickDuration();
		} else {
			if (!this.retrieveCard) {
				if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {

					AbstractDungeon.actionManager.addToTop(new PokerCardTransformAction(
							shapeshiftCard, AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy()));
					AbstractDungeon.cardRewardScreen.discoveryCard = null;

					isActive = false;

					this.retrieveCard = true;
					this.isDone = true;
				} else {
					AbstractDungeon.topPanel.unhoverHitboxes();
					AbstractDungeon.isScreenUp = true;
					AbstractDungeon.screen = AbstractDungeon.CurrentScreen.CARD_REWARD;
					AbstractDungeon.dynamicBanner.appear(CardRewardScreen.TEXT[1]);
					AbstractDungeon.overlayMenu.showBlackScreen();
					SkipCardButton skipButton = (SkipCardButton) ReflectionHacks.getPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "skipButton");
					SingingBowlButton bowlButton = (SingingBowlButton) ReflectionHacks.getPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "bowlButton");
					skipButton.hide();
					bowlButton.hide();
				}
			}
		}
	}
}
