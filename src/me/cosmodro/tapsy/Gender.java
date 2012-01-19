package me.cosmodro.tapsy;

public enum Gender {
  MALE (0.68),
  FEMALE (0.55);
  
  public final double eliminationRate;

  Gender(double rate){
	  this.eliminationRate = rate;
  }
}
