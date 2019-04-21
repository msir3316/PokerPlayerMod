package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.PokerCardEndOfTurnAction;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.patches.CardTypeEnum;
import ThePokerPlayer.patches.PokerCardTypePatch;
import ThePokerPlayer.relics.AceCard;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PokerCard extends CustomCard {
	private static final String RAW_ID = "PokerCard";
	private static final String ID = PokerPlayerMod.makeID(RAW_ID); // This ID is never used for actual ID for poker pokerCards so don't use it.

	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;

	public static final String BLANK_IMG = PokerPlayerMod.GetCardPath(RAW_ID);

	public static final String[] SUIT_TO_RAW_ID = new String[]{"Heart", "Spade", "Club", "Diamond"};
	public static final Texture[] SUIT_TO_IMG = new Texture[]{
			PokerPlayerMod.loadTexture(PokerPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Heart.value])),
			PokerPlayerMod.loadTexture(PokerPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Spade.value])),
			PokerPlayerMod.loadTexture(PokerPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Club.value])),
			PokerPlayerMod.loadTexture(PokerPlayerMod.GetCardPath(SUIT_TO_RAW_ID[Suit.Diamond.value]))
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

	public static float typeWidthPoker;
	public static float typeOffsetPoker;

	public boolean aced = false;
	public int multiplier = 1;

	static {
		float d = 48.0F * Settings.scale;
		GlyphLayout gl = new GlyphLayout();
		gl.setText(FontHelper.cardTypeFont_L, PokerCardTypePatch.TEXT[0]);
		typeOffsetPoker = (gl.width - 48.0F * Settings.scale) / 2.0F;
		typeWidthPoker = (gl.width / d - 1.0F) * 2.0F + 1.0F;
	}


	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardTypeEnum.POKER;
	private static final CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final CardTarget TARGET = CardTarget.SELF;

	public enum Suit {
		Heart(0),
		Spade(1),
		Club(2),
		Diamond(3);

		public int value;

		Suit(int value) {
			this.value = value;
		}

		public Texture getImage() {
			return SUIT_TO_IMG[value];
		}
	}

	public String getCardName(Suit suit, int num) {
		return EXTENDED_DESCRIPTION[suit.value * 2] + num + EXTENDED_DESCRIPTION[suit.value * 2 + 1];
	}

	public String getCardDescription(Suit suit, int num) {
		return EXTENDED_DESCRIPTION[suit.value * 2 + 8] + num + EXTENDED_DESCRIPTION[suit.value * 2 + 9];
	}

	public Suit suit;
	public int rank;

	public static String getID(Suit suit, int num) {
		return PokerPlayerMod.makeID(SUIT_TO_RAW_ID[suit.value] + num);
	}

	public PokerCard(Suit suit, int rank, boolean isEthereal) {
		super(getID(suit, rank), NAME, BLANK_IMG, COST, DESCRIPTION, TYPE, COLOR, CardRarity.SPECIAL, TARGET);

		this.suit = suit;
		this.rank = MathUtils.clamp(rank, 1, 10);
		ReflectionHacks.setPrivate(this, AbstractCard.class, "bannerColor", CardHelper.getColor(168, 204, 159));

		initCard(isEthereal);
	}

	public PokerCard(Suit suit, int rank) {
		this(suit, rank, false);
	}

	public void initCard(boolean isEthereal) {
		this.name = getCardName(suit, rank);
		if (multiplier > 1) {
			this.name += EXTENDED_DESCRIPTION[17] + multiplier + EXTENDED_DESCRIPTION[18];
		}
		if (suit == Suit.Heart && !this.tags.contains(CardTags.HEALING)) {
			this.tags.add(CardTags.HEALING);
		}
		this.isEthereal = (suit == Suit.Heart) || isEthereal;
		this.rawDescription = getCardDescription(suit, rank);
		if (this.isEthereal) {
			this.rawDescription = EXTENDED_DESCRIPTION[16] + this.rawDescription;
		}

		this.initializeTitle();
		this.initializeDescription();
		this.cardID = getID(suit, rank);

		boolean aceCheck = rank == 1 && AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(AceCard.ID);
		if (aced != aceCheck) {
			aced = aceCheck;
			if (aceCheck) {
				modifyCostForCombat(-9);
			} else {
				this.costForTurn = this.cost = COST;
				this.isCostModified = false;
			}
		}
	}

	@Override
	public void triggerOnEndOfTurnForPlayingCard() {
		AbstractDungeon.actionManager.addToBottom(new PokerCardEndOfTurnAction(this));
	}

	@Override
	public AbstractCard makeCopy() {
		return new PokerCard(this.suit, this.rank, this.isEthereal);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
	}

	@Override
	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard card = super.makeStatEquivalentCopy();
		if (card instanceof PokerCard) {
			PokerCard pc = (PokerCard) card;
			pc.aced = this.aced;
			pc.multiplier = this.multiplier;
			pc.initCard(this.isEthereal);
		}
		return card;
	}

	public void renderSuit(SpriteBatch sb) {
		float drawX = this.current_x - SUIT_WIDTH / 2.0f;
		float drawY = this.current_y - SUIT_HEIGHT / 2.0f;
		float yOffset = 72.0f / 2.0f;

		for (int i = 0; i < rank; i++) {
			float scale = this.drawScale * Settings.scale * 2;
			float dx = OFFSETS_X[rank - 1][i] / 2.0f;
			float dy = OFFSETS_Y[rank - 1][i] / 2.0f;
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

	@SpireOverride
	protected void renderPortraitFrame(SpriteBatch sb, float x, float y) {
		try {
			Method method = AbstractCard.class.getDeclaredMethod("renderSkillPortrait", SpriteBatch.class, float.class, float.class);
			method.setAccessible(true);
			method.invoke(this, sb, x, y);
			float tWidth = typeWidthPoker;
			float tOffset = typeOffsetPoker;

			Method method2 = AbstractCard.class.getDeclaredMethod("renderDynamicFrame", SpriteBatch.class, float.class, float.class, float.class, float.class);
			method2.setAccessible(true);
			method2.invoke(this, sb, x, y, tOffset, tWidth);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			PokerPlayerMod.logger.warn("renderPortraitFrame Failed - " + ex.toString());
		}
	}

	@Override
	public Texture getCardBg() {
		return ImageMaster.CARD_SKILL_BG_SILHOUETTE;
	}

	@Override
	public void initializeDescription() {
		super.initializeDescription();
		if (!keywords.contains("pokerplayer:poker card")) {
			keywords.add(0, "pokerplayer:poker card");
		}
		if (!keywords.contains("pokerplayer:showdown")) {
			keywords.add(1, "pokerplayer:showdown");
		}
		keywords.remove(GameDictionary.ETHEREAL.NAMES[0]);
	}

	@Override
	public boolean canUpgrade() {
		return this.rank < 10;
	}

	@Override
	public void upgrade() {
		rankChange(1);
	}

	public void rankChange(int amount) {
		this.rank += amount;
		if (this.rank > 10) {
			this.rank = 10;
		} else if (this.rank < 1) {
			this.rank = 1;
		}
		initCard(this.isEthereal);
	}

	public void rankSet(int amount) {
		this.rank = amount;
		if (this.rank > 10) {
			this.rank = 10;
		} else if (this.rank < 1) {
			this.rank = 1;
		}
		initCard(this.isEthereal);
	}

	public void multiplyEffect(int mult) {
		this.multiplier *= mult;
		initCard(this.isEthereal);
	}
}
