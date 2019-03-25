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

import java.util.function.Predicate;

public class BottledPoker extends CustomRelic implements CustomBottleRelic, CustomSavable<Integer> {
	private static final String RAW_ID = "BottledPoker";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	private boolean cardSelected = false;
	private AbstractCard bottledCard;

	public BottledPoker() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.COMMON, LandingSound.CLINK);
	}

	@Override
	public Predicate<AbstractCard> isOnCard() {
		return BottledPokerField.inBottledPoker::get;
	}

	public void onEquip() {
		cardSelected = false;
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
		AbstractDungeon.gridSelectScreen.open(group,
				1, DESCRIPTIONS[1] + name + ".",
				false, false, false, false);
	}

	public void onUnequip() {
		if (bottledCard != null) {
			AbstractCard cardInDeck = AbstractDungeon.player.masterDeck.getSpecificCard(this.bottledCard);
			if (cardInDeck != null) {
				BottledPokerField.inBottledPoker.set(bottledCard, false);
			}
		}
	}

	@Override
	public Integer onSave() {
		return AbstractDungeon.player.masterDeck.group.indexOf(bottledCard);
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public void onLoad(Integer cardIndex) {
		if (cardIndex == null) {
			return;
		}
		if (cardIndex >= 0 && cardIndex < AbstractDungeon.player.masterDeck.group.size()) {
			bottledCard = AbstractDungeon.player.masterDeck.group.get(cardIndex);
			if (bottledCard != null) {
				BottledPokerField.inBottledPoker.set(bottledCard, true);
				setDescriptionAfterLoading();
			}
		}
	}

	private void setDescriptionAfterLoading() {
		description = this.DESCRIPTIONS[2] + FontHelper.colorString(this.bottledCard.name, "y") + this.DESCRIPTIONS[3];
		tips.clear();
		tips.add(new PowerTip(name, description));
		initializeTips();
	}

	@Override
	public void update() {
		super.update();
		if (!this.cardSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
			this.cardSelected = true;
			bottledCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
			BottledPokerField.inBottledPoker.set(bottledCard, true);
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.gridSelectScreen.selectedCards.clear();
			this.description = this.DESCRIPTIONS[2] + FontHelper.colorString(this.bottledCard.name, "y") + this.DESCRIPTIONS[3];
			this.tips.clear();
			this.tips.add(new PowerTip(this.name, this.description));
			this.initializeTips();
		}
	}

	@Override
	public AbstractRelic makeCopy() {
		return new BottledPoker();
	}
}
