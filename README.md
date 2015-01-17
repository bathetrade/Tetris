Tetris
======

What it is
----------

This is my implementation of Tetris using Java and the Slick2D library. At this point, it's nothing fancy; there's no scoring system, difficulty scaling, t-spins, combos, or anything like that. It does have wall-kicking and piece-kicking (rotating the active piece into other pieces will "kick" the piece away), as well as a fancy physics-based falling animation when rows are deleted.

Playing the game
----------------

If you would like to play the game, just clone the project somewhere on your computer and go into the bin/Tetris folder. If you don't want to clone the whole project, you can download a zipped version of just the game folder from [here](http://www.filedropper.com/tetris_1). Go to the Tetris directory on your computer, and then click on "tetris.jar" and it *should* run on Windows 32-bit and 64-bit. It also runs on Linux of course, but I took out the Linux .so libraries for the time being to decrease the size of download (temporary).

Purpose of the project
----------------------

The purpose of this project was not to create a great Tetris clone, but rather, to help myself learn Java. Prior to this project, I'd never written anything in Java. Because of this, some of the code is understandably less clean than I would like. Specifically, there are quite a few naked arrays. Also, some of the relationships between related ideas are implemented loosely and not explicitly shown in the code; for instance, the relationship between "GameBoardSquare"s, "Point" arrays, and the "ActivePiece." Ideally, these would be much more tightly defined and related. Next, I would like to improve how the animation is handled, namely, to implement some of kind of AnimationManager that has the ability to queue up animations, and also has support for both blocking and non-blocking animations. Finally, the input code could be a bit better (to enable key repeats when holding down a key, for example).
