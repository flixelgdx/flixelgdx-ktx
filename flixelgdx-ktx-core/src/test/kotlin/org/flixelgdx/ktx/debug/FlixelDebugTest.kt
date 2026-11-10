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

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import org.flixelgdx.Flixel
import org.flixelgdx.backend.FlixelRuntimeDevice
import org.flixelgdx.backend.FlixelRuntimeMode
import org.flixelgdx.debug.FlixelDebugManager

class FlixelDebugTest {

  private lateinit var originalRuntime: FlixelRuntimeDevice

  @BeforeTest
  fun setUp() {
    originalRuntime = Flixel.runtime
    // Flixel.debug is only created when a game starts, so give the tests their own.
    Flixel.debug = FlixelDebugManager()
  }

  @AfterTest
  fun tearDown() {
    Flixel.runtime = originalRuntime
  }

  private fun useMode(mode: FlixelRuntimeMode) {
    Flixel.runtime =
      object : FlixelRuntimeDevice {
        override fun getMode(): FlixelRuntimeMode = mode
      }
  }

  @Test
  fun runsWithTheDebugManagerInDebugMode() {
    useMode(FlixelRuntimeMode.DEBUG)
    var receiver: FlixelDebugManager? = null

    debugOnly { receiver = this }

    assertSame(Flixel.debug, receiver)
  }

  @Test
  fun skipsTheBlockInReleaseMode() {
    useMode(FlixelRuntimeMode.RELEASE)
    var ran = false

    debugOnly { ran = true }

    assertFalse(ran)
  }
}
