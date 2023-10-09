package mazerunner

import java.util.Collections


class Node(val name: String) {
    var neighbors = listOf<Node>()
    var visited = false
    var prev: Node? = null
}

class ShortestPath (private val nodesList: List<Node>) {

    private val start = getNode(true)
    private val end = getNode(false)

    private fun getNode(start: Boolean): Node {
        var cell: Cell = Cell(0,0)
        val column = if (start) 0 else maze!!.length - 1
        for (x in 0 until maze!!.height) {
            if (!maze!!.grid[x][column].isBlocked) {
                cell = maze!!.grid[x][column]
                break
            }
        }
        val name = "${cell.x},${cell.y}"
        return nodesList.find { it.name == name }!!
    }

    fun bfs() {
        val queue = mutableListOf<Node>()
        start.visited = true
        queue.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (node in current.neighbors) {
                if (!node.visited) {
                    node.visited = true
                    queue.add(node)
                    node.prev = current
                    if (node == end) {
                        queue.clear()
                        break
                    }
                }
            }
        }
        traceRoute()
    }

    private fun traceRoute() {
        var node: Node? = end
        val route = mutableListOf<Node>()
        while (node != null) {
            route.add(node)
            node = node.prev
        }
        Collections.reverse(route)
        route.forEach {
            val cords = it.name.split(",").map { it.toInt() }
            val x= cords[0]
            val y = cords[1]
            maze!!.grid[x][y].isEscapeWay = true
        }
        maze!!.printMaze()
    }
}

