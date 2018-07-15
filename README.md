# Tesserapp

Small hobby project, playing with 4D geometry.

## What's this apps purpose?

I'm writing this app to my own joy and curiosity.
I try to apply various new designs and concepts I come across, such as the MVVM construct.

## Git Branching Model

I tried various branching models, but now I'm sticking to this one,
greatly explained [here](https://nvie.com/posts/a-successful-git-branching-model/?).

## Math

The vector, matrix and transform math is implemented in C++.
For two reasons, that module has moved to its very [own repository](https://github.com/Jim-Eckerlein/fmath):
Firstly, to be modular, so I can reuse the heavily templated math code.
Secondly, Android Studio is not suitable to work on "advanced" C++ code.
