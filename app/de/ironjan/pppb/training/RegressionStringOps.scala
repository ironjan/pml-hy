package de.ironjan.pppb.training

import org.joda.time.DateTime
import smile.regression.{GradientTreeBoost, RandomForest, Regression, RegressionTree}

object RegressionStringOps {
  implicit class RegressionOps(regression: Regression[Array[Double]]) {
    def toPrintable: String = regression.getClass.getName
  }

  implicit class RegressionTreeOps(regressionTree: RegressionTree)
  extends RegressionOps(regressionTree){
    override def toPrintable: String = {
      val importance = regressionTree.importance().mkString(", ")
      s"RegressionTree: maxDepth = ${regressionTree.maxDepth()}, importance = [$importance]"
    }
  }

  implicit class RandomForestOps(rdf: RandomForest)
    extends RegressionOps(rdf){
    override def toPrintable: String = {
      val importance = rdf.importance().mkString(", ")
      s"RandomForest: importance = [$importance]"
    }
  }


  implicit class GradientTreeBoostOps(gtb: GradientTreeBoost)
    extends RegressionOps(gtb){
    override def toPrintable: String = {
      val importance = gtb.importance().mkString(", ")
      s"GradientTreeBoost: importance = [$importance]"
    }
  }

}
