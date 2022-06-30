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
import magnolify.test.ADT._
import magnolify.test.Simple._
import magnolify.test._
import magnolify.cats.semiauto.ShowDerivation
import org.scalacheck._
import cats.laws.discipline.{ContravariantTests, MiniInt}
import cats.laws.discipline.arbitrary._
import cats.laws.discipline.eq._

import scala.reflect._

import java.net.URI
import java.time.Duration

class ShowDerivationSuite extends MagnolifySuite with magnolify.cats.AutoDerivation {

  private def test[T: Arbitrary: ClassTag: Show]: Unit = {
//    val show = ensureSerializable(implicitly[Show[T]])
    val show = implicitly[Show[T]]
    val name = className[T]
    include(ContravariantTests[Show].contravariant[MiniInt, Int, Boolean].all, s"$name.")

    property(s"$name.fullName") {
      Prop.forAll { (v: T) =>
        val fullName = v.getClass.getCanonicalName.stripSuffix("$")
        val s = show.show(v)
        s.startsWith(s"$fullName {") && s.endsWith("}")
      }
    }
  }

  import magnolify.scalacheck.test.TestArbitraryImplicits._
  implicit val showArray: Show[Array[Int]] = Show.fromToString
  implicit val showUri: Show[URI] = Show.fromToString
  implicit val showDuration: Show[Duration] = Show.fromToString

  test[Numbers]
  test[Required]
  test[Nullable]
  test[Repeated]
  test[Nested]
  test[Collections]
  test[Custom]

  // recursive structures require to assign the derived value to an implicit variable
  implicit lazy val showNode: Show[Node] = ShowDerivation[Node]
  implicit lazy val showGNode: Show[GNode[Int]] = ShowDerivation[GNode[Int]]
  test[Node]
  test[GNode[Int]]

  test[Shape]
  test[Color]
}
