package de.ironjan.pppb.core

object MeanStd {
  /**
    * http://www.scalaformachinelearning.com/2015/10/recursive-mean-and-standard-deviation.html
    *
    * @param x
    * @return
    */
  def meanStd(x: Array[Double]): (Double, Double) = {

    @scala.annotation.tailrec
    def meanStd(
                 x: Array[Double],
                 mu: Double,
                 Q: Double,
                 count: Int): (Double, Double) =
      if (count >= x.length) (mu, Math.sqrt(Q / x.length))
      else {
        val newCount = count + 1
        val newMu = x(count) / newCount + mu * (1.0 - 1.0 / newCount)
        val newQ = Q + (x(count) - mu) * (x(count) - newMu)
        meanStd(x, newMu, newQ, newCount)
      }

    meanStd(x, 0.0, 0.0, 0)
  }
}
