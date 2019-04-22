package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.patches.BottledPokerField;
import basemod.abstracts.CustomBottleRelic;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.function.Predicate;

public class BottledPoker extends CustomRelic implements CustomBottleRelic, CustomSavable<ArrayList<Integer>> {
	private static final String RAW_ID = "BottledPoker";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	private ArrayList<AbstractCard> bottledCards;

	public enum bottleStatus {
		None,
		GridOpen,
		GridSelected,
		Done
	}

	public static bottleStatus status = bottleStatus.None;

	private static int NUM = 3;

	public BottledPoker() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.CLINK);
	}

	@Override
	public Predicate<AbstractCard> isOnCard() {
		return BottledPokerField.inBottledPoker::get;
	}

	public void onEquip() {
		status = bottleStatus.None;
		if (AbstractDungeon.isScreenUp) {
			AbstractDungeon.dynamicBanner.hide();
			AbstractDungeon.overlayMenu.cancelButton.hide();
			AbstractDungeon.previousScreen = AbstractDungeon.screen;
		}
		AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
		CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (AbstractCard c : AbstractDungeon.player.masterDeck.getPurgeableCards().group) {
			if (c instanceof PokerCard) {
				group.addToTop(c);
			}
		}
		status = bottleStatus.GridOpen;
		AbstractDungeon.gridSelectScreen.open(group,
				NUM, true, DESCRIPTIONS[2] + name + ".");
	}

	public void onUnequip() {
		if (bottledCards != null) {
			for (AbstractCard c : bottledCards) {
				AbstractCard cardInDeck = AbstractDungeon.player.masterDeck.getSpecificCard(c);
				if (cardInDeck != null) {
					BottledPokerField.inBottledPoker.set(bottledCards, false);
				}
			}
		}
	}

	@Override
	public ArrayList<Integer> onSave() {
		ArrayList<Integer> result = new ArrayList<>();
		for (AbstractCard c : bottledCards) {
			result.add(AbstractDungeon.player.masterDeck.group.indexOf(c));
		}
		return result;
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + NUM + DESCRIPTIONS[1];
	}

	@Override
	public void onLoad(ArrayList<Integer> cardIndexes) {
		if (cardIndexes != null) {
			bottledCards = new ArrayList<>();
			for (int i : cardIndexes) {
				if (i < AbstractDungeon.player.masterDeck.group.size()) {
					AbstractCard c = AbstractDungeon.player.masterDeck.group.get(i);
					bottledCards.add(c);
					BottledPokerField.inBottledPoker.set(c, true);
				}
			}
			setDescriptionAfterLoading();
		}
		status = bottleStatus.Done;
	}

	private void setDescriptionAfterLoading() {
		description = this.DESCRIPTIONS[3];
		boolean first = true;
		for (AbstractCard c : bottledCards) {
			if (first) {
				description += FontHelper.colorString(c.name, "y");
				first = false;
			} else {
				description += this.DESCRIPTIONS[4] + FontHelper.colorString(c.name, "y");
			}
		}
		description += this.DESCRIPTIONS[5];

		tips.clear();
		tips.add(new PowerTip(name, description));
		initializeTips();
	}

	@Override
	public void update() {
		super.update();
		if (status == bottleStatus.GridSelected) {
			bottledCards = new ArrayList<>();
			bottledCards.addAll(AbstractDungeon.gridSelectScreen.selectedCards);
			for (AbstractCard c : bottledCards) {
				BottledPokerField.inBottledPoker.set(c, true);
			}
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			setDescriptionAfterLoading();
			status = bottleStatus.Done;
		}
	}

	@Override
	public AbstractRelic makeCopy() {
		return new BottledPoker();
	}
}
