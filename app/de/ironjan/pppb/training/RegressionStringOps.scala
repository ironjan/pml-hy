package de.ironjan.pppb.training

import org.joda.time.DateTime
import smile.regression.{GradientTreeBoost, RandomForest, Regression, RegressionTree}

object RegressionStringOps {

  implicit class RegressionOps(regression: Regression[Array[Double]]) {
    def toPrintable: String = {
    regression match {
      case rt: RegressionTree => {
        val importance = rt.importance().mkString(", ")
        s"RegressionTree: maxDepth = ${rt.maxDepth()}, importance = [$importance]"
      }
      case rf : RandomForest => {
        val importance = rf.importance().mkString(", ")
        s"RandomForest: importance = [$importance]"
      }
      case gtb: GradientTreeBoost => {
        val importance = gtb.importance().mkString(", ")
        s"GradientTreeBoost: importance = [$importance]"
      }
      case r => r.getClass.getName
    }
    }
  }

}
