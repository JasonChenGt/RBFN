package application;

import java.io.* ;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Menu extends Scene {
	Button loadFileB = (Button)lookup("#loadFile");
	Button newTrainingB = (Button)lookup("#newTraining") ;
	Button enterB = (Button)lookup("#enter") ;
	
	Text path = (Text)lookup("#path") ;
	Text trainingTimes = (Text)lookup("#trainingTimes") ;
	Text rmse = (Text)lookup("#rmse") ;
	Text trainingRecRate = (Text)lookup("#trainingRecRate") ;
	Text testingRecRate = (Text)lookup("#testingRecRate") ;
	TextField learningRateT = (TextField)lookup("#learningRate") ;
	TextField maxTimesT = (TextField)lookup("#maxTimes") ;
	TextField minRecRateT = (TextField)lookup("#minRecRate") ;
	TextField hideNumberT = (TextField)lookup("#hideNumber") ;
	TextField rateDecayT = (TextField)lookup("#rateDecay") ;
	TextField errorToleranceT = (TextField)lookup("#errorTolerance") ;
	
	VBox boxTest = (VBox)lookup("#boxTest") ;
	VBox boxTrain = (VBox)lookup("#boxTrain") ;
	
	HBox errorBlock = (HBox)lookup("#errorBlock") ;
	
	ArrayList<Data> listOfData ;
	ArrayList<Data> listOfTrainingData ;
	ArrayList<Data> listOfTestingData ;
	
	File file ;
	Kmeans kmeans ;
	RBFN rbfn ;
	
	int numberOfTrain ;
	int numberOfTest ;
	int numberOfData ;
	int maxTimes ;
	int classCount ;
	int hideNumber ;
	int dimension ;
	int errorCount ;
	double rateDecay ;
	double errorTolerance ;
	double learningRate ;
	double minRecRate ;
	double[] classification ;
	double maxX ;
	double minX ;
	double maxY ;
	double minY ;
	
	public Menu(Main m,Parent root) {
		super(root);
		initial() ;
		loadFileB.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(extFilter);
	            fileChooser.setTitle("Choose a txt file");
	            file = fileChooser.showOpenDialog(m.stage);
	            listOfData = new ArrayList<>();	
            	listOfTrainingData = new ArrayList<>();	
            	listOfTestingData = new ArrayList<>();
                if (file != null) {
                    loadFile(file) ;
                    run() ;
                }
			}
		});
		
		newTrainingB.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (file != null) {
					run() ;
				}
			}
		});	
		
		enterB.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				errorBlock.setVisible(false);
			}
		});	
		
		learningRateT.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("[0]") || newValue.matches("[0][.]") || newValue.matches("[0][.]([0-9]*)[1-9]([0-9]*)") ) {
		    		learningRateT.setStyle("-fx-text-inner-color: black;"); 
	    	    	learningRate = Double.parseDouble(learningRateT.getText());
	    	    } 
		    	else if (newValue.isEmpty()) {
		        	learningRate = 0.5 ;
		    	}
		    	else {
	    	    	learningRateT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
		
		maxTimesT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("[1-9]([0-9]*)")) {
		    		maxTimesT.setStyle("-fx-text-inner-color: black;"); 
	    	    	maxTimes = Integer.parseInt(maxTimesT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		        	maxTimes = 5000 ;
		    	}
		    	else {
	    	    	maxTimesT.setStyle("-fx-text-inner-color: red;"); 
	    	    }
		    }
		});
		
		minRecRateT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("([1-9]?)([0-9])(([.][0-9]*)?)") || newValue.matches("100([.][0]*)?")) {
		    		minRecRateT.setStyle("-fx-text-inner-color: black;"); 
	    	    	minRecRate = Double.parseDouble(minRecRateT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		    		minRecRate = 100 ;
		    	}
		    	else {
	    	    	minRecRateT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
		
		hideNumberT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("[1-9][0-9]*")) {
		    		hideNumberT.setStyle("-fx-text-inner-color: black;"); 
	    	    	hideNumber = Integer.parseInt(hideNumberT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		    		hideNumberT.setStyle("-fx-text-inner-color: black;");
		    		hideNumber = 2 ;
				} 
		    	else {
	    	    	hideNumberT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
		
		rateDecayT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("[1-9][0-9][0-9][0-9]*")) {
		    		rateDecayT.setStyle("-fx-text-inner-color: black;"); 
		    		rateDecay = Double.parseDouble(rateDecayT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		    		rateDecayT.setStyle("-fx-text-inner-color: black;");
		    		rateDecay = 100 ;
				} 
		    	else {
		    		rateDecayT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
		
		errorToleranceT.textProperty().addListener(new ChangeListener<String>() {
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (newValue.matches("[0]") || newValue.matches("[0][.]") || newValue.matches("[0][.]([0-9]*)[1-9]([0-9]*)")) {
		    		errorToleranceT.setStyle("-fx-text-inner-color: black;"); 
		    		errorTolerance = Double.parseDouble(errorToleranceT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		    		errorToleranceT.setStyle("-fx-text-inner-color: black;");
		    		errorTolerance = 0.00001 ;
				} 
		    	else {
		    		errorToleranceT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
	}

	private void initial() {
		errorCount = 0 ;
		learningRate = 0.5 ;
		errorTolerance = 0.00001 ;
		maxTimes = 5000 ;
		minRecRate = 100 ;
		hideNumber = 2 ;
		rateDecay = 100 ;
	}

	private void run() {
		boolean error = false ;
		DecimalFormat df = new DecimalFormat("######0.00");
		// 非監督式K-means
		try {
			kmeans = new Kmeans(listOfTrainingData , hideNumber , dimension) ;
		} catch (Exception e) {
			// TODO: handle exception
			error = true ;
		}
		// 監督式RBFN
		try {
			rbfn = new RBFN(kmeans, listOfTrainingData, maxTimes, learningRate, minRecRate, rateDecay , errorTolerance , classification , classCount) ;
		} catch (Exception e) {
			// TODO: handle exception
			error = true ;
		}
		try {
			if (Double.isNaN(kmeans.central.get(1).weight) ) {	
				error = true ;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (!error) {
			// 輸出顯示
			trainingTimes.setText(""+(int)rbfn.Times) ;
			rmse.setText(""+ rbfn.rmse);
			double tr = rbfn.TrRecRate ;
			trainingRecRate.setText(""+ df.format(tr) + " %");

			double correct = 0 ;
			for (int j = 0; j < numberOfTest ; j++) {
				listOfTestingData.get(j).y = (float)rbfn.output(kmeans.central , listOfTestingData.get(j)) ;
				if (rbfn.judge(j , listOfTestingData, classification , classCount)) {
					correct++ ;
				}
			}
			correct = 100*correct/listOfTestingData.size() ;
			testingRecRate.setText("" + df.format(correct) + " %");

			if (dimension == 2 && classCount == 2) {
				draw2(boxTest , listOfTestingData) ;
				draw2(boxTrain , listOfTrainingData) ;
			}
			else {
				if (!boxTest.getChildren().isEmpty()) {
					boxTest.getChildren().remove(0) ;
				}
				if (!boxTrain.getChildren().isEmpty()) {
					boxTrain.getChildren().remove(0) ;
				}
			}
			errorCount = 0 ;
		}
		else if (errorCount < 1000) {
			errorCount++ ;
			run();
		}
		else {
			initial();
			learningRateT.clear();;
			maxTimesT.clear();
			minRecRateT.clear();
			hideNumberT.clear();
			rateDecayT.clear();
			errorToleranceT.clear();
			errorBlock.setVisible(true);
		}
	}
	
	private void draw2( VBox box , ArrayList<Data> listOfData) {
		double unitX = maxX-minX/20 ;
		double unitY = maxY-minY/20 ;
		XYChart.Series<Number,Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number,Number> series2 = new XYChart.Series<>();
        
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLowerBound(minX-unitX/2.5);
        xAxis.setUpperBound(maxX+unitX/2.5);
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(unitX);
        yAxis.setLowerBound(minY-unitY/2.5);
        yAxis.setUpperBound(maxY+unitY/2.5);
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(unitY);
        
        final LineChart<Number,Number> chart = new LineChart<>(xAxis , yAxis);
        
        chart.setLegendVisible(false);
        chart.getData().add(series1) ;
        chart.getData().add(series2) ;
        chart.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        for (int i = 0; i < listOfData.size() ; i++) {
        	if (listOfData.get(i).d == classification[0]) {
        		series1.getData().add(new XYChart.Data<>(listOfData.get(i).x[0] , listOfData.get(i).x[1] ));
			}
        	else {
        		series2.getData().add(new XYChart.Data<>(listOfData.get(i).x[0] , listOfData.get(i).x[1] ));
        	}
        }
        
        if (!box.getChildren().isEmpty()) {
			box.getChildren().remove(0) ;
		}
        box.getChildren().add(chart) ;
	}
	
	private void loadFile(File file) {
    	maxX = -100000 ;
    	minX = 100000 ;
    	maxY = -100000 ;
    	minY = 100000 ;

    	classification = new double[25] ;
    	classCount = 0 ;
		BufferedReader bufferedReader = null ;
		Data data ;
		path.setText(file.getPath());
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
            	String[] array = text.split(" ");
            	data = new Data(array) ;
            	listOfData.add(data) ;
            }
		}catch (Exception e) {
			// TODO: handle exception
        } finally {
            try {
                bufferedReader.close();
                Collections.shuffle(listOfData);
            } catch (Exception e) {}
        }
		
		//判斷有幾類，X、Y邊界
		for (int i = 0; i < listOfData.size() ; i++) {
			boolean add = true ;
			if (listOfData.get(i).x[0] > maxX) {
				maxX = listOfData.get(i).x[0] ;
			}
			else if (listOfData.get(i).x[0] < minX) {
				minX = listOfData.get(i).x[0] ;
			}
			if (listOfData.get(i).x[1] > maxY) {
				maxY = listOfData.get(i).x[1] ;
			}
			else if (listOfData.get(i).x[1] < minY) {
				minY = listOfData.get(i).x[1] ;
			}
			
			for (int j = 0; j < classCount ; j++) {
				if (listOfData.get(i).d == classification[j]) {
					add = false ;
					break ;
				}
			}
			if (add) {
				classification[classCount++] = listOfData.get(i).d ;
			}
		}

		// 將資料集分成訓練資料集和測試資料集
		numberOfData = listOfData.size() ;
		if (numberOfData > 100) {
			// 資料集夠多，將2/3當作訓練資料，1/3當作測試資料
			numberOfTrain = (int)Math.ceil((double)(listOfData.size()*2)/3) ;
			numberOfTest = numberOfData-numberOfTrain ;

			for (int i = 0 ; i < numberOfTrain ; i++) {
				listOfTrainingData.add(listOfData.get(0)) ;
				listOfData.remove(0) ;
			}
			for (int i = 0 ; i < numberOfTest ; i++) {
				listOfTestingData.add(listOfData.get(0)) ;
				listOfData.remove(0) ;
			}
		}
		else {
			// 資料集不夠多，將所有資料當作訓練資料，1/3當作測試資料
			numberOfTrain = numberOfData ;
			numberOfTest = (int)Math.ceil((double)(listOfData.size())/3) ;
			
			for (int i = 0 ; i < numberOfTrain ; i++) {
				listOfTrainingData.add(listOfData.get(0)) ;
				listOfData.remove(0) ;
			}
			for (int i = 0 ; i < numberOfTest ; i++) {
				listOfTestingData.add(listOfTrainingData.get(i)) ;
			}
		}
		dimension = listOfTrainingData.get(0).dimension ;
	}
}