package ThePokerPlayer.events;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.SuperBite;
import ThePokerPlayer.cards.ThinkingTime;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.BloodVial;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class VampiresPoker extends AbstractImageEvent {
	private static final String RAW_ID = "Vampires";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
	private static final String NAME = eventStrings.NAME;
	private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
	private static final String[] OPTIONS = eventStrings.OPTIONS;

	private CurrentScreen curScreen = CurrentScreen.INTRO;
	private boolean hasVial;

	private enum CurrentScreen {
		INTRO, DONE
	}

	public VampiresPoker() {
		super(NAME, DESCRIPTIONS[0], "images/events/vampires.jpg");

		AbstractCard c = new ThinkingTime();
		c.update();
		imageEventText.setDialogOption(OPTIONS[0], c);
		this.hasVial = AbstractDungeon.player.hasRelic(BloodVial.ID);
		if (hasVial) {
			imageEventText.setDialogOption(OPTIONS[1] + (new BloodVial()).name + OPTIONS[2], new SuperBite());
		}
		imageEventText.setDialogOption(OPTIONS[3]);
	}

	@Override
	protected void buttonEffect(int buttonPressed) {
		AbstractCard c;
		switch (curScreen) {
			case INTRO:
				switch (buttonPressed) {
					case 0:
						c = new ThinkingTime();
						AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
						logMetricObtainCard(ID, "Think Hard", c);
						this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
						this.imageEventText.updateDialogOption(0, OPTIONS[4]);
						this.imageEventText.clearRemainingOptions();
						break;
					case 1:
						if (hasVial) {
							c = new SuperBite();
							AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
							logMetricObtainCard(ID, "Became a vampire (Super)", c);
							AbstractDungeon.player.loseRelic(BloodVial.ID);
							this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
							this.imageEventText.updateDialogOption(0, OPTIONS[4]);
							this.imageEventText.clearRemainingOptions();
							break;
						}
					default:
						this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
						this.imageEventText.updateDialogOption(0, OPTIONS[4]);
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
