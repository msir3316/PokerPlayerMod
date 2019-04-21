Also available in: [한국어](README-KOR.md)

# PokerPlayerMod

This mod adds a character named `The Poker Player` to Slay the Spire. This character defies traditional style of combat, and fights with poker rules. This mod plays similarly to a game called 'Trump Battle', created by *HSymbol* in a small game jam, adapted to slay the spire mechanics.

# Gameplay

[Crappy showcase video of The Poker Player](https://youtu.be/jc1tZhmU0UY)

In addition to traditional Attack, Skill, Powers etc in the base StS game, The Poker Player uses another card type: Poker.

## Poker Card
There are 40 cards, four suits each with ranks ranging from 1 to 10. They cost 1 and when you play one, it only cycles itself without doing anything.

If you keep at least one of them until the end of your turn, you enter Showdown. During Showdown, each Poker Card in your hand activates its ability based on its suit.
- Spade X: Gain X Block.
- Diamond X: Deal X damage to lowest health enemy.
- Heart X: Heal X HP. Exhausts after activated.
- Clover X: Deal X damage to all enemies.

In addition, if you score a [Poker Hand](https://en.wikipedia.org/wiki/List_of_poker_hands), effects of them are boosted.
- 1 Pair: +50%
- 2 Pairs: +100%
- Three of a kind: +150%
- Straight: +250%
- Full House: +200%
- Four of a kind: +300%
- Five of a kind: +400%
- Flush can be added to any of the above hand (including No pairs), which adds an additional +200% to the modifier. For example,
  - (No pairs) Flush: +200%
  - 1 Pair Flush: +250%
  - Straight Flush: +450%

This Bonus is decreased if you choose "Hard Mode" in the mod config.

If you upgrade a Poker Card, its rank will go up by 1. This means the one with rank 10 cannot be upgraded.

# Compatibility
Unlike most Slay the Spire mods, The Poker Player is not intended to be used with any other mods. Sine the majority of cards are not played in a traditional way, many relics/cards/etc added by mods will be played much differently with this character. Similarly, playing with Custom Mods are not intended.

To play with this mod only, only check BaseMod, StSLib and The Poker Player and run in the Mod The Spire menu.

However, nothing stops you from trying this mod with others. Just keep in mind that I'm not going to change any cross-mod interactions other than crash or softlock issues.

# See Also
The Poker Player is not a traditional mod. If you want to play as a character that feels normal, see [Gatherer](https://github.com/Celicath/GathererMod), or look for [List of Known Mods](https://github.com/kiooeht/ModTheSpire/wiki/List-of-Known-Mods) page on Mod The Spire wiki.

## Special Thanks To
- HSymbol for the original game.

## License
- All code is in MIT license.

## Contacts
- Celicath@gmail.com
- celicath#3192 at Discord
