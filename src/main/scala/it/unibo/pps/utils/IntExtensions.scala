package it.unibo.pps.utils

import scala.math.Ordering.Int

/** Helper object that contains extension methods for [[Int]]
 * 
 * Contains:
 * - [[inRange()]]
 * - [[inBetween()]]
 * - [[half]]
 */
object IntExtensions:
  extension (x: Int)
    /** Extension method of [[Int]].
     *
     * This is used to know if a number is inside a specified range.
     *
     * It's not necessary to write as `y` the smaller number and the bigger one as `z`, it works either way.
     *
     * @param y one of the two limits of the range.
     * @param z the other limit of the range.
     * @return `true` if the number is inside the range, limits included, `false` otherwise.
     */
    def inRange(y: Int, z: Int): Boolean =
      x <= Int.max(y, z) && x >= Int.min(y, z)

    /** Extension method of [[Int]].
     *
     * This is used to know if a number is between two numbers.
     *
     * It's not necessary to write as `y` the smaller number and the bigger one as `z`, it works either way.
     *
     * @param y one of the two numbers.
     * @param z the other number.
     * @return `true` if the number is between `y` and `z`, `y` and `z` excluded, `false` otherwise.
     */
    def inBetween(y: Int, z: Int): Boolean =
      x.inRange(Int.min(y, z) + 1, Int.max(y, z) - 1)
    
    /** Extension method of [[Int]].
     *
     * @return the number divided by 2.
     */
    def half: Int = x / 2
