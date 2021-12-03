package elevate.heuristic_search.heuristics

import elevate.heuristic_search.{Heuristic, HeuristicPanel}
import elevate.heuristic_search.util.{Path, PathElement, Solution}

import scala.collection.immutable.Queue

class Exhaustive[P] extends Heuristic[P] {


  def start(panel:HeuristicPanel[P], initialSolution:Solution[P], depth:Int): (P, Option[Double], Path[P]) = {

    println("depth: " + depth)

    var solution = initialSolution
    val solutionValue = panel.f(solution)

    // craete path
    val path = new Path(solution.expression, solutionValue, null, null, 0)

    var queue = Queue.empty[(Solution[P], PathElement[P])]
    queue = queue.enqueue(solution, path.initial)

    var i = 0
    while(!queue.isEmpty && i < depth) {
      i = i + 1

//      println("i: " + i)
//      println("queue: " + queue)

      // get element from queue
      val current = queue.dequeue

//      println("current: " + current)

      // update current path element
      //      path.setCurrent(current._1._2)
      // todo reach this from start (step by step)
      path.add(current._1._2.program, current._1._2.strategy, current._1._2.value)

      // start at initial node
      var down = path.initial

      println("\n")
      println(" --------- go down ---------- ")
      // go down step by step until reaching current program
      while (Integer.toHexString(current._1._2.program.hashCode()) != Integer.toHexString(down.program.hashCode())) {
        println("down: " + Integer.toHexString(down.program.hashCode()))
        println("current: " + Integer.toHexString(current._1._2.program.hashCode()))
        down = down.successor
        //        tmp.program.hashCode() == tmp.successor.program.hashCode()){
        // go one step down
        path.add(down.program, down.strategy, down.value)
      }
      println(" --------- finished ---------- ")
      println("\n")


      // update queue
      queue = current._2

      // get neighborhood
      val Ns = panel.N(current._1._1)

      Ns.foreach(ne => {
//        path.writePathToDot("/home/jo/development/rise-lang/shine/exploration/dot/mv.dot")
        // eval function value
        val fne = panel.f(ne)

        // add path element
        path.add(ne.expression, ne.strategies.last, fne)

        // add path element and solution to queue
        queue = queue.enqueue((ne, path.current))

        // revert path
        path.add(current._1._1.expression, elevate.core.strategies.basic.revert, current._1._2.value)
      })


      println("\n")
      println(" --------- go up ---------- ")
      var up = current._1._2
      while (up.predecessor != null) {
        up = up.predecessor
        path.add(up.program, elevate.core.strategies.basic.revert, up.value)
      }
      println(" --------- finished ---------- ")
      println("\n")
//      current._1._2.predecessor match {
//        case null => // do nothing
//        case _ =>
//           go back to parent
//          path.add(current._1._2.predecessor.program, elevate.core.strategies.basic.revert, current._1._2.predecessor.value)
//      }

    }

    // last?
    (solution.expression, solutionValue, path)
  }
}
