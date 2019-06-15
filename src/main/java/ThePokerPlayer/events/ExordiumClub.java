package ThePokerPlayer.events;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class ExordiumClub extends AbstractImageEvent {
	private static final String RAW_ID = "ExordiumClub";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	private static final String NAME = eventStrings.NAME;
	private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
	private static final String[] OPTIONS = eventStrings.OPTIONS;

	private CurrentScreen curScreen = CurrentScreen.INTRO;

	private enum CurrentScreen {
		INTRO, DONE
	}

	private CardGroup tradableCards;

	public ExordiumClub() {
		super(NAME, DESCRIPTIONS[0], PokerPlayerMod.GetEventPath(RAW_ID));

		tradableCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c instanceof PokerCard) {
				tradableCards.addToTop(c);
			}
		}

		imageEventText.setDialogOption(OPTIONS[0], new PokerCard(PokerCard.Suit.Club, 6));
		if (tradableCards.size() > 0) {
			imageEventText.setDialogOption(OPTIONS[1]);
		} else {
			imageEventText.setDialogOption(OPTIONS[2], true);
		}
		imageEventText.setDialogOption(OPTIONS[3]);
	}

	@Override
	public void update() {
		super.update();

		if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			AbstractDungeon.player.masterDeck.removeCard(c);

			PokerCard replacement = new PokerCard(
					PokerCard.Suit.Club,
					AbstractDungeon.miscRng.random(1, 10));
			AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(replacement, com.megacrit.cardcrawl.core.Settings.WIDTH / 2.0F, com.megacrit.cardcrawl.core.Settings.HEIGHT / 2.0F));
			AbstractDungeon.gridSelectScreen.selectedCards.clear();

			logMetricObtainCardAndLoseCard(ID, "Trade", replacement, c);
		}
	}

	@Override
	protected void buttonEffect(int buttonPressed) {
		switch (curScreen) {
			case INTRO:
				switch (buttonPressed) {
					case 0:
						ArrayList<String> cards = new ArrayList<>();
						for (int i = 2; i <= 6; ++i) {
							AbstractCard c = new PokerCard(PokerCard.Suit.Club, i);
							AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH * (i * 0.15F - 0.1f), (float) Settings.HEIGHT / 2.0F));
							cards.add(c.cardID);
							logMetricObtainCards(ID, "Jackpot", cards);
						}
						this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
						this.imageEventText.updateDialogOption(0, OPTIONS[5]);
						this.imageEventText.clearRemainingOptions();
						break;
					case 1:
						if (tradableCards.size() > 0) {
							this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
							AbstractDungeon.gridSelectScreen.open(tradableCards, 1, OPTIONS[4], false);
						}
						this.imageEventText.updateDialogOption(0, OPTIONS[5]);
						this.imageEventText.clearRemainingOptions();
						break;
					case 2:
						this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
						this.imageEventText.updateDialogOption(0, OPTIONS[5]);
						this.imageEventText.clearRemainingOptions();
						logMetricIgnored(ID);
						break;
				}
				curScreen = CurrentScreen.DONE;
				break;
			case DONE:
				openMap();
				break;
		}
	}
}
