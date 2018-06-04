# HeatAndClimateLib
the Mod necessary for defeatedcrow's mod in 1.12.2

## Project Site
Please visit my new Wiki: http://defeatedcrow.jp/modwiki/HeatAndClimate

## Introduction
This mod is API and core mod necessary for defeatedcrow's mod in 1.9+. Of course, you can stand-alone use this mod, or use in your modding. <br>このmodは1.9+においてdefeatedcrowが作成するmodの前提MODです。もちろん、このmodを単独で使用したり、あなたのmodの前提modとしても利用することが出来ます。

## 動作環境 Current operating environment:
- Minecraft 1.12.2
- MinecraftForge 14.23.3.2655+  
   
## ライセンス Licenses
このmodは以下のライセンスの元で公開されます。 <br>
This mod is distributed under the following license:
### Code: All Rights Reserved
#### Permissions
- ソースコードを改変したり、あなたのmodプロジェクトに利用することができます。<br>You can fork, modify, or use the code in your project.
- このmodの非改変パッケージをmodpackに同梱したり、マルチプレイサーバー内で再配布することができます。<br>You can redistribute this mod's unmodified package to modpack or multiplayer server.

#### Conditions
- 著作権者の表示 Copyright notice
- 商用利用禁止 Commercial use prohibition

#### Limitations
- このmodは「現状のまま」提供され、一切の動作を保証しません。<br>This mod is provided "as is" without warranty of any kind.
- 作者および著作権者は、いかなる場合でも、このmodの使用その他によって生じる責任を負いません。<br>In any case, the author or copyright holder is not responsible for any claims, damages or other liabilities arising out of this mod or its use.

### Assets: CC-BY-NC-SA 4.0 <br>
 https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode

## 注意事項 Caution!
1. Now this mod is still WIP. The correct operation cannot be expected. Please be careful! <br> 当modは開発途中です。正常に動作しない場合がありますので、ご了承の上でご利用下さい。<br>
2. About PR  This mod is authored only by defeatedcrow. PR contributor is treated as contributor, but the contributors do not have a licensing rights of this mod.  In addition, for lang file PR, please use child repository. <br> 当modへPRする場合、当modのContributorとして扱われ、当modのライセンス権を持たないことにご了承下さい。また、langファイルへのPRはこのリポジトリではなく、langファイル管理用の子リポジトリをご利用下さい。  <br>

## Function
1. Climate System  <br>They are similar to the heat source system of pots and iron plate in AMT2. Changing it in more general. It is used as "IClimate" interface for heat, humidity, air flow in current BlockPos. <br> AMT2の鍋・鉄板のHeatSourceをより汎用化したシステムです。ある座標の温度・湿度・通気性を「気候」としてIClimateインターフェイスを定義し、調理や装置稼働の条件として使用します。<br><br>
 Example: <br>
 - For cooking, using the natural climate elements with the biome block.  <br>自然の熱源など、その座標の気候を利用した調理。<br>
 - Using the climate of the coordinates to the growth of the plant.  <br>植物の生長に特定の気候条件を要求する。 <br> 
<br>
2. HeatSource  <br>They are used in order to change the temperature of the coordinates. It is used for the devices or recipes that require high temperature.  <br>座標の持つ温度を増減させる熱源。高温を必要とする装置やレシピに利用されます。<br><br>
 Example: <br>
 - When you place the "raw dough" near the fire as an object, it becomes "bread". <br>  火の近くにEntityとして「生のパン生地」を置いておくと、「焼けたパン」に変わります。  <br>
<br>
3. Climate damage and prevent material. <br>Too high or low temp will hurt player and mobs. It can prevent easily by armors or charms.<br> 極度の高温・低温でプレイヤーやMOBがダメージを受けるようになります。ダメージは防具やアクセサリーで簡単に防ぐことができます。(防具を身につける動機付けです。)<br><br>

Copyright (c) defeatedcrow 2016
