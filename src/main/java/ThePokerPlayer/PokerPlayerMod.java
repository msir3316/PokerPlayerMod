package ThePokerPlayer;

import ThePokerPlayer.actions.PlayingCardAction;
import ThePokerPlayer.cards.*;
import ThePokerPlayer.characters.ThePokerPlayer;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.patches.ThePokerPlayerEnum;
import ThePokerPlayer.relics.PlaceholderRelic2;
import ThePokerPlayer.relics.ProtectiveDeckHolder;
import ThePokerPlayer.variables.DefaultCustomVariable;
import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.abstracts.CustomCard;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpireInitializer
public class PokerPlayerMod
		implements EditCardsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber,
		EditCharactersSubscriber, PostInitializeSubscriber, OnStartBattleSubscriber {
	public static final Logger logger = LogManager.getLogger(PokerPlayerMod.class.getName());

	//This is for the in-game mod settings pannel.
	private static final String MODNAME = "The Poker Player";
	private static final String AUTHOR = "Celicath";
	private static final String DESCRIPTION = "Adds a character called The Poker Player, who fights with his own rule inspired from poker.";

	// =============== IMPUT TEXTURE LOCATION =================

	// Colors (RGB)
	// Character Color
	public static final Color POKER_PLAYER_GRAY = CardHelper.getColor(64.0f, 70.0f, 70.0f);

	// Image folder name
	private static final String POKER_PLAYER_MOD_ASSETS_FOLDER = "PokerPlayerMod/images";

	// Card backgrounds
	private static final String ATTACK_DEAFULT_GRAY = "512/bg_attack_default_gray.png";
	private static final String POWER_DEAFULT_GRAY = "512/bg_power_default_gray.png";
	private static final String SKILL_DEAFULT_GRAY = "512/bg_skill_default_gray.png";
	private static final String ENERGY_ORB_DEAFULT_GRAY = "512/card_default_gray_orb.png";
	private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";

	private static final String ATTACK_DEAFULT_GRAY_PORTRAIT = "1024/bg_attack_default_gray.png";
	private static final String POWER_DEAFULT_GRAY_PORTRAIT = "1024/bg_power_default_gray.png";
	private static final String SKILL_DEAFULT_GRAY_PORTRAIT = "1024/bg_skill_default_gray.png";
	private static final String ENERGY_ORB_DEAFULT_GRAY_PORTRAIT = "1024/card_default_gray_orb.png";

	// Card images
	public static final String DEFAULT_COMMON_ATTACK = "cards/Attack.png";
	public static final String DEFAULT_COMMON_SKILL = "cards/Skill.png";
	public static final String DEFAULT_COMMON_POWER = "cards/Power.png";
	public static final String DEFAULT_UNCOMMON_ATTACK = "cards/Attack.png";
	public static final String DEFAULT_UNCOMMON_SKILL = "cards/Skill.png";
	public static final String DEFAULT_UNCOMMON_POWER = "cards/Power.png";
	public static final String DEFAULT_RARE_ATTACK = "cards/Attack.png";
	public static final String DEFAULT_RARE_SKILL = "cards/Skill.png";
	public static final String DEFAULT_RARE_POWER = "cards/Power.png";

	// Power images
	public static final String COMMON_POWER = "powers/placeholder_power.png";
	public static final String UNCOMMON_POWER = "powers/placeholder_power.png";
	public static final String RARE_POWER = "powers/placeholder_power.png";

	// Relic images
	public static final String PLACEHOLDER_RELIC = "relics/placeholder_relic.png";
	public static final String PLACEHOLDER_RELIC_OUTLINE = "relics/outline/placeholder_relic.png";

	public static final String PLACEHOLDER_RELIC_2 = "relics/placeholder_relic2.png";
	public static final String PLACEHOLDER_RELIC_OUTLINE_2 = "relics/outline/placeholder_relic2.png";

	// Character assets
	private static final String THE_DEFAULT_BUTTON = "charSelect/DefaultCharacterButton.png";
	private static final String THE_DEFAULT_PORTRAIT = "charSelect/DeafultCharacterPortraitBG.png";
	public static final String THE_DEFAULT_SHOULDER_1 = "char/ThePokerPlayer/shoulder.png";
	public static final String THE_DEFAULT_SHOULDER_2 = "char/ThePokerPlayer/shoulder2.png";
	public static final String THE_DEFAULT_CORPSE = "char/ThePokerPlayer/corpse.png";

	//Mod Badge
	public static final String BADGE_IMAGE = "Badge.png";

	// Animations atlas and JSON files
	public static final String THE_DEFAULT_SKELETON_ATLAS = "char/ThePokerPlayer/skeleton.atlas";
	public static final String THE_DEFAULT_SKELETON_JSON = "char/ThePokerPlayer/skeleton.json";

	// Logics
	public static AbstractCard cardSelectScreenCard;
	public static float transformAnimTimer;

	// =============== /INPUT TEXTURE LOCATION/ =================

	/**
	 * Makes a full path for a resource path
	 *
	 * @param resource the resource, must *NOT* have a leading "/"
	 * @return the full path
	 */
	public static final String makePath(String resource) {
		return POKER_PLAYER_MOD_ASSETS_FOLDER + "/" + resource;
	}

	// =============== SUBSCRIBE, CREATE THE COLOR, INITIALIZE =================

	public PokerPlayerMod() {
		logger.info("Subscribe to basemod hooks");

		BaseMod.subscribe(this);

		logger.info("Done subscribing");

		logger.info("Creating the color " + CardColorEnum.POKER_PLAYER_GRAY.toString());

		BaseMod.addColor(CardColorEnum.POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY,
				POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, makePath(ATTACK_DEAFULT_GRAY),
				makePath(SKILL_DEAFULT_GRAY), makePath(POWER_DEAFULT_GRAY),
				makePath(ENERGY_ORB_DEAFULT_GRAY), makePath(ATTACK_DEAFULT_GRAY_PORTRAIT),
				makePath(SKILL_DEAFULT_GRAY_PORTRAIT), makePath(POWER_DEAFULT_GRAY_PORTRAIT),
				makePath(ENERGY_ORB_DEAFULT_GRAY_PORTRAIT), makePath(CARD_ENERGY_ORB));

		logger.info("Done Creating the color");
	}

	@SuppressWarnings("unused")
	public static void initialize() {
		logger.info("========================= Initializing Default Mod. Hi. =========================");
		PokerPlayerMod defaultmod = new PokerPlayerMod();
		logger.info("========================= /Default Mod Initialized/ =========================");
	}

	// ============== /SUBSCRIBE, CREATE THE COLOR, INITIALIZE/ =================


	// =============== LOAD THE CHARACTER =================

	@Override
	public void receiveEditCharacters() {
		logger.info("begin editing characters. " + "Add " + ThePokerPlayerEnum.THE_POKER_PLAYER.toString());

		BaseMod.addCharacter(new ThePokerPlayer(ThePokerPlayer.charStrings.NAMES[1], ThePokerPlayerEnum.THE_POKER_PLAYER),
				makePath(THE_DEFAULT_BUTTON), makePath(THE_DEFAULT_PORTRAIT), ThePokerPlayerEnum.THE_POKER_PLAYER);

		receiveEditPotions();
		logger.info("done editing characters");
	}

	// =============== /LOAD THE CHARACTER/ =================


	// =============== POST-INITIALIZE =================


	@Override
	public void receivePostInitialize() {

		logger.info("Load Badge Image and mod options");
		// Load the Mod Badge
		Texture badgeTexture = new Texture(makePath(BADGE_IMAGE));

		// Create the Mod Menu
		ModPanel settingsPanel = new ModPanel();
		settingsPanel.addUIElement(new ModLabel("PokerPlayerMod doesn't have any settings!", 400.0f, 700.0f,
				settingsPanel, (me) -> {
		}));
		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

		logger.info("Done loading badge Image and mod options");

	}

	// =============== / POST-INITIALIZE/ =================


	// ================ ADD POTIONS ===================


	public void receiveEditPotions() {
		logger.info("begin editing potions");

		logger.info("end editing potions");
	}

	// ================ /ADD POTIONS/ ===================


	// ================ ADD RELICS ===================

	@Override
	public void receiveEditRelics() {
		logger.info("Add relics");

		// This adds a character specific relic. Only when you play with the mentioned color, will you get this relic.
		BaseMod.addRelicToCustomPool(new ProtectiveDeckHolder(), CardColorEnum.POKER_PLAYER_GRAY);

		// This adds a relic to the Shared pool. Every character can find this relic.
		BaseMod.addRelic(new PlaceholderRelic2(), RelicType.SHARED);

		logger.info("Done adding relics!");
	}

	// ================ /ADD RELICS/ ===================


	// ================ ADD CARDS ===================

	@Override
	public void receiveEditCards() {
		// Add the Custom Dynamic Variables
		BaseMod.addDynamicVariable(new DefaultCustomVariable());

		// Add the cards
		List<CustomCard> cards = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			for (int j = 1; j <= 10; j++) {
				cards.add(new PokerCard(PokerCard.Suit.values()[i], j));
			}
		}
		cards.add(new DiamondStrike());
		cards.add(new BadJoker());
		cards.add(new Mulligan());

		cards.add(new CloakShield());
		cards.add(new Configure());
		cards.add(new WildCard());
		cards.add(new DartThrow());
		cards.add(new Manipulation());
		cards.add(new HotShotCut());
		cards.add(new ClubsClub());
		cards.add(new SecondChance());
		cards.add(new Trickery());

		cards.add(new DefaultRareAttack());
		cards.add(new DefaultUncommonPower());
		cards.add(new PowerPower());
		cards.add(new DefaultRarePower());

		for (CustomCard card : cards) {
			BaseMod.addCard(card);
			UnlockTracker.unlockCard(card.cardID);
		}
	}

	// ================ /ADD CARDS/ ===================


	// ================ LOAD THE TEXT ===================

	@Override
	public void receiveEditStrings() {
		logger.info("Begin editing strings");

		String loc = getLocCode();

		// RelicStrings
		String relicStrings = GetLocString(loc, "relics");
		BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
		// CardStrings
		String cardStrings = GetLocString(loc, "cards");
		BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
		// PotionStrings
		String potionStrings = GetLocString(loc, "potions");
		BaseMod.loadCustomStrings(PotionStrings.class, potionStrings);
		// PowerStrings
		String powerStrings = GetLocString(loc, "powers");
		BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
		// UIStrings
		String uiStrings = GetLocString(loc, "ui");
		BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
		// EventStrings
		String eventStrings = GetLocString(loc, "events");
		BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
		// CharacterStrings
		String characterStrings = GetLocString(loc, "characters");
		BaseMod.loadCustomStrings(CharacterStrings.class, characterStrings);

		logger.info("Done editing strings");
	}

	// ================ /LOAD THE TEXT/ ===================

	public static String getLocCode() {
		if (Settings.language == Settings.GameLanguage.KOR) {
			return "eng"; // TODO: change this to kor later
		} else {
			return "eng";
		}
	}

	// ================ LOAD THE KEYWORDS ===================

	@Override
	public void receiveEditKeywords() {
		logger.debug("receiveEditKeywords started.");
		Gson gson = new Gson();
		String loc = getLocCode();

		String json = GetLocString(loc, "keywords");
		Keyword[] keywords = gson.fromJson(json, Keyword[].class);

		if (keywords != null) {
			for (Keyword keyword : keywords) {
				BaseMod.addKeyword("pokerplayer", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
			}
		}
		logger.debug("receiveEditKeywords finished.");
	}

	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		PlayingCardAction.cards.clear();
		PlayingCardAction.pendingEffects.clear();
	}

	// ================ /LOAD THE KEYWORDS/ ===================

	// this adds "ModName: " before the ID of any card/relic/power etc.
	// in order to avoid conflicts if any other mod uses the same ID.
	public static String makeID(String idText) {
		return "PokerPlayerMod:" + idText;
	}

	public static String GetCardPath(String id) {
		return "PokerPlayerMod/images/cards/" + id + ".png";
	}

	public static String GetPowerPath(String id) {
		return "PokerPlayerMod/images/powers/" + id + ".png";
	}

	public static String GetRelicPath(String id) {
		return "PokerPlayerMod/images/relics/" + id + ".png";
	}

	public static String GetEventPath(String id) {
		return "PokerPlayerMod/images/events/" + id + ".png";
	}

	private static String GetLocString(String locCode, String name) {
		return Gdx.files.internal("PokerPlayerMod/localization/" + locCode + "/" + name + ".json").readString(
				String.valueOf(StandardCharsets.UTF_8));
	}

	private static HashMap<String, Texture> imgMap = new HashMap<>();

	public static Texture loadTexture(String path) {
		if (!imgMap.containsKey(path)) {
			imgMap.put(path, ImageMaster.loadImage(path));
		}
		return imgMap.get(path);
	}
}
