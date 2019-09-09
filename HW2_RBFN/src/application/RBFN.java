package application;

import java.util.ArrayList;

import javafx.scene.Group;

public class RBFN extends Group{
	double Times ;
	double TrRecRate ;
	double mse ;
	double rmse ;
	double learningRate ;
	Kmeans kmeans ;
	
	public RBFN (Kmeans kmeans , ArrayList<Data> listOfTrainingData , int maxTimes , double learningRate , double minRecRate
			, double rateDecay , double errorTolerance , double[] classification , int classCount) {
		this.TrRecRate = 0 ;
		this.kmeans = kmeans ;
		int teg = 0 ;
		for (this.Times = 0 ; this.Times < maxTimes ; this.Times++) {
			this.mse = 0 ;
			teg = (int)(Math.random()*listOfTrainingData.size()) ;
///////////////////////////////////////////////////////////// 放射狀基底函數網路 ///////////////////////////////////////////////////////////	
			listOfTrainingData.get(teg).y = (float)output(kmeans.central , listOfTrainingData.get(teg)) ;
			// 隨訓練時間變動的學習率
			this.learningRate = learningRate/(1+this.Times/rateDecay) ;
			
			// 保留現在的值供調整時使用
			ArrayList<Double> tempGaussian = new ArrayList<>() ;
			ArrayList<Double> tempDistance = new ArrayList<>() ;
			for (int i = 0; i < kmeans.central.size() ; i++) {
				tempGaussian.add(gaussian(kmeans.central.get(i) , listOfTrainingData.get(teg))) ;
			}
			for (int i = 0 ; i < kmeans.central.size() ; i++) {
				tempDistance.add( kmeans.distance(listOfTrainingData.get(teg).dimension 
						, listOfTrainingData.get(teg) , kmeans.central.get(i)) );
			}
			
			// 調整每個中心的位置
			for (int i = 0 ; i < kmeans.central.size() ; i++) {
				for (int j = 0; j < listOfTrainingData.get(teg).dimension; j++) {
					kmeans.central.get(i).x[j] += this.learningRate 
							* (listOfTrainingData.get(teg).d-listOfTrainingData.get(teg).y) 
							* kmeans.central.get(i).weight * tempGaussian.get(i)
							* (listOfTrainingData.get(teg).x[j]-kmeans.central.get(i).x[j]) / Math.pow(kmeans.central.get(i).sigma , 2);
				}
			}
			
			// 調整sigma值
			for (int i = 0 ; i < kmeans.central.size() ; i++) {
				kmeans.central.get(i).sigma += this.learningRate 
						* (listOfTrainingData.get(teg).d-listOfTrainingData.get(teg).y) 
						* kmeans.central.get(i).weight * tempGaussian.get(i)
						* Math.pow(tempDistance.get(i) , 2) / Math.pow(kmeans.central.get(i).sigma , 3);
			}
			
			// 調整權重
			for (int i = 0; i < kmeans.central.size(); i++) {
				kmeans.central.get(i).weight += this.learningRate 
						* (listOfTrainingData.get(teg).d-listOfTrainingData.get(teg).y) * tempGaussian.get(i) ;
			}
			// 測試訓練用測資
			int correct = 0 ;
			for (int i = 0; i < listOfTrainingData.size() ; i++) {
				if (judge(i, listOfTrainingData, classification , classCount)) {
					correct++ ;
				}
				this.mse += Math.pow(listOfTrainingData.get(teg).y-listOfTrainingData.get(teg).d , 2) ;
			}
			if (100*correct/listOfTrainingData.size() > this.TrRecRate) {
				this.TrRecRate = 100*correct/listOfTrainingData.size() ;
			}
			this.rmse = Math.sqrt(this.mse/listOfTrainingData.size()) ;
			
			// 當辨識率超過設定的最小辨識率或當均方根誤差小於均方根誤差容忍值，則結束訓練
			if (rmse <= errorTolerance || this.TrRecRate >= minRecRate ) {
				break ;
			}
		}	
	}
	
	public boolean judge (int teg , ArrayList<Data> listOfData , double[] classification , int classCount) {
		double dataClass = classification[0];
		double temp = 99999 ;
		for (int i = 0; i < classCount ; i++) {
			if (Math.abs(listOfData.get(teg).y-classification[i]) < temp) {
				temp = Math.abs(listOfData.get(teg).y-classification[i]) ;
				dataClass = classification[i] ;
			}
		}
		// 判斷分到哪一類，若與期望輸出一樣，回傳正確，否則回傳失敗
		if (dataClass == listOfData.get(teg).d) {
			return true ;
		}
		else
			return false ;
	}
	
	public double output(ArrayList<Data> central , Data point) {
		double temp =  0 ;
		for (int i = 0; i < central.size() ; i++) {
			temp += central.get(i).weight * gaussian(central.get(i) , point) ;
		}
		return temp ;
	}
	
	private double gaussian(Data central , Data point) {
		return Math.exp( -1 * Math.pow(kmeans.distance(point.dimension , central , point) , 2) / (2 * Math.pow(central.sigma , 2) ) ) ;
	}
}
