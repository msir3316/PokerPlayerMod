package trumpMod.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import trumpMod.cards.TrumpNormalCard;
import trumpMod.vfx.TrumpNormalCardEffect;

import java.util.ArrayList;
import java.util.LinkedList;

import static trumpMod.vfx.TrumpNormalCardEffect.EFFECT_DUR;

public class TrumpNormalCardAction extends AbstractGameAction {
	private AbstractPlayer p;
	public static ArrayList<TrumpNormalCard> cards = new ArrayList<>();
	public static final float DUR_DELTA = 0.05f;
	public static final float DUR = EFFECT_DUR + DUR_DELTA * 2;

	public static LinkedList<ImmutablePair<TrumpNormalCard.Suit, AbstractMonster>> pendingEffects = new LinkedList<>();
	public static boolean onAction;

	private float timer;
	private int index;
	private static int[] pow;
	private boolean init;

	public TrumpNormalCardAction(TrumpNormalCard card) {
		this.p = AbstractDungeon.player;
		this.duration = EFFECT_DUR;
		this.actionType = ActionType.SPECIAL;
		this.timer = DUR_DELTA;
		this.index = 0;
		this.init = false;
		cards.add(card);
	}

	@Override
	public void update() {
		if (cards.isEmpty()) {
			this.isDone = true;
			return;
		}
		onAction = true;
		if (!init) {
			pow = new int[4];
			for (TrumpNormalCard card : cards) {
				pow[card.suit.value] += card.num;
			}
			index = 0;
			init = true;
		}

		timer -= Gdx.graphics.getDeltaTime();
		if (timer <= 0.0f && index < pow.length) {
			if (pow[index] > 0) {
				AbstractDungeon.effectList.add(
						new TrumpNormalCardEffect(TrumpNormalCard.Suit.values()[index])
				);
				timer = DUR_DELTA;
				this.duration = DUR;
				pow[index]--;
			} else {
				index++;
			}
		}
		if (pendingEffects.size() > 0) {
			Pair<TrumpNormalCard.Suit, AbstractMonster> smPair = pendingEffects.pop();

			doEffect(smPair.getKey(), smPair.getValue());

			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			}
		}
		for (AbstractCard card : cards) {
			if (card instanceof TrumpNormalCard) {
				card.target_y = Settings.HEIGHT * 0.10f;
				card.angle = 0;
			}
		}
		this.tickDuration();

		if (isDone) {
			for (AbstractCard card : cards) {
				if (AbstractDungeon.player.hand.contains(card)) {
					AbstractDungeon.player.hand.moveToDiscardPile(card);
				}
			}
			cards.clear();
			onAction = false;
		}
	}

	public void doEffect(TrumpNormalCard.Suit suit, AbstractMonster target) {
		switch (suit) {
			case Spade:
				AbstractDungeon.effectList.add(new FlashAtkImgEffect(p.hb.cX, p.hb.cY, AbstractGameAction.AttackEffect.SHIELD));
				p.addBlock(1);
				break;
			case Diamond:
				if (target != null && !target.isDying && target.currentHealth > 0 && !target.isEscaping) {
					AbstractDungeon.effectList.add(new FlashAtkImgEffect(
							target.hb.cX,
							target.hb.cY,
							AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
					target.damage(new DamageInfo(p, 1, DamageInfo.DamageType.THORNS));
				}
				break;
			case Heart:
				p.heal(1);
				break;
			case Clover:
				boolean first = true;
				for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); ++i) {
					AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
					if (m != null && !m.isDying && m.currentHealth > 0 && !m.isEscaping) {
						AbstractDungeon.effectList.add(new FlashAtkImgEffect(
								m.hb.cX,
								m.hb.cY,
								AbstractGameAction.AttackEffect.FIRE, !first));
						first = false;
						m.damage(new DamageInfo(p, 1, DamageInfo.DamageType.THORNS));
					}
				}
				break;
		}
	}
}
