/*
 * Copyright 2022 majk-p
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

package net.michalp.identicon4s

import cats.Applicative

trait Hashing[F[_]] {
  def hash(input: String): F[Long]
}

object Hashing {
  def apply[F[_]](implicit ev: Hashing[F]): Hashing[F] = ev

  def instance[F[_]: Applicative] = new Hashing[F] {
    def hash(input: String): F[Long] =
      Applicative[F].pure(input.hashCode().toLong)
  }

}
