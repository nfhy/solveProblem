package wang.nfhy.puzzlesolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 解谜过程中的一个状态或者一个步骤，与问题建模有关。在推箱子游戏中，是工人的一步移动；<br>
 * 每个状态都维护一个父状态和一个关闭状态列表。父状态用于解决方法的回溯，从目标状态返回起点状态；关闭列表保存了到达当前状态前已经处理过的状态。<br>
 * 关闭列表在每个状态中作为内部变量维护，保证即使父状态和子状态由不同线程处理，其结果都是一致的。<br>
 * @author nfhy
 * 2015年9月29日
 * <br> 
   myBlog: http://nfhy.wang
 */
public abstract class PuzzleState {
  
  /**每个状态的唯一ID.*/
  protected String uniqueId;
  
  /**指向父状态，计算解决方法价值，回溯解决方法时使用.*/
  protected PuzzleState parent;
  
  /**关闭状态列表，保存状态id，这一列表中的状态不需要再次处理.*/
  private ArrayList<String> closeList;
  
  /**生成状态唯一ID，与问题建模有关.*/
  public abstract void generateUniqueId();
  
  public String getUniqueId() {
    return this.uniqueId;
  }
  
  public void setParent(PuzzleState parent) {
    this.parent = parent;
  }
  
  /**当前状态的后续状态，一般情况下，如果后续状态出现在closeList中，这一状态可以跳过不处理.*/
  public abstract <K extends PuzzleState> List<K> next();
  
  /**从当前状态回溯到起始状态，返回一个状态队列.*/
  public abstract List<PuzzleState> traceBackToStart();
  
  /**计算从当前状态回溯到起始状态的步数，当谜题有多个解时，可以通过这一方法找到最优解.*/
  public abstract int traceBackCost();

  /**在console中打印状态简图的方法.*/
  public void draw(){}
  
}
