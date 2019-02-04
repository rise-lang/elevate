package idealised.DPIA.FunctionalPrimitives


import idealised.DPIA.Compilation.RewriteToImperative
import idealised.DPIA.DSL._
import idealised.DPIA.IntermediatePrimitives.ScanSeqI
import idealised.DPIA.Phrases._
import idealised.DPIA.Semantics.OperationalSemantics._
import idealised.DPIA.Types._
import idealised.DPIA._

import scala.xml.Elem
/**
  * Created by federico on 13/01/18.
  */
abstract  class AbstractScan(n: Nat,
                             dt1: DataType,
                             dt2: DataType,
                             f: Phrase[ExpType -> (ExpType -> ExpType)],
                             init:Phrase[ExpType],
                             array: Phrase[ExpType])
  extends ExpPrimitive {

  def makeScan: (Nat, DataType, DataType, Phrase[ExpType -> (ExpType -> ExpType)], Phrase[ExpType], Phrase[ExpType]) => AbstractScan

  def makeScanI: (Nat, DataType, DataType,
    Phrase[ExpType -> (ExpType -> (AccType -> CommandType))], Phrase[ExpType], Phrase[ExpType], Phrase[AccType]) => Phrase[CommandType]

  override val `type`: ExpType =
    (n: Nat) -> (dt1: DataType) -> (dt2: DataType) ->
      (f :: t"exp[$dt1] -> exp[$dt2] -> exp[$dt2]") ->
      (init :: exp"[$dt2]") ->
      (array :: exp"[$n.$dt1]") -> exp"[$n.$dt2]"

  override def visitAndRebuild(fun: VisitAndRebuild.Visitor): Phrase[ExpType] = {
    makeScan(fun(n), fun(dt1), fun(dt2),
      VisitAndRebuild(f, fun), VisitAndRebuild(init, fun), VisitAndRebuild(array, fun))
  }

  override def eval(s: Store): Data = ???

  override def prettyPrint: String =
    s"${this.getClass.getSimpleName} (${PrettyPhrasePrinter(f)}) " +
      s"(${PrettyPhrasePrinter(init)}) (${PrettyPhrasePrinter(array)})"

  override def acceptorTranslation(A: Phrase[AccType]): Phrase[CommandType] = {
    import RewriteToImperative._

    con(array)(λ(exp"[$n.$dt1]")(x =>
      con(init)(λ(exp"[$dt2]")(y =>
        makeScanI(n, dt1, dt2,
          λ(exp"[$dt1]")(x => λ(exp"[$dt2]")(y => λ(acc"[$dt2]")(o => acc(f(x)(y))(o)))),
          y, x, A)
      )
      ))
    )
  }

  override def continuationTranslation(C: Phrase[ExpType -> CommandType]): Phrase[CommandType] = {
    import RewriteToImperative._

    `new`(dt"[$n.$dt2]", idealised.OpenCL.GlobalMemory, λ(exp"[$n.$dt2]" x acc"[$n.$dt2]")(tmp =>
      acc(this)(tmp.wr) `;` C(tmp.rd) ))
  }

  override def xmlPrinter: Elem =
    <reduce n={ToString(n)} dt1={ToString(dt1)} dt2={ToString(dt2)}>
      <f type={ToString(ExpType(dt1) -> (ExpType(dt2) -> ExpType(dt2)))}>
        {Phrases.xmlPrinter(f)}
      </f>
      <init type={ToString(ExpType(dt2))}>
        {Phrases.xmlPrinter(init)}
      </init>
      <input type={ToString(ExpType(ArrayType(n, dt1)))}>
        {Phrases.xmlPrinter(array)}
      </input>
    </reduce>.copy(label = {
      val name = this.getClass.getSimpleName
      Character.toLowerCase(name.charAt(0)) + name.substring(1)
    })

}
