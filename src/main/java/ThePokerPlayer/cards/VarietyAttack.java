package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.WristBlade;
import com.megacrit.cardcrawl.vfx.combat.GrandFinalEffect;

import java.util.ArrayList;

public class VarietyAttack extends CustomCard {
	private static final String RAW_ID = "VarietyAttack";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = 0;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.ATTACK;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.RARE;
	private static final AbstractCard.CardTarget TARGET = CardTarget.ALL_ENEMY;

	private static final int POWER = 3;
	private static final int UPGRADE_BONUS = 1;

	public VarietyAttack() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseDamage = POWER;
	}

	private int multiplier() {
		int result = 1;
		boolean[] haveSuit = new boolean[4];
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c instanceof PokerCard) {
				haveSuit[((PokerCard) c).suit.value] = true;
			}
		}
		for (boolean b : haveSuit) {
			if (b) {
				result <<= 1;
			}
		}
		return result;
	}

	@Override
	public void applyPowers() {
		super.applyPowers();
		int mult = multiplier();
		if (mult > 1) {
			this.damage *= mult;
			this.isDamageModified = true;
		}
	}

	@Override
	public void calculateCardDamage(AbstractMonster mo) {
		ArrayList<AbstractMonster> m = AbstractDungeon.getCurrRoom().monsters.monsters;
		float[] tmp = new float[m.size()];

		int mult = multiplier();

		for (int i = 0; i < tmp.length; ++i) {
			tmp[i] = (float) this.baseDamage;
		}

		for (int i = 0; i < tmp.length; ++i) {
			if (AbstractDungeon.player.hasRelic(WristBlade.ID) && (this.costForTurn == 0 || this.freeToPlayOnce)) {
				tmp[i] += 3.0F;
				if (this.baseDamage != (int) tmp[i]) {
					this.isDamageModified = true;
				}
			}

			for (AbstractPower p : AbstractDungeon.player.powers) {
				tmp[i] = p.atDamageGive(tmp[i], this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp[i]) {
					this.isDamageModified = true;
				}
			}

			if (mult > 1) {
				tmp[i] *= mult;
			}

			for (AbstractPower p : m.get(i).powers) {
				if (!m.get(i).isDying && !m.get(i).isEscaping) {
					tmp[i] = p.atDamageReceive(tmp[i], this.damageTypeForTurn);
				}
			}

			for (AbstractPower p : AbstractDungeon.player.powers) {
				tmp[i] = p.atDamageFinalGive(tmp[i], this.damageTypeForTurn);
				if (this.baseDamage != (int) tmp[i]) {
					this.isDamageModified = true;
				}
			}

			for (AbstractPower p : m.get(i).powers) {
				if (!m.get(i).isDying && !m.get(i).isEscaping) {
					tmp[i] = p.atDamageFinalReceive(tmp[i], this.damageTypeForTurn);
				}
			}

			if (tmp[i] < 0.0F) {
				tmp[i] = 0.0F;
			}
		}

		this.multiDamage = new int[tmp.length];

		for (int i = 0; i < tmp.length; ++i) {
			this.multiDamage[i] = MathUtils.floor(tmp[i]);
		}
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		if (multiplier() > 10) {
			AbstractDungeon.actionManager.addToBottom(new VFXAction(new GrandFinalEffect(), 1.0F));
		}
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HEAVY));
	}

	public AbstractCard makeCopy() {
		return new VarietyAttack();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			this.upgradeDamage(UPGRADE_BONUS);
		}
	}
}
