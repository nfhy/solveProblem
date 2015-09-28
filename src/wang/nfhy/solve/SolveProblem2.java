package wang.nfhy.solve;

import wang.nfhy.problem.SingleState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class SolveProblem2 {
  
  final ExecutorService es = 
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
  final ConcurrentHashMap<String, SingleState> closeList =
      new ConcurrentHashMap<>(Integer.MAX_VALUE);
  
  final LinkedBlockingQueue<SingleState> openList = new LinkedBlockingQueue<SingleState>();
  
  final LinkedBlockingQueue<SingleState> findStates = new LinkedBlockingQueue<SingleState>();
  
  Thread checkThread;
  
  SingleState[] targetStates;
  
  public void start() {
    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
      es.submit(new Solving(targetStates));
    }
    checkFindStates();
    checkThread.start();
  }
  
  public void shutdown() {
    if (!es.isTerminated()) {
      es.shutdown();
    }
  }
  
  public void checkFindStates() {
    checkThread = new Thread(new Runnable() {
      public void run() {
        SingleState state;
        try {
          while ((state = findStates.poll(10, TimeUnit.SECONDS)) != null) {
            System.out.println(state.traceBackCost());
          }
        } catch (Exception e) {
          System.out.println("checkFindThread interrupted");
        }
      }
    });
  }
  
  public SolveProblem2(SingleState[] targetStates) {
    this.targetStates = targetStates;
  }
  
  class Solving implements Callable<Boolean> {
    
    SingleState nowState;
    
    SingleState[] targetStates;
    
    public Solving(SingleState[] targetStates) {
      this.targetStates = targetStates;
    }
    
    private boolean checkTarget(SingleState state) {
      for (SingleState target : targetStates) {
        if (null != target && target.equals(state)) {
          return true;
        }
      }
      return false;
    }
    
    private boolean getState() throws Exception {
      while ((nowState = openList.poll(10, TimeUnit.SECONDS)) != null) {
        if (closeList.containsKey(nowState.getUniqueId())) {
          continue;
        } else {
          closeList.put(nowState.getUniqueId(), nowState);
        }
        SingleState[] nextStates = nowState.next();
        for (SingleState state : nextStates) {
          if (closeList.containsKey(state.getUniqueId())) {
            continue;
          }
          if (checkTarget(state)) {
            findStates.add(state);
          }
          if (!openList.contains(state)) {
            openList.put(state);
          }
        }
      }
      return true;
    }

    @Override
    public Boolean call() {
      try {
        return getState();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }
    
  }
}
