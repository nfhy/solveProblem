package wang.nfhy.pathfinding;

import wang.nfhy.problem.SingleState;

import java.util.List;

public class PathFindingState extends SingleState {
  
  private List<Integer[]> openList;
  
  private List<Integer[]> closeList;
  
  @Override
  public void generateUniqueId() {
    
  }

  @Override
  public List<PathFindingState> traceBackToStart() {
    PathFindingState state;
    return null;
  }

  @Override
  public int traceBackCost() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public SingleState[] next() {
    return null;
  }

}
