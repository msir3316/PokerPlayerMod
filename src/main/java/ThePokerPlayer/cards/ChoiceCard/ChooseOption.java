package ThePokerPlayer.cards.ChoiceCard;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChooseOption extends CustomCard {
	private static final String RAW_ID = "ChooseOption";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetCardPath("Blank");

	private Runnable action;

	public ChooseOption(CustomCard card, String name, String description, Runnable action) {
		super(ID, name, card.textureImg, -2, description, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);

		baseDamage = card.baseDamage;
		baseBlock = card.baseBlock;
		magicNumber = baseMagicNumber = card.baseMagicNumber;

		this.action = action;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		action.run();
	}

	@Override
	public boolean canUpgrade() {
		return false;
	}

	@Override
	public void upgrade() {
	}
}
