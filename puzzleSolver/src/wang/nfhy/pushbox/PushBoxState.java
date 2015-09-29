package wang.nfhy.pushbox;


import wang.nfhy.puzzlesolver.PuzzleState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 推箱子问题中的一个状态，包括问题建模引用; 箱子所在坐标； 工人所在坐标； 当前状态的关闭状态列表
 * @author nfhy
 * 2015年9月29日
 * <br> 
   myBlog: http://nfhy.wang
 */
public class PushBoxState extends PuzzleState {
  
  private final PushBoxModeling modeling;
  
  private final List<String> boxList;
  
  private final int[] workerPosition;
  
  private ArrayList<String> closeList;
  
  public PushBoxState(PushBoxModeling modeling, List<String> boxList,
      int[] workerPosition, ArrayList<String> closeList) {
    this.modeling = modeling;
    this.boxList = boxList;
    this.workerPosition = workerPosition;
    this.closeList = (ArrayList<String>) closeList.clone();
    this.generateUniqueId();
  }
  
  public List<String> getBoxList() {
    return this.boxList;
  }
  
  public int[] getWorkerPosition() {
    return this.workerPosition;
  }
  
  public ArrayList<String> getCloseList() {
    return this.closeList;
  }
  
  @Override
  public void generateUniqueId() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(workerPosition[0]).append(workerPosition[1])
      .append(PushboxConstants.Worker);
    
    Collections.sort(boxList, new Comparator<String>(){
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
      
    });
    
    for (String key : boxList) {
      stringBuilder.append(key).append(PushboxConstants.Box);
    }
    this.uniqueId = stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<PushBoxState> next() {
    this.closeList.add(this.uniqueId);
    return modeling.next(this);
  }

  @Override
  public List<PuzzleState> traceBackToStart() {
    List<PuzzleState> arrayList = new ArrayList<>();
    PushBoxState state = this;
    arrayList.add(state);
    while ((state = (PushBoxState) state.parent) != null) {
      arrayList.add(state);
    }
    return arrayList;
  }

  @Override
  public int traceBackCost() {
    PushBoxState state = this;
    int cost = 0;
    while ((state = (PushBoxState) state.parent) != null) {
      ++cost;
    }
    return cost;
  }
  
  @Override
  public void draw() {
    synchronized (PushBoxState.class) {
      System.out.println(Thread.currentThread().getName());
      System.out.println(this.uniqueId);
      int maxX = modeling.getMaxX();
      int maxY = modeling.getMaxY();
      for (int j = maxY - 1; j >= 0; --j) {
        for (int i = 0; i < maxX ; ++i) {
          String key = "" + i + j;
          Grid grid = modeling.getGrid(key);
          if (workerPosition[0] == i && workerPosition[1] == j) {
            System.out.print(" W");
          } else if (boxList.contains(key)) {
            System.out.print(" B");
          } else if (PushboxConstants.Wall.equals(grid.getGridType())) {
            System.out.print(" #");
          } else if (PushboxConstants.Storage.equals(grid.getGridType())) {
            System.out.print(" S");
          } else {
            System.out.print(" *");
          }
          if (i == maxX - 1) {
            System.out.println();
          }
        }
      }
    }
  }
  
}
