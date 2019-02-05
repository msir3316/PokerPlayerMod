package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class ProtectiveDeckHolder extends CustomRelic {

	public static final String ID = PokerPlayerMod.makeID("ProtectiveDeckHolder");
	public static final String IMG = PokerPlayerMod.makePath(PokerPlayerMod.PLACEHOLDER_RELIC);
	public static final String OUTLINE = PokerPlayerMod.makePath(PokerPlayerMod.PLACEHOLDER_RELIC_OUTLINE);

	public ProtectiveDeckHolder() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.STARTER, LandingSound.MAGICAL);
	}

	@Override
	public void onCardDraw(AbstractCard drawnCard) {
		if (drawnCard.type == AbstractCard.CardType.STATUS && AbstractDungeon.cardRandomRng.randomBoolean()) {
			AbstractDungeon.actionManager.addToBottom(new ExhaustSpecificCardAction(drawnCard, AbstractDungeon.player.hand));
			AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new ProtectiveDeckHolder();
	}
}
