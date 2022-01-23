// Assignment 9

import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//represents a game object
abstract class AGameObject {
  //determines if there is a magenta path to the bottom 
  public boolean findBottom(ArrayList<Integer> arrayList, int side) {
    return false;
  }

  //determines if there is a pink path to the right side
  public boolean findSide(ArrayList<Integer> arrayList, int side) {
    return false;
  }
}

//represents a wall
class Wall extends AGameObject {

}

// represents a cell in the game
class Cell extends AGameObject {
  int x;
  int y;
  Color color;
  AGameObject top = new Wall();
  AGameObject right = new Wall();
  AGameObject bottom = new Wall();
  AGameObject left = new Wall();

  Cell(int row, int col, Color color) {
    this.x = row;
    this.y = col;
    this.color = color;
  }

  // draws the cell
  WorldImage drawCell() {
    return new RectangleImage(50, 50, "solid", this.color);
  }

  // EFFECT: assigns the neighbors to this cell given the gameBoard 2D array
  void assignNeighbors(ArrayList<ArrayList<Cell>> gameBoard) {
    if (this.y > 0) {
      this.top = gameBoard.get(this.x).get(this.y - 1);
    }
    if (this.x < gameBoard.size() - 1) {
      this.right = gameBoard.get(this.x + 1).get(this.y);
    }
    if (this.y < gameBoard.size() - 1) {
      this.bottom = gameBoard.get(this.x).get(this.y + 1);
    }
    if (this.x > 0) {
      this.left = gameBoard.get(this.x - 1).get(this.y);
    }
  }

  // overriding the equals method
  public boolean equals(Object o) {
    return this.hashCode() == o.hashCode();
  }

  // assigns a hashCode to this object
  public int hashCode() {
    return (x + 1) * 1000 + y + 1;
  }

  // finds if there is a magenta path to the bottom
  public boolean findBottom(ArrayList<Integer> seen, int side) {
    if (this.color.equals(Color.MAGENTA) && !seen.contains(this.hashCode())) {
      if (this.hashCode() % 100 == side) {
        return true;
      }
      else {

        seen.add(this.hashCode());
        return this.left.findBottom(seen, side) || this.right.findBottom(seen, side)
            || this.bottom.findBottom(seen, side) || this.top.findBottom(seen, side);
      }
    }
    return false;
  }

  // finds if there is a pink path to the other side
  public boolean findSide(ArrayList<Integer> seen, int side) {
    if (this.color.equals(Color.PINK) && !seen.contains(this.hashCode())) {

      if (this.hashCode() / 1000 == side) {
        return true;
      }
      else {
        seen.add(this.hashCode());
        return this.left.findSide(seen, side) || this.right.findSide(seen, side)
            || this.bottom.findSide(seen, side) || this.top.findSide(seen, side);
      }
    }
    return false;
  }

  // changes the cell if it is white based on the turn number
  // and returns true if the cell was changed
  public boolean changeCell(int turn) {
    if (this.color == Color.WHITE) {
      if (turn % 2 == 0) {
        this.color = Color.MAGENTA;
      }
      else {
        this.color = Color.PINK;
      }
      return true;
    }
    return false;
  }

}

// represents the BridgIt Game
class BridgIt extends World {
  ArrayList<ArrayList<Cell>> gameBoard;
  int sideLength; // number of cells on each side
  int turn = 0;
  boolean gameInProgress = true;

  // constructor for BridgIt
  BridgIt(int sideLength) {
    if (sideLength % 2 == 1 && sideLength >= 3) {
      this.sideLength = sideLength;
      this.gameBoard = createBoard();
      this.linkCells();
    }
    else {
      throw new IllegalArgumentException(
          "The dimentions of the game has to be an odd number and greater than 2!");
    }
  }

  // constructor for testing BridgIt
  BridgIt() {
    this.sideLength = 3;
    this.gameBoard = createBoard();
    this.linkCells();
  }

  // constructor for testing BridgIt
  BridgIt(ArrayList<ArrayList<Cell>> gameBoard) {
    this.sideLength = 3;
    this.gameBoard = gameBoard;
    this.linkCells();
  }

  // creates the board for the bridgIt game
  ArrayList<ArrayList<Cell>> createBoard() {
    ArrayList<ArrayList<Cell>> tempBoard = new ArrayList<ArrayList<Cell>>();

    // loop through all elements of the array list
    for (int row = 0; row < this.sideLength; row++) {
      ArrayList<Cell> tempRow = new ArrayList<Cell>();
      for (int col = 0; col < this.sideLength; col++) {
        if (row % 2 == col % 2) {
          tempRow.add(new Cell(row, col, Color.white));
        }
        else if (col % 2 == 1) {
          tempRow.add(new Cell(row, col, Color.PINK));
        }
        else {
          tempRow.add(new Cell(row, col, Color.MAGENTA));
        }
      }
      tempBoard.add(tempRow);
    }
    return tempBoard;
  }

  // EFFECT: assign neighbors to each cell of the game board
  void linkCells() {
    // loop through all elements
    for (int col = 0; col < this.sideLength; col++) {
      ArrayList<Cell> x = this.gameBoard.get(col);
      for (int row = 0; row < this.sideLength; row++) {
        Cell c = x.get(row);
        // assign neighbors of this cell
        c.assignNeighbors(this.gameBoard);
      }
    }
  }

  // makeScene method for BridgIt Game
  public WorldScene makeScene() {
    WorldScene s = this.getEmptyScene();
    for (ArrayList<Cell> row : this.gameBoard) {
      for (Cell c : row) {
        WorldImage cell = c.drawCell();
        s.placeImageXY(cell, c.x * 50 + 25, c.y * 50 + 25); // field of field??
      }
    }
    return s;
  }

  // on key handler method for BridgIt Game
  // EFFECT: when r is pressed the game is reset
  public void onKeyEvent(String s) {
    if (s.equals("r")) {
      this.gameBoard = createBoard();
      this.linkCells();
      gameInProgress = true;
    }
  }

  //EFFECT: when a valid square is selected it fills it with a color based on the turn
  public void onMouseClicked(Posn p) {
    if (gameInProgress) {
      int x = Math.floorDiv(p.x, 50);
      int y = Math.floorDiv(p.y, 50);
      if (x != 0 && y != 0 && x != sideLength - 1 && y != sideLength - 1) {
        if (this.gameBoard.get(x).get(y).changeCell(turn)) {
          if (turn % 2 == 0) {
            turn++;
            if (this.findVerticleWin()) {
              this.endOfWorld("Player 1 wins");
            }
          }
          else {
            turn++;
            if (this.findHorizontalWin()) {
              this.endOfWorld("Player 2 wins");
            }
          }
        }
      }
    }
  }

  // last scene method for BridgIt Game
  public WorldScene lastScene(String s) {
    if (s.equals("Player 1 wins")) {
      TextImage t1 = new TextImage("Player one has won!", Color.black);
      WorldScene endScene = this.makeScene();
      endScene.placeImageXY(t1, (50 * sideLength) / 2, (50 * sideLength) / 2);
      return endScene;
    }
    else {
      TextImage t2 = new TextImage("Player two has won!", Color.black);
      WorldScene endScene2 = this.makeScene();
      endScene2.placeImageXY(t2, (50 * sideLength) / 2, (50 * sideLength) / 2);
      return endScene2;
    }
  }

  //determines if player 2 has won
  boolean findHorizontalWin() {
    for (int y = 1; y < sideLength; y = y + 2) {
      if (this.gameBoard.get(0).get(y).findSide(new ArrayList<Integer>(), sideLength)) {
        return true;
      }
    }
    return false;
  }

  //determines if player 1 has won
  boolean findVerticleWin() {
    for (int x = 1; x < sideLength; x = x + 2) {
      if (this.gameBoard.get(x).get(0).findBottom(new ArrayList<Integer>(), sideLength)) {
        return true;
      }
    }
    return false;
  }
}

// examples class for BridgIt Game
class ExamplesBridgIt {
  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Cell cell5;
  Cell cell6;
  Cell cell7;
  Cell cell8;
  Cell cell9;
  ArrayList<Cell> col0;
  ArrayList<Cell> col1;
  ArrayList<Cell> col2;
  ArrayList<ArrayList<Cell>> board0;

  void initData() {
    cell1 = new Cell(0, 0, Color.white);
    cell2 = new Cell(0, 1, Color.pink);
    cell3 = new Cell(0, 2, Color.white);
    cell4 = new Cell(1, 0, Color.magenta);
    cell5 = new Cell(1, 1, Color.white);
    cell6 = new Cell(1, 2, Color.magenta);
    cell7 = new Cell(2, 0, Color.white);
    cell8 = new Cell(2, 1, Color.pink);
    cell9 = new Cell(2, 2, Color.white);

    col0 = new ArrayList<Cell>();
    col1 = new ArrayList<Cell>();
    col2 = new ArrayList<Cell>();

    board0 = new ArrayList<ArrayList<Cell>>();

    col0.add(cell1);
    col0.add(cell2);
    col0.add(cell3);
    col1.add(cell4);
    col1.add(cell5);
    col1.add(cell6);
    col2.add(cell7);
    col2.add(cell8);
    col2.add(cell9);

    board0.add(col0);
    board0.add(col1);
    board0.add(col2);
  }

  // tester for draw cell method for Cell
  boolean testDrawCell(Tester t) {
    initData();
    return t.checkExpect(this.cell1.drawCell(), new RectangleImage(50, 50, "solid", Color.white))
        && t.checkExpect(this.cell2.drawCell(), new RectangleImage(50, 50, "solid", Color.pink))
        && t.checkExpect(this.cell4.drawCell(), new RectangleImage(50, 50, "solid", Color.magenta));
  }

  // tester for assigning hashCode method for Cell
  boolean testHashCode(Tester t) {
    initData();
    return t.checkExpect(cell1.hashCode(), 1001) && t.checkExpect(cell2.hashCode(), 1002)
        && t.checkExpect(cell3.hashCode(), 1003);
  }

  // tester for findBottom method for Cell
  void testFindBottom(Tester t) {
    initData();
    ArrayList<Integer> mt = new ArrayList<Integer>();
    t.checkExpect(cell1.findBottom(mt, 3), false);
    ArrayList<Integer> seen1 = new ArrayList<Integer>(
        Arrays.asList(cell4.hashCode(), cell5.hashCode()));
    t.checkExpect(cell6.findBottom(seen1, 3), true);
  }

  // tester for findSide method for Cell
  void testFindSide(Tester t) {
    initData();
    ArrayList<Integer> mt = new ArrayList<Integer>();
    t.checkExpect(cell2.findSide(mt, 3), false);
    ArrayList<Integer> seen1 = new ArrayList<Integer>(
        Arrays.asList(cell2.hashCode(), cell5.hashCode()));
    t.checkExpect(cell8.findSide(seen1, 3), true);
  }

  //tester for the change cell method
  void testChangeCell(Tester t) {
    Cell c = new Cell(0, 0, Color.white);
    Cell c2 = new Cell(0, 0, Color.pink);
    t.checkExpect(c.color, Color.white);
    t.checkExpect(c.changeCell(2), true);
    t.checkExpect(c.color, Color.magenta);
    t.checkExpect(c2.changeCell(2), false);
  }

  // tester for the assignNeighbors method for Cell
  void testAssignNeighbors(Tester t) {
    initData();
    t.checkExpect(cell5.bottom, new Wall());
    t.checkExpect(cell5.top, new Wall());
    t.checkExpect(cell5.left, new Wall());
    t.checkExpect(cell5.right, new Wall());
    cell5.assignNeighbors(board0);
    t.checkExpect(cell5.bottom, cell6);
    t.checkExpect(cell5.top, cell4);
    t.checkExpect(cell5.left, cell2);
    t.checkExpect(cell5.right, cell8);

    t.checkExpect(cell1.bottom, new Wall());
    t.checkExpect(cell1.top, new Wall());
    t.checkExpect(cell1.left, new Wall());
    t.checkExpect(cell1.right, new Wall());
    cell1.assignNeighbors(board0);
    t.checkExpect(cell1.bottom, cell2);
    t.checkExpect(cell1.top, new Wall());
    t.checkExpect(cell1.left, new Wall());
    t.checkExpect(cell1.right, cell4);
  }

  // tester for linkCells method for BridgIt Game
  void testLinkCells(Tester t) {
    initData();
    BridgIt game1 = new BridgIt(3);
    ArrayList<ArrayList<Cell>> game1board = game1.gameBoard;
    Cell c0 = game1board.get(0).get(0);
    Cell right = game1board.get(1).get(0); // get the cell to the right of 0, 0
    Cell bottom = game1board.get(0).get(1); // get the cell to the bottom of 0, 0
    t.checkExpect(c0.top, new Wall());
    t.checkExpect(c0.left, new Wall());
    t.checkExpect(c0.right, right);
    t.checkExpect(c0.bottom, bottom);

    Cell c = game1board.get(1).get(1); // middle cell      0 t 0
    Cell top1 = game1board.get(1).get(0); //               l c r
    Cell bottom1 = game1board.get(1).get(2); //            0 b 0
    Cell right1 = game1board.get(2).get(1);
    Cell left1 = game1board.get(0).get(1);
    t.checkExpect(c.top, top1);
    t.checkExpect(c.left, left1);
    t.checkExpect(c.right, right1);
    t.checkExpect(c.bottom, bottom1);

  }

  // tester for create board method for BridgItGame
  void testCreateBoard(Tester t) {
    initData();
    BridgIt game1 = new BridgIt();
    cell1.assignNeighbors(board0);
    cell2.assignNeighbors(board0);
    cell3.assignNeighbors(board0);
    cell4.assignNeighbors(board0);
    cell5.assignNeighbors(board0);
    cell6.assignNeighbors(board0);
    cell7.assignNeighbors(board0);
    cell8.assignNeighbors(board0);
    cell9.assignNeighbors(board0);
    ArrayList<ArrayList<Cell>> game1board = game1.gameBoard;
    t.checkExpect(game1board, this.board0);
  }

  // tester for the make scene method for BridgIt
  void testMakeScene(Tester t) {
    BridgIt game0 = new BridgIt();
    Cell cell0 = new Cell(0, 0, Color.WHITE);
    Cell cell1 = new Cell(0, 0, Color.pink);
    Cell cell2 = new Cell(0, 0, Color.WHITE);
    Cell cell3 = new Cell(0, 0, Color.magenta);
    Cell cell4 = new Cell(0, 0, Color.WHITE);
    Cell cell5 = new Cell(0, 0, Color.magenta);
    Cell cell6 = new Cell(0, 0, Color.WHITE);
    Cell cell7 = new Cell(0, 0, Color.pink);
    Cell cell8 = new Cell(0, 0, Color.WHITE);
    WorldScene s = game0.getEmptyScene();
    s.placeImageXY(cell0.drawCell(), 25, 25);
    s.placeImageXY(cell1.drawCell(), 75, 25);
    s.placeImageXY(cell2.drawCell(), 125, 25);
    s.placeImageXY(cell3.drawCell(), 25, 75);
    s.placeImageXY(cell4.drawCell(), 75, 75);
    s.placeImageXY(cell5.drawCell(), 125, 75);
    s.placeImageXY(cell6.drawCell(), 25, 125);
    s.placeImageXY(cell7.drawCell(), 75, 125);
    s.placeImageXY(cell8.drawCell(), 125, 125);
    t.checkExpect(game0.makeScene(), s);
  }

  // tester for onKey Event for BridgIt 
  void testOnKey(Tester t) {
    initData();
    BridgIt game0 = new BridgIt(3);
    game0.onMouseClicked(new Posn(55, 55));
    t.checkExpect(game0.gameBoard.get(1).get(1).color, Color.magenta);
    game0.onKeyEvent("r");
    // after reset first cell should go back to white
    t.checkExpect(game0.gameBoard.get(1).get(1).color, Color.white);

  }

  //test for the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    BridgIt game1 = new BridgIt(5);
    game1.onMouseClicked(new Posn(55, 55));
    // first cell should turn magenta
    t.checkExpect(game1.gameBoard.get(1).get(1).color, Color.MAGENTA);
    // turn should increment at each mouse click
    t.checkExpect(game1.turn, 1);
    game1.onMouseClicked(new Posn(105, 105));
    // middle cell should turn pink
    t.checkExpect(game1.gameBoard.get(2).get(2).color, Color.PINK);
    // turn should increment again
    t.checkExpect(game1.turn, 2);
  }

  // tester for lastScene method for BridgIt
  void testLastScene(Tester t) {
    // test last scene for player one winning
    BridgIt game1 = new BridgIt();
    WorldScene baseScene = game1.makeScene();
    TextImage text1 = new TextImage("Player one has Won!", Color.black);
    baseScene.placeImageXY(text1, (50 * 3) / 2, (50 * 3) / 2);
    t.checkExpect(game1.lastScene("Player one has Won!"), baseScene);

    // test last scene for player two winning
    BridgIt game2 = new BridgIt();
    WorldScene baseScene2 = game2.makeScene();
    TextImage text2 = new TextImage("Player one has won!", Color.black);
    baseScene2.placeImageXY(text2, (50 * 3) / 2, (50 * 3) / 2);
    t.checkExpect(game2.lastScene("Player one has won!"), baseScene2);
  }

  // tester for findHorizontalWin method for BridgIt
  void testFindHorizontalWin(Tester t) {
    initData();
    BridgIt game0 = new BridgIt(5);
    t.checkExpect(game0.findHorizontalWin(), false);
    game0.onMouseClicked(new Posn(55, 55));
    t.checkExpect(game0.findHorizontalWin(), false);
    game0.onMouseClicked(new Posn(55, 155));
    t.checkExpect(game0.findHorizontalWin(), false);
    game0.onMouseClicked(new Posn(155, 55));
    t.checkExpect(game0.findHorizontalWin(), false);
    game0.onMouseClicked(new Posn(155, 155));
    t.checkExpect(game0.findHorizontalWin(), true);
  }

  // tester for FindVerticalWin method for BridgIt
  void testfindVerticleWin(Tester t) {
    initData();
    BridgIt game0 = new BridgIt(board0);
    t.checkExpect(game0.findVerticleWin(), false);
    game0.onMouseClicked(new Posn(140, 18));
    t.checkExpect(game0.findVerticleWin(), false);
    game0.onMouseClicked(new Posn(140, 140));
    t.checkExpect(game0.findVerticleWin(), false);
    game0.onMouseClicked(new Posn(75, 75));
    t.checkExpect(game0.findVerticleWin(), true);

  }

  // tester for big bang method for BridgIt Game
  void testBridgIt(Tester t) {
    t.checkConstructorException(
        new IllegalArgumentException(
            "The dimentions of the game has to be an odd number and greater than 2!"),
        "BridgIt", 10);
    int side = 11;
    BridgIt game1 = new BridgIt(side);
    game1.bigBang(50 * side, 50 * side, 1);
  }
}