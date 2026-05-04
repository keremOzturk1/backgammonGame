# Backgammon Game

An interactive desktop Backgammon game developed in Java using Swing.  
This project focuses on implementing core backgammon mechanics, custom board rendering, click-based gameplay, move validation, and a polished visual interface.

### Overview

This project is a Java-based Backgammon game with a custom graphical user interface.  
The game supports interactive checker movement, dice-based legal move validation, bar entry, bearing off, hit detection, undo functionality, and visual move highlighting.

The main goal of the project was to combine object-oriented game logic with an event-driven GUI structure while creating a playable and visually refined board game application.

### Features

- Custom-rendered Backgammon board using Java Swing
- Click-based checker selection and movement
- Dice rolling system with support for doubles
- Legal move validation based on dice values
- Highlighted playable checkers
- Highlighted target points for selected checkers
- Hit detection and bar entry mechanics
- Bearing off support
- Undo functionality for recent moves
- Automatic turn handling when no valid moves are available
- Visual dice display with used dice indication
- Custom board colors, checkers, bar area, hinges, and wooden frame design

### Technologies Used

- Java
- Java Swing
- Object-Oriented Programming
- Event-driven GUI programming

## Main Components

### Game Logic

The game logic is separated into classes such as Game, Board, MoveValidator, Move, Player, and Point.

This structure allows the game rules and board state to remain separate from the visual interface.

### GUI

The graphical interface is built with Java Swing.
BoardPanel handles the custom drawing of the board, checkers, bar area, target highlights, and user interaction.

### Move Validation

MoveValidator is responsible for checking legal moves based on:

* Dice values
* Player direction
* Blocked points
* Bar entry rules
* Hit rules
* Bearing off rules

### Undo System

The game stores previous states using the GameState class.
This allows the player to undo recent moves before the next dice roll.

### How to RUN

Compile the project:
```
javac *.java
```

Run the gamet:
```
java Main
```

### Future Improvements

Possible future improvements include:

* Adding a main menu screen
* Improving game-over and restart screens
* Adding player name customization
* Adding sound effects
* Adding score tracking
* Improving AI opponent logic
* Adding online or local multiplayer modes
* Refining the board visuals further

### About This Project

This project was developed as a personal Java desktop application to practice object-oriented programming, GUI development, event handling, and game-rule implementation.

It demonstrates how core programming concepts can be combined with visual design and interactive gameplay in a complete desktop application.


### Project Structure

```text
backgammonGame/
├── Main.java
├── Game.java
├── Board.java
├── BoardPanel.java
├── Dice.java
├── DiceView.java
├── GameState.java
├── Move.java
├── MoveOption.java
├── MoveValidator.java
├── Player.java
└── Point.java



