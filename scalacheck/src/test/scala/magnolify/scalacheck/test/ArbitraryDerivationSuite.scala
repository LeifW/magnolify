/*
 * Copyright 2019 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package magnolify.scalacheck.test

//import magnolify.scalacheck.Fallback
//import magnolify.scalacheck.semiauto.ArbitraryDerivation
import magnolify.test.ADT._
import magnolify.test.Simple._
import magnolify.test._
import org.scalacheck._
import org.scalacheck.rng.Seed

import scala.reflect._

class ArbitraryDerivationSuite extends MagnolifySuite with magnolify.scalacheck.AutoDerivation {
  private def test[T: Arbitrary: ClassTag]: Unit = test[T](null)
  private def test[T: Arbitrary: ClassTag](suffix: String): Unit = test[T, T](identity, suffix)

  private def test[T: ClassTag, U](f: T => U, suffix: String = null)(implicit
    t: Arbitrary[T]
  ): Unit = {
    // val g = ensureSerializable(t).arbitrary
    val g = t.arbitrary
    val name = className[T] + (if (suffix == null) "" else "." + suffix)
    val prms = Gen.Parameters.default
    // `forAll(Gen.listOfN(10, g))` fails for `Repeated` & `Collections` when size parameter <= 1
    property(s"$name.uniqueness") {
      Prop.forAll { (l: Long) =>
        val seed = Seed(l) // prevent Magnolia from deriving `Seed`
        val xs = Gen.listOfN(10, g)(prms, seed).get
        xs.iterator.map(f).toSet.size > 1
      }
    }
    property(s"$name.consistency") {
      Prop.forAll { (l: Long) =>
        val seed = Seed(l) // prevent Magnolia from deriving `Seed`
        f(g(prms, seed).get) == f(g(prms, seed).get)
      }
    }
  }

  import TestArbitraryImplicits.{arbDuration, arbUri}

  test[Numbers]
  test[Required]
  test[Nullable]
  test[Repeated]
  test((c: Collections) => (c.a.toList, c.l, c.v))
  test[Nested]

  {
    implicit val arbInt: Arbitrary[Int] = Arbitrary(Gen.chooseNum(0, 100))
    implicit val arbLong: Arbitrary[Long] = Arbitrary(Gen.chooseNum(100, 10000))
    property("implicits") {
      Prop.forAll { (x: Integers) =>
        x.i >= 0 && x.i <= 100 && x.l >= 100 && x.l <= 10000
      }
    }
  }

  test[Custom]

//  // recursive structures require to assign the derived value to an implicit variable
//  {
//    implicit val f: Fallback[Node] = Fallback[Leaf]
//    implicit lazy val arbNode: Arbitrary[Node] = ArbitraryDerivation[Node]
//    test[Node]
//  }
//
//  {
//    implicit val f: Fallback[GNode[Int]] = Fallback(Gen.const(GLeaf(0)))
//    implicit lazy val arbGNode: Arbitrary[GNode[Int]] = ArbitraryDerivation[GNode[Int]]
//    test[GNode[Int]]("Fallback(G: Gen[T])")
//  }
//
//  {
//    implicit val f: Fallback[GNode[Int]] = Fallback(GLeaf(0))
//    implicit lazy val arbGNode: Arbitrary[GNode[Int]] = ArbitraryDerivation[GNode[Int]]
//    test[GNode[Int]]("Fallback(v: T)")
//  }
//
//  {
//    implicit val f: Fallback[GNode[Int]] = Fallback[GLeaf[Int]]
//    implicit lazy val arbGNode: Arbitrary[GNode[Int]] = ArbitraryDerivation[GNode[Int]]
//    test[GNode[Int]]("Fallback[T]")
//  }

  test[Shape]
  test[Color]

  property("Seed") {
    Prop.forAll { (seed: Seed) =>
      seed.next != seed
    }
  }
}
