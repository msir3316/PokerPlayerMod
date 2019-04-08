package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class AceCard extends CustomRelic implements CustomSavable<Void> {

	private static final String RAW_ID = "AceCard";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	private static final int BLOCK = 2;

	public AceCard() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.BOSS, LandingSound.FLAT);
	}

	@Override
	public void onEquip() {
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c instanceof PokerCard && ((PokerCard) c).rank == 1) {
				c.modifyCostForCombat(-9);
			}
		}
	}

	@Override
	public Void onSave() {
		return null;
	}

	@Override
	public void onLoad(Void v) {
		if (AbstractDungeon.player != null) {
			for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
				if (c instanceof PokerCard && ((PokerCard) c).rank == 1) {
					c.modifyCostForCombat(-9);
				}
			}
		}
	}

	@Override
	public void onUseCard(AbstractCard card, UseCardAction action) {
		if (card instanceof PokerCard && ((PokerCard) card).rank == 1) {
			AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK));
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + BLOCK + DESCRIPTIONS[1];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new AceCard();
	}
}
