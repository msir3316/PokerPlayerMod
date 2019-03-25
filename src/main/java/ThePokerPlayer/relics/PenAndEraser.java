package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PenAndEraser extends CustomRelic {

	private static final String RAW_ID = "PenAndEraser";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);
	public static final int UPGRADE_AMOUNT = 2;

	public PenAndEraser() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.SHOP, LandingSound.CLINK);
	}

	public void onEquip() {
		ArrayList<PokerCard> upgradableCards = new ArrayList<>();
		ArrayList<PokerCard> upgradableCards2 = new ArrayList<>();

		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c instanceof PokerCard) {
				PokerCard pc = (PokerCard) c;
				if (pc.rank <= 8) {
					upgradableCards.add(pc);
				} else if (pc.rank == 9) {
					upgradableCards2.add(pc);
				}
			}
		}

		Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
		Collections.shuffle(upgradableCards2, new Random(AbstractDungeon.miscRng.randomLong()));
		upgradableCards.addAll(upgradableCards2);

		if (!upgradableCards.isEmpty()) {
			if (upgradableCards.size() == 1) {
				upgradableCards.get(0).rankChange(UPGRADE_AMOUNT, true);
				AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(upgradableCards.get(0).makeStatEquivalentCopy()));
				AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
			} else {
				upgradableCards.get(0).rankChange(UPGRADE_AMOUNT, true);
				upgradableCards.get(1).rankChange(UPGRADE_AMOUNT, true);
				AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
				AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(upgradableCards.get(0).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 20.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(upgradableCards.get(1).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 20.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
			}
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new PenAndEraser();
	}
}
