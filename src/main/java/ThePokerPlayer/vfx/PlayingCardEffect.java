package ThePokerPlayer.vfx;

import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;

public class PlayingCardEffect extends AbstractGameEffect {
	public static final float EFFECT_DUR = 0.8f;
	private boolean init = false;
	public static final float randomXRange = 100.0f;
	public static final float height = 250.0f;

	private PokerCard.Suit suit;
	private float originX, originY;
	private AbstractPlayer p;

	private float rotation;
	private ArrayList<Vector2> dest1;
	private ArrayList<Vector2> dest2;
	private ArrayList<Vector2> curPos;
	private float curA;
	AbstractMonster selected = null;

	public PlayingCardEffect(PokerCard.Suit suit) {
		this.duration = EFFECT_DUR;

		p = AbstractDungeon.player;
		this.originX = p.hb.cX;
		this.originY = p.hb.cY;

		this.suit = suit;
		rotation = MathUtils.random(1000.0f);
		dest1 = new ArrayList<>();
		dest2 = new ArrayList<>();
		curPos = new ArrayList<>();
	}

	void addDests(Hitbox hb) {
		dest1.add(new Vector2(
				originX + MathUtils.random(-randomXRange, randomXRange) * Settings.scale,
				originY + height * MathUtils.random(0.7f, 1.0f) * Settings.scale));
		dest2.add(new Vector2(hb.cX, hb.cY));
		curPos.add(new Vector2(originX, originY));
	}

	AbstractMonster getTarget() {
		int min = 0;

		AbstractMonster result = null;
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDeadOrEscaped()) {
				if (suit == PokerCard.Suit.Club) {
					addDests(m.hb);
				} else {
					if (result == null || min > m.currentHealth) {
						result = m;
						min = m.currentHealth;
					}
				}
			}
		}
		return result;
	}

	@Override
	public void update() {
		if (!this.init) {
			CardCrawlGame.sound.playV("CARD_DRAW_8", 0.8f);

			if (suit == PokerCard.Suit.Heart || suit == PokerCard.Suit.Spade) {
				addDests(p.hb);
			} else {
				selected = getTarget();
				if (suit == PokerCard.Suit.Diamond) {
					if (selected == null) {
						isDone = true;
						return;
					}
					addDests(selected.hb);
				}
			}
			curA = 0;
			this.init = true;
		} else if (suit == PokerCard.Suit.Diamond) {
			if (selected.isDeadOrEscaped()) {
				AbstractMonster m = getTarget();
				if (m != null) selected = m;
			}
		}

		this.duration -= Gdx.graphics.getDeltaTime();
		float t = (EFFECT_DUR - this.duration) / EFFECT_DUR;
		this.scale = 1.0f + 0.5f * t * Settings.scale;

		if (t > 1) {
			this.isDone = true;
		} else {
			for (int i = 0; i < dest1.size(); i++) {
				float x, y;
				if (t < 0.5f) {
					Vector2 v1 = dest1.get(i);
					x = Interpolation.linear.apply(originX, v1.x, t * 2);
					y = Interpolation.linear.apply(originY, v1.y, t * 2);
				} else {
					float a = t * 2 - 1;
					a *= a;

					float p = (curA > 0.999f ? 1 : (a - curA) / (1 - curA));

					Vector2 vc = curPos.get(i);
					Vector2 v2 = dest2.get(i);
					x = Interpolation.linear.apply(vc.x, v2.x, p);
					y = Interpolation.linear.apply(vc.y, v2.y, p);
					curA = a;
				}

				curPos.set(i, new Vector2(x, y));
			}
		}

		if (isDone) {
			ShowdownAction.pendingEffects.add(new ImmutablePair<>(suit, selected));
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE.cpy());
		for (int i = 0; i < curPos.size(); i++) {
			this.renderImg(sb, PokerCard.SUIT_TO_IMG[suit.value], rotation, i);
		}
	}

	private void renderImg(SpriteBatch sb, Texture img, float rotation, int loc) {
		Vector2 v = curPos.get(loc);
		sb.draw(img, v.x, v.y,
				PokerCard.SUIT_WIDTH / 2.0f,
				PokerCard.SUIT_HEIGHT / 2.0f,
				PokerCard.SUIT_WIDTH,
				PokerCard.SUIT_HEIGHT,
				this.scale * 2, this.scale * 2, rotation, 0, 0,
				PokerCard.SUIT_WIDTH,
				PokerCard.SUIT_HEIGHT,
				false, false);
	}

	public void dispose() {
	}
}
