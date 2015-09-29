package wang.nfhy.pushbox;

public class Grid {
  
  private String gridType;
  
  private int gridX;
  private int gridY;
  
  public Grid(String gridType, int gridx, int gridy) {
    this.gridType = gridType;
    this.gridX = gridx;
    this.gridY = gridy;
  }
  
  public int getGridX() {
    return this.gridX;
  }
  
  public int getGridY() {
    return this.gridY;
  }
  
  public String getGridType() {
    return this.gridType;
  }
  
}
