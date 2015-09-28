package wang.nfhy.solve;

import wang.nfhy.problem.SingleState;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SolveProblem {
  
  private ExecutorService es;
  
  private CopyOnWriteArrayList<SingleState> targetStates;
  
  private ConcurrentLinkedQueue<SingleState> openStates;
  
  private ConcurrentHashMap<String,Object> closedStates;
  
  /**.
   * SolveProblem
   * @param targetStates target states all thread looking for
   * @throws Exception throws if targetStates is empty
   */
  public SolveProblem(List<SingleState> targetStates) throws Exception {
    if (null == targetStates || targetStates.isEmpty()) {
      throw new Exception("no target to reach");
    }
    this.targetStates = new CopyOnWriteArrayList<>(targetStates);
    this.es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    this.openStates = new ConcurrentLinkedQueue<>();
    this.closedStates = new ConcurrentHashMap<>();
    
  }
  
  public boolean reachTarget(SingleState state) {
    for (SingleState targetState : targetStates) {
      if (targetState.equals(state)) {
        return true;
      }
    }
    return false;
  }
  
  static class SolveOneState implements Callable<String> {
    
    private SingleState nowState;
    
    private SolveProblem solveProblem;
    
    public SolveOneState(SingleState nowState,SolveProblem solveProblem) {
      this.nowState = nowState;
      this.solveProblem = solveProblem;
    }
    
    public boolean solving() {
      boolean hasAvailableState = false;
      SingleState[] nextStates = nowState.next();
      for (SingleState state : nextStates) {
        if (!solveProblem.closedStates.contains(state.getUniqueId())) {
          if (!solveProblem.openStates.contains(state)) {
            solveProblem.openStates.add(state);
          }
          hasAvailableState = true;
        }
      }
      return hasAvailableState;
    }
    
    @Override
    public String call() throws Exception {
      solving();
      return null;
    }
    
  }
  
}
