package com.sy.tree

import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

data class Data(val data: Int?, val point: Node?)

data class Node(val m: Int, val isLeave: Boolean = true) {
    val arr = arrayOfNulls<Data?>(m * 2 + 1)
    var size = 0
    var id = 0

    fun find(data: Int): Int {
        var start = 0
        var end = size * 2 + 1

        fun loop(): Int {
            var p = start + ((end - start) / 2)
            p = if (p % 2 == 0) {
                if (end - start <= 2) {
                    return p
                }
                p += 1
                p
            } else {
                p
            }
            val d = arr[p]?.data!!
            if (d == data) {
                return p
            }
            if (d > data) {
                end = p
                return loop()
            } else {
                start = p
                return loop()
            }
        }

        val index = loop()
        return index
    }

    fun addToLeave(index: Int, data: Int) {
        //if (this.isLeave) {//叶子
        if (index % 2 == 1) {
            throw RuntimeException("主键冲突")
        }
        //0 1 2 3 4 5 6 7 8 9 10 11 12
        //0 1 0 2 0 3 0 5 0 7 0  0  0
        val i = index + 1
        val s = size * 2 - index
        System.arraycopy(this.arr, i, this.arr, i + 2, s)
        this.arr[i] = Data(data, null)
        size++
        //}
    }

    fun setNode(index: Int, node: Node) {
        this.arr[index] = Data(null, node)
    }

    fun setData(index: Int, data: Data) {
        this.arr[index] = data
    }

    fun size(): Int {
        return size
    }

    companion object {
        fun show(node: Node) {
            node.arr.forEachIndexed { index, d ->
                if (index % 2 == 1) {
                    print("${d?.data} ")
                }
            }

            if (!node.isLeave) {
                node.arr.forEachIndexed { index, d ->
                    if (index % 2 == 0) {
                        d?.also {
                            it.point?.also { show(it) }
                        }
                    }
                }
            }
            println()
        }
    }
}

class BplusTree(val m: Int) {
    private var root = Node(m)
    fun spiltData(node: Node): Node {
        val half = m / 2
        node.size = half
        val less = m - half
        val b = Node(m, node.isLeave)
        b.id = BplusTree.getId()
        System.arraycopy(node.arr, half * 2, b.arr, 0, less * 2 + 1)
        b.size = less
        node.arr.fill(null, half * 2)
        node.arr[half * 2] = Data(null, b)
        return b
    }

    fun spiltNode(node: Node): Pair<Int, Node> {
        val half = m / 2
        node.size = half
        val less = m - half
        val b = Node(m, node.isLeave)
        System.arraycopy(node.arr, (half + 1) * 2, b.arr, 0, less * 2 - 1)
        b.size = less - 1
        val d = node.arr[half * 2 + 1]!!.data!!
        node.arr.fill(null, half * 2 + 1)
        return d to b
    }

    fun insert(data: Int) {
        fun loop(node: Node): Pair<Int, Node>? {
            if (node.isLeave) {
                val i = node.find(data)
                node.addToLeave(i, data)
                if (node.size() == this.m) {
                    val new = spiltData(node)
                    val x = new.arr[1]!!
                    if (node === root) {
                        root = Node(m, false)
                        root.addToLeave(0, x.data!!)
                        root.setNode(0, node)
                        root.setNode(2, new)
                    }
                    return x.data!! to new
                } else {
                    return null
                }
            } else {
                val i = node.find(data)
                val son = node.arr[i]?.point!!
                val res = loop(son)
                if (res != null) {
                    node.addToLeave(i, res.first)
                    node.setNode(i + 2, res.second)
                    if (node.size == m) {
                        val (d, new) = spiltNode(node)
                        if (node === root) {
                            root = Node(m, false)
                            root.addToLeave(0, d)
                            root.setNode(2, new)
                            root.setNode(0, node)
                            return null
                        }
                        return d to new
                    }
                }
                return null
            }
        }
        loop(root)
    }

    fun show() {
        Node.show(root)
    }

    fun find(d: Int): Int? {
        fun loop(node: Node): Int? {
            val i = node.find(d)
            if (node.isLeave) {
                return node.arr[i]?.data?.let { node.id }
            }
            val x = if (i % 2 == 1) {
                i + 1
            } else {
                i
            }
            val n = node.arr[x]!!.point!!
            return loop(n)
        }
        return loop(root)
    }



    companion object {
        val ids = AtomicInteger()
        fun getId(): Int {
            return ids.incrementAndGet()
        }
    }
}

fun main() {
    val s = System.currentTimeMillis()
    val node = BplusTree(256)
    (1 until 100000).forEach { a ->
        node.insert(a)
    }
    node.insert(-1)
    val e = System.currentTimeMillis()
    node.show()

    println("time:${e - s}")
    val ss = System.currentTimeMillis()
    println(node.find(5000))
    println(node.find(7000))
    println(node.find(9000))
    println(node.find(21000))
    println(node.find(8000))
    println(node.find(80000))
    val ee = System.currentTimeMillis()
    println("time:${ee - ss}")
}