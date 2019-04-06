package ThePokerPlayer.events;

import ThePokerPlayer.PokerPlayerMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BackToBasicsPoker extends AbstractImageEvent {
	private static final String RAW_ID = "BackToBasicsPoker";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	public static final String NAME = eventStrings.NAME;
	public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
	public static final String[] OPTIONS = eventStrings.OPTIONS;
	private BackToBasicsPoker.CUR_SCREEN screen;
	private boolean removeSelected;

	private enum CUR_SCREEN {
		INTRO,
		COMPLETE
	}

	private CardGroup removableCards;
	private CardGroup cloneableCards;

	public BackToBasicsPoker() {
		super(NAME, DESCRIPTIONS[0], "images/events/backToBasics.jpg");

		removableCards = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards());
		cloneableCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c.rarity == AbstractCard.CardRarity.BASIC) {
				cloneableCards.addToTop(c);
			}
		}

		this.screen = BackToBasicsPoker.CUR_SCREEN.INTRO;
		if (removableCards.size() > 0) {
			this.imageEventText.setDialogOption(OPTIONS[0]);
		} else {
			this.imageEventText.setDialogOption(OPTIONS[1], true);
		}
		if (cloneableCards.size() > 0) {
			this.imageEventText.setDialogOption(OPTIONS[2]);
		} else {
			this.imageEventText.setDialogOption(OPTIONS[3], true);
		}
		this.imageEventText.setDialogOption(OPTIONS[4]);
	}

	public void onEnterRoom() {
		if (Settings.AMBIANCE_ON) {
			CardCrawlGame.sound.play("EVENT_ANCIENT");
		}
	}

	public void update() {
		super.update();
		if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			if (removeSelected) {
				AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
				AbstractDungeon.effectList.add(new PurgeCardEffect(c));
				AbstractEvent.logMetricCardRemoval(ID, "Elegance", c);
				AbstractDungeon.player.masterDeck.removeCard(c);
				AbstractDungeon.gridSelectScreen.selectedCards.remove(c);
			} else {
				AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeStatEquivalentCopy();
				AbstractEvent.logMetricObtainCard(ID, "Copied", c);
				c.inBottleFlame = false;
				c.inBottleLightning = false;
				c.inBottleTornado = false;
				AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
			}
		}
	}

	protected void buttonEffect(int buttonPressed) {
		switch (this.screen) {
			case INTRO:
				switch (buttonPressed) {
					case 0:
						if (removableCards.size() > 0) {
							this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
							removeSelected = true;
							AbstractDungeon.gridSelectScreen.open(removableCards, 1, OPTIONS[5], false);
						}
						this.imageEventText.updateDialogOption(0, OPTIONS[7]);
						this.imageEventText.clearRemainingOptions();
						break;
					case 1:
						if (cloneableCards.size() > 0) {
							this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
							removeSelected = false;
							AbstractDungeon.gridSelectScreen.open(cloneableCards, 1, OPTIONS[6], false);
						}
						this.imageEventText.updateDialogOption(0, OPTIONS[7]);
						this.imageEventText.clearRemainingOptions();
						break;
					case 2:
						this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
						this.imageEventText.updateDialogOption(0, OPTIONS[7]);
						this.imageEventText.clearRemainingOptions();
						logMetricIgnored(ID);
						break;
				}

				this.screen = BackToBasicsPoker.CUR_SCREEN.COMPLETE;
				break;
			case COMPLETE:
				this.openMap();
		}
	}
}
