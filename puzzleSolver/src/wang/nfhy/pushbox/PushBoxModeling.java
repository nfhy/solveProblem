package wang.nfhy.pushbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 推箱子游戏，用工人推箱子，将所有箱子推到仓库中。工人只能向前推箱子，不能向后拖，不能侧向移动。<br>
 * 推箱子界面转化为二维坐标系地图，地图宽maxX，高maxY，每个方格一个坐标{x,y}<br>
 * 方格状态有:<br>
 * worker:        方格上有工人，暂时没有使用<br>
 * box:           方格上有箱子，暂时没有使用<br>
 * wall:          方格是墙壁，工人和箱子都不能穿过<br>
 * storage:       方格是仓库，暂时没有使用<br>
 * floor:         方格是地面，工人和箱子都可以在这一格移动<br>
 * 
 * @author nfhy
 * 2015年9月28日
 * <br> 
   myBlog: http://nfhy.wang
 */
public class PushBoxModeling {
  
  private final int maxX;
  private final int maxY;
  
  private final Map<String, Grid> gridInfo;
  
  public int getMaxX() {
    return maxX;
  }
  
  public int getMaxY() {
    return maxY;
  }
  
  public Grid getGrid(String key) {
	  return gridInfo.get(key);
  }
  
  /**
   * @param maxX 最大横坐标
   * @param maxY 最大纵坐标
   * @param gridInfo 保存节点坐标和节点状态
   */
  public PushBoxModeling(int maxX, int maxY, Map<String, Grid> gridInfo) {
    this.maxX = maxX;
    this.maxY = maxY;
    this.gridInfo = gridInfo;
  }
  
  /**
   * 在推箱子问题中，后续状态就是worker向上下左右移动一步后的状态。<br>
   * 如果移动后的状态已经存在于关闭列表中，放弃该状态。
   * @param presentState 当前状态
   * @return 后续状态
   */
  protected List<PushBoxState> next(PushBoxState presentState) {
    presentState.getCloseList().add(presentState.getUniqueId());
    List<PushBoxState> nextStates = new ArrayList<>();
    int[] workerPosition = presentState.getWorkerPosition();
    final List<String> boxList = presentState.getBoxList();
    int presentX = workerPosition[0];
    int presentY = workerPosition[1];
    int[] moveLeft = new int[]{presentX - 1, presentY};
    int[] moveRight = new int[]{presentX + 1, presentY};
    int[] moveUp = new int[]{presentX, presentY + 1};
    int[] moveDown = new int[]{presentX, presentY - 1};
    int[][] posibleMove = new int[][]{moveLeft,moveRight,moveUp,moveDown};
    PushBoxState state;
    for (int i = 0; i < posibleMove.length; ++i) {
      state = nextStateIfMoveTo(i, posibleMove[i], boxList, presentState.getCloseList());
      if (state != null) {
        if (presentState.getCloseList().contains(state.getUniqueId())) {
          continue;
        }
        state.setParent(presentState);
        nextStates.add(state);
      }
    }
    return nextStates;
  }
  /**
   * worker移动到moveTo节点，如果可以移动，返回新的state；如果不可以移动，返回null<br>
   * moveTo节点如果是box，还需要考虑box是否可以同步向下一格移动。
   * @param moveTo worker的下一个位置
   * @param direction 0左 1右 2上 3下
   * @param boxList 箱子的坐标
   * @return 可移动，新state；不可移动，null
   */
  private PushBoxState nextStateIfMoveTo(int direction, int[] moveTo,
      List<String> boxList, ArrayList<String> closeList) {
    ArrayList<String> copyList = new ArrayList<>(boxList);
    Collections.copy(copyList, boxList);
    String key = "" + moveTo[0] + moveTo[1];
    Grid grid = this.gridInfo.get(key);
    if (null == grid) {
      return null;
    }
    if (PushboxConstants.Wall.equals(grid.getGridType())) {
      return null;
    }
    boolean isBox = copyList.contains(key);
    if (isBox) {
      int[] nextOfMoveTo = null;
      switch (direction) {
        case 0: nextOfMoveTo = new int[]{moveTo[0] - 1, moveTo[1]};
                break;
        case 1: nextOfMoveTo = new int[]{moveTo[0] + 1, moveTo[1]};
                break;
        case 2: nextOfMoveTo = new int[]{moveTo[0], moveTo[1] + 1};
                break;
        case 3: nextOfMoveTo = new int[]{moveTo[0], moveTo[1] - 1};
                break;
        default: break;
      }
      if (nextOfMoveTo == null) {
        return null;
      }
      String nextKey = "" + nextOfMoveTo[0] + nextOfMoveTo[1];
      Grid nextGrid = this.gridInfo.get(nextKey);
      if (copyList.contains(nextKey) || PushboxConstants.Wall.equals(nextGrid.getGridType())) {
        return null;
      } else {
        copyList.remove(key);
        copyList.add(nextKey);
      }
    }
    return new PushBoxState(this, copyList, moveTo, closeList);
  }
}
