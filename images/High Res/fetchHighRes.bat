::Script for fetching high resolution card images for use in The Game or Gempukku
::Written by Brian Ouellette (Hida Tatsura)

::To use this script you must have wget for Windows installed (binary in the same directory). A version of the wget is included.

::To get cards not in a set (IE promotional cards) you'll need to do it by hand or guess the filename and grab the # from cards.xml
::Diagotsu will store images as SETcardname
::Ex: http://daigotsu.com/images/ccg/KD/KDsoulofbushido.jpg
::Not all promos are part of a set and it will be PromoCardName
::Ex: http://daigotsu.com/images/ccg/Promo/PromoRavagedLands.jpg
::NOTE: Capitalization is not consitent

::Perhaps use the card search and parse links then images? Would require cards.xml in order to look up the P# still

Set SET=KD
Set DIRECT=images\cards\
mkdir "%DIRECT%%SET%"
Set EXT=.jpg

FOR /L %%G IN (1,1,9) DO (
wget -O "%DIRECT%%SET%\%SET%00%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%00%%G%EXT%")

FOR /L %%G IN (10,1,99) DO (
wget -O "%DIRECT%%SET%\%SET%0%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%0%%G%EXT%")

FOR /L %%G IN (100,1,156) DO (
wget -O "%DIRECT%%SET%\%SET%%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%%%G%EXT%")

Set SET=TTT
mkdir "%DIRECT%%SET%"

FOR /L %%G IN (1,1,9) DO (
wget -O "%DIRECT%%SET%\%SET%00%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%00%%G%EXT%")

FOR /L %%G IN (10,1,99) DO (
wget -O "%DIRECT%%SET%\%SET%0%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%0%%G%EXT%")

FOR /L %%G IN (100,1,156) DO (
wget -O "%DIRECT%%SET%\%SET%%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%%%G%EXT%")

::Daigotsu doesn't use the same format as cards.xml for SE
::If this happens again just replace the %SET% variable in the URL with Samurai or whatever the custom name is
::SE is a base set as well so it contains 412 cards instead of 156
Set SET=SE
mkdir "%DIRECT%%SET%"

FOR /L %%G IN (1,1,9) DO (
wget -O "%DIRECT%%SET%\%SET%00%%G%EXT%" "http://daigotsu.com/images/ccg/Samurai/Samurai00%%G%EXT%")

FOR /L %%G IN (10,1,99) DO (
wget -O "%DIRECT%%SET%\%SET%0%%G%EXT%" "http://daigotsu.com/images/ccg/Samurai/Samurai0%%G%EXT%")

FOR /L %%G IN (100,1,412) DO (
wget -O "%DIRECT%%SET%\%SET%%%G%EXT%" "http://daigotsu.com/images/ccg/Samurai/Samurai%%G%EXT%")

Set SET=STS
mkdir "%DIRECT%%SET%"

FOR /L %%G IN (1,1,9) DO (
wget -O "%DIRECT%%SET%\%SET%00%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%00%%G%EXT%")

FOR /L %%G IN (10,1,99) DO (
wget -O "%DIRECT%%SET%\%SET%0%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%0%%G%EXT%")

FOR /L %%G IN (100,1,156) DO (
wget -O "%DIRECT%%SET%\%SET%%%G%EXT%" "http://daigotsu.com/images/ccg/%SET%/%SET%%%G%EXT%")