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

package magnolify.cats.test

import cats._
import cats.kernel.laws.discipline._
import magnolify.cats.semiauto.EqDerivation
import magnolify.test.ADT._
import magnolify.test.Simple._
import magnolify.test._
import org.scalacheck._

import scala.reflect._

class EqDerivationSuite extends MagnolifySuite with magnolify.cats.AutoDerivation {

  private def test[T: Arbitrary: ClassTag: Cogen: Eq]: Unit = {
//    val eq = ensureSerializable(implicitly[Eq[T]])
    val eq = implicitly[Eq[T]]
    include(EqTests[T](eq).eqv.all, className[T] + ".")
  }

  {
    // prefer cats Eq instance as auto derivation
    import cats.Eq._
    import magnolify.scalacheck.test.TestArbitraryImplicits._
    import magnolify.scalacheck.test.TestCogenImplicits._
    import magnolify.cats.test.TestEqImplicits.{eqArray, eqDuration, eqUri}

    test[Numbers]
    test[Required]
    test[Nullable]
    test[Repeated]
    test[Nested]
    test[Collections]
    test[Custom]

    // recursive structures require to assign the derived value to an implicit variable
    implicit lazy val eqNode: Eq[Node] = EqDerivation[Node]
    implicit lazy val eqGNode: Eq[GNode[Int]] = EqDerivation[GNode[Int]]
    test[Node]
    test[GNode[Int]]

    test[Shape]
    test[Color]
  }
}
