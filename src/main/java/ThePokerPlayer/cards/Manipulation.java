package ThePokerPlayer.cards;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ManipulationAction;
import ThePokerPlayer.patches.CardColorEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Manipulation extends CustomCard {
	private static final String RAW_ID = "Manipulation";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = PokerPlayerMod.GetCardPath(RAW_ID);
	private static final int COST = -1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final AbstractCard.CardType TYPE = CardType.ATTACK;
	private static final AbstractCard.CardColor COLOR = CardColorEnum.POKER_PLAYER_GRAY;
	private static final AbstractCard.CardRarity RARITY = CardRarity.UNCOMMON;
	private static final AbstractCard.CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 3;
	private static final int UPGRADE_BONUS = 2;

	public Manipulation() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		this.baseDamage = POWER;
	}

	public void use(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new ManipulationAction(p, m, this.damage, this.damageTypeForTurn, this.freeToPlayOnce, this.energyOnUse));
	}

	public AbstractCard makeCopy() {
		return new Manipulation();
	}

	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			this.upgradeDamage(UPGRADE_BONUS);
		}
	}
}
