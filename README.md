# sp21-cs242-assignment1.2 (UNO)

Table of Contents
-----------------

* [Introduction](#Introduction)
* [MVC Design](#MVC_Design)
* [Environment](#Environment)

## Introdution

This is the completet version of the UNO MP (CS242@illinois).

It currently contains three packages: UNO, Test, and GUI.

1. UNO package provides the implementation of basic game logics for a **single round** of UNO (deck managing, validating play of all 108 cards in various context). For specification of functioning, please refer to [the requirement webpage](https://wiki.illinois.edu/wiki/pages/viewpage.action?pageId=528356500). You can also find a doxygen configuration file in the doxygen directory. Run "doxygen Doxyfile" for auto-generated documents. UNO package also provides two AI player families - a primitive AI that plays randomly and a more strategic AI. The stretegic AI has ~58% winning rate against primitive AI when tested in 100000 complete games.  Finally, this UNO package support two extra rule - addition of two cards and subtraction of two cards.

2. Test package provides comprehensive JUnit tests for testing the functionality of UNO, including the ruleController, player, card manager, AI.

3. In GUI package, fivestatic, non-interacive GUI (JFrames), including (i) welcome page (ii) player number input page (iii) game stage page (iv) choose color page (v) ending page. It can now support integration of AI into the game using GUI.


### MVC Design

The overall interactive control is implemented following MVC pattern. Specifically, the **UNO.ruleController & UNO.Player** classes serve as the **Model** that stores the game state data (e.g. allowed color, symbol, previous played card, previous player action, etc.) and player hand cards, **UNO.Game** serves as the **Controller**, and **GUI package** serves as the **Viewer**.

Viewer will never directly interact with Model (you will not find anything about RuleController with Ctrl+F in the Viewer classes), and will always interact with the controller (Game class).



Environment
-----------

Java JDK - 15.0.2

JUnit - 4.13.1

Junit-jupyter - 5.4.2

junit-platform - 1.4.2

IntelliJ - education - 2020.3

Notice these are only environment where this software got developed and is guranteed to run. They are not meant to be hard requirements.
