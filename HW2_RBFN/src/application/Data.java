package application;

import javafx.scene.Group;

public class Data extends Group{
	int dimension ; // 維度p
	double[] x = new double[25] ; // 輸入值x1...xp
	double d ; // 期望輸出d
	double y ; // 實際輸出
	double sigma ;
	double weight ;
	
	public Data() { 
		this.sigma = 0 ;
		this.weight = 0 ;
	}
	public Data(String array[]) { // 資料設定處理
		this.dimension = array.length-1 ; 
		this.d = Float.parseFloat(array[this.dimension]) ; 
		this.y = 0 ;
		for (int i = 0 ; i < array.length ; i++) { 
			this.x[i] = Float.parseFloat(array[i]) ;
		}
	}
}
