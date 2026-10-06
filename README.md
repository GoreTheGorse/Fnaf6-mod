# Pizzeria Simulator (FNaF 6 Fan Mod) - Stage 2

Fabric mod for Minecraft 1.21.1, Java 21. Fan-made; not affiliated with Scott Cawthon.

## Build the jar
1. Install JDK 21 (Temurin, adoptium.net).
2. Install Gradle 8.10 or newer (gradle.org/install), then run ONCE in this folder:
       gradle wrapper --gradle-version 8.10
   (This creates gradlew / gradlew.bat. Alternatively open the folder in IntelliJ IDEA
   and run the "build" task from the Gradle panel.)
3. Build:
       Windows:      gradlew.bat build
       Mac / Linux:  ./gradlew build
4. The mod is build/libs/fnaf6-mod-0.2.0.jar (NOT the -sources jar).

## Play it in CurseForge
- Create a custom profile: Minecraft 1.21.1 + Fabric.
- Install "Fabric API" (1.21.1) from the app.
- Put fnaf6-mod-0.2.0.jar in the profile's mods folder.

## Test
Creative mode -> "Pizzeria Simulator" tab.
/fnaf6 give @s 50   then   /fnaf6 balance
Right-click a Faz-Tablet to see your balance; right-click a Faz-Coin to deposit it.

## Stage 2: running a pizzeria
1. Get some Faz-Coins: `/fnaf6 give @s 500` (founding costs 100 by default).
2. Place a **Pizzeria Terminal**. That founds your pizzeria.
3. Place **Party Tables**, **Arcade Cabinets** and **Prize Counters** within 24 blocks of the terminal.
   Each earns Faz-Coins every 60 seconds (only while the terminal's chunk is loaded).
4. Right-click the terminal for status. Sneak + right-click (owner) collects the bank into your balance.
5. Commands (all act on your own pizzeria):
       /fnaf6 pizzeria info
       /fnaf6 pizzeria rename <name>
       /fnaf6 pizzeria upgrade <menu|marketing|expansion|security>
       /fnaf6 pizzeria withdraw <amount|all>
       /fnaf6 pizzeria deposit <amount>
   Upgrades are paid from the pizzeria bank. Menu +25% income/level, Marketing +15%/level,
   Expansion +4 attraction slots/level, Security is reserved for the risk system in a later stage.
New config keys in config/fnaf6.json: pizzeriaIncomeIntervalSeconds, pizzeriaRadius,
maxPizzeriasPerPlayer, pizzeriaFoundingCost.

## Regenerate placeholder textures (already included)
    python tools/generate_placeholder_textures.py

If dependency versions in gradle.properties fail to resolve, check fabricmc.net/develop
for the current 1.21.1 values and update them.

## Build online (no installs)
Create a free GitHub account, make a new repository, and upload the contents of this folder
(including the hidden .github folder). Open the Actions tab, wait for "Build mod" to finish,
open the run, and download the "fnaf6-mod-jar" artifact. Inside, use fnaf6-mod-0.2.0.jar (not -sources).
