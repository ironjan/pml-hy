package de.ironjan.pppb.training

import smile.regression.Regression

trait RegressionTraining[T] {
   def train(x: Array[Array[T]], y: Array[T]): Regression[Array[T]]
}

object RegressionTreeTraining extends RegressionTraining[Double]{
  override def train(x: Array[Array[Double]], y: Array[Double]) = smile.regression.cart(x, y, maxNodes = 100)
}

object RandomForestClassificator extends RegressionTraining[Double]{
  override def train(x: Array[Array[Double]], y: Array[Double]) = smile.regression.randomForest(x,y)
}

object S extends RegressionTraining[Double] {
  override def train(x: Array[Array[Double]], y: Array[Double]) = smile.regression.ols(x,y)

}