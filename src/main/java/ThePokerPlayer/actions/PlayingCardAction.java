package ThePokerPlayer.actions;

import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.vfx.PlayingCardEffect;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;

import static ThePokerPlayer.vfx.PlayingCardEffect.EFFECT_DUR;

public class PlayingCardAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("PokerPlayerMod:PokerHands");
	public static final String[] TEXT = uiStrings.TEXT;

	private AbstractPlayer p;
	public static ArrayList<PokerCard> cards = new ArrayList<>();
	public static final float START_DELAY = 0.4f;
	public static final float TALK_DUR = 2.5f;
	public static final float DUR_DELTA = 0.05f;
	public static final float DUR = EFFECT_DUR + DUR_DELTA * 2;

	public static LinkedList<ImmutablePair<PokerCard.Suit, AbstractMonster>> pendingEffects = new LinkedList<>();
	public static boolean onAction;

	private float timer;
	private int index;
	private static int[] pow;
	private static boolean[] parity;
	private boolean init;

	private int hand = 0;
	private int modifier = 0;
	private boolean flush = false;

	public PlayingCardAction(PokerCard card) {
		this.p = AbstractDungeon.player;
		this.duration = EFFECT_DUR;
		this.actionType = ActionType.SPECIAL;
		this.timer = START_DELAY;
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
			int[] nums = new int[11];
			int[] suits = new int[4];

			pow = new int[4];
			parity = new boolean[4];
			for (PokerCard card : cards) {
				pow[card.suit.value] += card.rank;
				nums[card.rank]++;
				suits[card.suit.value]++;
			}
			index = 0;
			init = true;

			for (int i = 1; i <= 10; i++) {
				if (nums[i] >= 5) {
					hand = 8;
				} else if (nums[i] == 4) {
					if (hand < 6) hand = 6;
				} else if (nums[i] == 3) {
					if (hand >= 1 && hand <= 3) hand = 4;
					else if (hand < 1) hand = 3;
				} else if (nums[i] == 2) {
					if (hand <= 3 && hand != 2) hand++;
				}
			}

			if (hand < 5) {
				for (int i = 1; i <= 6; i++) {
					if (nums[i] >= 1 && nums[i + 1] >= 1 && nums[i + 2] >= 1 && nums[i + 3] >= 1 && nums[i + 4] >= 1) {
						hand = 5;
						break;
					}
				}
			}

			for (int i = 0; i < 4; i++) {
				if (suits[i] >= 5) {
					flush = true;
				}
			}

			modifier = (hand + (flush ? 3 : 0)) * 50;
			String msg = flush ? (hand == 0 ? TEXT[10] : TEXT[hand] + TEXT[10]) : TEXT[hand] + TEXT[9];
			if (modifier > 0) msg += TEXT[11] + modifier + TEXT[12];

			AbstractDungeon.effectList.add(new SpeechBubble(p.dialogX, p.dialogY, TALK_DUR, msg, true));
		}

		timer -= Gdx.graphics.getDeltaTime();
		if (timer <= 0.0f && index < pow.length) {
			if (pow[index] > 0) {
				AbstractDungeon.effectList.add(
						new PlayingCardEffect(PokerCard.Suit.values()[index])
				);
				timer = DUR_DELTA;
				this.duration = DUR;
				pow[index]--;
			} else {
				index++;
			}
		}
		if (pendingEffects.size() > 0) {
			Pair<PokerCard.Suit, AbstractMonster> smPair = pendingEffects.pop();

			doEffect(smPair.getKey(), smPair.getValue());

			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
				cards.clear();
				onAction = false;
			}
		}
		for (AbstractCard card : cards) {
			if (card instanceof PokerCard) {
				card.target_y = Settings.HEIGHT * 0.1f;
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

	private void doEffect(PokerCard.Suit suit, AbstractMonster target) {
		int eff = 1 + (parity[suit.value] ? (modifier / 50 + 1) / 2 : modifier / 50 / 2);
		parity[suit.value] = !parity[suit.value];
		switch (suit) {
			case Spade:
				AbstractDungeon.effectList.add(new FlashAtkImgEffect(p.hb.cX, p.hb.cY, AbstractGameAction.AttackEffect.SHIELD));
				p.addBlock(eff);
				break;
			case Diamond:
				if (target != null && !target.isDying && target.currentHealth > 0 && !target.isEscaping) {
					AbstractDungeon.effectList.add(new FlashAtkImgEffect(
							target.hb.cX,
							target.hb.cY,
							AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
					target.damage(new DamageInfo(p, eff, DamageInfo.DamageType.THORNS));
				}
				break;
			case Heart:
				p.heal(eff);
				break;
			case Club:
				boolean first = true;
				for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); ++i) {
					AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
					if (m != null && !m.isDying && m.currentHealth > 0 && !m.isEscaping) {
						AbstractDungeon.effectList.add(new FlashAtkImgEffect(
								m.hb.cX,
								m.hb.cY,
								AbstractGameAction.AttackEffect.FIRE, !first));
						first = false;
						m.damage(new DamageInfo(p, eff, DamageInfo.DamageType.THORNS));
					}
				}
				break;
		}
	}
}
