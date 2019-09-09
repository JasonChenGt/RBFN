package application;

import java.util.ArrayList;

import javafx.scene.Group;

public class Kmeans extends Group{
	double Times = 999999999 ;
	ArrayList<Data> central ;
	ArrayList<ArrayList<Data>> member ;
	
	public Kmeans(ArrayList<Data> listOfTrainingData , int hideNumber , int dimension) {
		central = new ArrayList<Data>() ; // 各群中心向量
		member = new ArrayList<ArrayList<Data>>() ; //各群的成員
		
		for (int i = 0; i < hideNumber; i++) { // 從data中挑選幾個點先當群聚中心，個數由隱藏層神經元個數決定
			boolean add = true ;
			int number = (int)(Math.random()*listOfTrainingData.size()) ;
			Data temp = new Data() ;
			for (int j = 0; j < dimension; j++) {
				temp.x[j] = listOfTrainingData.get(number).x[j] ;
			}
			for (int j = 0; j < central.size(); j++) {
				for (int k = 0 ; k < dimension ; k++) {
					if (temp.x[k] == central.get(j).x[k]) {	// 判斷這個點是否已經被選當群聚中心
						add = false ;
						continue ;
					}
					else {
						add = true ;
						break ;
					}
				}
			}
			if (add) {
				central.add(temp) ;
			}
			ArrayList<Data> array = new ArrayList<Data>() ;
			member.add(array) ;
		}
		while (Times-- > 0) {
			for (int i = 0; i < listOfTrainingData.size(); i++) {
				int team = 0 ;
				double shortest = 1000000 ;
				for (int j = 0 ; j < hideNumber ; j++) { // 將每個點分配到最近的群中
					double tempDi = distance(dimension , listOfTrainingData.get(i) , central.get(j)) ;
					if ( tempDi < shortest) {
						shortest = tempDi ;
						team = j ;
					}
				}
				member.get(team).add(listOfTrainingData.get(i)) ;
			}
			ArrayList<Data> oldCentral = new ArrayList<>() ;
			for (int i = 0; i < hideNumber ; i++) { // 各群依照其成員調整自己的群聚中心位置
				oldCentral.add(new Data()) ;
				for (int j = 0; j < dimension ; j++) {
					double temp = 0 ; 
					for (int k = 0; k < member.get(i).size() ; k++) {
						temp += member.get(i).get(k).x[j] ;
					}
					oldCentral.get(i).x[j] = central.get(i).x[j] ;
					central.get(i).x[j] = temp/member.get(i).size() ;
				}
			}
			double judge = 0 ;
			for (int i = 0 ; i < hideNumber ; i++) { // 計算每個群聚中心更新前後的差距，且選出最大的距離
				double tempDi = distance(dimension , central.get(i) , oldCentral.get(i)) ;
				if ( tempDi > judge) {
					judge = tempDi;
				}
			}
			if (judge < 0.01) { // 若每個群聚中心更新前後的差距不大，則計算初始sigma值，且結束更新群聚中心
				double temp = 0 ;
				for (int i = 0; i < hideNumber; i++) {
					for (int j = 0; j < hideNumber; j++) {
						double tempDi = distance(dimension , central.get(i) , central.get(j)) ;
						if ( tempDi > temp) {
							temp = tempDi ;
						}
					}
				}
				for (int i = 0; i < hideNumber ; i++) {
					central.get(i).sigma = temp/(float)Math.sqrt(hideNumber) ;
				}
				break ;
			}
		}
	}
	
	public double distance(int dimension , Data pointA , Data pointB) {
		double distance = 0 ;
		for (int i = 0 ; i < dimension ; i++) {
			distance += Math.pow(pointA.x[i]-pointB.x[i], 2) ;
		}
		return Math.sqrt(distance) ;
	}
}
