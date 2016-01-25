def average(list: List[Double]): Double = list match {
  case head :: tail => tail.foldLeft( (head,1) )((r,c) =>
    {println("(("+r._1+" + ("+c+"/"+r._2+")) * "+r._2+" / ("+r._2+"+1), "+r._2+"+1)");
      ((r._1 + (c/r._2)) * r._2 / (r._2+1), r._2+1)} )._1
}

average(List(10,20,30,40))

