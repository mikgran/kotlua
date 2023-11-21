package mg.util.fn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@Suppress("KotlinConstantConditions")
class OptTest {

    @Test
    fun test_map() {
        val opt = Opt.of(VALUE)
        val candidate = opt
                .map { s -> "$s!" }
                .get()

        assertNotNull(candidate)
        assertEquals("$VALUE!", candidate)
    }

    @Test
    fun test_mapTo() {

        val strAny: Any = VALUE

        val opt = Opt.of(strAny)

        val candidate = opt.mapTo(String::class)

        assertNotNull(candidate)
        assertNotNull(candidate.get())
        assertEquals(candidate.get()!!::class, String::class)
        assertEquals(VALUE, candidate.get())
    }

    @Test
    fun test_mapTo_doesNotThrow() {
        assertDoesNotThrow {
            val strAny2: Any = VALUE
            val candidate = Opt.of(strAny2)
                    .mapTo(Int::class)

            assertNotNull(candidate)
            assertNull(candidate.get())
        }
    }

    @Test
    fun test_of() {

        val opt = Opt.of("a")

        assertNotNull(opt)
        assertNotNull(opt.get())
        assertEquals("a", opt.get())
    }

    @Test
    fun test_ofWithNull() {

        val nullStr: String? = null
        val opt = Opt.of(nullStr)

        assertNotNull(opt)
        assertNull(opt.get())
    }

    @Test
    fun test_ofOpt() {

        val anotherOpt = Opt.of(VALUE)
        val opt = Opt.of(anotherOpt)

        assertNotNull(opt)
        assertEquals("value", opt.get())
    }

    @Test
    fun test_get() {

        val opt = Opt.of(VALUE)

        assertNotNull(opt)
        assertEquals("value", opt.get())

        val nullStr: String? = null
        val opt2 = Opt.of(nullStr)

        assertNotNull(opt2)
        assertNull(opt2.get())
    }

    @Test
    fun test_ifEmptyWithSupplier() {

        val opt = Opt.of(VALUE)

        opt.use { "newString" }

        assertNotNull(opt)
        assertNotNull(opt.get())
        assertEquals(VALUE, opt.get())

        val nullStr: String? = null
        val opt2 = Opt.of(nullStr)

        assertNotNull(Opt)
        assertNull(opt2.get())

        val opt3 = opt2.use { NEW_STRING }

        assertNotNull(opt3)
        assertNotNull(opt3.get())
        assertEquals(NEW_STRING, opt3.get())
    }


    @Test
    fun test_ifEmptyWithConditionalSupplier() {

        val opt = Opt.of(VALUE)

        var conditionalSupplier: (() -> String)? = null
        opt.onSupplier(conditionalSupplier)

        assertNotNull(opt)
        assertNotNull(opt.get())
        assertEquals(VALUE, opt.get())

        val nullStr: String? = null
        val opt2 = Opt.of(nullStr)

        assertNotNull(Opt)
        assertNull(opt2.get())

        val opt4 = opt2.onSupplier(conditionalSupplier)

        assertNotNull(opt4)
        assertNull(opt4.get())

        conditionalSupplier = { NEW_STRING }
        val opt3 = opt.onSupplier(conditionalSupplier)

        assertNotNull(opt3)
        assertNotNull(opt3.get())
        assertEquals(NEW_STRING, opt3.get())

        conditionalSupplier = { ANOTHER_STRING }
        val str2 = opt
                .onSupplier(conditionalSupplier)
                .get()

        assertNotNull(str2)
        assertEquals(ANOTHER_STRING, str2)
    }

    @Test
    fun test_noneWithSideEffects() {

        var str = ""
        val nullValue: String? = null

        assertNotNull(str)
        assertEquals("", str)

        Opt.of(nullValue)
                .none { str = "text" }

        assertNotNull(str)
        assertEquals("text", str)

        var str2 = "text2"

        assertNotNull(str2)
        assertEquals("text2", str2)

        Opt.of(str2)
                .none { str2 = "" }

        assertNotNull(str2)
        assertEquals("text2", str2)
    }

    @Test
    fun test_isMissingThrow() {

        val nullValue: String? = null

        assertThrows(Exception::class.java) {

            Opt.of(nullValue)
                    .noneThrow { Exception() }
        }

        assertDoesNotThrow {

            Opt.of(VALUE)
                    .noneThrow { Exception() }
        }
    }

    @Test
    fun test_isPresentThrow() {

        val nullValue: String? = null

        assertDoesNotThrow {

            Opt.of(nullValue)
                    .someThrow { Exception() }
        }

        assertThrows(Exception::class.java) {

            Opt.of(VALUE)
                    .someThrow { Exception() }
        }
    }

    @Test
    fun test_filter() {

        val candidate = Opt
                .of(VALUE)
                .filter { it == ANOTHER_STRING }
                .get()

        assertNull(candidate)

        val candidate2 = Opt
                .of(VALUE)
                .filter { it == VALUE }
                .get()

        assertNotNull(candidate2)
        assertEquals(VALUE, candidate2)
    }

    @Test
    fun test_filterNot() {

        val candidate = Opt.of(VALUE)
                .filterNot { it == ANOTHER_STRING }
                .get()

        assertNotNull(candidate)
        assertEquals(VALUE, candidate)

        val candidate2 = Opt.of(VALUE)
                .filterNot { it == VALUE }
                .get()

        assertNull(candidate2)
    }

    @Test
    fun test_equals() {
        val opt1 = Opt.of(VALUE1)
        val opt2 = Opt.of(VALUE2)

        assertNotNull(opt1)
        assertNotNull(opt2)
        assertFalse(opt1 == opt2)

        val opt3 = Opt.of(VALUE2)

        assertNotNull(opt3)
        assertTrue(opt2 == opt3)
    }

    @Test
    fun test_hashCode() {
        // collisions aside, testing with 99% deterministic way
        val opt1 = Opt.of(VALUE1)
        val opt2 = Opt.of(VALUE2)
        val opt3 = Opt.of(VALUE3)

        assertNotNull(opt1)
        assertNotNull(opt2)
        assertNotEquals(opt1.hashCode(), opt2.hashCode())

        assertNotNull(opt3)
        assertEquals(opt2.hashCode(), opt3.hashCode())
    }

//    @Test
//    fun test_match() {
//
//        val opt = Opt.of(VALUE1)
//
//        val rr = opt.match("", { true }, { 3 })
//                .right()
//
//        assertNotNull(rr)
//        assertEquals(3, rr.get())
//
//        val candidate = opt.match("", { it == "someValue" }, { "$it!!" })
//                .match("", { s -> VALUE1 == s }, { "$it!" })
//
//        assertNotNull(candidate)
//        assertEquals("$VALUE1!", candidate.right().get())
//    }

//    @Test
//    fun test_matchWithNullAndNoValue() {
//
//        val stringNull: String? = null
//        val opt = Opt.of(stringNull)
//
//        val matched: BiOpt<String, Int> = opt.match("", { true }, { 3 })
//
//        val empty = Opt.empty<String>()
//
//        assertNotNull(matched)
//        assertNull(matched.right().get())
//        assertEquals(empty.get(), matched.right().get())
//    }

//    @Test
//    fun test_matchStringValueAgainstInt() {
//
//        val value = Opt.of(VALUE)
//
//        val candidate = value.match(3, { true }, { s -> s + 1 })
//
//        assertNotNull(candidate)
//        assertEquals(VALUE, candidate.left().get())
//    }

//    @Test
//    fun test_matchStringValueAgainstString() {
//
//        val value = Opt.of(VALUE)
//
//        val candidate: BiOpt<String, String> = value.match("",
//                { s -> s == "value" },
//                { s -> "$s!" })
//
//        val expected = Opt.of("$VALUE!")
//
//        assertNotNull(candidate)
//        assertEquals(expected.get(), candidate.right().get())
//    }

//    @Test
//    fun test_matchStringContent() {
//
//        val value = Opt.of(VALUE)
//
//        val candidate = TempValue("")
//
//        value.match(String::isNotBlank) {
//            candidate.a = it
//        }
//
//        assertNotNull(candidate.a)
//        assertEquals(VALUE, candidate.a)
//    }

    // ifPresent consumer & producer
    @Test
    fun test_ifPresentConsumer() {

        val value = Opt.of(VALUE)

        val candidate = TempValue("")

        value.some { s -> candidate.a = s }

        assertNotNull(candidate.a)
        assertEquals(VALUE, candidate.a)
    }

    @Test
    fun test_ifPresentConsumerWith() {
        fun <T : Any> mapWithAssert(expectedValue: Any?, value: Opt<String>, value2: T) {
            val candidate = TempValue("")
            val v2 = value2.toOpt()
            value.mapWith(v2) { s, s2 -> candidate.a = s + s2 }

            assertNotNull(candidate.a)
            assertEquals(expectedValue, candidate.a)
        }

        val expectedValue = VALUE + VALUE2
        val value = VALUE.toOpt()
        mapWithAssert(expectedValue, value, VALUE2.toOpt())
        mapWithAssert(expectedValue, value, VALUE2)
    }

    @Test
    fun test_getOrElse() {

        val candidate = Opt.of(VALUE)
                .getOrElse(ANOTHER_STRING)

        assertEqualsValue(candidate)

        val candidate2 = Opt.of<String>(null)
                .getOrElse(ANOTHER_STRING)

        assertEqualsAnotherString(candidate2)
    }

    @Test
    fun test_getOrElseProducer() {

        val candidate = Opt.of(VALUE)
                .getOrElse { ANOTHER_STRING }

        assertEqualsValue(candidate)

        val candidate2 = Opt.of<String>(null)
                .getOrElse { ANOTHER_STRING }

        assertEqualsAnotherString(candidate2)
    }

    @Test
    fun test_getAndMap() {

        val candidate = Opt.of(VALUE)
                .getAndMap { s -> s + "B" }

        assertNotNull(candidate)
        assertEquals(VALUE + "B", candidate)

        val candidate2 = Opt.of<String>(null)
                .getAndMap { s -> s + "X" }

        assertNull(candidate2) // no mapping if value == null
    }

    @Test
    fun test_toString() {

        val str = Opt.of("aString").toString()

        assertNotNull(str)
        assertEquals("aString", str)
    }

//      @Test
//      fun test_caseOf() {
//
//          val opt = Opt.of(1)
//
//          val biOpt = opt.case({ i -> i == 1 }, { i -> i + 1 })
//
//          assertNotNull(biOpt)
//          assertEquals(2, biOpt.right().get())
//
//          val candidate = Opt
//                  .of(10)
//                  .case({ it == 5 }, { it + 100 }) // predicate is false, not mapped here
//                  .case({ it == 10 }, { it + 50 }) // predicate is true, the mapping is applied
//                  .case({ it == 60 }, { it + 10 }) // predicate is true, but since the previous result is stored in the right no mapping is done
//
//          assertNotNull(candidate)
//          assertNotNull(candidate.right())
//          assertEquals(60, candidate.right().get())
//      }

    @Test
    fun test_getOrElseThrow() {

        assertDoesNotThrow {

            val opt = Opt.of("a")
            val candidate = opt.getOrElseThrow { Exception() }

            assertNotNull(candidate)
            assertEquals("a", candidate)
        }

        assertThrows(Exception::class.java) {

            val opt = Opt.of<String>(null)

            opt.getOrElseThrow { Exception() }
        }
    }

    @Test
    fun test_MapWith() {

        var str: String? = "1"

        val candidate = Opt.of("2")
                .mapWith(str) { a, b -> "$a $b" }
                .getOrElse("4")

        assertEquals("2 1", candidate)

        str = null

        @Suppress("UNREACHABLE_CODE")
        val candidate2 = Opt.of("3")
                .mapWith(str) { a, b -> "$a $b" }
                .getOrElse("4")

        assertEquals("4", candidate2)

        val str2: String? = str
        val candidate3 = Opt.of(str2)
                .mapWith("5") { a, b -> "$a $b" }
                .getOrElse("6")

        assertEquals("6", candidate3)

        val ropt7 = Opt.of("7")

        val candidate4 = Opt.of("8")
                .mapWith(ropt7) { a, b -> "$a $b" }
                .getOrElse("9")

        assertEquals("8 7", candidate4)
        assertNotEquals("9", candidate4)
    }

    @Test
    fun test_MapWith2() {

        val candidate = Opt.of("1")
                .mapWith("2", "3") { a, b, c -> "$a$b$c" }
                .getOrElse("9")

        assertEquals("123", candidate)

        val str: String? = null
        val candidate2 = Opt.of("1")
                .mapWith("2", str) { a, b, c -> "$a$b$c" }
                .getOrElse("9")

        assertEquals("9", candidate2)
    }

//      @Test
//      fun test_xmap() {
//          Opt.of(listOf(1, 2, 3, 4))
//                  .xmap { filter { it < 3 } }
//                  .apply {
//                      assertNotNull(get())
//                      assertEquals(listOf(1, 2), get())
//                  }
//
//          Opt.of("someData")
//                  .xmap { length }
//                  .apply {
//                      assertEquals(8, get())
//                  }
//      }

//      @Test
//      fun test_x() {
//          Opt.of(listOf(1, 2, 3, 4))
//                  .x {
//                      val subList = filter { it < 3 }
//                      assertNotNull(subList)
//                      assertEquals(2, subList.size)
//                      assertEquals(3, subList.sum())
//                  }
//                  .apply {
//                      val t = get()
//                      assertNotNull(t)
//                      assertEquals(4, t?.size)
//                      assertEquals(10, t?.sum())
//                  }
//
//          Opt.of(mutableListOf(1, 2, 3, 4))
//                  .x {
//                      add(5)
//                  }
//                  .apply {
//                      val list = get()
//                      assertNotNull(list)
//                      assertEquals(5, list?.size)
//                      assertEquals(15, list?.sum())
//                  }
//      }
//
//      @Test
//      fun test_c() {
//          Opt.of(listOf(1, 2, 3, 4))
//                  .c { list ->
//                      val subList = list.filter { it < 3 }
//                      assertNotNull(subList)
//                      assertEquals(2, subList.size)
//                      assertEquals(3, subList.sum())
//                  }
//                  .apply {
//                      val t = get()
//                      assertNotNull(t)
//                      assertEquals(4, t?.size)
//                      assertEquals(10, t?.sum())
//                  }
//
//          Opt.of(mutableListOf(1, 2, 3, 4))
//                  .c { list ->
//                      list.add(5)
//                  }
//                  .apply {
//                      val list = get()
//                      assertNotNull(list)
//                      assertEquals(5, list?.size)
//                      assertEquals(15, list?.sum())
//                  }
//      }

//      @Test
//      fun test_lxmap() {
//
//          fun twice(list: List<Int>): List<String> = list.map { "${it * 2}" }
//
//          val twiceFun =
//                  fun List<Int>.(): List<String> {
//                      return map { "${it * 2}" }
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lxmap(::twice)
//                  .apply {
//                      assertEquals(listOf("2", "4", "6", "8"), get())
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lxmap<Int, String> { twice(this) }
//                  .apply {
//                      assertEquals(listOf("2", "4", "6", "8"), get())
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lxmap(twiceFun)
//                  .apply {
//                      assertEquals(listOf("2", "4", "6", "8"), get())
//                  }
//      }
//
//      @Test
//      fun test_lxforEach() {
//
//          val mutableList = mutableListOf<Int>()
//
//          assertEquals(0, mutableList.sum())
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lxforEach(mutableList::add)
//                  .apply {
//                      val candidate: List<Int>? = get()
//                      assertNotNull(candidate)
//                      assertEquals(10, mutableList.sum())
//                  }
//      }
//
//      @Test
//      fun test_lfilter() {
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lfilter { i: Int -> i % 2 == 0 }
//                  .apply {
//                      assertEquals("2,4", get()?.joinToString(","))
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4, 5, 6, 7, 8))
//                  .lfilter<Int> { false }
//                  .apply {
//                      assertNotNull(get())
//                      assertTrue(get()?.isEmpty() ?: false)
//                  }
//      }

//      @Test
//      fun test_l_i_map() {
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lmap { i: Int -> "A$i" }
//                  .apply {
//                      assertEquals("A1,A2,A3,A4", get()?.joinToString(","))
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4))
//                  .lmap(::intToAiString)
//                  .apply {
//                      assertEquals("A1,A2,A3,A4", get()?.joinToString(","))
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4, "str"))
//                  .toList<Int>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertEquals(10, sum())
//                  }
//
//          Opt.of(listOf(1, 2, 3, 4).iterator())
//                  .lmap(::intToAiString)
//                  .apply {
//                      assertEquals("A1,A2,A3,A4", get()?.joinToString(","))
//                  }
//      }
//
//      private fun intToAiString(i: Int): String = "A$i"
//

    @Test
    fun testOptOfExtension() {

        val someData = "someData"
        val candidate: Opt<String> = someData.toOpt()
        assertNotNull(candidate)
        assertEquals(someData, candidate.get())
    }

//      @Test
//      fun testToList() {
//
//          val str = "A"
//          val list = listOf(1, 2, 3, 4)
//          val list2: List<Any> = listOf(1, 2, 3, str, 4)
//          val strNull: String? = null
//
//          str.toOpt()
//                  .toList<String>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertTrue(contains(str))
//                  }
//
//          strNull.toOpt()
//                  .toList<Any>()
//                  .apply {
//                      assertTrue(isEmpty())
//                  }
//
//          list.toOpt()
//                  .toList<Int>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertTrue(containsAll(list))
//                  }
//
//          list2.toOpt()
//                  .toList<Int>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertTrue(containsAll(list))
//                  }
//
//          list2.toOpt()
//                  .toList<String>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertTrue(contains(str))
//                      assertEquals(1, size)
//                  }
//
//          list2.toOpt()
//                  .toList<Any>()
//                  .apply {
//                      assertTrue(isNotEmpty())
//                      assertTrue(containsAll(list2))
//                  }
//      }

    @Test
    fun testMapOnTrue() {

        val testVar = "test"
        val nullT: Boolean? = null
        val trueT = true
        val falseT = false

        // Boolean -> Opt
        nullT.onTrue { testVar }
                .apply {
                    assertFalse(some())
                }

        trueT.onTrue { testVar }
                .apply {
                    assertTrue(some())
                }

        falseT.onTrue { testVar }
                .apply {
                    assertFalse(some())
                }

        // existing Opts
        nullT.toOpt()
                .onTrue { testVar }
                .apply {
                    assertFalse(some())
                }

        trueT.toOpt()
                .onTrue { testVar }
                .apply {
                    assertTrue(some())
                }

        falseT.toOpt()
                .onTrue { testVar }
                .apply {
                    assertFalse(some())
                }
    }

    @Test
    fun testMapOnFalse() {

        val testVar = "test"
        val nullT: Boolean? = null
        val trueT = true
        val falseT = false

        // Boolean -> Opt
        nullT.onFalse { testVar }
                .apply {
                    assertFalse(some())
                }

        trueT.onFalse { testVar }
                .apply {
                    assertFalse(some())
                }

        falseT.onFalse { testVar }
                .apply {
                    assertEquals(testVar, get())
                }

        // existing Opts
        nullT.toOpt()
                .onFalse { testVar }
                .apply {
                    assertFalse(some())
                }

        trueT.toOpt()
                .onFalse { testVar }
                .apply {
                    assertFalse(some())
                }

        falseT.toOpt()
                .onFalse { testVar }
                .apply {
                    assertEquals(testVar, get())
                }
    }

    @Test
    fun testMapIfWithPredicate() {

        Opt.empty<Int>()
                .mapWhen(true) { 1 }
                .apply {
                    assertFalse(some())
                }

        Opt.of(1)
                .mapWhen(true) { it + 1 }
                .apply {
                    assertEquals(2, get())
                }

        Opt.empty<Int>()
                .mapWhen(false) { 1 }
                .apply {
                    assertFalse(some())
                }

        Opt.empty<String>()
                .mapWhen(false) { 1 }
                .apply {
                    assertFalse(some())
                }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isTrue(any: Any) = true
    private fun isOdd(i: Int) = i % 2 == 1
    private fun hasLetters(a: Any) = a.toString().isNotBlank()

    @Test
    fun testMapWhen() {

        Opt.empty<Int>()
                .mapWhen(::isTrue) { 1 }
                .apply {
                    assertFalse(some())
                }

        Opt.of(1)
                .mapWhen(::isOdd) { it + 1 }
                .apply {
                    assertEquals(2, get())
                }

        Opt.of(2)
                .mapWhen(::isOdd) { it + 1 }
                .apply {
                    assertEquals(2, get())
                }

        Opt.of("str")
                .mapWhen(::hasLetters) { "${it}1" }
                .apply {
                    assertEquals("str1", get())
                }

        Opt.of("str")
                .mapWhen({ it.length > 3 }) { "$it!" }
                .apply {
                    assertEquals("str", get())
                }

        Opt.of("str")
                .mapWhen(true) { it + 1 }
                .apply {
                    assertEquals("str1", get())
                }

        Opt.of("str")
                .mapWhen(false) { it + 1 }
                .apply {
                    assertEquals("str", get())
                }
    }

    private fun assertEqualsAnotherString(candidate2: String) {
        assertNotNull(candidate2)
        assertEquals(ANOTHER_STRING, candidate2)
    }

    private fun assertEqualsValue(candidate: String) {
        assertNotNull(candidate)
        assertEquals(VALUE, candidate)
    }

    companion object {
        const val ANOTHER_STRING = "anotherString"
        const val NEW_STRING = "newString"
        const val VALUE = "value"
        const val VALUE1 = "value1"
        const val VALUE2 = "value2"
        const val VALUE3 = "value2"
    }
}

class TempValue(var a: String?) {
    fun set(s: String) {
        a = s
    }
}