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

package magnolify.cats

import cats._
import cats.kernel.{Band, CommutativeGroup, CommutativeMonoid, CommutativeSemigroup}
import magnolify.test.Simple._

object ScopeTest {
  case class Sets(s: Set[Int])

  object Auto {
    import magnolify.cats.auto._
    implicitly[Eq[Numbers]]
    implicitly[Hash[Numbers]]
    implicitly[Semigroup[Numbers]]
    implicitly[CommutativeSemigroup[Numbers]]
    implicitly[Band[Sets]]
    implicitly[Monoid[Numbers]]
    implicitly[CommutativeMonoid[Numbers]]
    implicitly[Group[Numbers]]
    implicitly[CommutativeGroup[Numbers]]
    implicitly[Show[Numbers]]
  }

  object Semi {
    import magnolify.cats.semiauto._
    EqDerivation[Numbers]
    HashDerivation[Numbers]
    SemigroupDerivation[Numbers]
    CommutativeSemigroupDerivation[Numbers]
    BandDerivation[Sets]
    MonoidDerivation[Numbers]
    CommutativeMonoidDerivation[Numbers]
    GroupDerivation[Numbers]
    CommutativeGroupDerivation[Numbers]
    ShowDerivation[Numbers]
  }
}
