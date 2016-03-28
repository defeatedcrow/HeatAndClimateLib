# HeatAndClimateLib
the Mod necessary for defeatedcrow's mod in 1.8.9+

## Introduction
- This mod is API and core mod necessary for defeatedcrow's mod in 1.8.9+. Of course, you can stand-alone use this mod, or use in your modding. <br>このmodは1.8.9+においてdefeatedcrowが作成するmodの前提MODです。もちろん、このmodを単独で使用したり、あなたのmodの前提modとしても利用することが出来ます。

## 動作環境 Current operating environment:
- Minecraft 1.8.9  
- MinecraftForge 11.15.1.1722+  
   
## ライセンス
- Code：MMPL-1.0  full text is included as License.txt  
- Resource：CC-BY-NC  

## 注意事項
1. Now this mod is still WIP. The correct operation cannot be expected. Please be careful! <br> 当modは開発途中です。正常に動作しない場合がありますので、ご了承の上でご利用下さい。<br>
2. About PR  This mod is authored only by defeatedcrow. PR contributor is treated as contributor, but the contributors do not have a licensing rights of this mod.  In addition, for lang file PR, please use child repository. <br> 当modへPRする場合、当modのContributorとして扱われ、当modのライセンス権を持たないことにご了承下さい。また、langファイルへのPRはこのリポジトリではなく、langファイル管理用の子リポジトリをご利用下さい。  <br>

## Function
1. Climate System  They are similar to the heat source system of pots and iron plate in AMT2. Changing it in more general. It is used as "IClimate" interface for heat, humidity, air flow in current BlockPos. <br> AMT2の鍋・鉄板のHeatSourceをより汎用化したシステムです。ある座標の温度・湿度・通気性を「気候」としてIClimateインターフェイスを定義し、調理や装置稼働の条件として使用します。<br>
Example:  <br>
 For cooking, using the natural climate elements with the biome block.  <br>自然の熱源など、その座標の気候を利用した調理。<br>
 Using the climate of the coordinates to the growth of the plant.  <br>植物の生長に特定の気候条件を要求する。 <br> 
  
2. HeatSource  They are used in order to change the temperature of the coordinates. It is used for the devices or recipes that require high temperature.  <br>座標の持つ温度を増減させる熱源。高温を必要とする装置やレシピに利用されます。<br>
Example:  <br>
 When you place the "raw dough" near the fire as an object, it becomes "bread". <br>  火の近くにEntityとして「生のパン生地」を置いておくと、「焼けたパン」に変わります。  <br>
<br>
(c) defeatedcrow 2016
