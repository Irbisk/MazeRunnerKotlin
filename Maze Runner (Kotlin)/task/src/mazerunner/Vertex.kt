package mazerunner

import kotlin.math.min
import kotlin.random.Random

var maze: Maze? = null


class Maze(val length: Int, val height: Int) {

    var grid = MutableList(height) { MutableList(length) { Cell(0,0) } }

    init {
        for (x in 0 until height) {
            for (y in 0 until length) {
                grid[x][y] = Cell(x, y)
            }
        }
    }

    fun createAdjList(): List<Node>  {
        val nodesList = mutableListOf<Node>()
        for (x in 0 until height) {
            for (y in 0 until length) {
                val cell = grid[x][y]
                nodesList.add(Node("${grid[x][y].x},${grid[x][y].y}"))
            }
        }
        nodesList.forEach {
            val cord = it.name.split(",").map { it.toInt() }
            val cellsList = Cell(cord[0], cord[1]).computeNeighbours()
            val neighbors = mutableListOf<Node?>()

            cellsList.forEach {
                val name = "${it.x},${it.y}"
                neighbors.add(nodesList.find { it.name == name })
            }
            it.neighbors = neighbors.filterNotNull()
        }
        return nodesList
    }



    fun generate() {
        val frontierSet = mutableSetOf<Cell>()

        var randomX = Random.nextInt(1, height)
        var randomY = Random.nextInt(1, length)

        while (randomX % 2 == 0) {
            randomX = Random.nextInt(1, height)
        }
        while (randomY % 2 == 0) {
            randomY = Random.nextInt(1, length)
        }


        grid[randomX][randomY].isBlocked = false

        var cellFrontierList = grid[randomX][randomY].computeFrontierOrClosestCells(true)
        cellFrontierList.forEach {
            frontierSet.add(it)
        }

        while (frontierSet.isNotEmpty()) {
            val cell = frontierSet.shuffled().first()
            cell.isBlocked = false
            cell.setInPassageCell()
            frontierSet.remove(cell)
            cellFrontierList = cell.computeFrontierOrClosestCells(true)
            cellFrontierList.forEach { frontierSet.add(it) }
        }
        createLastWallsAndExits()
    }


    private fun createLastWallsAndExits() {
        for (x in 0 until height) {
            grid[x][length - 1].isBlocked = true
        }
        for (y in 0 until length) {
            grid[height - 1][y].isBlocked = true
        }

        //create entry
        var x = Random.nextInt(1, height - 2)
        var y = 0
        grid[x][y].isBlocked = false
        while (grid[x][y + 1].isBlocked) {
            grid[x][y + 1].isBlocked = false
            y++
        }
        //create exit
        x = Random.nextInt(1, height - 2)
        y = length - 1
        grid[x][y].isBlocked = false
        while (grid[x][y - 1].isBlocked) {
            grid[x][y - 1].isBlocked = false
            y--
        }
    }

    private fun Cell.setInPassageCell() {
        val cellsAvailable = computeFrontierOrClosestCells(false)
        for (cell in cellsAvailable.shuffled()) {
            if (!grid[cell.x][cell.y].isBlocked) {
                if (this.x == cell.x) {
                    grid[this.x][min(this.y, cell.y) + 1].isBlocked = false
                } else {
                    grid[min(this.x, cell.x) + 1][this.y].isBlocked = false
                }
                break
            }
        }
    }

    private fun Cell.computeFrontierOrClosestCells(frontier: Boolean): List<Cell> {
        val list = mutableListOf<Cell?>()
        list.add(Cell(this.x, this.y + 2).getGridCellOrNull(frontier))
        list.add(Cell(this.x, this.y - 2).getGridCellOrNull(frontier))
        list.add(Cell(this.x + 2, this.y).getGridCellOrNull(frontier))
        list.add(Cell(this.x - 2, this.y).getGridCellOrNull(frontier))
        return list.filterNotNull()
    }

    private fun Cell.computeNeighbours(): List<Cell> {
        val list = mutableListOf<Cell?>()
        list.add(Cell(this.x, this.y + 1).getGridCellOrNull(false))
        list.add(Cell(this.x, this.y - 1).getGridCellOrNull(false))
        list.add(Cell(this.x + 1, this.y).getGridCellOrNull(false))
        list.add(Cell(this.x - 1, this.y).getGridCellOrNull(false))
        return list.filterNotNull()


    }

    private fun Cell.getGridCellOrNull(frontier: Boolean): Cell? {
        return if (frontier) {
            if (this.x < 0 || this.x >= height - 1  ||
                this.y < 0 || this.y >= length - 1 || !grid[x][y].isBlocked
            ) null
            else grid[x][y]
        } else {
            if (this.x < 0 || this.x >= height ||
                this.y < 0 || this.y >= length || grid[x][y].isBlocked
            ) null
            else grid[x][y]
        }

    }

    fun printMaze() {
        val wall = "\u2588\u2588"
        val pass = "  "
        val escapeWay = "//"
        this.grid.forEach { it ->
            it.forEach {
                if (it.isBlocked) print(wall)
                else if (it.isEscapeWay) print(escapeWay)
                else print(pass)
            }
            println()
        }
        println()
    }
}

class Cell(val x: Int, val y: Int, var isBlocked: Boolean = true, var isEscapeWay: Boolean = false)