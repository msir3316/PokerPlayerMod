package ThePokerPlayer;

import ThePokerPlayer.actions.PokerCardEndOfTurnAction;
import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.*;
import ThePokerPlayer.characters.ThePokerPlayer;
import ThePokerPlayer.modules.ModLabeledButton;
import ThePokerPlayer.modules.PokerScoreViewer;
import ThePokerPlayer.patches.CardColorEnum;
import ThePokerPlayer.patches.ThePokerPlayerEnum;
import ThePokerPlayer.relics.*;
import ThePokerPlayer.variables.DefaultCustomVariable;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomCard;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class PokerPlayerMod
		implements EditCardsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, EditKeywordsSubscriber,
		EditCharactersSubscriber, PostInitializeSubscriber, OnStartBattleSubscriber, PreMonsterTurnSubscriber,
		PostDungeonInitializeSubscriber {
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
	private static final String ATTACK_POKER_PLAYER_GRAY = "512/bg_attack_default_gray.png";
	private static final String POWER_POKER_PLAYER_GRAY = "512/bg_power_default_gray.png";
	private static final String SKILL_POKER_PLAYER_GRAY = "512/bg_skill_default_gray.png";
	private static final String ENERGY_ORB_POKER_PLAYER_GRAY = "512/card_default_gray_orb.png";
	private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";

	private static final String ATTACK_POKER_PLAYER_GRAY_PORTRAIT = "1024/bg_attack_default_gray.png";
	private static final String POWER_POKER_PLAYER_GRAY_PORTRAIT = "1024/bg_power_default_gray.png";
	private static final String SKILL_POKER_PLAYER_GRAY_PORTRAIT = "1024/bg_skill_default_gray.png";
	private static final String ENERGY_ORB_POKER_PLAYER_GRAY_PORTRAIT = "1024/card_default_gray_orb.png";

	// Character assets
	private static final String THE_DEFAULT_BUTTON = "charSelect/PokerPlayerButton.png";
	private static final String THE_DEFAULT_PORTRAIT = "charSelect/PokerPlayerPortraitBG.png";
	public static final String THE_DEFAULT_SHOULDER_1 = "char/ThePokerPlayer/shoulder.png";
	public static final String THE_DEFAULT_SHOULDER_2 = "char/ThePokerPlayer/shoulder2.png";
	public static final String THE_DEFAULT_CORPSE = "char/ThePokerPlayer/corpse.png";

	// Mod Badge
	public static final String BADGE_IMAGE = "Badge.png";

	// Animations atlas and JSON files
	public static final String THE_DEFAULT_SKELETON_ATLAS = "char/ThePokerPlayer/skeleton.atlas";
	public static final String THE_DEFAULT_SKELETON_JSON = "char/ThePokerPlayer/skeleton.json";

	// Logics
	public static AbstractCard cardSelectScreenCard;
	public static float transformAnimTimer;
	public static HashMap<AbstractCard, AbstractCard> shapeshiftReturns = new HashMap<>();
	public static int genCards;

	// Modules
	public static PokerScoreViewer pokerScoreViewer;

	// Bans
	public static final ArrayList<String> bannedRelics = new ArrayList<>(Arrays.asList(
			SneckoEye.ID, CentennialPuzzle.ID, RunicPyramid.ID, BagOfPreparation.ID, Pocketwatch.ID, GremlinHorn.ID, PandorasBox.ID, UnceasingTop.ID, DeadBranch.ID
	));
	public static final HashSet<String> bannedCards = new HashSet<>(Arrays.asList(
			DeepBreath.ID, Impatience.ID, MasterOfStrategy.ID, Mayhem.ID, Magnetism.ID, Violence.ID
	));

	// Configs
	public static Properties pokerDefaults = new Properties();
	public static boolean hardMode = false;
	public static boolean banContents = true;
	public static boolean exordiumAll = false;
	public static final int CONFIG_VERSION = 1;

	//ModLabeledToggleButton hardModeButton;
	ModLabeledToggleButton banContentsButton;
	ModLabeledToggleButton exordiumAllButton;

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
				POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, POKER_PLAYER_GRAY, makePath(ATTACK_POKER_PLAYER_GRAY),
				makePath(SKILL_POKER_PLAYER_GRAY), makePath(POWER_POKER_PLAYER_GRAY),
				makePath(ENERGY_ORB_POKER_PLAYER_GRAY), makePath(ATTACK_POKER_PLAYER_GRAY_PORTRAIT),
				makePath(SKILL_POKER_PLAYER_GRAY_PORTRAIT), makePath(POWER_POKER_PLAYER_GRAY_PORTRAIT),
				makePath(ENERGY_ORB_POKER_PLAYER_GRAY_PORTRAIT), makePath(CARD_ENERGY_ORB));

		logger.info("Done Creating the color");

		loadConfig();

		logger.debug("Constructor finished.");
	}

	@SuppressWarnings("unused")
	public static void initialize() {
		PokerPlayerMod mod = new PokerPlayerMod();
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

	public static void loadConfig() {
		int version = 0;
		try {
			SpireConfig config = new SpireConfig("PokerPlayerMod", "PokerPlayerModSaveData", pokerDefaults);
			try {
				version = config.getInt("saveVersion");
			} catch (Exception e) {
				e.printStackTrace();
				version = 0;
			}
			config.load();
			hardMode = config.getBool("hardMode");
			banContents = config.getBool("banContents");
			exordiumAll = config.getBool("exordiumAll");
			if (version < CONFIG_VERSION) {
				logger.debug("Version is " + version + ". Resetting banContents config.");
				banContents = true;
				saveConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
			defaultConfig();
		}
		logger.debug("loadConfig finished.");
	}

	public static void defaultConfig() {
		//hardMode = false;
		banContents = true;
		exordiumAll = false;
	}

	public static void saveConfig() {
		try {
			SpireConfig config = new SpireConfig("PokerPlayerMod", "PokerPlayerModSaveData", pokerDefaults);
			//config.setBool("hardMode", hardMode);
			config.setBool("banContents", banContents);
			config.setBool("exordiumAll", exordiumAll);
			config.setInt("saveVersion", CONFIG_VERSION);
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("saveConfig finished.");
	}

	@Override
	public void receivePostInitialize() {
		// Load the Mod Badge
		Texture badgeTexture = new Texture(makePath(BADGE_IMAGE));

		// Create the Mod Menu
		ModPanel settingsPanel = new ModPanel();

		/*
		hardModeButton = new ModLabeledToggleButton(
				CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("HardModeConfig")).TEXT[0],
				400.0f, 650.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
				hardMode, settingsPanel, (label) -> {
		}, (button) -> {
			hardMode = button.enabled;
			saveConfig();
		});
		*/
		banContentsButton = new ModLabeledToggleButton(
				CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("BanContentsConfig")).TEXT[0],
				400.0f, 600.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
				banContents, settingsPanel, (label) -> {
		}, (button) -> {
			banContents = button.enabled;
			saveConfig();
		});
		exordiumAllButton = new ModLabeledToggleButton(
				CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("ExordiumClubConfig")).TEXT[0],
				400.0f, 550.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
				exordiumAll, settingsPanel, (label) -> {
		}, (button) -> {
			exordiumAll = button.enabled;
			saveConfig();
		});

		ModLabeledButton restoreDefaultButton = new ModLabeledButton(
				CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("RestoreDefaultConfig")).TEXT[0],
				380.0f, 450.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
				settingsPanel, (label) -> {
		}, (button) -> {
			defaultConfig();
			saveConfig();
			//hardModeButton.toggle.enabled = hardMode;
			banContentsButton.toggle.enabled = banContents;
			exordiumAllButton.toggle.enabled = exordiumAll;
		});

		/*
		ModLabel label = new ModLabel(
				CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("ConfigHelp")).TEXT[0],
				400.0f, 350.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, (l) -> {
		}
		);
		*/

		//settingsPanel.addUIElement(hardModeButton);
		settingsPanel.addUIElement(banContentsButton);
		settingsPanel.addUIElement(exordiumAllButton);
		settingsPanel.addUIElement(restoreDefaultButton);
		//settingsPanel.addUIElement(label);

		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

		pokerScoreViewer = new PokerScoreViewer();
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

		BaseMod.addRelicToCustomPool(new DeckCase(), CardColorEnum.POKER_PLAYER_GRAY);

		BaseMod.addRelicToCustomPool(new AceCard(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new BottledPoker(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new BrokenClock(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new ClubPass(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new CoolBox(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new EnergeticBat(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new FairLicense(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new JackpotMachine(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new PenAndEraser(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new PendantOfEscape(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new Splitter(), CardColorEnum.POKER_PLAYER_GRAY);
		BaseMod.addRelicToCustomPool(new StuffedPocket(), CardColorEnum.POKER_PLAYER_GRAY);

		logger.info("Done adding relics!");
	}

	// ================ /ADD RELICS/ ===================


	// ================ ADD CARDS ===================

	@Override
	public void receiveEditCards() {
		// Add the Custom Dynamic Variables
		BaseMod.addDynamicVariable(new DefaultCustomVariable());

		// Add the pokerCards
		List<CustomCard> cards = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			for (int j = 1; j <= 10; j++) {
				cards.add(new PokerCard(PokerCard.Suit.values()[i], j));
			}
		}

		cards.add(new BadJoker());
		cards.add(new CardBurn());
		cards.add(new CardPrinter());
		cards.add(new ChangeRules());
		cards.add(new CloakAndDiamond());
		cards.add(new ClubDance());
		cards.add(new ClubsClub());
		cards.add(new ClubShade());
		cards.add(new ClubStrike());
		cards.add(new Configure());
		cards.add(new DamnStraight());
		cards.add(new DartThrow());
		cards.add(new DiamondStrike());
		cards.add(new Duplicate());
		cards.add(new Extraction());
		cards.add(new FakeSymbols());
		cards.add(new FillThePot());
		cards.add(new FlyingCard());
		cards.add(new Fold());
		cards.add(new GamblerForm());
		cards.add(new WasteCollection());
		cards.add(new HeartStrike());
		cards.add(new Harden());
		cards.add(new HiddenCard());
		cards.add(new HotShotCut());
		cards.add(new ItsShowtime());
		cards.add(new JackOfSpades());
		cards.add(new MagicTrick());
		cards.add(new Manipulation());
		cards.add(new Mulligan());
		cards.add(new Overdeal());
		cards.add(new Promotion());
		cards.add(new Raise());
		cards.add(new RiskyBet());
		cards.add(new PoorCopy());
		cards.add(new RoyalStrike());
		cards.add(new SecondChance());
		cards.add(new SecretDealer());
		cards.add(new Sharpen());
		cards.add(new SpadeStrike());
		cards.add(new StackedDeck());
		cards.add(new TheDieIsCast());
		cards.add(new ThinkingTime());
		cards.add(new Trickery());
		cards.add(new TrumpStrike());
		cards.add(new VarietyAttack());
		cards.add(new WildCard());

		cards.add(new SuperBite());

		for (CustomCard card : cards) {
			BaseMod.addCard(card);
			//UnlockTracker.unlockCard(card.cardID);
		}
	}

	// ================ /ADD CARDS/ ===================


	@Override
	public void receivePostDungeonInitialize() {
		if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER && banContents) {
			ArrayList<ArrayList<String>> relicPools = new ArrayList<>();
			relicPools.add(AbstractDungeon.commonRelicPool);
			relicPools.add(AbstractDungeon.shopRelicPool);
			relicPools.add(AbstractDungeon.uncommonRelicPool);
			relicPools.add(AbstractDungeon.rareRelicPool);
			relicPools.add(AbstractDungeon.bossRelicPool);

			for (ArrayList<String> pool : relicPools) {
				logger.debug("Relic pool count (before) = " + pool.size());
				pool.removeAll(bannedRelics);
				logger.debug("Relic pool count (after) = " + pool.size());
			}

			logger.debug("Colorless card pool count (before) = " + AbstractDungeon.colorlessCardPool.size());
			AbstractDungeon.colorlessCardPool.group.removeIf(i -> bannedCards.contains(i.cardID));
			logger.debug("Colorless card pool count (after) = " + AbstractDungeon.colorlessCardPool.size());
		}
	}

	// ================ LOAD THE TEXT ===================

	@Override
	public void receiveEditStrings() {
		logger.info("Begin editing strings");

		loadLocStrings("eng");
		loadLocStrings(getLocCode());

		logger.info("Done editing strings");
	}

	void loadLocStrings(String lang) {
		// RelicStrings
		String relicStrings = GetLocString(lang, "relics");
		BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
		// CardStrings
		String cardStrings = GetLocString(lang, "cards");
		BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
		// PotionStrings
		String potionStrings = GetLocString(lang, "potions");
		BaseMod.loadCustomStrings(PotionStrings.class, potionStrings);
		// PowerStrings
		String powerStrings = GetLocString(lang, "powers");
		BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
		// UIStrings
		String uiStrings = GetLocString(lang, "ui");
		BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
		// EventStrings
		String eventStrings = GetLocString(lang, "events");
		BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
		// CharacterStrings
		String characterStrings = GetLocString(lang, "characters");
		BaseMod.loadCustomStrings(CharacterStrings.class, characterStrings);

	}

	// ================ /LOAD THE TEXT/ ===================

	public static String getLocCode() {
		if (Settings.language == Settings.GameLanguage.KOR)
			return "kor";
		else if (Settings.language == Settings.GameLanguage.ZHS)
			return "zhs";
		else
			return "eng";
	}

	// ================ LOAD THE KEYWORDS ===================

	@Override
	public void receiveEditKeywords() {
		logger.debug("receiveEditKeywords started.");

		loadLocKeywords("eng");
		loadLocKeywords(getLocCode());
	}

	void loadLocKeywords(String lang) {
		Gson gson = new Gson();

		String json = GetLocString(lang, "keywords");
		Keyword[] keywords = gson.fromJson(json, Keyword[].class);

		if (keywords != null) {
			for (Keyword keyword : keywords) {
				BaseMod.addKeyword("pokerplayer", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
			}
		}
		logger.debug("receiveEditKeywords finished.");
	}

	// ================ /LOAD THE KEYWORDS/ ===================

	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		ShowdownAction.pokerCards.clear();
		ShowdownAction.otherCards.clear();
		ShowdownAction.pendingEffects.clear();
		PokerCardEndOfTurnAction.cards.clear();
		shapeshiftReturns.clear();
		genCards = 0;
	}

	@Override
	public boolean receivePreMonsterTurn(AbstractMonster m) {
		CardGroup[] groups = new CardGroup[]{
				AbstractDungeon.player.hand,
				AbstractDungeon.player.drawPile,
				AbstractDungeon.player.discardPile,
				AbstractDungeon.player.exhaustPile
		};
		for (CardGroup cg : groups) {
			for (int i = 0; i < cg.size(); i++) {
				AbstractCard c = cg.group.get(i);
				if (shapeshiftReturns.containsKey(c)) {
					cg.group.set(i, shapeshiftReturns.get(c));
					cg.group.get(i).stopGlowing();
					cg.group.get(i).unfadeOut();
				}
			}
		}
		shapeshiftReturns.clear();
		return true;
	}

	// this adds "ModName: " before the ID of any card/relic/power etc.
	// in order to avoid conflicts if any other mod uses the same ID.
	public static String makeID(String idText) {
		return "PokerPlayerMod:" + idText;
	}

	public static String GetCardPath(String id) {
		return "PokerPlayerMod/images/cards/" + id + ".png";
	}

	public static String GetPowerPath(String id, int size) {
		return "PokerPlayerMod/images/powers/" + id + "_" + size + ".png";
	}

	public static String GetOtherPath(String id) {
		return "PokerPlayerMod/images/other/" + id + ".png";
	}

	public static String GetRelicPath(String id) {
		return "PokerPlayerMod/images/relics/" + id + ".png";
	}

	public static String GetRelicOutlinePath(String id) {
		return "PokerPlayerMod/images/relics/outline/" + id + ".png";
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
