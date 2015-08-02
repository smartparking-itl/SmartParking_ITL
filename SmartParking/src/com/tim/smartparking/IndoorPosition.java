package com.tim.smartparking;


public class IndoorPosition {
	
	public static double[][] base = new double[2][2];
	public static double[][] ro = new double[base.length][base.length];
	
	public static double[] findMe(double[][] ro) {
		double i, j;
		int k, m;
		double maxX = base[2][0];
		double maxY = base[2][1];
		double c[] = new double[2];
		for(i = 0; i < maxY; i=i+0.1) {
			for(j = 0; j < maxX; j=j+0.1) {
				c[0] = i;
				c[1] = j;
				for(k = 0; k < base.length; k++) {
					checkRast(c, k);
				}
			}
		}
		return null;
	}
	
	protected static double rast(double[] in1, double[] in2) {
		double r;
		r = Math.sqrt((in1[0]-in2[0])*(in1[0]-in2[0]) + (in1[1] - in2[1])*(in1[1] - in2[1]));
		return r;
	}
	
	protected static boolean checkRast(double[] c, int k) {
		//if(rast(c, base[k]) <= rast(c, base[k]) - ro[k]) {
			
		//}
		return false;
	}

}
