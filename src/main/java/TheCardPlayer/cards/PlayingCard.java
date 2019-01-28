package TheCardPlayer.cards;

import TheCardPlayer.actions.PlayingCardAction;
import TheCardPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import TheCardPlayer.CardPlayerMod;

public class PlayingCard extends CustomCard {
	private static final String RAW_ID = "PlayingCard";
	public static final String ID = CardPlayerMod.makeID(RAW_ID);

	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;

	public static final String BLANK_IMG = CardPlayerMod.GetCardPath(RAW_ID);

	public static final String[] SUIT_TO_RAW_ID = new String[]{"Heart", "Spade", "Clover", "Diamond"};
	public static final Texture[] SUIT_TO_IMG = new Texture[]{
			CardPlayerMod.loadTexture(CardPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Heart.value])),
			CardPlayerMod.loadTexture(CardPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Spade.value])),
			CardPlayerMod.loadTexture(CardPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Clover.value])),
			CardPlayerMod.loadTexture(CardPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Diamond.value]))
	};

	public static float X_LIMIT = 60.0f;
	public static float Y_LIMIT = 40.0f;
	public static final float[][] OFFSETS_X = new float[][]{
			new float[]{0},
			new float[]{-X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, 0, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, 0, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, 0, 0, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, -X_LIMIT / 2, 0, 0, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, -X_LIMIT / 2, 0, 0, X_LIMIT / 2, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, -X_LIMIT / 3, -X_LIMIT / 3, 0, X_LIMIT / 3, X_LIMIT / 3, X_LIMIT, X_LIMIT},
			new float[]{-X_LIMIT, -X_LIMIT, -2 * X_LIMIT / 3, -X_LIMIT / 3, -X_LIMIT / 3, X_LIMIT / 3, X_LIMIT / 3, 2 * X_LIMIT / 3, X_LIMIT, X_LIMIT}
	};
	public static final float[][] OFFSETS_Y = new float[][]{
			new float[]{0},
			new float[]{0, 0},
			new float[]{0, 0, 0},
			new float[]{-Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT},
			new float[]{-Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT, -Y_LIMIT, Y_LIMIT, 0, -Y_LIMIT, Y_LIMIT}
	};
	public static final int SUIT_WIDTH = 17;
	public static final int SUIT_HEIGHT = 17;
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CardColorEnum.CARD_PLAYER_GRAY;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int NEW_COST = 1;

	public enum Suit {
		Heart(0),
		Spade(1),
		Clover(2),
		Diamond(3);

		public int value;

		Suit(int value) {
			this.value = value;
		}

		public Texture getImage() {
			return SUIT_TO_IMG[value];
		}

		public String toString() {
			return EXTENDED_DESCRIPTION[value];
		}
	}

	public Suit suit;
	public int num;

	public static String getID(Suit suit, int num) {
		return CardPlayerMod.makeID(SUIT_TO_RAW_ID[suit.value] + num);
	}

	public static CardRarity getRarity(Suit suit, int num) {
		switch (suit) {
			case Spade:
				if (num == 5 || num >= 8)
					return CardRarity.UNCOMMON;
				else return CardRarity.COMMON;
			case Diamond:
				if (num == 5 || num == 10)
					return CardRarity.UNCOMMON;
				else return CardRarity.COMMON;
			case Heart:
				if (num <= 2)
					return CardRarity.COMMON;
				else if (num < 5)
					return CardRarity.UNCOMMON;
				else return CardRarity.RARE;
			case Clover:
				if (num <= 3)
					return CardRarity.COMMON;
				else if (num <= 7 && num != 5)
					return CardRarity.UNCOMMON;
				else return CardRarity.RARE;
			default:
				return CardRarity.SPECIAL;
		}
	}

	public PlayingCard(Suit suit, int num) {
		super(getID(suit, num), NAME, BLANK_IMG, COST, DESCRIPTION, TYPE, COLOR, getRarity(suit, num), TARGET);

		this.suit = suit;
		this.num = num;
		this.baseMagicNumber = num;
		this.magicNumber = this.baseMagicNumber;
		this.baseBlock = num;
		this.baseDamage = num;

		this.name = suit.toString() + " " + (num == 10 ? "π" : "") + num;
		this.rawDescription = this.name;
		this.initializeTitle();
		this.initializeDescription();
	}

	@Override
	public void triggerOnEndOfTurnForPlayingCard() {
		AbstractDungeon.actionManager.addToBottom(new PlayingCardAction(this));
		//doEffect(AbstractDungeon.player, this);
	}

	@Override
	public AbstractCard makeCopy() {
		return new PlayingCard(this.suit, this.num);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
	}

	public void renderSuit(SpriteBatch sb) {
		float drawX = this.current_x - SUIT_WIDTH / 2.0f;
		float drawY = this.current_y - SUIT_HEIGHT / 2.0f;
		float yOffset = 72.0f / 2.0f;

		for (int i = 0; i < num; i++) {
			float scale = this.drawScale * Settings.scale * 2;
			float dx = OFFSETS_X[num - 1][i] / 2.0f;
			float dy = OFFSETS_Y[num - 1][i] / 2.0f;
			sb.draw(
					suit.getImage(),
					drawX + dx,
					drawY + dy + yOffset,
					SUIT_WIDTH / 2.0f - dx,
					SUIT_HEIGHT / 2.0f - yOffset - dy,
					SUIT_WIDTH,
					SUIT_HEIGHT,
					scale,
					scale,
					this.angle, 0, 0, SUIT_WIDTH, SUIT_HEIGHT, false, false);
		}
	}

	@SpireOverride
	protected void renderImage(SpriteBatch sb, boolean hovered, boolean selected) {
		SpireSuper.call(sb, hovered, selected);
		renderSuit(sb);
	}

	@Override
	public void initializeDescription() {
		super.initializeDescription();
		if (!keywords.contains("normal card")) {
			keywords.add(0, "normal card");
		}
	}

	public void upgrade() {
		if (!this.upgraded) {
			this.upgradeName();
			this.upgradeBaseCost(NEW_COST);
		}
	}
}
