package wang.nfhy.pushbox;

/**
 * 节点状态常量
 * @author nfhy
 * 2015年9月29日
  <br> 
   myBlog: http://nfhy.wang
 */
class PushboxConstants {
  public static final String Worker = Integer.toHexString(0b11 << 0); 
  public static final String Box = Integer.toHexString(0b1 << 1);
  public static final String Wall = Integer.toHexString(0b1 << 2);
  public static final String Storage = Integer.toHexString(0b1 << 3);
  public static final String Floor = Integer.toHexString(0b1 << 0);
}