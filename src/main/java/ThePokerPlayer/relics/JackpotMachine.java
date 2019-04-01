package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.interfaces.IShowdownEffect;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class JackpotMachine extends CustomRelic implements IShowdownEffect {

	private static final String RAW_ID = "JackpotMachine";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);
	public static final int DAMAGE = 52;

	public JackpotMachine() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.RARE, LandingSound.HEAVY);
	}

	@Override
	public void onShowdownStart() {
		int seven = 0;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c instanceof PokerCard && ((PokerCard) c).rank == 7) seven++;
		}
		if (seven >= 3) {
			AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, this));
			AbstractDungeon.actionManager.addToBottom(new WaitAction(0.1f));
			AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(DAMAGE, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
		}
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + DAMAGE + DESCRIPTIONS[1];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new JackpotMachine();
	}
}
