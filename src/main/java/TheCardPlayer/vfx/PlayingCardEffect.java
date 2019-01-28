package TheCardPlayer.vfx;

import TheCardPlayer.actions.PlayingCardAction;
import TheCardPlayer.cards.PlayingCard;
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

	private PlayingCard.Suit suit;
	private float originX, originY;
	private AbstractPlayer p;

	private float rotation;
	private ArrayList<Vector2> dest1;
	private ArrayList<Vector2> dest2;
	private ArrayList<Vector2> curPos;
	AbstractMonster selected = null;

	public PlayingCardEffect(PlayingCard.Suit suit) {
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

	@Override
	public void update() {
		if (!this.init) {
			CardCrawlGame.sound.playV("CARD_DRAW_8", 0.8f);

			if (suit == PlayingCard.Suit.Heart || suit == PlayingCard.Suit.Spade) {
				addDests(p.hb);
			} else {
				int num = 0;
				for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
					if (!m.halfDead && !m.isDying && !m.isEscaping) {
						if (suit == PlayingCard.Suit.Clover) {
							addDests(m.hb);
						} else {
							if (AbstractDungeon.cardRandomRng.random(num) == 0) {
								selected = m;
							}
							num++;
						}
					}
				}
				if (suit == PlayingCard.Suit.Diamond) {
					if (selected == null) {
						isDone = true;
						return;
					}
					addDests(selected.hb);
				}
			}
			this.init = true;
		}

		this.duration -= Gdx.graphics.getDeltaTime();
		float t = (EFFECT_DUR - this.duration) / EFFECT_DUR;
		this.scale = 1.0f + 0.5f * t;

		if (t > 1) {
			this.isDone = true;
		} else {
			for (int i = 0; i < dest1.size(); i++) {
				float x, y;
				Vector2 v1 = dest1.get(i);
				if (t < 0.5f) {
					x = Interpolation.linear.apply(originX, v1.x, t * 2);
					y = Interpolation.linear.apply(originY, v1.y, t * 2);
				} else {
					Vector2 v2 = dest2.get(i);
					x = Interpolation.linear.apply(v1.x, v2.x, t * 2 - 1);
					y = Interpolation.linear.apply(v1.y, v2.y, t * 2 - 1);
				}

				curPos.set(i, new Vector2(x, y));
			}
		}

		if (isDone) {
			PlayingCardAction.pendingEffects.add(new ImmutablePair<>(suit, selected));
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE);
		for (int i = 0; i < curPos.size(); i++) {
			this.renderImg(sb, PlayingCard.SUIT_TO_IMG[suit.value], rotation, i);
		}
	}

	private void renderImg(SpriteBatch sb, Texture img, float rotation, int loc) {
		Vector2 v = curPos.get(loc);
		sb.draw(img, v.x, v.y,
				PlayingCard.SUIT_WIDTH / 2.0f,
				PlayingCard.SUIT_HEIGHT / 2.0f,
				PlayingCard.SUIT_WIDTH,
				PlayingCard.SUIT_HEIGHT,
				this.scale * 2, this.scale * 2, rotation, 0, 0,
				PlayingCard.SUIT_WIDTH,
				PlayingCard.SUIT_HEIGHT,
				false, false);
	}

	public void dispose() {
	}
}
