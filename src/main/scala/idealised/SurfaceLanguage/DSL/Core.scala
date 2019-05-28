package idealised.SurfaceLanguage.DSL

import idealised.DPIA.NatFunIdentifier
import idealised.SurfaceLanguage.Types._
import idealised.SurfaceLanguage._
import lift.arithmetic.NamedVar

object identifier {
  def apply(name: String): IdentifierExpr = IdentifierExpr(name, None)

  def apply(name: String, dt: DataType): IdentifierExpr = IdentifierExpr(name, Some(dt))
}

object fun {
  def apply[T <: Type](f: IdentifierExpr => Expr): Expr = {
    val param = identifier(newName())
    LambdaExpr(param, f(param))
  }

  def apply[T <: Type](f: (IdentifierExpr, IdentifierExpr) => Expr): Expr = {
    val p1 = identifier(newName())
    val p2 = identifier(newName())
    LambdaExpr(p1, LambdaExpr(p2, f(p1, p2)))
  }

  def apply[T <: Type](dt: DataType)
                      (f: IdentifierExpr => Expr): Expr = {
    val param = identifier(newName(), dt)
    LambdaExpr(param, f(param))
  }

}

object nFun {
  def apply[T <: Type](f: (NatIdentifier, NatIdentifier, NatIdentifier, NatIdentifier, NatIdentifier) => Expr): Expr = {
    val p1 = NamedVar(newName())
    val p2 = NamedVar(newName())
    val p3 = NamedVar(newName())
    val p4 = NamedVar(newName())
    val p5 = NamedVar(newName())
    NatDependentLambdaExpr(p1, NatDependentLambdaExpr(p2,
      NatDependentLambdaExpr(p3, NatDependentLambdaExpr(p4, NatDependentLambdaExpr(p5, f(p1, p2, p3, p4, p5))))))
  }

  def apply[T <: Type](f: (NatIdentifier, NatIdentifier, NatIdentifier, NatIdentifier) => Expr): Expr = {
    val p1 = NamedVar(newName())
    val p2 = NamedVar(newName())
    val p3 = NamedVar(newName())
    val p4 = NamedVar(newName())
    NatDependentLambdaExpr(p1, NatDependentLambdaExpr(p2,
      NatDependentLambdaExpr(p3, NatDependentLambdaExpr(p4, f(p1, p2, p3, p4)))))
  }

  def apply[T <: Type](f: (NatIdentifier, NatIdentifier, NatIdentifier) => Expr): Expr = {
    val p1 = NamedVar(newName())
    val p2 = NamedVar(newName())
    val p3 = NamedVar(newName())
    NatDependentLambdaExpr(p1, NatDependentLambdaExpr(p2, NatDependentLambdaExpr(p3, f(p1, p2, p3))))
  }

  def apply[T <: Type](f: (NatIdentifier, NatIdentifier) => Expr): Expr = {
    val p1 = NamedVar(newName())
    val p2 = NamedVar(newName())
    NatDependentLambdaExpr(p1, NatDependentLambdaExpr(p2, f(p1, p2)))
  }

  def apply[T <: Type](f: NatIdentifier => Expr): NatDependentLambdaExpr = {
    val x = NamedVar(newName())
    NatDependentLambdaExpr(x, f(x))
  }
}

object tFun {
  def apply[T <: Type](f: DataTypeIdentifier => Expr): TypeDependentLambdaExpr = {
    val x = DataTypeIdentifier(newName())
    TypeDependentLambdaExpr(x, f(x))
  }

}

object letNat {
  def apply(defn:Expr, makeBody:NatFunIdentifier => Expr):LetNat = {
    val identifier = NatFunIdentifier()
    LetNat(identifier, defn, makeBody(identifier))
  }
}
