#!/bin/bash

# Missing following sets:
# Promotional Card
# Imperial Edition
# Shadowlands
# Emerald Edition
# Forbidden Knowledge
# Battle of Beiden Pass
# Anvil of Despair
# Crimson & Jade
# Obsidian Edition
# Time of the Void
# Scorpion Clan Coup - Scroll 1
# Scorpion Clan Coup - Scroll 2
# Scorpion Clan Coup - Scroll 3
# Jade Edition
# The Hidden Emperor - Episode 1
# The Hidden Emperor - Episode 2
# The Hidden Emperor - Episode 3
# The Hidden Emperor - Episode 4
# The Hidden Emperor - Episode 5
# The Hidden Emperor - Episode 6
# The Hidden Emperor - Dark Journey Home
# Pearl Edition
# Siege of Sleeping Mountain
# Honor Bound
# Ambition's Debt
# Fire & Shadow
# Heroes of Rokugan
# Storms Over Matsu Palace
# Soul of the Empire
# The Spirit Wars
# Gold Edition
# A Perfect Cut
# An Oni's Fury
# Dark Allies
# Broken Blades
# 1000 Years of Darkness
# Fall of Otosan Uchi
# Winds of Change
# The Training Grounds
# The Training Grounds 2
# Clan of the Month
# Death at Koten
# Imperial Gift 2
# Path of the Destroyer
# The Harbinger
# The Plague Wars

function fetchSet {
	mkdir "$base/$set_short"
	count=1
	max=$(( $set_max + 1 ))
	while [ $count -lt $max ]; do
		URL=$( printf "%03d" "$count" )
		wget -O "$base/$set_short/$set_short$URL.jpg" "http://daigotsu.com/images/ccg/$set_long/$set_long$URL.jpg"
		echo $URL
		count=$(( $count + 1 ))
	done
}

base=../cards

set_short=CE
set_long=$set_short
set_max=395
fetchSet

set_short=GotE
set_long=$set_short
set_max=166
fetchSet

set_short=IG1
set_long=$set_short
set_max=28
fetchSet

set_short=THW
set_long=$set_short
set_max=166
fetchSet

set_short=WaD
set_long=W&D
set_max=166
fetchSet

set_short=HV
set_long=$set_short
set_max=156
fetchSet

set_short=EJC
set_long=E&JC
set_max=52
fetchSet

set_short=STS
set_long=$set_short
set_max=156
fetchSet

set_short=SE
set_long=Samurai
set_max=412
fetchSet

set_short=TTT
set_long=$set_short
set_max=156
fetchSet

set_short=Tomorrow
set_long=$set_short
set_max=21
fetchSet

set_short=KD
set_long=$set_short
set_max=156
fetchSet

set_short=RotS
set_long=$set_short
set_max=156
fetchSet

set_short=ToE
set_long=$set_short
set_max=124
fetchSet

set_short=DoW
set_long=$set_short
set_max=156
fetchSet

set_short=PoH
set_long=$set_short
set_max=156
fetchSet

set_short=LE
set_long=$set_short
set_max=486
fetchSet

set_short=CoB
set_long=$set_short
set_max=156
fetchSet

set_short=EoME
set_long=$set_short
set_max=156
fetchSet

set_short=WoL
set_long=$set_short
set_max=156
fetchSet

set_short=DotE
set_long=$set_short
set_max=108
fetchSet

set_short=WoE
set_long=$set_short
set_max=156
fetchSet

set_short=HC
set_long=$set_short
set_max=156
fetchSet

set_short=RoB
set_long=$set_short
set_max=157
fetchSet

set_short=DE
set_long=$set_short
set_max=488
fetchSet

set_short=HaE
set_long=H&E
set_max=157
fetchSet
