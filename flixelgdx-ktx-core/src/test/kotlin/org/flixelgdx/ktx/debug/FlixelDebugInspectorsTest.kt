/*
 * MIT License
 *
 * Copyright (c) 2026 stringdotjar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flixelgdx.ktx.debug

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.flixelgdx.collections.FlixelArray
import org.flixelgdx.debug.FlixelDebugObjectIds
import org.flixelgdx.debug.feeds.FlixelDebugInspector
import org.flixelgdx.debug.inspector.FlixelDebugProperty
import org.flixelgdx.debug.inspector.FlixelDebugSection
import org.flixelgdx.debug.inspector.FlixelDebugValue
import org.flixelgdx.debug.remote.FlixelDebugMessageReader
import org.flixelgdx.debug.remote.FlixelDebugMessageWriter
import org.flixelgdx.debug.remote.FlixelDebugProtocol
import org.flixelgdx.util.FlixelString

class FlixelDebugInspectorsTest {

  enum class Mood {
    CALM,
    ANGRY,
  }

  class Weapon(var ammo: Int)

  class Player {
    var health = 10
    var speed = 2.5f
    var score = 1L shl 40
    var luck = 0.25
    var alive = true
    var name = "Ana"
    var mood = Mood.CALM
    var weapon: Weapon? = Weapon(6)
  }

  private val inspector = FlixelDebugInspector(FlixelDebugObjectIds())

  private fun section(title: String, selected: Any): FlixelDebugSection {
    inspector.select(selected)
    val sections = FlixelArray<FlixelDebugSection>()
    inspector.collectSections(sections)
    return (0 until sections.size).map { sections[it] }.first { it.displayName == title }
  }

  private fun FlixelDebugSection.row(name: String): FlixelDebugProperty<Any> =
    (0 until count).map { getProperty(it) }.first { it.name == name }

  /** Writes [row] for [target] and reads its value back the way the app would. */
  private fun read(row: FlixelDebugProperty<Any>, target: Any): FlixelDebugValue {
    val writer = FlixelDebugMessageWriter(64)
    writer.beginMessage(FlixelDebugProtocol.MSG_FEED_DATA)
    row.writeRow(writer, target, 1)
    val reader = FlixelDebugMessageReader(writer.finish())
    reader.readU8()
    reader.readString(FlixelString())
    return FlixelDebugValue().read(reader)
  }

  /** Builds an edit the way the app would send it. */
  private fun edit(write: FlixelDebugMessageWriter.() -> Unit): FlixelDebugValue {
    val writer = FlixelDebugMessageWriter(64)
    writer.beginMessage(FlixelDebugProtocol.MSG_FEED_DATA)
    writer.write()
    val reader = FlixelDebugMessageReader(writer.finish())
    reader.readU8()
    return FlixelDebugValue().read(reader)
  }

  @Test
  fun inspectRegistersRowsForEachType() {
    inspector.inspect<Player>("Player") {
      int("Health", Player::health) { p, v -> p.health = v }
      float("Speed") { it.speed }
      long("Score", Player::score) { p, v -> p.score = v }
      double("Luck", Player::luck) { p, v -> p.luck = v }
      boolean("Alive", Player::alive) { p, v -> p.alive = v }
      text("Name", { p, out -> out.concat(p.name) }) { p, v -> p.name = v.toString() }
      enum("Mood", get = Player::mood)
      int("Id", once = true) { 7 }
    }
    val player = Player()
    val section = section("Player", player)

    assertEquals(8, section.count)
    assertEquals(10, read(section.row("Health"), player).asInt())
    assertEquals(2.5f, read(section.row("Speed"), player).asFloat())
    assertEquals("CALM", read(section.row("Mood"), player).text.toString())
    assertTrue(section.row("Health").isEditable)
    assertFalse(section.row("Speed").isEditable)
    assertTrue(section.row("Id").isOnce)
    assertFalse(section.row("Health").isOnce)

    section.row("Health").apply(player, edit { writeValue(3) })
    section.row("Score").apply(player, edit { writeValue(5L) })
    section.row("Luck").apply(player, edit { writeValue(0.5) })
    section.row("Alive").apply(player, edit { writeValue(false) })
    section.row("Name").apply(player, edit { writeValue("Bo") })

    assertEquals(3, player.health)
    assertEquals(5L, player.score)
    assertEquals(0.5, player.luck)
    assertFalse(player.alive)
    assertEquals("Bo", player.name)
  }

  @Test
  fun inspectViaReadsFromTheRelatedObject() {
    inspector.inspect("Weapon", via = Player::weapon) {
      int("Ammo", Weapon::ammo) { w, v -> w.ammo = v }
    }
    val player = Player()
    val section = section("Weapon", player)

    assertSame(player.weapon, section.resolve(player))
    player.weapon = null
    assertNull(section.resolve(player))
  }

  @Test
  fun reifiedHelpersMatchTheClassBasedCalls() {
    inspector.inspect<Player>("Player") { int("Health") { it.health } }
    assertTrue(inspector.isRegistered<Player>())

    val player = Player()
    inspector.select(player)
    assertSame(player, inspector.selectedAs<Player>())
    assertNull(inspector.selectedAs<Weapon>())

    assertTrue(inspector.unregister<Player>())
    assertFalse(inspector.isRegistered<Player>())
  }

  @Test
  fun onSelectOnlyFiresForTheType() {
    val seen = mutableListOf<Player>()
    val listener = inspector.onSelect<Player> { seen += it }
    val player = Player()

    inspector.select(Weapon(1))
    inspector.select(player)
    inspector.removeSelectionListener(listener)
    inspector.select(Player())

    assertEquals(listOf(player), seen)
  }
}
