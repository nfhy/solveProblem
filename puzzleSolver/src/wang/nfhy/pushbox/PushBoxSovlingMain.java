package wang.nfhy.pushbox;

import wang.nfhy.puzzlesolver.SolvePuzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推箱子问题解决演示类
 * @author nfhy
 * 2015年9月29日
  <br> 
   myBlog: http://nfhy.wang
 */
public class PushBoxSovlingMain {
  
  private static PushBoxState getStartState(PushBoxModeling modeling) {
    List<String> boxList = new ArrayList<>();
    boxList.add("33");
    boxList.add("22");
    return new PushBoxState(modeling, boxList, new int[]{1, 2}, new ArrayList<String>());
  }
  
  private static PushBoxState[] getTargetStates(PushBoxModeling modeling) {
    List<String> boxList = new ArrayList<>();
    boxList.add("43");
    boxList.add("41");
    return new PushBoxState[]{new PushBoxState(modeling, boxList, new int[]{1, 1}, new ArrayList<String>())};
  }
  
  public static void main(String ... args) {
    Map<String, Grid> gridInfo = new HashMap<String, Grid>();
    int maxX = 6;
    int maxY = 5;
    String key;
    for (int i = 0; i < maxX; ++i) {
      for (int j = 0; j < maxY; ++j) {
        key = "" + i + j;
        Grid grid;
        if (i == 0 || i == 5 || j == 0 || j == 4) {
          grid = new Grid(PushboxConstants.Wall, i, j);
        } else if (i == 3 && j == 2) {
          grid = new Grid(PushboxConstants.Wall, i, j);
        } else if (i == 4 && j == 3 || i == 4 && j == 1) {
          grid = new Grid(PushboxConstants.Storage, i, j);
        } else {
          grid = new Grid(PushboxConstants.Floor, i, j); 
        }
        gridInfo.put(key, grid);
      }
    }
    PushBoxModeling modeling = new PushBoxModeling(maxX, maxY, gridInfo);
    PushBoxState start = getStartState(modeling);
    start.draw();
    PushBoxState[] targets = getTargetStates(modeling);
    targets[0].draw();
    SolvePuzzle solve = new SolvePuzzle(targets, start);
    solve.start();
  }
  
}
