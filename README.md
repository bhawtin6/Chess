#Chess

## Overview

A functional, playable chess board, that allows two players can play locally. 

Supports legal movement of all pieces, including unique moves such as initial pawn pushes, castling, en passent, and pawn promotion. Legal moves, checks, checkmates, and stalemates are automatically detected.
Note: pawn promotion currently automatically promotes the pawn to queen. In the future, the option to under-promote may be added, but it has been left as-is for now since pawns are queened 99.9% of the time anyways. Only forced stalemates are currently determined. If players are in stalemate via insufficient material, they may play on if they wish, or can manually end/restart the game.

## CONTROLS

White moves first, as is customary in chess. White moves up the board, black moves down the board. For chess rules, just google it.

To move a piece, first click it. Legal moves for that square are highlighted. You are only able to move a piece of the colour whose turn it is. Clicking one of the highlighted squares will move the piece to the targeted square, and trigger the other player's turn to begin. Shall a player attempt to move to an illegal square or a square the piece is currently on, nothing will happen and the piece will be deselected. 

When the game ends in either checkmate or forced stalemate, the bottom panel can be clicked to set up a new game. 
If the players wish to manually reset the game, they can press any key at any time and click the bottom panel to confirm their decision (or play on to ignore).

Disclaimer: This project is a work in progress, however the game is completely playable in its current form.
