package ThePokerPlayer.relics;

import ThePokerPlayer.PokerPlayerMod;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BrokenClock extends CustomRelic {

	private static final String RAW_ID = "BrokenClock";
	public static final String ID = PokerPlayerMod.makeID(RAW_ID);
	public static final String IMG = PokerPlayerMod.GetRelicPath(RAW_ID);
	public static final String OUTLINE = PokerPlayerMod.GetRelicOutlinePath(RAW_ID);

	public BrokenClock() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.CLINK);
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new BrokenClock();
	}
}
