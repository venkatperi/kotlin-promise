package com.vperi.promise.internal

class AscTree {

  class Node(val promise: PImpl<*>) {
    internal val children
      get() = promise.handlers

    override fun toString(): String =
      promise.toString()
  }

  companion object {
    private val strings = mapOf(
      "tail" to "└─",
      "continuation" to "├─",
      "prefix_tail" to "  ",
      "prefix_continuation" to "│ ")

    fun toTree(node: Node, prefix: String = "", isTail: Boolean = true): String {
      val s = if (isTail) strings["tail"] else strings["continuation"]
      val sPrefix = if (isTail) strings["prefix_tail"] else strings["prefix_continuation"]
      return listOf(prefix, s, node.toString(), "\n").joinToString("") +
        node.children.mapIndexed { index, c ->
          toTree(Node(c.promise), prefix = "$prefix$sPrefix",
            isTail = index >= node.children.size - 1)
        }.joinToString("")
    }
  }
}