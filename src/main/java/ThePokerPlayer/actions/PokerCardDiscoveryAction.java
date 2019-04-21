package ThePokerPlayer.actions;

import ThePokerPlayer.cards.ChoiceCard.BrokenClockChoice;
import ThePokerPlayer.cards.PokerCard;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

public class PokerCardDiscoveryAction extends AbstractGameAction {
	private boolean retrieveCard = false;
	public static int choices;
	public static PokerCard.Suit suit;
	public static int min;
	public static int max;
	public static boolean ethereal;

	public static boolean isActive = false;

	public PokerCardDiscoveryAction(int choices) {
		this(choices, null, 1, 10, true);
	}

	public PokerCardDiscoveryAction(int choices, PokerCard.Suit suit) {
		this(choices, suit, 1, 10, true);
	}

	public PokerCardDiscoveryAction(int choices, int min, int max) {
		this(choices, null, min, max, true);
	}

	// suit == null -> non-Heart
	public PokerCardDiscoveryAction(int choices, PokerCard.Suit suit, int min, int max, boolean ethereal) {
		this.actionType = ActionType.CARD_MANIPULATION;
		this.duration = Settings.ACTION_DUR_FAST;
		PokerCardDiscoveryAction.choices = choices;
		PokerCardDiscoveryAction.suit = suit;
		PokerCardDiscoveryAction.min = min;
		PokerCardDiscoveryAction.max = max;
		PokerCardDiscoveryAction.ethereal = ethereal;
	}

	public void update() {
		if (this.duration == Settings.ACTION_DUR_FAST) {
			isActive = true;
			AbstractDungeon.cardRewardScreen.discoveryOpen();

			this.tickDuration();
		} else {
			if (!this.retrieveCard) {
				if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {

					if (AbstractDungeon.cardRewardScreen.discoveryCard instanceof BrokenClockChoice) {
						AbstractDungeon.actionManager.addToTop(new DrawCardAction(AbstractDungeon.player, 1));
					} else {
						AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(
								AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy(), true));
					}
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
