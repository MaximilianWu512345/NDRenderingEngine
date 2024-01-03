public class YCC {
   
   public double channelLuminance;
   
   public double channelChrominanceBlue;
   
   public double channelChrominanceRed;
   
   public YCC(double cL, double cCB, double cCR) {
      channelLuminance = cL;
      channelChrominanceBlue = cCB;
      channelChrominanceRed = cCR;
   }
   
   public void setLuminance(double arg) {
      channelLuminance = arg;
   }
   
   public void setChrominanceBlue(double arg) {
      channelChrominanceBlue = arg;
   }
   
   public void setChrominanceRed(double arg) {
      channelChrominanceRed = arg;
   }
   
   public double getLuminance() {
      return channelLuminance;
   }
   
   public double getChrominanceBlue() {
      return channelChrominanceBlue;
   }
   
   public double getChrominanceRed() {
      return channelChrominanceRed;
   }
   
   public String toString() {
      return getLuminance() + " " + getChrominanceBlue() + " " + getChrominanceRed();
   }
   
   public double[] toArray() {
      return new double[] { getLuminance(), getChrominanceBlue(), getChrominanceRed() };
   }
}