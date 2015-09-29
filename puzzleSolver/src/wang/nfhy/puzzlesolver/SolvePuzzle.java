package wang.nfhy.puzzlesolver;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 解谜线程管理类。维护一个线程池 es；
 * 维护一个开启状态阻塞队列 openList，队列中的状态都是待处理的，所有线程都可以在这里取得任务；<br>
 * 保存一个或多个目标状态 targetStates；维护一个解决方法阻塞队列 findStates，
 * 每找到与目标状态相同的状态，就保存在队列中；<br>
 * 保存一个开始状态 start，所有后续状态的顶层父状态都是开始状态；
 * 保存一个闭锁 latch，当所有线程执行完毕或异常退出时，latch-1<br>
 * @author nfhy
 * 2015年9月29日
 * <br> 
   myBlog: http://nfhy.wang
 */
public class SolvePuzzle {
  
  /**
   * 等待线程执行的最长等待时间.
   */
  private static final int maxWaitLength = 10;
  /**
   * 解密线程的线程池.
   */
  final ExecutorService es = 
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
  /**开启状态阻塞队列，保存待处理的状态，线程以阻塞方式从队列中取值，阻塞时间超过规定时间，表明没有新的状态产生，线程结束.*/
  final LinkedBlockingQueue<PuzzleState> openList = new LinkedBlockingQueue<PuzzleState>();
  
  /**解决方法阻塞队列，保存找到的解决方法，也就是和目标状态相同的状态.*/
  final LinkedBlockingQueue<PuzzleState> findStates = new LinkedBlockingQueue<PuzzleState>();
  
  /**一个或多个目标状态，有的谜题可能有多个解.*/
  PuzzleState[] targetStates;
  
  /**谜题初始状态.*/
  PuzzleState start;
  
  /**线程执行闭锁，只有所有线程执行完毕后，才开是梳理解决方法.*/
  final CountDownLatch latch = new CountDownLatch(Runtime.getRuntime().availableProcessors());
  
  /**构造方法，提供目标状态和初始状态.
   * @param targetStates 目标状态
   * @param start 初始状态
   */
  public SolvePuzzle(PuzzleState[] targetStates, PuzzleState start) {
    this.targetStates = targetStates;
    this.start = start;
  }
  
  /**解谜开始方法.*/
  public void start() {
    try {
      openList.put(start);
    } catch (InterruptedException e) {
      System.out.println("start interrupted, abort");
      return;
    }
    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
      es.submit(new Solving(targetStates));
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      System.out.println("latch waiting interrupted");
    }
    shutdown();
    handleResult();
  }
  
  /**遍历解决方法，找到最优解.*/
  private void handleResult() {
    PuzzleState result;
    PuzzleState minCostState = null;
    while ((result = findStates.poll()) != null) {
      System.out.println("---- " + result.traceBackCost());
      if (null == minCostState || minCostState.traceBackCost() > result.traceBackCost()) {
        minCostState = result;
      }
    }
    minCostState.draw();
    List<PuzzleState> traces = minCostState.traceBackToStart();
    Collections.reverse(traces);
    for (PuzzleState state : traces) {
      state.draw();
    }  
  }
  
  /**等待线程池关闭，如果100秒仍没有关闭，手动关闭.*/
  public void shutdown() {
    if (!es.isTerminated()) {
      try {
        es.awaitTermination(maxWaitLength, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        System.out.println("wait for termination interrupted");
      } finally {
        es.shutdown();
      }
    }
  }
  
  /**
   * @Title SolvePuzzle.java
   * @author nfhy
   * @date 2015年9月29日
   * @Description 解谜线程类，定义了解谜的过程：<br>
   *     1.从开启队列中取得待处理状态，如果超过等待时间仍没有取到新的状态，跳转步骤5<br>
   *     2.获取该状态的后续状态<br>
   *     3.如果不存在后续状态，返回步骤1；
   *     4.如果存在后续状态，遍历这些状态，如果这些状态与目标状态相同，记录在解决方法队列中，如果这些状态不在开启列表中，将它们放入开启列表，返回步骤1。<br>
   *     5.结束线程
   * <br> 
     myBlog: http://nfhy.wang
   */
  class Solving implements Callable<Boolean> {
    
    PuzzleState nowState;
    
    PuzzleState[] targetStates;
    
    public Solving(PuzzleState[] targetStates) {
      this.targetStates = targetStates;
    }
    
    private boolean checkTarget(PuzzleState state) {
      for (PuzzleState target : targetStates) {
        if (null != target && target.getUniqueId().equals(state.getUniqueId())) {
          return true;
        }
      }
      return false;
    }
    
    private boolean getState() throws Exception {
      while ((nowState = openList.poll(maxWaitLength, TimeUnit.SECONDS)) != null) {
        List<PuzzleState> nextStates = nowState.next();
        for (PuzzleState state : nextStates) {
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
        getState();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        latch.countDown();
      }
      return true;
    }
    
  }
}
