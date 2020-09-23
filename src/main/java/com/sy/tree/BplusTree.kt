package com.sy.tree

import java.lang.RuntimeException

data class Data(val data: Int?, val point: Node?)

data class Node(val m: Int, val isLeave: Boolean = true) {
    val arr = arrayOfNulls<Data?>(m * 2 + 1)
    var size = 0

    fun set(list: List<Int>) {
        list.forEachIndexed { i, d ->
            val ii = i * 2 + 1
            //arr.add(Data(d, null))
            //arr.add(null)
            arr[ii] = Data(d, null)
            size++
        }
    }

    fun find(data: Int): Int {
        var start = 0
        /*var end = if (arr.size % 2 == 0) {
            arr.size + 1
        } else {
            arr.size
        }*/
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
        if (this.isLeave) {//叶子
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
            /*if (size == m) {
                val pair = spilt(this)
                return pair
            } else {
                return null to this
            }*/
        }
    }

    fun setNode(index: Int, node: Node) {
        this.arr[index] = Data(null, node)
    }

    /*fun insert(data: Int): Pair<Data?, Node> {
        val index = find(data)

        if (this.isLeave) {//叶子
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
            if (size == m) {
                val pair = spilt(this)
                return pair
            } else {
                return null to this
            }
        } else {//树枝
            val d = this.arr[index]
            return d to d?.point!!
        }
        //return null to this
    }*/


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
            println()
            node.arr.forEachIndexed { index, d ->
                if (index % 2 == 0) {
                    d?.also {
                        it.point?.also { show(it) }
                    }
                }
            }
        }


    }
}

class BplusTree(val m: Int) {
    private var root = Node(m)

    fun spilt(node: Node): Node {
        val half = m / 2
        System.arraycopy(node.arr, 0, node.arr, 0, half * 2)
        node.size = half
        val less = m - half
        val b = Node(m)
        System.arraycopy(node.arr, half * 2, b.arr, 0, less * 2 + 1)
        b.size = less
        node.arr.fill(null, half * 2)
        node.arr[half * 2] = Data(null, b)
        return b
    }

    fun insert(data: Int) {
        fun loop(node: Node): Node? {
            if (node.isLeave) {
                val i = node.find(data)
                node.addToLeave(i, data)
                if (node.size() == this.m) {
                    val new = spilt(node)
                    return new
                } else {
                    return null
                }
            } else {
                val i = node.find(data)
                val son = node.arr[i]?.point!!
                val res = loop(son)
                if (res != null) {
                    node.addToLeave(i + 1, res.arr[1]!!.data!!)
                    node.setNode(i + 2, res)
                    if (node.size == m) {
                        val new = spilt(node)
                        return new

                    }
                }
                return null
            }
        }
        loop(root)
        Node.show(root)
    }
}

fun main() {
    val node = BplusTree(4)
    node.insert(1)
    node.insert(2)
    node.insert(3)
    node.insert(4)
    //node.insert(7)
    node.insert(-1)
    node.insert(19)
    node.insert(5)
    node.insert(7)



}