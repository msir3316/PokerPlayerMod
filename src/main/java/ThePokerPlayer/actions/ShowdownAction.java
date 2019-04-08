package ThePokerPlayer.actions;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.powers.*;
import ThePokerPlayer.relics.ClubPass;
import ThePokerPlayer.vfx.ShowdownEffect;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;

import static ThePokerPlayer.actions.PokerCardChangeAction.Mode.RANK_CHANGE_ANY;
import static ThePokerPlayer.actions.PokerCardChangeAction.Mode.RANK_CHANGE_SET;
import static ThePokerPlayer.vfx.ShowdownEffect.EFFECT_DUR;

public class ShowdownAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerHands"));
	public static final String[] TEXT = uiStrings.TEXT;

	private AbstractPlayer p;
	public static ArrayList<PokerCard> pokerCards = new ArrayList<>();
	public static ArrayList<AbstractCard> otherCards = new ArrayList<>();
	public static final float START_DELAY = 0.4f;
	public static final float TALK_DUR = 2.5f;
	public static final float DUR_DELTA = 0.05f;
	public static final float DUR = EFFECT_DUR + DUR_DELTA * 2;

	public static final int MULTIPLIER = 25;

	public static LinkedList<ImmutablePair<PokerCard.Suit, AbstractMonster>> pendingEffects = new LinkedList<>();
	public static boolean onAction;

	private float timer;
	private int index;
	private static int[] partial;
	private boolean init;

	public static int[] pow = new int[4];
	public static int[] powView = new int[4];
	public static int hand;
	public static int modifier;
	public static boolean flush;

	boolean discardAtEnd;

	public ShowdownAction(boolean discardAtEnd) {
		this.p = AbstractDungeon.player;
		this.duration = EFFECT_DUR;
		this.actionType = ActionType.SPECIAL;
		this.timer = START_DELAY;
		this.index = 0;
		this.init = false;
		this.discardAtEnd = discardAtEnd;
	}

	public static void calculateShowdown() {
		ShowdownAction.pokerCards.clear();
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c instanceof PokerCard) {
				ShowdownAction.pokerCards.add((PokerCard) c);
			}
		}
		if (PokerCardChangeAction.ref != null) {
			if (PokerCardChangeAction.ref.mode == RANK_CHANGE_ANY &&
					PokerCardChangeAction.ref.rankChange > 0 &&
					AbstractDungeon.handCardSelectScreen.upgradePreviewCard instanceof PokerCard) {
				ShowdownAction.pokerCards.add((PokerCard) AbstractDungeon.handCardSelectScreen.upgradePreviewCard);
			} else if (PokerCardChangeAction.ref.mode == RANK_CHANGE_SET) {
				for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
					if (c instanceof PokerCard) {
						PokerCard pc = (PokerCard) c;
						ShowdownAction.pokerCards.add(new PokerCard(pc.suit, PokerCardChangeAction.ref.rankChange));
					}
				}
			}
		}
		int[] nums = new int[11];
		int[] suits = new int[4];

		pow = new int[4];
		powView = new int[4];
		partial = new int[4];
		for (PokerCard card : pokerCards) {
			pow[card.suit.value] += card.rank;

			if (card.suit == PokerCard.Suit.Diamond && AbstractDungeon.player.hasPower(SharpenPower.POWER_ID)) {
				pow[PokerCard.Suit.Diamond.value] += AbstractDungeon.player.getPower(SharpenPower.POWER_ID).amount;
			}
			if (card.suit == PokerCard.Suit.Club) {
				if (AbstractDungeon.player.hasPower(ClubShadePower.POWER_ID)) {
					pow[PokerCard.Suit.Spade.value] += AbstractDungeon.player.getPower(ClubShadePower.POWER_ID).amount;
				}
				if (AbstractDungeon.player.hasRelic(ClubPass.ID)) {
					pow[PokerCard.Suit.Club.value] += AbstractDungeon.player.getRelic(ClubPass.ID).counter;
				}
			}

			nums[card.rank]++;
			suits[card.suit.value]++;
		}

		hand = 0;
		flush = false;
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

		if (hand >= 2 && AbstractDungeon.player.hasPower(GamblerFormPower.POWER_ID)) {
			for (PokerCard card : pokerCards) {
				pow[card.suit.value] += AbstractDungeon.player.getPower(GamblerFormPower.POWER_ID).amount;
			}
		}

		int straightModifier = 1;
		if (AbstractDungeon.player.hasPower(DamnStraightPower.POWER_ID)) {
			straightModifier <<= AbstractDungeon.player.getPower(DamnStraightPower.POWER_ID).amount;
		}
		if (hand < 5 || straightModifier > 1) {
			for (int i = 1; i <= 6; i++) {
				if (nums[i] >= 1 && nums[i + 1] >= 1 && nums[i + 2] >= 1 && nums[i + 3] >= 1 && nums[i + 4] >= 1) {
					hand = 5;
					break;
				}
			}
		}

		int flushThreshold = 5;
		if (AbstractDungeon.player.hasPower(FakeSymbolsPower.POWER_ID)) {
			flushThreshold -= AbstractDungeon.player.getPower(FakeSymbolsPower.POWER_ID).amount;
		}
		for (int i = 0; i < 4; i++) {
			if (suits[i] >= flushThreshold) {
				flush = true;
			}
		}

		modifier = modifierByHand(hand);
		if (flush) modifier += flushModifier();

		for (int i = 0; i < 4; i++)
			powView[i] = pow[i] * (100 + modifier) / 100;
	}

	public static int modifierByHand(int hand) {
		int result = hand * MULTIPLIER;
		if (hand == 5) {
			int straightModifier = 1;
			if (AbstractDungeon.currMapNode != null &&
					AbstractDungeon.getCurrRoom() != null &&
					AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT &&
					AbstractDungeon.player.hasPower(DamnStraightPower.POWER_ID)) {
				straightModifier <<= AbstractDungeon.player.getPower(DamnStraightPower.POWER_ID).amount;
			}
			result *= straightModifier;
		}
		return result;
	}

	public static int flushModifier() {
		return 4 * MULTIPLIER;
	}

	public static String getHandName() {
		return flush ? (hand == 0 ? TEXT[10] : TEXT[hand] + TEXT[10]) : TEXT[hand];
	}

	@Override
	public void update() {
		if (AbstractDungeon.getCurrRoom().isBattleEnding()) {
			this.isDone = true;
			return;
		}
		if (!init) {
			calculateShowdown();
			if (pokerCards.isEmpty()) {
				this.isDone = true;
				return;
			}
			onAction = true;

			index = 0;
			init = true;

			String msg = getHandName() + TEXT[9];
			if (modifier > 0) msg += TEXT[11] + modifier + TEXT[12];
			AbstractDungeon.effectList.add(new SpeechBubble(p.dialogX, p.dialogY, TALK_DUR, msg, true));
		}

		timer -= Gdx.graphics.getDeltaTime();
		if (timer <= 0.0f && index < pow.length) {
			if (pow[index] > 0) {
				AbstractDungeon.effectList.add(
						new ShowdownEffect(PokerCard.Suit.values()[index])
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
				pokerCards.clear();
				onAction = false;
			}
		}
		for (PokerCard card : pokerCards) {
			card.target_y = Settings.HEIGHT * 0.1f;
			card.angle = 0;
		}
		this.tickDuration();

		if (isDone) {
			if (discardAtEnd) {
				for (PokerCard card : pokerCards) {
					if (AbstractDungeon.player.hand.contains(card)) {
						if (card.isEthereal) {
							AbstractDungeon.player.hand.moveToExhaustPile(card);
							card.exhaustOnUseOnce = false;
							card.freeToPlayOnce = false;
						} else {
							AbstractDungeon.player.hand.moveToDiscardPile(card);
						}
					}
				}
			}
			for (AbstractCard card : otherCards) {
				this.p.hand.addToTop(card);
			}
			pokerCards.clear();
			otherCards.clear();
			onAction = false;
		}
	}

	private void doEffect(PokerCard.Suit suit, AbstractMonster target) {
		partial[suit.value] += modifier;
		int eff = 1 + partial[suit.value] / 100;
		partial[suit.value] %= 100;

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
